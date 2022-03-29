package Jeriscv

import chisel3._
import chisel3.util._

class RegFileInterface(width : Int) extends Bundle{
  val rs1_addr = Input(UInt(5.W))
  val rs2_addr = Input(UInt(5.W))
  val rd_addr = Input(UInt(5.W))
  val rd_wdata = Input(UInt(width.W))
  val rs_read = Input(Bool())
  val rd_write = Input(Bool())
  val rs1_rdata = Output(UInt(width.W))
  val rs2_rdata = Output(UInt(width.W))
}


/* asynchronous-read, synchronous-write */
class RegFile(width : Int) extends Module{
  val io = IO(new RegFileInterface(width))

  val RegMem = Mem(32, UInt(width.W))

  io.rs1_rdata := Mux(io.rs1_addr =/= 0.U, Mux(io.rs_read,RegMem.read(io.rs1_addr),0.U), 0.U)
  io.rs2_rdata := Mux(io.rs2_addr =/= 0.U, Mux(io.rs_read,RegMem.read(io.rs2_addr),0.U), 0.U)

  when(io.rd_write && (io.rd_addr =/= 0.U)){
    RegMem.write(io.rd_addr, io.rd_wdata)
  }
}
