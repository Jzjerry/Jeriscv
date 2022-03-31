package Jeriscv

import chisel3.util._

class JeriscvConfig{

  val DebugInstMem      : Boolean = true

  val RegFileWidth      : Int = 32
  val DataMemSize       : Int = 256
  val InstNum           : Int = 256
  val InstMemSrc        : String = ""
  val InstMemAddrWidth  : Int = log2Ceil(InstNum * 4)
  val ALUOneHotOptimize       : Boolean = true
  val BRUOneHotOptimize       : Boolean = true
}
