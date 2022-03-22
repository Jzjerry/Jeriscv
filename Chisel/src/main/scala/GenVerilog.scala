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
    (new ChiselStage).emitVerilog(new ALU(32, true))
  }
}