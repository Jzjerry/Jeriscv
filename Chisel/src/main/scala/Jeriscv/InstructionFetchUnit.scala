package Jeriscv

import Jeriscv.ISA._
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

  val Flush = IO(Input(Bool()))
  val In2F = IO(Input(new EndToFetchInterface(Config)))
  val F2D = IO(Output(new Fetch2DecodeInterface(Config)))
  val vmem = IO(new Bundle{
    val InstData = Input(UInt(32.W))
    val InstAddr = Output(UInt(Config.InstMemAddrWidth.W))
  })

  val ProgramCounter = RegInit(UInt(Config.InstMemAddrWidth.W), 0.U)

  when(In2F.PCEnable){
      ProgramCounter := ProgramCounter + 4.U
  }
  when(In2F.BranchFlag){
    ProgramCounter := In2F.BranchAddr
  }

  if(Config.VirtualInstMem){
    vmem.InstAddr := ProgramCounter
    F2D.InstData := Mux(In2F.PCEnable & !Flush, vmem.InstData, RV32I_ALU.NOP)
  }
  else{
    vmem.InstAddr := DontCare
    if(Config.InstMemBlackBox) {
      val InstMemBB = Module(new InstructionMemBlackBox(Config.InstMemAddrWidth))
      InstMemBB.io.address := ProgramCounter(Config.InstMemAddrWidth - 1, 2)
      InstMemBB.io.clock := (~clock.asUInt).asBool.asClock
      InstMemBB.io.wren := false.B
      InstMemBB.io.data := 0.U
      F2D.InstData := Mux(In2F.PCEnable & !Flush, InstMemBB.io.q, RV32I_ALU.NOP) // Bubble
//      F2D.InstData := InstMemBB.io.q
    }
    else{
      val InstMem = Module(new InstructionMem(Config.InstMemSrc, Config.InstNum))
      InstMem.io.InstAddr := ProgramCounter
      F2D.InstData := Mux(In2F.PCEnable & !Flush, InstMem.io.InstData, RV32I_ALU.NOP) // Bubble
//      F2D.InstData := InstMem.io.InstData
    }
  }
  F2D.InstAddr := ProgramCounter
}
