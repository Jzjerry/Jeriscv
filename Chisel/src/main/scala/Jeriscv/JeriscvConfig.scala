package Jeriscv

import chisel3.util._

class JeriscvConfig{

  var DebugOutput       : Boolean = true
  var VirtualInstMem    : Boolean = false
  var InstMemBlackBox   : Boolean = true

  var RegFileWidth      : Int = 32

  var DataMemSize       : Int = 4096
  var DataMemBlackBox   : Boolean = true
  var SyncDataMem       : Boolean = false

  var InstNum           : Int = 256
  var InstMemSrc        : String = ""
  var InstMemAddrWidth  : Int = 32

  var ALUOneHotOptimize       : Boolean = true
  var BRUOneHotOptimize       : Boolean = true

  var SimplePipeline          : Boolean = false
}
