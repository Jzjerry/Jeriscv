package Jeriscv.Bus

import chisel3._
import chisel3.util._
import chisel3.experimental.ChiselEnum

object AXI4LiteResp extends ChiselEnum{
  val OKEY, SLVERR, DECERR = Value
}

case class AXI4LiteConfig
(
  dataWidth : Int = 32,
  addressWidth : Int = 32,
)

case class AXI4LiteMaster2Slave(config : AXI4LiteConfig) extends Bundle{
  val AW = Decoupled(UInt(config.addressWidth.W)) // Master Address Write Port
  val W = Decoupled(new Bundle{                   // Master Data Write Port
    val DATA = UInt(config.dataWidth.W)
    val STRB = UInt(4.W)
  })
  val AR = Decoupled(UInt(config.addressWidth.W))
}
case class AXI4LiteSlave2Master(config : AXI4LiteConfig) extends Bundle{
  val B = Decoupled(AXI4LiteResp()) // Slave Respond Port
  val R = Decoupled(new Bundle{     // Slave Data Read Port
    val DATA = UInt(config.dataWidth.W)
    val RESP = AXI4LiteResp()
  })
}

case class AXI4LiteInterface(config: AXI4LiteConfig, isMaster : Boolean = true) extends Bundle{
  if(isMaster){
    val M2S = AXI4LiteMaster2Slave(config)
    val S2M = Flipped(AXI4LiteSlave2Master(config))
  } else{
    val M2S = Flipped(AXI4LiteMaster2Slave(config))
    val S2M = AXI4LiteSlave2Master(config)
  }
}