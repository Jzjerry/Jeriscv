package Jeriscv

import chisel3._
import chisel3.util._

import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class PipelineTest extends AnyFlatSpec with ChiselScalatestTester {

  val Config = new JeriscvConfig
  Config.DebugOutput = true
  Config.VirtualInstMem = true
  Config.InstMemBlackBox = false
  Config.DataMemBlackBox = false
  Config.SyncDataMem = false
  Config.SimplePipeline = true

  behavior of "Pipelined Core"
  it should "Bypass Test" in{
    test(new core(Config)) { dut =>
      // I test
      // Data Dependency
      println("InstAddr: " + dut.vmem.InstAddr.peek())
      dut.vmem.InstData.poke("b000000000001_00001_000_00001_0010011".asUInt)
      println("ADDI 0x1, x1, x1")
      println("Result(step 0): " + dut.io_o.m2w.ALUResult.peek())
      println("Bypass(step 0): " + dut.BypassIO.peek())
      dut.clock.step()
      println("InstAddr: " + dut.vmem.InstAddr.peek())
      dut.vmem.InstData.poke("b000000000001_00010_000_00010_0010011".asUInt)
      println("ADDI 0x1, x2, x2")
      println("Result(step 1): " + dut.io_o.m2w.ALUResult.peek())
      println("Bypass(step 1): " + dut.BypassIO.peek())
      dut.clock.step()
      println("InstAddr: " + dut.vmem.InstAddr.peek())
      dut.vmem.InstData.poke("b000000000001_00010_000_00011_0110011".asUInt)
      println("ADD x3, x1, x2")
      println("Result(step 2): " + dut.io_o.m2w.ALUResult.peek())
      println("Bypass(step 2): " + dut.BypassIO.peek())
      dut.clock.step()

      println("InstAddr: " + dut.vmem.InstAddr.peek())
      dut.vmem.InstData.poke(RV32I_ALU.NOP)
      println("Result(step 3): " + dut.io_o.m2w.ALUResult.peek())
      println("Bypass(step 3): " + dut.BypassIO.peek())
      dut.clock.step()

      println("InstAddr: " + dut.vmem.InstAddr.peek())
      dut.vmem.InstData.poke(RV32I_ALU.NOP)
      println("Result(step 4): " + dut.io_o.m2w.ALUResult.peek())
      println("Bypass(step 4): " + dut.BypassIO.peek())
      dut.clock.step()

      println("InstAddr: " + dut.vmem.InstAddr.peek())
      dut.vmem.InstData.poke(RV32I_ALU.NOP)
      println("Result(step 5): " + dut.io_o.m2w.ALUResult.peek())
      println("Bypass(step 5): " + dut.BypassIO.peek())
      dut.clock.step()

      println("InstAddr: " + dut.vmem.InstAddr.peek())
      dut.vmem.InstData.poke(RV32I_ALU.NOP)
      println("Result(step 6): " + dut.io_o.m2w.ALUResult.peek())
      println("Bypass(step 6): " + dut.BypassIO.peek())
      dut.clock.step()

      println("InstAddr: " + dut.vmem.InstAddr.peek())
      dut.vmem.InstData.poke(RV32I_ALU.NOP)
      println("Result(step 7): " + dut.io_o.m2w.ALUResult.peek())
      println("Bypass(step 7): " + dut.BypassIO.peek())
      dut.clock.step()

    }
  }
  it should "Stall Pipeline when Hazard" in{
    test(new core(Config)) { dut =>

      println("InstAddr: " + dut.vmem.InstAddr.peek())
      dut.vmem.InstData.poke("b000000000001_00001_000_00001_0010011".asUInt)
      println("ADDI 0x1, x1, x1")
      println("Result(step 0): " + dut.io_o.m2w.ALUResult.peek())
      println("Bypass(step 0): " + dut.BypassIO.peek())
      dut.clock.step()

      println("InstAddr: " + dut.vmem.InstAddr.peek())
      dut.vmem.InstData.poke("b00000_00000_00000_000_00100_0100011".asUInt)
      println("SB 0x4, x0, x0")
      println("Result(step 1): " + dut.io_o.m2w.ALUResult.peek())
      println("Bypass(step 1): " + dut.BypassIO.peek())
      dut.clock.step()

      println("InstAddr: " + dut.vmem.InstAddr.peek())
      dut.vmem.InstData.poke("b000000000100_00000_100_00010_0000011".asUInt)
      println("LBU 0x4, x0, x2")
      println("Result(step 2): " + dut.io_o.m2w.ALUResult.peek())
      println("Bypass(step 2): " + dut.BypassIO.peek())
      dut.clock.step()

      println("InstAddr: " + dut.vmem.InstAddr.peek())
      dut.vmem.InstData.poke("b000000000001_00010_000_00010_0010011".asUInt)
      println("ADDI 0x1, x2, x2")
      println("Result(step 3): " + dut.io_o.m2w.ALUResult.peek())
      println("Bypass(step 3): " + dut.BypassIO.peek())
      dut.clock.step()

      println("InstAddr: " + dut.vmem.InstAddr.peek())
      println("Result(step 4): " + dut.io_o.m2w.ALUResult.peek())
      println("Bypass(step 4): " + dut.BypassIO.peek())
      dut.clock.step()

      println("InstAddr: " + dut.vmem.InstAddr.peek())
      println("Result(step 5): " + dut.io_o.m2w.ALUResult.peek())
      println("Bypass(step 5): " + dut.BypassIO.peek())
      dut.clock.step()

      println("InstAddr: " + dut.vmem.InstAddr.peek())
      println("Result(step 6): " + dut.io_o.m2w.ALUResult.peek())
      println("Bypass(step 6): " + dut.BypassIO.peek())
      dut.clock.step()

      println("InstAddr: " + dut.vmem.InstAddr.peek())
      println("Result(step 7): " + dut.io_o.m2w.ALUResult.peek())
      println("Bypass(step 7): " + dut.BypassIO.peek())
      dut.clock.step()

      println("InstAddr: " + dut.vmem.InstAddr.peek())
      println("Result(step 8): " + dut.io_o.m2w.ALUResult.peek())
      println("Bypass(step 8): " + dut.BypassIO.peek())
      dut.clock.step()
    }
  }
  it should "Stall Pipeline when branches" in{
    test(new core(Config)){dut=>

      val vmem = Map(
        0.U(32.W) -> "b000000000001_00001_000_00001_0010011".U(32.W),
        0x4.U(32.W) -> "b000000000001_00001_000_00001_0010011".U(32.W)
      )

      print(dut.vmem.InstAddr.peek())
      print(vmem(0.U(32.W)))
      dut.vmem.InstData.poke(vmem(dut.vmem.InstAddr.peek()))
      dut.clock.step()

    }
  }

}
