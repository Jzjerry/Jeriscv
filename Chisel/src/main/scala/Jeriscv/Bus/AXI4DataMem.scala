package Jeriscv.Bus

import Jeriscv._
import chisel3._
import chisel3.util._



class AXI4DataMem(config : JeriscvConfig) extends Module{

  val S2M = IO(AXI4LiteSlave2Master(AXI4LiteConfig(config.RegFileWidth, config.AddrWidth)))
  val M2S = IO(Flipped(AXI4LiteMaster2Slave(AXI4LiteConfig(config.RegFileWidth, config.AddrWidth))))
  val DMem = Module(new DataMem(config.DataMemSize,config.SyncDataMem,config.DataMemFile))

  when(M2S.AR.valid){
  }

}
