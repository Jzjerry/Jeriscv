package Jeriscv

import chisel3._
import chisel3.util._

import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec


class IDUTest  extends AnyFlatSpec with ChiselScalatestTester{
  behavior of "Instruction Decode Unit"
  it should "Decode Instructions Somehow" in{

    test(new InstructionDecodeUnit(new JeriscvConfig)) { dut =>

      dut.F2D.InstData.poke("b000000000001_00000_000_00001_0010011".U)
      println("Decode Receive Instr:" + dut.inst.peek())
      println("Decode Op1:" + dut.D2E.Op1.peek())
      println("Decode Op2:" + dut.D2E.Op2.peek())
    }

  }

}
