package Jeriscv

/* Single Cycle Non-Pipeline Version */

import chisel3._
import chisel3.util._

class core(Config : JeriscvConfig) extends Module{


  val io_i = IO(Input(new EndToFetchInterface(Config)))
  val io_o = IO(Output(new Memory2WritebackInterface(Config)))

  val IFU = Module(new InstructionFetchUnit(Config))
  val IDU = Module(new InstructionDecodeUnit(Config))
  val EX = Module(new ExecuteUnit(Config))
  val MEM = Module(new MemoryUnit(Config))


  IFU.In2F := io_i
  val virtualFetch = IO(Input(new Fetch2DecodeInterface(Config)))
  if(Config.DebugInstMem){
    IDU.F2D := virtualFetch
  }else{
    IDU.F2D := IFU.F2D
  }
  IDU.W2D := MEM.M2W
  EX.D2E := IDU.D2E
  MEM.E2M := EX.E2M
  io_o := MEM.M2W
}
