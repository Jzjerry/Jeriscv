package Jeriscv

import chisel3._
import chisel3.util._


class Execute2MemInterface (Config : JeriscvConfig) extends Bundle{
  val ALUResult = UInt(Config.RegFileWidth.W)
  val MemoryWriteData = UInt(Config.RegFileWidth.W)
  val MemoryAddress = UInt(log2Ceil(Config.DataMemSize).W)
  val MemoryWriteEnable_n = Bool()
}

class ExecuteUnit(Config : JeriscvConfig) extends Module{

  val D2E = IO(Input(new Decode2ExecuteInterface(Config)))
  val E2M = IO(Output(new Execute2MemInterface(Config)))

  val alu = Module(new ALU(Config.RegFileWidth, Config.ALUOptimize))


  // ALU Input
  alu.io.op1 := D2E.Op1
  alu.io.op2 := D2E.Op2

  alu.io.funct3 := D2E.ALUFunct

  // ALU Output
  E2M.ALUResult := alu.io.result
  E2M.MemoryAddress := alu.io.result

  // Unit Output
  E2M.MemoryWriteData := D2E.MemoryWriteData
  E2M.MemoryWriteEnable_n := D2E.MemoryWriteEnable_n

}
