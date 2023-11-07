package Jeriscv

import Jeriscv.Peripheral._
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
  val E2MD = IO(Input(new Execute2MemNoDelayInterface(Config)))
  val M2W = IO(Output(new Memory2WritebackInterface(Config)))
  val M2F = IO(Output(new Memory2FetchInterface(Config)))

  val lsu = Module(new LSU(Config.BusDataWidth))

  val master = Wire(new mmioInterface(32))


  master.ByteEnable := 0.U
  when(E2MD.LSUFunct === LSUFunct3.sw) {
    master.ByteEnable := "b1111".U
  }.elsewhen(E2MD.LSUFunct === LSUFunct3.sh) {
    master.ByteEnable := "b0011".U << E2MD.MemoryAddress(1,0)
  }.elsewhen(E2MD.LSUFunct === LSUFunct3.sb) {
    master.ByteEnable := "b0001".U << E2MD.MemoryAddress(1,0)
  }
  master.WriteEn := E2MD.MemoryWriteEnable
  master.ReadEn := E2MD.MemoryReadEnable
  master.WriteData := E2MD.MemoryWriteData
  master.Addr := E2MD.MemoryAddress

  lsu.io.mem_data := 0.U
  val devices = List(
    Module(new mmioDMem(0x10000000L, 2048)),
//    Module(new mmioDMemBlackBox(0x20000000L, 1024))
  )
  def connect(device : mmioModule) : Unit = {
    device.mmio_in := master
    device.select :=
        (master.Addr >= device.BaseAddr.U) &&
        (master.Addr < device.BaseAddr.U + device.AddrRange.U)
      when(device.select) { lsu.io.mem_data := device.mmio_out }
  }
  devices.foreach(connect)

  lsu.io.funct := E2MD.LSUFunct
  lsu.io.byteaddr := E2MD.MemoryAddress(1,0)
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