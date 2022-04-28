package Jeriscv

import Jeriscv.Bus._
import chisel3._
import chisel3.util._

class Memory2WritebackInterface(Config : JeriscvConfig) extends Bundle{
  val ALUResult = UInt(Config.RegFileWidth.W)

  val NextAddr = UInt(Config.InstMemAddrWidth.W)

  val MemoryReadData = UInt(Config.RegFileWidth.W)

  val WriteBackSrc = WriteBackType()
  val WriteBackEn = Bool()
  val WriteBackDest = UInt(5.W)
}

class Memory2FetchInterface(Config : JeriscvConfig) extends Bundle{
  val BranchFlag = Bool()
  val BranchAddr = UInt(Config.InstMemAddrWidth.W)
}

class MemoryUnit(Config : JeriscvConfig) extends Module {

  val E2M = IO(Input(new Execute2MemInterface(Config)))
  val M2W = IO(Output(new Memory2WritebackInterface(Config)))
  val M2F = IO(Output(new Memory2FetchInterface(Config)))
//  val DBus = IO(AXI4LiteInterface(AXI4LiteConfig()))

  val lsu = Module(new LSU(Config.RegFileWidth))

  if(Config.DBusInterface) {

  } else if(Config.DataMemBlackBox) {
    val DMem = Module(new DataMemBlackBox(Config.DataMemSize))
    DMem.io.byteena_a := 0xF.U
    when(E2M.LSUFunct === LSUFunct3.sw) {
      DMem.io.byteena_a := 0xF.U
    }.elsewhen(E2M.LSUFunct === LSUFunct3.sh) {
      DMem.io.byteena_a := 0x3.U << E2M.MemoryAddress(1,0)
    }.elsewhen(E2M.LSUFunct === LSUFunct3.sb) {
      DMem.io.byteena_a := 0x1.U << E2M.MemoryAddress(1,0)
    }
    DMem.io.wren := E2M.MemoryWriteEnable
    DMem.io.rden := E2M.MemoryReadEnable
    DMem.io.rd_aclr := reset
    DMem.io.data := E2M.MemoryWriteData
    DMem.io.rdaddress := E2M.MemoryAddress(log2Ceil(Config.DataMemSize) - 1, 2)
    DMem.io.wraddress := E2M.MemoryAddress(log2Ceil(Config.DataMemSize) - 1, 2)
    DMem.io.rdclock := (~clock.asUInt).asBool.asClock
    DMem.io.wrclock := (~clock.asUInt).asBool.asClock

    lsu.io.mem_data := DMem.io.q
  }
  else {
    val DMem = Module(new DataMem(Config.DataMemSize, Config.SyncDataMem, Config.DataMemFile))
    DMem.io.ByteEnable := 0.U
    when(E2M.LSUFunct === LSUFunct3.sw) {
      DMem.io.ByteEnable := "b1111".U
    }.elsewhen(E2M.LSUFunct === LSUFunct3.sh) {
      DMem.io.ByteEnable := "b0011".U << E2M.MemoryAddress(1,0)
    }.elsewhen(E2M.LSUFunct === LSUFunct3.sb) {
      DMem.io.ByteEnable := "b0001".U << E2M.MemoryAddress(1,0)
    }
    DMem.clock := (~clock.asUInt).asBool.asClock
    DMem.io.WriteEn := E2M.MemoryWriteEnable
    DMem.io.ReadEn := E2M.MemoryReadEnable
    DMem.io.WriteData := E2M.MemoryWriteData
    DMem.io.Addr := E2M.MemoryAddress(log2Ceil(Config.DataMemSize) - 1, 2)

    lsu.io.mem_data := DMem.io.ReadData
  }

  lsu.io.funct := E2M.LSUFunct
  lsu.io.byteaddr := E2M.MemoryAddress(1,0)
  M2W.MemoryReadData := lsu.io.LSUResult
  // Stage Output
  M2W.ALUResult := E2M.ALUResult
  M2F.BranchAddr := E2M.BranchAddr
  M2F.BranchFlag := E2M.BranchFlag
  M2W.NextAddr := E2M.InstAddr + 4.U
  M2W.WriteBackEn := E2M.WriteBackEn
  M2W.WriteBackDest := E2M.WriteBackDest
  M2W.WriteBackSrc := E2M.WriteBackSrc
}
