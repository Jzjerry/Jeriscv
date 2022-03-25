package Jeriscv

import chisel3.util._

class JeriscvConfig{
  val RegFileWidth = 32
  val DataMemSize = 256
  val InstNum = 256
  val InstMemSrc = "bin/inst.hex"
  val InstMemAddrWidth = log2Ceil(InstNum * 4)
  val ALUOptimize = true
}
