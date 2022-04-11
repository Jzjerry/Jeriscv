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
  val data = io.mem_data >> (io.byteaddr << 3.U)

  switch(io.funct){
    is(LSUFunct3.lb){
      io.LSUResult := Cat(Fill(24,data(7)),data(7,0))
    }
    is(LSUFunct3.lh){
      io.LSUResult := Cat(Fill(16,data(15)),data(15,0))
    }
    is(LSUFunct3.lw){
      io.LSUResult := data
    }
    is(LSUFunct3.lbu){
      io.LSUResult := data(7,0)
    }
    is(LSUFunct3.lhu){
      io.LSUResult := data(15,0)
    }
  }
}
