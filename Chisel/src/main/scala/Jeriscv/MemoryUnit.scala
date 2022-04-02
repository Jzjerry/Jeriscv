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

  val lsu = Module(new LSU(Config.RegFileWidth))

  if(Config.DataMemBlackBox) {
    val DMem = Module(new DataMemBlackBox(Config.DataMemSize))
    DMem.io.byteena_a := 0.U
    when(E2M.LSUFunct === LSUFunct3.sw) {
      DMem.io.byteena_a := "b1111".U
    }.elsewhen(E2M.LSUFunct === LSUFunct3.sh) {
      DMem.io.byteena_a := "b0011".U << E2M.MemoryAddress(1,0)
    }.elsewhen(E2M.LSUFunct === LSUFunct3.sb) {
      DMem.io.byteena_a := "b0001".U << E2M.MemoryAddress(1,0)
    }
    DMem.io.wren := ~E2M.MemoryWriteEnable_n
    DMem.io.data := E2M.MemoryWriteData
    DMem.io.rdaddress := E2M.MemoryAddress(log2Ceil(Config.DataMemSize) - 1, 2)
    DMem.io.wraddress := E2M.MemoryAddress(log2Ceil(Config.DataMemSize) - 1, 2)
    DMem.io.rdclock := clock
    DMem.io.wrclock := clock
    lsu.io.funct := E2M.LSUFunct
    lsu.io.mem_data := DMem.io.q
  }
  else {
    val DMem = Module(new DataMem(Config.DataMemSize, Config.SyncDataMem))
    DMem.io.WriteLength := 0.U
    when(E2M.LSUFunct === LSUFunct3.sw) {
      DMem.io.WriteLength := "b11".U
    }.elsewhen(E2M.LSUFunct === LSUFunct3.sh) {
      DMem.io.WriteLength := "b01".U
    }.elsewhen(E2M.LSUFunct === LSUFunct3.sb) {
      DMem.io.WriteLength := "b00".U
    }
    DMem.io.ReadWrite := E2M.MemoryWriteEnable_n
    DMem.io.WriteData := E2M.MemoryWriteData
    DMem.io.Addr := E2M.MemoryAddress

    lsu.io.funct := E2M.LSUFunct
    lsu.io.mem_data := DMem.io.ReadData
  }
  M2W.MemoryReadData := lsu.io.LSUResult
  M2W.ALUResult := E2M.ALUResult

  M2F.BranchAddr := E2M.BranchAddr
  M2F.BranchFlag := E2M.BranchFlag
  M2W.NextAddr := E2M.InstAddr + 4.U
  M2W.WriteBackSrc := E2M.WriteBackSrc
}
