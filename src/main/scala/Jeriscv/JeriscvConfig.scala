package Jeriscv

import spinal.core._

import Jeriscv.isa._

case class JeriscvConfig(){

  var withDebugPort = false
  var withDPIPort   = false
  var withRVFormal  = false

  var ProgramReset  = 0x80000000L

  var isaTable      = (RV32I.TableALU ++ RV32I.TableBRU ++ RV32I.TableLS
                      ++ RV64I.TableALU ++ RV64I.TableLS
                      ++ RV32M.Table ++ RV64M.Table)

  var isRV64I       = true
  var isRV32M       = true
  var isRV64M       = true

  var XLEN          = 64
}

object JeriscvConfig {
  def apply(
             withDPIPort : Boolean = false,
             withDebugPort : Boolean = false
           ): JeriscvConfig = {
    val config = JeriscvConfig()
    config.withDPIPort = withDPIPort
    config.withDebugPort = withDebugPort
    config
  }
}