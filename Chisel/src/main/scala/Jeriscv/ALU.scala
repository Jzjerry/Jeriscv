package Jeriscv

import chisel3._
import chisel3.util._

class ALUInterface (width : Int, OneHot : Boolean)extends Bundle{
  val op1 = Input(UInt(width.W))
  val op2 = Input(UInt(width.W))
  val funct3 = if(OneHot){Input(UInt(8.W))} else{Input(UInt(3.W))}
  val result = Output(UInt(width.W))
}


// Not Optimized -- Binary Code funct3t3
// Optimized -- One Hot Code funct3
// Reference: RVCoreP
class ALU (width : Int, OneHot : Boolean)extends Module {
  val io = IO(new ALUInterface(width, OneHot))

  if(OneHot){
    val resultVec = Wire(Vec(8, UInt(width.W)))
    for(i <- 0 until 8){ resultVec(i) := 0.U }
    when(io.funct3(0) === true.B){ resultVec(0) := io.op1 + io.op2}
    when(io.funct3(1) === true.B){ resultVec(1) := io.op1 - io.op2}
    when(io.funct3(2) === true.B){ resultVec(2) := io.op1 ^ io.op2}
    when(io.funct3(3) === true.B){ resultVec(3) := io.op1 | io.op2}
    when(io.funct3(4) === true.B){ resultVec(4) := io.op1 & io.op2}
    when(io.funct3(5) === true.B){ resultVec(5) := io.op1 >> io.op2(4,0)}
    when(io.funct3(6) === true.B){ resultVec(6) := io.op1 << io.op2(4,0)}
    io.result := resultVec.reduce(_^_)
  }
  else{
    io.result := 0.U
    switch(io.funct3)
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
