package Jeriscv

import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import chisel3._
import chisel3.util._



class RegFileTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "RegFile"
  it should "RegFile Do Read/Write " in{
    test(new RegFile(32)){ dut=>

      dut.io.rs_read.poke(true.B)
      dut.io.rd_write.poke(false.B)

      dut.io.rs1_addr.poke(0.U)
      dut.io.rs1_rdata.expect(0.U)

      dut.io.rd_write.poke(true.B)
      dut.io.rs_read.poke(false.B)

      dut.io.rd_addr.poke(1.U)
      dut.io.rd_wdata.poke(0xFF.U)
      dut.clock.step()

      dut.io.rs_read.poke(true.B)
      dut.io.rd_write.poke(false.B)

      dut.io.rs2_addr.poke(1.U)
      dut.io.rs2_rdata.expect(0xFF.U)
    }
  }
}
