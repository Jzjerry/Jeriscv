package Jeriscv

import chisel3._
import chisel3.util._
import Jeriscv.Peripheral._

class JeriscvConfig {

  var DebugOutput       : Boolean = true
  var VirtualInstMem    : Boolean = false
  var InstMemBlackBox   : Boolean = true
  var AddrWidth         : Int = 32

  var RegFileWidth      : Int = 32

  var DBusInterface     : Boolean = false
  var IBusInterface     : Boolean = false

  var BusAddrWidth      : Int = 32
  var BusDataWidth      : Int = 32

  var BranchFlush       : Boolean = true

  var DataMemSize       : Int = 4096
  var DataMemBlackBox   : Boolean = true
  var SyncDataMem       : Boolean = true
  var DataMemFile       : String = ""

  var InstNum           : Int = 256
  var InstMemSrc        : String = ""
  var InstMemAddrWidth  : Int = 32

  var ALUOneHotOptimize       : Boolean = true
  var BRUOneHotOptimize       : Boolean = true

  var SimplePipeline          : Boolean = true
  var HasRV32M                : Boolean = false
  var HasMul                  : Boolean = false
  var HasDiv                  : Boolean = false // Don't open it
  var HasRem                  : Boolean = false // Don't open it

}