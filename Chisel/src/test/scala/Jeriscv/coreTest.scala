package Jeriscv

import chisel3._
import chisel3.util._

import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class coreTest extends AnyFlatSpec with ChiselScalatestTester {

  val Config = new JeriscvConfig
  Config.DataMemBlackBox = false
  Config.InstMemBlackBox = false
  Config.VirtualInstMem = true
  Config.SyncDataMem = false

  behavior of "Core"
  it should "Caculate Basic R-Type & I-Type Instruction" in{
    test(new core(Config)) { dut =>

      // I test
      dut.vmem.InstData.poke("b000000000001_00000_000_00001_0010011".asUInt)
      println("ADDI 0x1, x0, x1")
      println("ALU Result(Before step): " + dut.io_o.m2w.ALUResult.peek())
      dut.io_o.m2w.ALUResult.expect(1.U)
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.m2w.ALUResult.peek())
      dut.io_o.m2w.ALUResult.expect(1.U)

      dut.vmem.InstData.poke("b000000000001_00001_000_00010_0010011".asUInt)
      println("ADDI 0x1, x1, x2")
      println("ALU Result(Before step): " + dut.io_o.m2w.ALUResult.peek())
      dut.io_o.m2w.ALUResult.expect(2.U)
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.m2w.ALUResult.peek())
      dut.io_o.m2w.ALUResult.expect(2.U)

      dut.vmem.InstData.poke("b111111111100_00010_000_00010_0010011".asUInt)
      println("ADDI -4, x1, x2")
      println("ALU Result(Before step): " + dut.io_o.m2w.ALUResult.peek())
//      dut.io_o.m2w.ALUResult.expect(-1)
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.m2w.ALUResult.peek())
//      dut.io_o.m2w.ALUResult.expect(0.U)


      dut.vmem.InstData.poke("b010000000001_00010_101_00010_0010011".asUInt)
      println("SRAI 0x1, x2, x2")
      println("ALU Result(Before step): " + dut.io_o.m2w.ALUResult.peek())
//      dut.io_o.m2w.ALUResult.expect(-1.S.asUInt)
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.m2w.ALUResult.peek())
//      dut.io_o.m2w.ALUResult.expect(0.U)

      // R test
      dut.vmem.InstData.poke("b000000000010_00001_000_00011_0110011".asUInt)
      println("ADD x1, x2, x3")
      println("ALU Result(Before step): " + dut.io_o.m2w.ALUResult.peek())
//      dut.io_o.m2w.ALUResult.expect(3.U)
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.m2w.ALUResult.peek())
//      dut.io_o.m2w.ALUResult.expect(3.U)

      println("SUB x3, x1, x3")
      dut.vmem.InstData.poke("b010000000001_00011_000_00011_0110011".asUInt)
      println("ALU Result(Before step): " + dut.io_o.m2w.ALUResult.peek())
//      dut.io_o.m2w.ALUResult.expect(2.U)
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.m2w.ALUResult.peek())
//      dut.io_o.m2w.ALUResult.expect(1.U)
    }
  }
  it should "Perform Basic U-Type Instruction" in{
    test(new core(Config)){ dut=>
      dut.vmem.InstData.poke("b00000000111111111111_00001_0110111".asUInt)
      println("LUI 0X00FFF, x1")
      println("ALU Result(Before step): " + dut.io_o.m2w.ALUResult.peek())
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.m2w.ALUResult.peek())

      dut.vmem.InstData.poke("b000000000001_00001_000_00010_0010011".asUInt)
      println("ADDI 0x1, x1, x1")
      println("ALU Result(Before step): " + dut.io_o.m2w.ALUResult.peek())
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.m2w.ALUResult.peek())

//      dut.virtualFetch.InstAddr.poke(4.U)
      dut.vmem.InstData.poke("b00000000111111111111_00001_0010111".asUInt)
      println("AUIPC 0X00FFF, x1")
      println("ALU Result(Before step): " + dut.io_o.m2w.ALUResult.peek())
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.m2w.ALUResult.peek())

      dut.vmem.InstData.poke("b000000000001_00001_000_00010_0010011".asUInt)
      println("ADDI 0x1, x1, x1")
      println("ALU Result(Before step): " + dut.io_o.m2w.ALUResult.peek())
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.m2w.ALUResult.peek())
    }
  }
  it should "Perform Basic J-Type Instruction" in{
    test(new core(Config)) { dut =>

      dut.vmem.InstData.poke("b000000000100_00001_000_00001_0010011".asUInt)
      println("ADDI 0x4, x1, x1")
      println("ALU Result(Before step): " + dut.io_o.m2w.ALUResult.peek())
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.m2w.ALUResult.peek())

//      dut.virtualFetch.InstAddr.poke(4.U)
      dut.vmem.InstData.poke("b11111111110111111111_00001_1101111".asUInt)
      println("JAL -4, x1")
      println("BRU Result(Before step): " + dut.io_o.m2f.BranchAddr.peek() + dut.io_o.m2f.BranchFlag.peek())
      dut.clock.step()
      println("BRU Result( After step): " + dut.io_o.m2f.BranchAddr.peek() + dut.io_o.m2f.BranchFlag.peek())

      dut.vmem.InstData.poke("b000000000100_00001_000_00001_0010011".asUInt)
      println("ADDI 0x4, x1, x1")
      println("ALU Result(Before step): " + dut.io_o.m2w.ALUResult.peek())
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.m2w.ALUResult.peek())

//      dut.virtualFetch.InstAddr.poke(16.U)
      dut.vmem.InstData.poke("b000000000010_00000_000_00001_1100111".asUInt)
      println("JALR 0x2, x0, x1")
      println("BRU Result(Before step): " + dut.io_o.m2f.BranchAddr.peek() + dut.io_o.m2f.BranchFlag.peek())
      dut.clock.step()
      println("BRU Result( After step): " + dut.io_o.m2f.BranchAddr.peek() + dut.io_o.m2f.BranchFlag.peek())

      dut.vmem.InstData.poke("b000000000100_00001_000_00001_0010011".asUInt)
      println("ADDI 0x4, x1, x1")
      println("ALU Result(Before step): " + dut.io_o.m2w.ALUResult.peek())
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.m2w.ALUResult.peek())
    }
  }
  it should "Perform Basic B-Type Instruction" in{
    test(new core(Config)) {dut=>

//      dut.virtualFetch.InstAddr.poke(4.U)
      dut.vmem.InstData.poke("b0000000_00001_00001_000_00000_1100011".asUInt)
      println("BEQ 0x0, x1, x1")
      println("BRU Result(Before step): " + dut.io_o.m2f.BranchAddr.peek() + dut.io_o.m2f.BranchFlag.peek())
      dut.clock.step()
      println("BRU Result( After step): " + dut.io_o.m2f.BranchAddr.peek() + dut.io_o.m2f.BranchFlag.peek())

      dut.vmem.InstData.poke("b000000000100_00001_000_00001_0010011".asUInt)
      println("ADDI 0x4, x1, x1")
      println("ALU Result(Before step): " + dut.io_o.m2w.ALUResult.peek())
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.m2w.ALUResult.peek())

//      dut.virtualFetch.InstAddr.poke(8.U)
      dut.vmem.InstData.poke("b0000001_00001_00001_001_00000_1100011".asUInt)
      println("BNE 0x0, x1, x1")
      println("BRU Result(Before step): " + dut.io_o.m2f.BranchAddr.peek() + dut.io_o.m2f.BranchFlag.peek())
      dut.clock.step()
      println("BRU Result( After step): " + dut.io_o.m2f.BranchAddr.peek() + dut.io_o.m2f.BranchFlag.peek())

      dut.vmem.InstData.poke("b0000010_00000_00001_001_00000_1100011".asUInt)
      println("BNE 0x0, x1, x0")
      println("BRU Result(Before step): " + dut.io_o.m2f.BranchAddr.peek() + dut.io_o.m2f.BranchFlag.peek())
      dut.clock.step()
      println("BRU Result( After step): " + dut.io_o.m2f.BranchAddr.peek() + dut.io_o.m2f.BranchFlag.peek())

      dut.vmem.InstData.poke("b0000100_00000_00001_100_00000_1100011".asUInt)
      println("BLT 0x0, x1, x0")
      println("BRU Result(Before step): " + dut.io_o.m2f.BranchAddr.peek() + dut.io_o.m2f.BranchFlag.peek())
      dut.clock.step()
      println("BRU Result( After step): " + dut.io_o.m2f.BranchAddr.peek() + dut.io_o.m2f.BranchFlag.peek())

      dut.vmem.InstData.poke("b0001000_00000_00001_101_00000_1100011".asUInt)
      println("BGE 0x0, x1, x0")
      println("BRU Result(Before step): " + dut.io_o.m2f.BranchAddr.peek() + dut.io_o.m2f.BranchFlag.peek())
      dut.clock.step()
      println("BRU Result( After step): " + dut.io_o.m2f.BranchAddr.peek() + dut.io_o.m2f.BranchFlag.peek())

    }
  }
  it should "Perform Store & Load Instruction" in {
    test(new core(Config)) { dut =>
      dut.vmem.InstData.poke("b111111111111_00000_000_00001_0010011".asUInt)
      println("ADDI 0xfff, x0, x1")
      println("ALU Result(Before step): " + dut.io_o.m2w.ALUResult.peek())
      //      dut.io_o.m2w.ALUResult.expect(4.U)
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.m2w.ALUResult.peek())
      //      dut.io_o.m2w.ALUResult.expect(4.U)

      dut.vmem.InstData.poke("b00000_00001_00000_000_00100_0100011".asUInt)
      println("SB 0x4, x0, x1")
      println("ALU Result(Before step): " + dut.io_o.m2w.ALUResult.peek())
      //      dut.io_o.m2w.ALUResult.expect(0.U)
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.m2w.ALUResult.peek())
      //      dut.io_o.m2w.ALUResult.expect(0.U)

      dut.vmem.InstData.poke("b000000000000_00000_000_00001_0010011".asUInt)
      println("ADDI 0x0, x0, x1")
      println("ALU Result(Before step): " + dut.io_o.m2w.ALUResult.peek())
      //      dut.io_o.m2w.ALUResult.expect(4.U)
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.m2w.ALUResult.peek())

      dut.vmem.InstData.poke("b00000_00001_00000_010_00000_0100011".asUInt)
      println("SW 0x0, x0, x1")
      println("ALU Result(Before step): " + dut.io_o.m2w.ALUResult.peek())
      //      dut.io_o.m2w.ALUResult.expect(0.U)
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.m2w.ALUResult.peek())

      dut.vmem.InstData.poke("b000000000100_00000_010_00010_0000011".asUInt)
      println("LW 0x4, x0, x2")
      println("MemRead Result(Before step): " + dut.io_o.m2w.MemoryReadData.peek())
      //      dut.io_o.m2w.ALUResult.expect(4.U)
      dut.clock.step()
      println("MemRead Result( After step): " + dut.io_o.m2w.MemoryReadData.peek())
      //      dut.io_o.m2w.ALUResult.expect(0.U)

      dut.vmem.InstData.poke("b000000000000_00010_000_00000_0010011".asUInt)
      println("ADDI 0x0, x2, x0")
      println("ALU Result(Before step): " + dut.io_o.m2w.ALUResult.peek())
      //      dut.io_o.m2w.ALUResult.expect(4.U)
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.m2w.ALUResult.peek())

      dut.vmem.InstData.poke("b000000000100_00000_100_00010_0000011".asUInt)
      println("LBU 0x4, x0, x2")
      println("MemRead Result(Before step): " + dut.io_o.m2w.MemoryReadData.peek())
      //      dut.io_o.m2w.ALUResult.expect(4.U)
      dut.clock.step()
      println("MemRead Result( After step): " + dut.io_o.m2w.MemoryReadData.peek())
      //      dut.io_o.m2w.ALUResult.expect(0.U)

      dut.vmem.InstData.poke("b000000000000_00010_000_00000_0010011".asUInt)
      println("ADDI 0x0, x2, x0")
      println("ALU Result(Before step): " + dut.io_o.m2w.ALUResult.peek())
      //      dut.io_o.m2w.ALUResult.expect(4.U)
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.m2w.ALUResult.peek())

      dut.vmem.InstData.poke("b000000000100_00000_101_00010_0000011".asUInt)
      println("LHU 0x4, x0, x2")
      println("MemRead Result(Before step): " + dut.io_o.m2w.MemoryReadData.peek())
      //      dut.io_o.m2w.ALUResult.expect(4.U)
      dut.clock.step()
      println("MemRead Result( After step): " + dut.io_o.m2w.MemoryReadData.peek())
      //      dut.io_o.m2w.ALUResult.expect(0.U)

      dut.vmem.InstData.poke("b000000000000_00010_000_00000_0010011".asUInt)
      println("ADDI 0x0, x2, x0")
      println("ALU Result(Before step): " + dut.io_o.m2w.ALUResult.peek())
      //      dut.io_o.m2w.ALUResult.expect(4.U)
      dut.clock.step()
      println("ALU Result( After step): " + dut.io_o.m2w.ALUResult.peek())
    }
  }
  it should "Generate Correct Immediate Num" in {
    test(new core(Config)) { dut =>
      println("Imm Type Test")
      for(imm <- dut.IDU.ImmTable)
        println(imm._1 + "->" + imm._2)
    }
  }
}
