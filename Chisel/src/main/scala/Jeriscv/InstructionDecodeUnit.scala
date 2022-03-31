package Jeriscv

import chisel3._
import chisel3.util._
import chisel3.experimental.ChiselEnum


class Decode2ExecuteInterface (Config : JeriscvConfig) extends Bundle{
  val Op1  = UInt(Config.RegFileWidth.W)
  val Op2 = UInt(Config.RegFileWidth.W)

  val MemoryWriteData = UInt(Config.RegFileWidth.W)
  val MemoryWriteEnable_n = Bool()

  val ALUFunct   = ALUFunct3()
}

object ALUOp2Src extends ChiselEnum{
  val rs2, imm, NextInstAddr = Value
}

object ALUOp1Src extends ChiselEnum{
  val rs1, zero, InstAddr = Value
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

  val op1src = Wire(ALUOp1Src())
  val op2src = Wire(ALUOp2Src())
  val inst_type = Wire(InstType())

  // Immediate Generation
  val ImmTable = Array(
    InstType.I_Type -> Cat(Fill(21,inst(31)), inst(30,20)),
    InstType.S_Type -> Cat(Fill(21,inst(31)), inst(30,25), inst(11,7)),
    InstType.B_Type -> Cat(Fill(20,inst(31)), inst(7), inst(30, 25), inst(11, 8), 0.U(1.W)),
    InstType.U_Type -> Cat(inst(31, 12), 0.U(12.W)),
    InstType.J_Type -> Cat(Fill(12,inst(31)), inst(19, 12), inst(20), inst(30, 21),0.U(1.W))
  )
  val immGen = Wire(UInt(32.W))



  // RegFile Input
  RegFile.io.rd_addr := rd
  RegFile.io.rs1_addr := rs1
  RegFile.io.rs2_addr := rs2
  //TODO: Signal Connection
  RegFile.io.rd_wdata := Mux(W2D.WriteBackSrc, W2D.MemoryReadData, W2D.ALUResult)
  RegFile.io.rd_write := true.B
  RegFile.io.rs_read := true.B

  // RegFile Output
  D2E.Op1 := RegFile.io.rs1_rdata
  D2E.Op2 := RegFile.io.rs2_rdata

  D2E.ALUFunct := ALUFunct3.default
  D2E.MemoryWriteData := RegFile.io.rs2_rdata
  D2E.MemoryWriteEnable_n := false.B

  op1src := ALUOp1Src.rs1
  op2src := ALUOp2Src.rs2
  inst_type := InstType.R_Type
  immGen := 0.U

  for(elem <- RV32I_ALU.table){
    when(inst === elem._1){
      inst_type := elem._2.head
      D2E.ALUFunct := elem._2.last
    }
  }
  for(imm <- ImmTable){
    when(inst_type === imm._1){
      immGen := imm._2
    }
  }

  switch(inst_type){
    is(InstType.I_Type){
      op1src := ALUOp1Src.rs1
      op2src := ALUOp2Src.imm
      when(inst === RV32I_ALU.JALR){
        op1src := ALUOp1Src.zero
        op2src := ALUOp2Src.NextInstAddr
      }
    }
    is(InstType.S_Type){}
    is(InstType.B_Type){}
    is(InstType.U_Type){
      when(inst === RV32I_ALU.AUIPC) {
        op1src := ALUOp1Src.InstAddr
        op2src := ALUOp2Src.imm
      }
      when(inst === RV32I_ALU.LUI) {
        op1src := ALUOp1Src.zero
        op2src := ALUOp2Src.imm
      }
    }
    is(InstType.J_Type){
      op1src := ALUOp1Src.zero
      op2src := ALUOp2Src.NextInstAddr
    }
    is(InstType.R_Type){
      op1src := ALUOp1Src.rs1
      op2src := ALUOp2Src.rs2
    }
  }
  switch(op1src){
    is(ALUOp1Src.rs1)      {D2E.Op1 := RegFile.io.rs1_rdata}
    is(ALUOp1Src.zero)     {D2E.Op1 := 0.U}
    is(ALUOp1Src.InstAddr) {D2E.Op1 := F2D.InstAddr}
  }
  switch(op2src){
    is(ALUOp2Src.rs2) {D2E.Op2 := RegFile.io.rs2_rdata}
    is(ALUOp2Src.imm) {D2E.Op2 := immGen}
    is(ALUOp2Src.NextInstAddr) {D2E.Op2 := F2D.InstAddr + 4.U}
  }
}
