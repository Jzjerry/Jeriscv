package Jeriscv

import chisel3._
import chisel3.util._
import chisel3.experimental.ChiselEnum

object BRUFunct3 extends ChiselEnum{
  val beq, bne, blt, bge, bltu, bgeu, jal, jalr, default = Value
}

class BRUInterface (width : Int, AddrWidth : Int)extends Bundle{
  val op1 = Input(UInt(width.W))
  val op2 = Input(UInt(width.W))
  val offset = Input(UInt(width.W))
  val InstAddr = Input(UInt(AddrWidth.W))
  val funct = Input(BRUFunct3())
  val BranchFlag = Output(Bool())
  val BranchAddr = Output(UInt(AddrWidth.W))
}

class BRU(width : Int, AddrWidth : Int, OneHot : Boolean) extends Module {
  val io = IO(new BRUInterface(width, AddrWidth))

  io.BranchAddr := io.InstAddr + io.offset

  if(OneHot){
    val BFlagVec = Wire(Vec(8, Bool()))
    val OHfunct = UIntToOH(io.funct.asUInt)
    for(i <- 0 until 8){ BFlagVec(i) := false.B }
    when(OHfunct(0) === true.B){ BFlagVec(0) := (io.op1 === io.op2) }
    when(OHfunct(1) === true.B){ BFlagVec(1) := (io.op1 =/= io.op2) }
    when(OHfunct(2) === true.B){ BFlagVec(2) := (io.op1.asSInt < io.op2.asSInt) }
    when(OHfunct(3) === true.B){ BFlagVec(3) := (io.op1.asSInt >= io.op2.asSInt) }
    when(OHfunct(4) === true.B){ BFlagVec(4) := (io.op1 < io.op2) }
    when(OHfunct(5) === true.B){ BFlagVec(5) := (io.op1 >= io.op2) }
    when(OHfunct(6) === true.B){
      BFlagVec(6) := true.B
    }
    when(OHfunct(7) === true.B){
      BFlagVec(7) := true.B
      io.BranchAddr := io.op1 + io.op2
    }
    io.BranchFlag := BFlagVec.reduce(_|_)
  }
  else{
    io.BranchFlag := false.B
    switch(io.funct)
    {
      is(BRUFunct3.beq) { io.BranchFlag := (io.op1 === io.op2) }
      is(BRUFunct3.bne) { io.BranchFlag := (io.op1 =/= io.op2) }
      is(BRUFunct3.blt) { io.BranchFlag := (io.op1.asSInt < io.op2.asSInt) }
      is(BRUFunct3.bge) { io.BranchFlag := (io.op1.asSInt >= io.op2.asSInt) }
      is(BRUFunct3.bltu){ io.BranchFlag := (io.op1 < io.op2) }
      is(BRUFunct3.bgeu){ io.BranchFlag := (io.op1 >= io.op2) }
      is(BRUFunct3.jal) {
        io.BranchFlag := true.B
      }
      is(BRUFunct3.jalr){
        io.BranchFlag := true.B
        io.BranchAddr := io.op1 + io.op2
      }
    }
  }
}
