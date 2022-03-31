package Jeriscv

import chisel3._
import chisel3.util._

import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class coreTest extends AnyFlatSpec with ChiselScalatestTester {

  behavior of "Core"
  it should "Caculate Basic R-Type & I-Type Instruction" in{
    test(new core(new JeriscvConfig)) { dut =>

      // I test
      dut.virtualFetch.InstData.poke("b000000000001_00000_000_00001_0010011".asUInt)
      println("ADDI 0x1, x0, x1")
      println("ALU Result(Before step): " + dut.io_o.ALUResult.peek())
      dut.io_o.ALUResult.expect(1.U)
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.ALUResult.peek())
      dut.io_o.ALUResult.expect(1.U)

      dut.virtualFetch.InstData.poke("b000000000001_00001_000_00010_0010011".asUInt)
      println("ADDI 0x1, x1, x2")
      println("ALU Result(Before step): " + dut.io_o.ALUResult.peek())
      dut.io_o.ALUResult.expect(2.U)
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.ALUResult.peek())
      dut.io_o.ALUResult.expect(2.U)

      dut.virtualFetch.InstData.poke("b111111111100_00010_000_00010_0010011".asUInt)
      println("ADDI -3, x1, x2")
      println("ALU Result(Before step): " + dut.io_o.ALUResult.peek())
//      dut.io_o.ALUResult.expect(-1)
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.ALUResult.peek())
//      dut.io_o.ALUResult.expect(0.U)


      dut.virtualFetch.InstData.poke("b010000000001_00010_101_00010_0010011".asUInt)
      println("SRAI 0x1, x2, x2")
      println("ALU Result(Before step): " + dut.io_o.ALUResult.peek())
//      dut.io_o.ALUResult.expect(-1.S.asUInt)
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.ALUResult.peek())
//      dut.io_o.ALUResult.expect(0.U)

      // R test
      dut.virtualFetch.InstData.poke("b000000000010_00001_000_00011_0110011".asUInt)
      println("ADD x1, x2, x3")
      println("ALU Result(Before step): " + dut.io_o.ALUResult.peek())
//      dut.io_o.ALUResult.expect(3.U)
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.ALUResult.peek())
//      dut.io_o.ALUResult.expect(3.U)

      println("SUB x3, x1, x3")
      dut.virtualFetch.InstData.poke("b010000000001_00011_000_00011_0110011".asUInt)
      println("ALU Result(Before step): " + dut.io_o.ALUResult.peek())
//      dut.io_o.ALUResult.expect(2.U)
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.ALUResult.peek())
//      dut.io_o.ALUResult.expect(1.U)
    }
  }
  it should "Perform Basic U-Type Instruction" in{
    test(new core(new JeriscvConfig)){ dut=>
      dut.virtualFetch.InstData.poke("b00000000111111111111_00001_0110111".asUInt)
      println("LUI 0X00FFF, x1")
      println("ALU Result(Before step): " + dut.io_o.ALUResult.peek())
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.ALUResult.peek())

      dut.virtualFetch.InstData.poke("b000000000001_00001_000_00010_0010011".asUInt)
      println("ADDI 0x1, x1, x1")
      println("ALU Result(Before step): " + dut.io_o.ALUResult.peek())
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.ALUResult.peek())

      dut.virtualFetch.InstAddr.poke(4.U)
      dut.virtualFetch.InstData.poke("b00000000111111111111_00001_0010111".asUInt)
      println("AUIPC 0X00FFF, x1")
      println("ALU Result(Before step): " + dut.io_o.ALUResult.peek())
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.ALUResult.peek())

      dut.virtualFetch.InstData.poke("b000000000001_00001_000_00010_0010011".asUInt)
      println("ADDI 0x1, x1, x1")
      println("ALU Result(Before step): " + dut.io_o.ALUResult.peek())
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.ALUResult.peek())
    }
  }
  it should "Perform Basic J-Type Instruction" in{
    test(new core(new JeriscvConfig)) { dut =>

      dut.virtualFetch.InstData.poke("b000000000100_00001_000_00001_0010011".asUInt)
      println("ADDI 0x4, x1, x1")
      println("ALU Result(Before step): " + dut.io_o.ALUResult.peek())
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.ALUResult.peek())

      dut.virtualFetch.InstAddr.poke(4.U)
      dut.virtualFetch.InstData.poke("b11111111110111111111_00001_1101111".asUInt) // "b11111111111111111111_00001_1101111"
      println("JAL -4, x1")
      println("BRU Result(Before step): " + dut.io_o.BranchAddr.peek() + dut.io_o.BranchFlag.peek())
      dut.clock.step()
      println("BRU Result( After step): " + dut.io_o.BranchAddr.peek() + dut.io_o.BranchFlag.peek())

      dut.virtualFetch.InstData.poke("b000000000100_00001_000_00001_0010011".asUInt)
      println("ADDI 0x4, x1, x1")
      println("ALU Result(Before step): " + dut.io_o.ALUResult.peek())
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.ALUResult.peek())

      dut.virtualFetch.InstAddr.poke(16.U)
      dut.virtualFetch.InstData.poke("b000000000010_00000_000_00001_1100111".asUInt)
      println("JALR 0x2, x0, x1")
      println("BRU Result(Before step): " + dut.io_o.BranchAddr.peek() + dut.io_o.BranchFlag.peek())
      dut.clock.step()
      println("BRU Result( After step): " + dut.io_o.BranchAddr.peek() + dut.io_o.BranchFlag.peek())

      dut.virtualFetch.InstData.poke("b000000000100_00001_000_00001_0010011".asUInt)
      println("ADDI 0x4, x1, x1")
      println("ALU Result(Before step): " + dut.io_o.ALUResult.peek())
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.ALUResult.peek())
    }
  }
  it should "Perform Basic B-Type Instruction" in{
    test(new core(new JeriscvConfig)) {dut=>

      dut.virtualFetch.InstAddr.poke(4.U)
      dut.virtualFetch.InstData.poke("b0000000_00001_00001_000_00000_1100011".asUInt)
      println("BEQ 0x0, x1, x1")
      println("BRU Result(Before step): " + dut.io_o.BranchAddr.peek() + dut.io_o.BranchFlag.peek())
      dut.clock.step()
      println("BRU Result( After step): " + dut.io_o.BranchAddr.peek() + dut.io_o.BranchFlag.peek())

      dut.virtualFetch.InstData.poke("b000000000100_00001_000_00001_0010011".asUInt)
      println("ADDI 0x4, x1, x1")
      println("ALU Result(Before step): " + dut.io_o.ALUResult.peek())
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.ALUResult.peek())

      dut.virtualFetch.InstAddr.poke(8.U)
      dut.virtualFetch.InstData.poke("b0000001_00001_00001_001_00000_1100011".asUInt)
      println("BNE 0x0, x1, x1")
      println("BRU Result(Before step): " + dut.io_o.BranchAddr.peek() + dut.io_o.BranchFlag.peek())
      dut.clock.step()
      println("BRU Result( After step): " + dut.io_o.BranchAddr.peek() + dut.io_o.BranchFlag.peek())

      dut.virtualFetch.InstData.poke("b0000010_00000_00001_001_00000_1100011".asUInt)
      println("BNE 0x0, x1, x0")
      println("BRU Result(Before step): " + dut.io_o.BranchAddr.peek() + dut.io_o.BranchFlag.peek())
      dut.clock.step()
      println("BRU Result( After step): " + dut.io_o.BranchAddr.peek() + dut.io_o.BranchFlag.peek())

      dut.virtualFetch.InstData.poke("b0000100_00000_00001_100_00000_1100011".asUInt)
      println("BLT 0x0, x1, x0")
      println("BRU Result(Before step): " + dut.io_o.BranchAddr.peek() + dut.io_o.BranchFlag.peek())
      dut.clock.step()
      println("BRU Result( After step): " + dut.io_o.BranchAddr.peek() + dut.io_o.BranchFlag.peek())

      dut.virtualFetch.InstData.poke("b0001000_00000_00001_101_00000_1100011".asUInt)
      println("BGE 0x0, x1, x0")
      println("BRU Result(Before step): " + dut.io_o.BranchAddr.peek() + dut.io_o.BranchFlag.peek())
      dut.clock.step()
      println("BRU Result( After step): " + dut.io_o.BranchAddr.peek() + dut.io_o.BranchFlag.peek())

    }
  }
  it should "Generate Correct Immediate Num" in {
    test(new core(new JeriscvConfig)) { dut =>
      println("Imm Type Test")
      for(imm <- dut.IDU.ImmTable)
        println(imm._1 + "->" + imm._2)
    }
  }
}
