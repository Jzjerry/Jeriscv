package Jeriscv

import chisel3._
import chisel3.util._

class Memory2WritebackInterface(Config : JeriscvConfig) extends Bundle{
  val ALUResult = UInt(Config.RegFileWidth.W)

  val BranchFlag = Bool()
  val BranchAddr = UInt(Config.InstMemAddrWidth.W)

  val NextAddr = UInt(Config.InstMemAddrWidth.W)

  val MemoryReadData = UInt(Config.RegFileWidth.W)
  val WriteBackSrc = WriteBackType()
}

class MemoryUnit(Config : JeriscvConfig) extends Module {

  val E2M = IO(Input(new Execute2MemInterface(Config)))
  val M2W = IO(Output(new Memory2WritebackInterface(Config)))
  val DMem = Module(new DataMem(Config.DataMemSize))

  DMem.io.ReadWrite := E2M.MemoryWriteEnable_n
  DMem.io.WriteData := E2M.MemoryWriteData
  DMem.io.Addr := E2M.MemoryAddress

  M2W.MemoryReadData := DMem.io.ReadData
  M2W.ALUResult := E2M.ALUResult

  M2W.BranchAddr := E2M.BranchAddr
  M2W.BranchFlag := E2M.BranchFlag
  M2W.NextAddr := E2M.InstAddr + 4.U
  M2W.WriteBackSrc := E2M.WriteBackSrc
}
