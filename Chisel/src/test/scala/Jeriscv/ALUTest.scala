package Jeriscv

import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import chisel3._
import chisel3.util._

class ALUTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "ALU"
  it should "calculate the result according to func" in{
    test( new ALU(32, false)){ dut=>

      for(i <- 0 to 0xFFFFFFFF ) {
        dut.io.op1.poke(i.U(32.W))
        for(j <- 0 to 0xFFFFFFFF ){
          dut.io.op2.poke(j.U(32.W))
        //TODO: Test is not so easy to do :(
        }
      }
    }
  }
}
