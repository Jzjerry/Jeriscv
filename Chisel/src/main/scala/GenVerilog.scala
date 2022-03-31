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
    (new ChiselStage).emitVerilog(new DataMem(256))
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

object Gencore{
  def main(args: Array[String]): Unit = {
    (new ChiselStage).emitVerilog(new core( new JeriscvConfig ))
  }
}