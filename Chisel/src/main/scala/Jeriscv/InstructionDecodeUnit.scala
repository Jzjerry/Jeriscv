package Jeriscv

import chisel3._
import chisel3.util._
import chisel3.experimental.ChiselEnum


class Decode2ExecuteInterface (Config : JeriscvConfig) extends Bundle{
  val Op1  = UInt(Config.RegFileWidth.W)
  val Op2 = UInt(Config.RegFileWidth.W)
  val BranchOffset = UInt(Config.RegFileWidth.W)

  val InstAddr = UInt(Config.InstMemAddrWidth.W)


  val ExecType   = ExecuteType()
  val ALUFunct   = ALUFunct3()
  val BRUFunct   = BRUFunct3()
  val LSUFunct   = LSUFunct3()

  val MemoryWriteData = UInt(Config.RegFileWidth.W)
  val MemoryWriteEnable_n = Bool()

  val WriteBackSrc = WriteBackType()
}

object WriteBackType extends ChiselEnum{
  val ALU, NextAddr, Mem, default = Value
}

object Op2SrcType extends ChiselEnum{
  val rs2, imm, default = Value
}

object Op1SrcType extends ChiselEnum{
  val rs1, zero, InstAddr, default = Value
}

class InstructionDecodeUnit(Config : JeriscvConfig) extends Module {

  val F2D = IO(Input(new Fetch2DecodeInterface(Config)))
  val W2D = IO(Input(new Memory2WritebackInterface(Config)))
  val D2E = IO(Output(new Decode2ExecuteInterface(Config)))

  val RegFile = Module(new RegFile(Config.RegFileWidth))

  val inst = F2D.InstData

  // Format Decode
  val funct7 = inst(31, 25)
  val rs2 = inst(24, 20)
  val rs1 = inst(19, 15)
  val funct3 = inst(14, 12)
  val rd = inst(11, 7)
  val opcode = inst(6, 0)

  val exec_type = Wire(ExecuteType())
  val inst_type = Wire(InstType())

  // ALU Decode
  val op1src = Wire(Op1SrcType())
  val op2src = Wire(Op2SrcType())


  // Immediate Generation
  val ImmTable = Array(
    InstType.I_Type -> Cat(Fill(21,inst(31)), inst(30,20)),
    InstType.S_Type -> Cat(Fill(21,inst(31)), inst(30,25), inst(11,7)),
    InstType.B_Type -> Cat(Fill(20,inst(31)), inst(7), inst(30, 25), inst(11, 8), 0.U(1.W)),
    InstType.U_Type -> Cat(inst(31, 12), 0.U(12.W)),
    InstType.J_Type -> Cat(Fill(12,inst(31)), inst(19, 12), inst(20), inst(30, 21), 0.U(1.W))
  )
  val immGen = Wire(UInt(32.W))


  // Default Wire
  // RegFile Input
  RegFile.io.rd_addr := rd  // This Wire should be delay to WB Unit
  RegFile.io.rs1_addr := rs1
  RegFile.io.rs2_addr := rs2

  RegFile.io.rd_wdata := Mux(W2D.WriteBackSrc === WriteBackType.Mem, W2D.MemoryReadData,
    Mux(W2D.WriteBackSrc === WriteBackType.ALU, W2D.ALUResult,
      Mux(W2D.WriteBackSrc === WriteBackType.NextAddr, W2D.NextAddr, 0.U)))

  RegFile.io.rd_write := true.B
  RegFile.io.rs_read := true.B
  when(W2D.WriteBackSrc === WriteBackType.default){
    RegFile.io.rd_write := false.B
  }

  // Decode2Execute Output
  D2E.Op1 := 0.U
  D2E.Op2 := 0.U
  D2E.BranchOffset := 0.U
  D2E.InstAddr := F2D.InstAddr

  D2E.WriteBackSrc := WriteBackType.default
  D2E.ALUFunct := ALUFunct3.default
  D2E.BRUFunct := BRUFunct3.default
  D2E.LSUFunct := LSUFunct3.default
  D2E.MemoryWriteData := RegFile.io.rs2_rdata
  D2E.MemoryWriteEnable_n := true.B

  immGen := 0.U

  // Enum default
  op1src := Op1SrcType.default
  op2src := Op2SrcType.default
  inst_type := InstType.default
  exec_type := ExecuteType.default


  // ALU Decode Generate
  for(elem <- RV32I_ALU.table){
    when(inst === elem._1){
      inst_type := elem._2.head
      exec_type := elem._2(1)
      D2E.ALUFunct := elem._2.last
    }
  }
  // BRU Decode Generate
  for(elem <- RV32I_BRU.table){
    when(inst === elem._1){
      inst_type := elem._2.head
      exec_type := elem._2(1)
      D2E.BRUFunct := elem._2.last
    }
  }
  // LSU Decode Generate
  for(elem <- RV32I_LSU.table){
    when(inst === elem._1){
      inst_type := elem._2.head
      exec_type := elem._2(1)
      D2E.LSUFunct := elem._2.last
    }
  }

  D2E.ExecType := exec_type

  for(imm <- ImmTable){
    when(inst_type === imm._1){
      immGen := imm._2
    }
  }

  switch(exec_type){
    is(ExecuteType.ALUType){
      D2E.WriteBackSrc := WriteBackType.ALU
      when(inst_type === InstType.I_Type){
        op1src := Op1SrcType.rs1
        op2src := Op2SrcType.imm
      }
      when(inst_type === InstType.U_Type){
        when(inst === RV32I_ALU.AUIPC) {
          op1src := Op1SrcType.InstAddr
          op2src := Op2SrcType.imm
        }
        when(inst === RV32I_ALU.LUI) {
          op1src := Op1SrcType.zero
          op2src := Op2SrcType.imm
        }
      }
      when(inst_type === InstType.R_Type){
        op1src := Op1SrcType.rs1
        op2src := Op2SrcType.rs2
      }
    }
    is(ExecuteType.BRUType){
      D2E.BranchOffset := immGen
      when(inst_type === InstType.I_Type){
        // JALR
        D2E.WriteBackSrc := WriteBackType.NextAddr
        op1src := Op1SrcType.rs1
        op2src := Op2SrcType.imm
      }
      when(inst_type === InstType.J_Type){
        // JAL
        D2E.WriteBackSrc := WriteBackType.NextAddr
        op1src := Op1SrcType.zero
        op2src := Op2SrcType.imm
      }
      when(inst_type === InstType.B_Type){
        op1src := Op1SrcType.rs1
        op2src := Op2SrcType.rs2
        RegFile.io.rd_write := false.B
      }
    }
    is(ExecuteType.LSUType){
      when(inst_type === InstType.I_Type) {
        // Load
        // Address Adding
        D2E.WriteBackSrc := WriteBackType.Mem
        op1src := Op1SrcType.rs1
        op2src := Op2SrcType.imm
        D2E.ALUFunct := ALUFunct3.add
      }
      when(inst_type === InstType.S_Type){
        // Store
        op1src := Op1SrcType.rs1
        op2src := Op2SrcType.imm
        D2E.ALUFunct := ALUFunct3.add
        D2E.MemoryWriteEnable_n := false.B
        RegFile.io.rd_write := false.B
      }
    }
  }

  switch(op1src){
    is(Op1SrcType.rs1)      {D2E.Op1 := RegFile.io.rs1_rdata}
    is(Op1SrcType.zero)     {D2E.Op1 := 0.U}
    is(Op1SrcType.InstAddr) {D2E.Op1 := F2D.InstAddr}
  }
  switch(op2src){
    is(Op2SrcType.rs2)      {D2E.Op2 := RegFile.io.rs2_rdata}
    is(Op2SrcType.imm)      {D2E.Op2 := immGen}
  }

}
