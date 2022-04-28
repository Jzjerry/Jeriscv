package Jeriscv


class JeriscvConfig{

  var DebugOutput       : Boolean = true
  var VirtualInstMem    : Boolean = false
  var InstMemBlackBox   : Boolean = true
  var AddrWidth         : Int = 32

  var RegFileWidth      : Int = 32

  var DBusInterface     : Boolean = false
  var IBusInterface     : Boolean = false

  var BranchFlush       : Boolean = true

  var DataMemSize       : Int = 4096
  var DataMemBlackBox   : Boolean = true
  var SyncDataMem       : Boolean = false
  var DataMemFile       : String = ""

  var InstNum           : Int = 256
  var InstMemSrc        : String = ""
  var InstMemAddrWidth  : Int = 32

  var ALUOneHotOptimize       : Boolean = true
  var BRUOneHotOptimize       : Boolean = true

  var SimplePipeline          : Boolean = false
}
