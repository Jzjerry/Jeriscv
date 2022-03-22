package Jeriscv

import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFileInline

class InstructionMemInterface(InstNum : Int) extends Bundle{
  val InstAddr = Input(UInt(log2Ceil(InstNum * 4).W))
  val InstData = Output(UInt(32.W))
}

class InstructionMem (memoryFile : String = "", InstNum : Int)extends Module {
  val width : Int = 8     /* the length of memory blocks should be 8 bits */
  val io = IO(new InstructionMemInterface(InstNum))
  val mem = SyncReadMem(InstNum * 4, UInt(width.W))
  if (memoryFile.trim().nonEmpty) {
    loadMemoryFromFileInline(mem, memoryFile)
  }
  val InsWidth: Int = 32  /* the length of RISC-V ISA should be 32 bits */
  /* Memory organized in Big Endian */
  io.InstData := Cat(
    mem(io.InstAddr + 3.U),
    mem(io.InstAddr + 2.U),
    mem(io.InstAddr + 1.U),
    mem(io.InstAddr)
  )
}

