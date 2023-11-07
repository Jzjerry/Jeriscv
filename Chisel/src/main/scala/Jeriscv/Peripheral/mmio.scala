package Jeriscv.Peripheral


import Jeriscv._
import chisel3._
import chisel3.util._

class mmioInterface(AddrWidth : Int = 32) extends Bundle{
  val Addr = Input(UInt(AddrWidth.W))
  val WriteData = Input(UInt(32.W))
  val ByteEnable = Input(UInt(4.W))
  val ReadEn  = Input(Bool())
  val WriteEn = Input(Bool())
}

class mmioModule extends Module {
  val mmio_in = IO(new mmioInterface)
  val select = IO(Input(Bool()))
  val mmio_out = IO(Output(UInt(32.W)))
  var BaseAddr : Long = 0x00000000L
  var AddrRange : Int = 4096
  mmio_out := 0.U
}

class mmioDMem(Base: Long, Range : Int) extends mmioModule{
  BaseAddr = Base
  AddrRange = Range
  val width : Int = 32     /* the length of memory blocks should be 8 bits */
  val mem = SyncReadMem(AddrRange/4 , UInt(width.W))
  val ByteAddr = ((mmio_in.Addr - BaseAddr.U) >> 2.U).asUInt
  /* Memory organized in Big Endian */
  when(mmio_in.ReadEn && select){
    mmio_out := mem.read(ByteAddr)
  }.elsewhen(mmio_in.WriteEn && select){
    mem.write(ByteAddr, mmio_in.WriteData)
  }
}
class mmioDMemBlackBox(Base: Long, Range : Int) extends mmioModule{
  BaseAddr = Base
  AddrRange = Range
  val width : Int = 32     /* the length of memory blocks should be 8 bits */
  val mem = Module(new DataMemBlackBox(32))
  val ByteAddr = ((mmio_in.Addr - BaseAddr.U) >> 2.U).asUInt
  /* Memory organized in Big Endian */
  mem.io.rdclock := clock
  mem.io.wrclock := clock
  mem.io.wraddress := ByteAddr
  mem.io.rdaddress := ByteAddr
  mem.io.rden := mmio_in.ReadEn & select
  mem.io.wren := mmio_in.WriteEn & select
  mem.io.data := mmio_in.WriteData
  mem.io.byteena_a := mmio_in.ByteEnable
  mem.io.rd_aclr := reset
  mmio_out := mem.io.q
}