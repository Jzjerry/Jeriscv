package Jeriscv

import chisel3._
import chisel3.util._
import chisel3.experimental.ChiselEnum

object LSUFunct3 extends ChiselEnum{
  val lb, lh, lw, lbu, lhu, sb, sh, sw, default = Value
}

class LSUInterface(width : Int) extends Bundle{
  val funct = Input(LSUFunct3())
  val byteaddr = Input(UInt(2.W))
  val mem_data = Input(UInt(width.W))
  val LSUResult = Output(UInt(width.W))
}

class LSU(width : Int) extends Module {
  val io = IO(new LSUInterface(width))

  io.LSUResult := 0.U
//  val data = io.mem_data >> (io.byteaddr << 3.U)
  val bytes = Wire(Vec(4,UInt(8.W)))

  bytes(0) := io.mem_data(7,0)
  bytes(1) := io.mem_data(15,8)
  bytes(2) := io.mem_data(23,16)
  bytes(3) := io.mem_data(31,24)

  switch(io.funct){
    is(LSUFunct3.lb){
      io.LSUResult := Cat(Fill(24,bytes(io.byteaddr)(7)), bytes(io.byteaddr))
    }
    is(LSUFunct3.lh){
      io.LSUResult := Cat(Fill(16,bytes(io.byteaddr + 1.U)(7)), bytes(io.byteaddr + 1.U), bytes(io.byteaddr))
    }
    is(LSUFunct3.lw){
      io.LSUResult := io.mem_data
    }
    is(LSUFunct3.lbu){
      io.LSUResult := bytes(io.byteaddr)
    }
    is(LSUFunct3.lhu){
      io.LSUResult := Cat(bytes(io.byteaddr+1.U), bytes(io.byteaddr))
    }
  }
}
