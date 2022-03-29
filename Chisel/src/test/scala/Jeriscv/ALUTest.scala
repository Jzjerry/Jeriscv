package Jeriscv

import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import chisel3._
import chisel3.util._

class ALUTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "ALU"
  it should "calculate the result according to func" in{
    test( new ALU(32, false)){ dut=>

      dut.io.funct3.poke(ALUFunct3.add)
      dut.io.op1.poke(1.U)
      dut.io.op2.poke(2.U)
      println("result:" +dut.io.result.peek())
    }
  }
}
