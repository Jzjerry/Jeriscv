package Jeriscv.Bus

import Jeriscv._
import chisel3._
import chisel3.stage.ChiselStage

object GenAXI4 {
  def main(args: Array[String]): Unit = {
    val Config = new JeriscvConfig
    Config.DebugOutput = true
    Config.VirtualInstMem = true
    Config.InstMemBlackBox = false
    Config.DataMemBlackBox = false
    Config.SyncDataMem = false
    //    print(new Memory2WritebackInterface(Config))
  }
}
