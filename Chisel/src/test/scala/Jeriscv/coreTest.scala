package Jeriscv

import chisel3._
import chisel3.util._

import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class coreTest extends AnyFlatSpec with ChiselScalatestTester {

  behavior of "Core"
  it should "Caculate Simple ADDI and ADD instruction" in{
    test(new core(new JeriscvConfig)) { dut =>

      // I test
      dut.virtualFetch.InstData.poke("b000000000001_00000_000_00001_0010011".asUInt)
      println("ADDI 0x1, x0, x1")
      println("ALU Result(Before step): " + dut.io_o.ALUResult.peek())
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.ALUResult.peek())

      dut.virtualFetch.InstData.poke("b000000000001_00001_000_00010_0010011".asUInt)
      println("ADDI 0x1, x1, x2")
      println("ALU Result(Before step): " + dut.io_o.ALUResult.peek())
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.ALUResult.peek())

      // R test
      dut.virtualFetch.InstData.poke("b000000000010_00001_000_00011_0110011".asUInt)
      println("ADD x1, x2, x3")
      println("ALU Result(Before step): " + dut.io_o.ALUResult.peek())
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.ALUResult.peek())

      println("SUB x3, x1, x3")
      dut.virtualFetch.InstData.poke("b010000000001_00011_000_00011_0110011".asUInt)
      println("ALU Result(Before step): " + dut.io_o.ALUResult.peek())
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.ALUResult.peek())
    }
  }
  it should "Generate Correct Immediate Num" in {
    test(new core(new JeriscvConfig)) { dut =>
      println("Imm Type Test")
      println(dut.IDU.I_imm)
      println(dut.IDU.S_imm)
      println(dut.IDU.B_imm)
      println(dut.IDU.U_imm)
      println(dut.IDU.J_imm)
    }
  }
}
