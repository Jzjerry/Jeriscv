package Jeriscv

import chisel3._
import chisel3.util._


class DataMemInterface(MemSize : Int) extends Bundle{
  val Addr = Input(UInt(log2Ceil(MemSize).W))
  val WriteData = Input(UInt(32.W))
  val ReadWrite = Input(Bool())   /* Read = True, Write = False */
  val ReadData = Output(UInt(32.W))
}

class DataMem (MemSize : Int)extends Module{
  val width : Int = 8     /* the length of memory blocks should be 8 bits */
  val io = IO(new DataMemInterface(MemSize))
  val mem = SyncReadMem(MemSize , UInt(width.W))

  io.ReadData := 0.U
  /* Read = True, Write = False*/
  /* Memory organized in Big Endian */
  when(io.ReadWrite){
    io.ReadData := Cat(
      mem(io.Addr + 3.U),
      mem(io.Addr + 2.U),
      mem(io.Addr + 1.U),
      mem(io.Addr)
    )
  }.otherwise{
    mem(io.Addr + 3.U) := io.WriteData(31,24)
    mem(io.Addr + 2.U) := io.WriteData(23,16)
    mem(io.Addr + 1.U) := io.WriteData(15,8)
    mem(io.Addr) := io.WriteData(7,0)
  }
}
