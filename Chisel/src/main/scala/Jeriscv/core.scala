package Jeriscv

/* Single Cycle Non-Pipeline Version */

import chisel3._
import chisel3.util._

class core(Config : JeriscvConfig) extends Module{


//  val io_i = IO(Input(new EndToFetchInterface(Config)))
  val io_o = IO(Output(new Bundle{
    val m2w = new Memory2WritebackInterface(Config)
    val m2f = new Memory2FetchInterface(Config)
  }
  ))

  val IFU = Module(new InstructionFetchUnit(Config))
  val IDU = Module(new InstructionDecodeUnit(Config))
  val EX = Module(new ExecuteUnit(Config))
  val MEM = Module(new MemoryUnit(Config))

  IFU.In2F.PCEnable := true.B
  IFU.In2F.BranchAddr := MEM.M2F.BranchAddr
  IFU.In2F.BranchFlag := MEM.M2F.BranchFlag
  val virtualFetch = IO(Input(new Fetch2DecodeInterface(Config)))
  if(Config.DebugInstMem){
    IDU.F2D := virtualFetch
  }else{
    IDU.F2D := IFU.F2D
  }
  IDU.W2D := MEM.M2W
  EX.D2E := IDU.D2E
  MEM.E2M := EX.E2M

  io_o.m2w := MEM.M2W
  io_o.m2f := MEM.M2F
}
