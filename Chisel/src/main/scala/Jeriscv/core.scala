package Jeriscv

/* Single Cycle Non-Pipeline Version */

import chisel3._
import chisel3.util._

class core(Config : JeriscvConfig) extends Module{

  val IFU = Module(new InstructionFetchUnit(Config))

}
