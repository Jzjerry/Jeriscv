package Jeriscv

import chisel3._
import chisel3.util._


class Execute2MemInterface (Config : JeriscvConfig) extends Bundle{
  val ALUResult = UInt(Config.RegFileWidth.W)

  val BranchFlag = Bool()
  val BranchAddr = UInt(Config.InstMemAddrWidth.W)

  val LSUFunct = LSUFunct3()
  val MemoryWriteData = UInt(Config.RegFileWidth.W)
  val MemoryAddress = UInt(log2Ceil(Config.DataMemSize).W)
  val MemoryWriteEnable_n = Bool()

  val InstAddr = UInt(Config.InstMemAddrWidth.W)
  val WriteBackSrc = WriteBackType()
}

class ExecuteUnit(Config : JeriscvConfig) extends Module{

  val D2E = IO(Input(new Decode2ExecuteInterface(Config)))
  val E2M = IO(Output(new Execute2MemInterface(Config)))

  val alu = Module(new ALU(Config.RegFileWidth, Config.ALUOneHotOptimize))
  val bru = Module(new BRU(Config.RegFileWidth, Config.InstMemAddrWidth, Config.BRUOneHotOptimize))


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
      alu.io.op1 := D2E.Op1
      alu.io.op2 := D2E.Op2
      alu.io.funct3 := D2E.ALUFunct
    }
    is(ExecuteType.BRUType){
      bru.io.op1 := D2E.Op1
      bru.io.op2 := D2E.Op2
      bru.io.offset := D2E.BranchOffset
      bru.io.InstAddr := D2E.InstAddr
      bru.io.funct := D2E.BRUFunct
    }
    is(ExecuteType.LSUType){
      alu.io.op1 := D2E.Op1
      alu.io.op2 := D2E.Op2
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
  E2M.MemoryWriteData := D2E.MemoryWriteData
  E2M.MemoryWriteEnable_n := D2E.MemoryWriteEnable_n

  E2M.InstAddr := D2E.InstAddr
  E2M.WriteBackSrc := D2E.WriteBackSrc
}
