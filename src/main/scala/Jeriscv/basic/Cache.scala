package Jeriscv.basic

import Jeriscv._
import spinal.core._
import spinal.lib._
import spinal.lib.bus.amba4.axi._

import scala.tools.jline_embedded.console.WCWidth

case class CacheConfig
(
  AddrStart       : Int,
  AddrEnd         : Int,
  AddrWidth       : Int,
  SetNum          : Int,
  SetSize         : Int,
  LineSizeBytes   : Int
){
  def offsetWidth = log2Up(LineSizeBytes)
  def indexWidth = log2Up(SetNum)
  def tagWidth = AddrWidth - indexWidth - offsetWidth
}

case class Axi4(config: Axi4Config) extends Bundle with IMasterSlave{
  val aw = Stream(Axi4Aw(config))
  val w  = Stream(Axi4W(config))
  val b  = Stream(Axi4B(config))
  val ar = Stream(Axi4Ar(config))
  val r  = Stream(Axi4R(config))

  override def asMaster(): Unit = {
    master(ar,aw,w)
    slave(r,b)
  }
}

class Set(config : CacheConfig) extends Area{



}

class Line(config : CacheConfig) extends Area{
  val data = Vec(RegInit(U(0, 8 bits)), config.LineSizeBytes)
  val tag = Reg(new Bundle{
    val valid = Bool()
    val tag = UInt(config.tagWidth bits)
  })

  def hit(tag_in : UInt) : Bool = {
    (tag_in === tag.tag) && tag.valid
  }

}

class Cache(config : CacheConfig) extends Component {

  val port = new Bundle {
    val offset = in UInt (config.offsetWidth bits)
    val index = in UInt (config.indexWidth bits)
    val tag = in UInt (config.tagWidth bits)

    val hit = out Bool()
  }

}

object GenCache{
  def main(args: Array[String]) {
    SpinalConfig(targetDirectory = "./npc/rtl/")
      .generateVerilog(new Cache(
        CacheConfig(
          0,0,32,8,8,64
        )
      )).printPruned()
  }
}