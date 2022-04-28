package Jeriscv

import Jeriscv.ISA._
import chisel3._
import chisel3.util._
import Jeriscv.Pipeline._

class Execute2BypassInterface(Config: JeriscvConfig) extends Bundle{
  val rs1 = UInt(5.W)
  val rs2 = UInt(5.W)
}

class Execute2MemInterface (Config : JeriscvConfig) extends Bundle{
  val ALUResult = UInt(Config.RegFileWidth.W)

  val BranchFlag = Bool()
  val BranchAddr = UInt(Config.InstMemAddrWidth.W)

  val LSUFunct = LSUFunct3()
  val MemoryWriteData = UInt(Config.RegFileWidth.W)
  val MemoryAddress = UInt(log2Ceil(Config.DataMemSize).W)
  val MemoryWriteEnable = Bool()
  val MemoryReadEnable = Bool()

  val InstAddr = UInt(Config.InstMemAddrWidth.W)

  val WriteBackDest = UInt(5.W)
  val WriteBackEn = Bool()
  val WriteBackSrc = WriteBackType()

  val JFlag = Bool()
}

class ExecuteUnit(Config : JeriscvConfig) extends Module{

  val Flush = IO(Input(Bool()))
  val B2E = IO(Input(new Bypass2ExecuteInterface(Config)))
  val D2E = IO(Input(new Decode2ExecuteInterface(Config)))
  val E2M = IO(Output(new Execute2MemInterface(Config)))
  val E2B = IO(Output(new Execute2BypassInterface(Config)))

  val alu = Module(new ALU(Config.RegFileWidth, Config.ALUOneHotOptimize))
  val bru = Module(new BRU(Config.RegFileWidth, Config.InstMemAddrWidth, Config.BRUOneHotOptimize))

  val Op1 = Wire(UInt(Config.RegFileWidth.W))
  val Op2 = Wire(UInt(Config.RegFileWidth.W))

  Op1 := Mux(D2E.op1src === Op1SrcType.rs1,
    Mux(B2E.BypassOp1Flag,
      B2E.BypassOp1Data, D2E.Op1),
    D2E.Op1)

  Op2 := Mux(D2E.op2src === Op2SrcType.rs2,
    Mux(B2E.BypassOp2Flag,
      B2E.BypassOp2Data, D2E.Op2),
    D2E.Op2)

  // Default Wire
  alu.io.op1 := 0.U
  alu.io.op2 := 0.U
  alu.io.funct3 := ALUFunct3.default

  bru.io.op1 := 0.U
  bru.io.op2 := 0.U
  bru.io.offset := 0.U
  bru.io.InstAddr := 0.U
  bru.io.funct := BRUFunct3.default

  switch(D2E.ExecType){
    is(ExecuteType.ALUType){
      alu.io.op1 := Op1
      alu.io.op2 := Op2
      alu.io.funct3 := D2E.ALUFunct
    }
    is(ExecuteType.BRUType){
      bru.io.op1 := Op1
      bru.io.op2 := Op2
      bru.io.offset := D2E.BranchOffset
      bru.io.InstAddr := D2E.InstAddr
      bru.io.funct := D2E.BRUFunct
    }
    is(ExecuteType.LSUType){
      alu.io.op1 := Op1
      alu.io.op2 := Op2
      alu.io.funct3 := D2E.ALUFunct
    }
  }

  // ALU Output
  E2M.ALUResult := alu.io.result
  E2M.MemoryAddress := alu.io.result

  // BRU Output
  E2M.BranchAddr := bru.io.BranchAddr
  E2M.BranchFlag := bru.io.BranchFlag

  // Unit Output
  E2M.LSUFunct := D2E.LSUFunct
  E2M.MemoryWriteData := Mux(B2E.BypassOp2Flag, B2E.BypassOp2Data, D2E.MemoryWriteData)

  E2M.MemoryWriteEnable := D2E.MemoryWriteEnable
  E2M.MemoryReadEnable := D2E.MemoryReadEnable

  E2M.InstAddr := D2E.InstAddr
  E2M.WriteBackSrc := D2E.WriteBackSrc
  E2M.WriteBackDest := D2E.WriteBackDest
  E2M.WriteBackEn := D2E.WriteBackEn
  E2M.JFlag := D2E.JFlag

  E2B.rs1 := D2E.rs1
  E2B.rs2 := D2E.rs2

  when(Flush){
    E2M.ALUResult := 0.U
    E2M.MemoryAddress := 0.U

    // BRU Output
    E2M.BranchAddr := 0.U
    E2M.BranchFlag := false.B

    // Unit Output
    E2M.LSUFunct := LSUFunct3.default
    E2M.MemoryWriteData := 0.U

    E2M.MemoryWriteEnable := false.B
    E2M.MemoryReadEnable := false.B

    E2M.InstAddr := D2E.InstAddr
    E2M.WriteBackSrc := WriteBackType.default
    E2M.WriteBackDest := 0.U
    E2M.WriteBackEn := false.B
    E2M.JFlag := D2E.JFlag

    E2B.rs1 := 0.U
    E2B.rs2 := 0.U
  }
}
