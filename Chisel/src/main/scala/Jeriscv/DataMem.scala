package Jeriscv

import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFileInline


class DataMemInterface(MemSize : Int) extends Bundle{
  val Addr = Input(UInt(log2Ceil(MemSize).W))
  val WriteData = Input(UInt(32.W))
  val ByteEnable = Input(UInt(4.W))
  val ReadEn  = Input(Bool())
  val WriteEn = Input(Bool())
  val ReadData = Output(UInt(32.W))
}

class DataMemBlackBox (AddrWidth : Int) extends BlackBox{
  val io = IO(new Bundle{
    val wrclock = Input(Clock())
    val rdclock = Input(Clock())
    val rdaddress = Input(UInt(AddrWidth.W))
    val wraddress = Input(UInt(AddrWidth.W))
    val wren = Input(Bool())
    val rden = Input(Bool())
    val rd_aclr = Input(Reset())
    val byteena_a = Input(UInt(4.W))
    val data = Input(UInt(32.W))
    val q = Output(UInt(32.W))
  })
}

class DataMem (MemSize : Int, memoryFile : String = "")extends Module{
  val width : Int = 32     /* the length of memory blocks should be 8 bits */
  val io = IO(new DataMemInterface(MemSize))
  val mem = SyncReadMem(MemSize/4 , UInt(width.W))

  if (memoryFile.trim().nonEmpty) {
    loadMemoryFromFileInline(mem, memoryFile)
  }
  io.ReadData := 0.U
  /* Memory organized in Big Endian */
  when(io.ReadEn){
    io.ReadData := Cat(mem.read(io.Addr))
  }.elsewhen(io.WriteEn){
      mem.write(io.Addr, io.WriteData)
  }
}