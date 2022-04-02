package Jeriscv

import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._

object ALUFunct3 extends ChiselEnum{
  val add, sub, xor, or, and, srl, sra, sll, slt, sltu, default = Value
}

class ALUInterface (width : Int)extends Bundle{
  val op1 = Input(UInt(width.W))
  val op2 = Input(UInt(width.W))
  val funct3 = Input(ALUFunct3())
  val result = Output(UInt(width.W))
}

// Not Optimized -- Binary Code funct3t3
// Optimized -- One Hot Code funct3
// Reference: RVCoreP
class ALU (width : Int, OneHot : Boolean)extends Module {
  val io = IO(new ALUInterface(width))

  if(OneHot){
    val resultVec = Wire(Vec(10, UInt(width.W)))
    val OHfunct3 = UIntToOH(io.funct3.asUInt)
    for(i <- 0 until 10){ resultVec(i) := 0.U }
    when(OHfunct3(0) === true.B){ resultVec(0) := io.op1 + io.op2}
    when(OHfunct3(1) === true.B){ resultVec(1) := io.op1 - io.op2}
    when(OHfunct3(2) === true.B){ resultVec(2) := io.op1 ^ io.op2}
    when(OHfunct3(3) === true.B){ resultVec(3) := io.op1 | io.op2}
    when(OHfunct3(4) === true.B){ resultVec(4) := io.op1 & io.op2}
    when(OHfunct3(5) === true.B){ resultVec(5) := io.op1 >> io.op2(4,0)}
    when(OHfunct3(6) === true.B){ resultVec(6) := (io.op1.asSInt >> io.op2(4,0)).asUInt}
    when(OHfunct3(7) === true.B){ resultVec(7) := io.op1 << io.op2(4,0)}
    when(OHfunct3(8) === true.B){ resultVec(8) := (io.op1.asSInt < io.op2.asSInt).asUInt}
    when(OHfunct3(9) === true.B){ resultVec(9) := (io.op1 < io.op2).asUInt}
    io.result := resultVec.reduce(_^_)
  }
  else{
    io.result := 0.U
    switch(io.funct3)
    {
      is(ALUFunct3.add) {io.result := io.op1 + io.op2}
      is(ALUFunct3.sub) {io.result := io.op1 - io.op2}
      is(ALUFunct3.xor) {io.result := io.op1 ^ io.op2}
      is(ALUFunct3.or)  {io.result := io.op1 | io.op2}
      is(ALUFunct3.and) {io.result := io.op1 & io.op2 }
      is(ALUFunct3.srl) {io.result := io.op1 >> io.op2(4,0)}
      is(ALUFunct3.sra) {io.result := (io.op1.asSInt >> io.op2(4,0)).asUInt}
      is(ALUFunct3.sll) {io.result := io.op1 << io.op2(4,0)}
      is(ALUFunct3.slt) {io.result := (io.op1.asSInt < io.op2.asSInt).asUInt}
      is(ALUFunct3.sltu) {io.result := (io.op1 < io.op2).asUInt}
    }
  }
}
