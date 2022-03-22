package Jeriscv

import chisel3._
import chisel3.util._

class ALUInterface (width : Int, Optimize : Boolean)extends Bundle{
  val op1 = Input(UInt(width.W))
  val op2 = Input(UInt(width.W))
  val func = if(Optimize){Input(UInt(8.W))} else{Input(UInt(3.W))}
  val result = Output(UInt(width.W))
}


// Not Optimized -- Binary Code Func
// Optimized -- One Hot Code Func
// Reference: RVCoreP
class ALU (width : Int, Optimize : Boolean)extends Module {
  val io = IO(new ALUInterface(width, Optimize))

  if(Optimize){
    val resultVec = Wire(Vec(8, UInt(width.W)))
    for(i <- 0 until 8){ resultVec(i) := 0.U }
    when(io.func(0) === true.B){ resultVec(0) := io.op1 + io.op2}
    when(io.func(1) === true.B){ resultVec(1) := io.op1 - io.op2}
    when(io.func(2) === true.B){ resultVec(2) := io.op1 ^ io.op2}
    when(io.func(3) === true.B){ resultVec(3) := io.op1 | io.op2}
    when(io.func(4) === true.B){ resultVec(4) := io.op1 & io.op2}
    when(io.func(5) === true.B){ resultVec(5) := io.op1 >> io.op2(4,0)}
    when(io.func(6) === true.B){ resultVec(6) := io.op1 << io.op2(4,0)}
    io.result := resultVec.reduce(_^_)
  }
  else{
    io.result := 0.U
    switch(io.func)
    {
      is("b000".U) {io.result := io.op1 + io.op2}
      is("b001".U) {io.result := io.op1 - io.op2}
      is("b010".U) {io.result := io.op1 ^ io.op2}
      is("b011".U) {io.result := io.op1 | io.op2}
      is("b100".U) {io.result := io.op1 & io.op2 }
      is("b101".U) {io.result := io.op1 >> io.op2(4,0)}
      is("b110".U) {io.result := io.op1 << io.op2(4,0)}
    }
  }
}
