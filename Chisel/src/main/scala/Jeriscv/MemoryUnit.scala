package Jeriscv

import chisel3._
import chisel3.util._

class Memory2WritebackInterface(Config : JeriscvConfig) extends Bundle{
  val ALUResult = UInt(Config.RegFileWidth.W)

  val NextAddr = UInt(Config.InstMemAddrWidth.W)

  val MemoryReadData = UInt(Config.RegFileWidth.W)
  val WriteBackSrc = WriteBackType()
}

class Memory2FetchInterface(Config : JeriscvConfig) extends Bundle{
  val BranchFlag = Bool()
  val BranchAddr = UInt(Config.InstMemAddrWidth.W)
}

class MemoryUnit(Config : JeriscvConfig) extends Module {

  val E2M = IO(Input(new Execute2MemInterface(Config)))
  val M2W = IO(Output(new Memory2WritebackInterface(Config)))
  val M2F = IO(Output(new Memory2FetchInterface(Config)))
  val DMem = Module(new DataMem(Config.DataMemSize))
  val lsu = Module(new LSU(Config.RegFileWidth))

  DMem.io.WriteLength := 0.U
  when( E2M.LSUFunct === LSUFunct3.sw){
    DMem.io.WriteLength := "b11".U
  }.elsewhen( E2M.LSUFunct === LSUFunct3.sh ){
    DMem.io.WriteLength := "b01".U
  }.elsewhen( E2M.LSUFunct === LSUFunct3.sb ){
    DMem.io.WriteLength := "b00".U
  }
  DMem.io.ReadWrite := E2M.MemoryWriteEnable_n
  DMem.io.WriteData := E2M.MemoryWriteData
  DMem.io.Addr := E2M.MemoryAddress

  lsu.io.funct := E2M.LSUFunct
  lsu.io.mem_data := DMem.io.ReadData

  M2W.MemoryReadData := lsu.io.LSUResult
  M2W.ALUResult := E2M.ALUResult

  M2F.BranchAddr := E2M.BranchAddr
  M2F.BranchFlag := E2M.BranchFlag
  M2W.NextAddr := E2M.InstAddr + 4.U
  M2W.WriteBackSrc := E2M.WriteBackSrc
}
