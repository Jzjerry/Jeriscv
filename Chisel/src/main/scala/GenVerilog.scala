import chisel3._
import chisel3.util._
import Jeriscv._
import chisel3.stage.ChiselStage

object GenInstMem{
  def main(args: Array[String]): Unit = {
    (new ChiselStage).emitVerilog(new InstructionMem("InstMem.hex", 256))
  }
}

object GenDataMem{
  def main(args: Array[String]): Unit = {
    (new ChiselStage).emitVerilog(new DataMem(256, true))
  }
}

object GenALU{
  def main(args: Array[String]): Unit = {
    (new ChiselStage).emitVerilog(new ALU(32, false))
  }
}

object GenRegFile{
  def main(args: Array[String]): Unit = {
    (new ChiselStage).emitVerilog(new RegFile(32))
  }
}

object GenBRU{
  def main(args: Array[String]): Unit = {
    (new ChiselStage).emitVerilog(new BRU(32, 32, false))
  }
}

object GenLSU{
  def main(args: Array[String]): Unit = {
    (new ChiselStage).emitVerilog(new LSU(32))
  }
}

object GenIDU{
  def main(args: Array[String]): Unit = {
    (new ChiselStage).emitVerilog(new InstructionDecodeUnit( new JeriscvConfig ))
  }
}

object GenIFU{
  def main(args: Array[String]): Unit = {
    (new ChiselStage).emitVerilog(new InstructionFetchUnit( new JeriscvConfig ))
  }
}

object GenCoreSynthesis{
  def main(args: Array[String]): Unit = {
    val Config = new JeriscvConfig
    Config.DebugOutput = false
    Config.VirtualInstMem = false
    Config.InstMemBlackBox = true
    Config.DataMemBlackBox = true
    Config.SyncDataMem = false
    (new ChiselStage).emitVerilog(new core(Config))
  }
}
object GenDebugCore{
  def main(args: Array[String]): Unit = {
    val Config = new JeriscvConfig
    Config.DebugOutput = true
    Config.VirtualInstMem = true
    Config.InstMemBlackBox = false
    Config.DataMemBlackBox = false
    Config.SyncDataMem = false
    (new ChiselStage).emitVerilog(new core(Config))
  }
}