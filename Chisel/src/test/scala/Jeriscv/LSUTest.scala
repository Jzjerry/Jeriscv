package Jeriscv

import chiseltest._
import chisel3._

import org.scalatest.flatspec.AnyFlatSpec

class LSUTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "LSU"
  it should "Sign-extend data from memory" in{
    test(new LSU(32)){ dut=>

      dut.io.mem_data.poke(0xFF.U)
      dut.io.funct.poke(LSUFunct3.lb)
      dut.io.LSUResult.expect("hffffffff".U)

      dut.io.funct.poke(LSUFunct3.lbu)
      dut.io.LSUResult.expect("h000000ff".U)

      dut.io.mem_data.poke(0xFFFF.U)
      dut.io.funct.poke(LSUFunct3.lh)
      dut.io.LSUResult.expect("hffffffff".U)

      dut.io.funct.poke(LSUFunct3.lhu)
      dut.io.LSUResult.expect("h0000ffff".U)

      dut.io.mem_data.poke(0xFFFFF.U)
      dut.io.funct.poke(LSUFunct3.lw)
      dut.io.LSUResult.expect("h000fffff".U)
    }
  }
}
