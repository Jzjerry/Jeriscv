package Jeriscv


import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import chisel3._
import chisel3.util._

class MemTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "DataMem"
  it should "Complete Read And Write" in {
    test(new DataMem(256)) { dut =>
      dut.io.WriteEn.poke(true.B)
      for (i <- 0 until 64) {
        dut.io.Addr.poke((i*4).U)
        dut.io.WriteData.poke(i.U)
        dut.clock.step()
      }
      dut.io.ReadEn.poke(true.B)
      for (i <- 0 until 64) {
        dut.io.Addr.poke((i*4).U)
        dut.clock.step()
        dut.io.ReadData.expect(i.U)
      }
    }
  }
}
