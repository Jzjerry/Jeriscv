package Jeriscv

import chisel3._
import chisel3.util._

class Fetch2DecodeInterface extends Bundle{
  val InstData = UInt(32.W)
}

class EndToFetchInterface(Config : JeriscvConfig) extends Bundle{
  val PCEnable = Bool()
  val BranchFlag = Bool()
  val BranchAddr = UInt(Config.InstMemAddrWidth.W)
}

class InstructionFetchUnit(Config : JeriscvConfig) extends Module {

  val In2F = IO(Input(new EndToFetchInterface(Config)))
  val F2D = IO(Output(new Fetch2DecodeInterface))

  val InstMem = Module(new InstructionMem(Config.InstMemSrc, Config.InstNum))

  val ProgramCounter = RegInit(UInt(Config.InstMemAddrWidth.W), 0.U)

  when(In2F.PCEnable){
    when(In2F.BranchFlag){
      ProgramCounter := In2F.BranchAddr
    }.otherwise{
      ProgramCounter := ProgramCounter + 4.U
    }
  }
  InstMem.io.InstAddr := ProgramCounter
  F2D.InstData := InstMem.io.InstData

}
