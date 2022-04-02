package Jeriscv

import chisel3._
import chisel3.util._
import chisel3.experimental._

class Fetch2DecodeInterface(Config : JeriscvConfig) extends Bundle{
  val InstData = UInt(32.W)
  val InstAddr = UInt(Config.InstMemAddrWidth.W)
}

class EndToFetchInterface(Config : JeriscvConfig) extends Bundle{
  val PCEnable = Bool()
  val BranchFlag = Bool()
  val BranchAddr = UInt(Config.InstMemAddrWidth.W)
}

class InstructionFetchUnit(Config : JeriscvConfig) extends Module {

  val In2F = IO(Input(new EndToFetchInterface(Config)))
  val F2D = IO(Output(new Fetch2DecodeInterface(Config)))

  val ProgramCounter = RegInit(UInt(Config.InstMemAddrWidth.W), 0.U)

  when(In2F.PCEnable){
    when(In2F.BranchFlag){
      ProgramCounter := In2F.BranchAddr
    }.otherwise{
      ProgramCounter := ProgramCounter + 4.U
    }
  }

  if(Config.InstMemBlackBox) {
    val InstMemBB = Module(new InstructionMemBlackBox(Config.InstMemAddrWidth))
    InstMemBB.io.address := ProgramCounter(Config.InstMemAddrWidth - 1, 2)
    InstMemBB.io.clock := clock
    InstMemBB.io.wren := false.B
    InstMemBB.io.data := 0.U
    F2D.InstData := InstMemBB.io.q
    }
  else{
    val InstMem = Module(new InstructionMem(Config.InstMemSrc, Config.InstNum))
    InstMem.io.InstAddr := ProgramCounter
    F2D.InstData := InstMem.io.InstData
  }
  F2D.InstAddr := ProgramCounter
}
