package Jeriscv

/* Single Cycle Non-Pipeline Version */

import chisel3._
import chisel3.util._
import Jeriscv.Pipeline._

class core(Config : JeriscvConfig) extends Module{

  val io_o = IO(Output(new Bundle{
    val m2w = new Memory2WritebackInterface(Config)
    val m2f = new Memory2FetchInterface(Config)
    val w2d = new WriteBack2DecodeInterface(Config)
  }))

  val IFU = Module(new InstructionFetchUnit(Config))
  val IDU = Module(new InstructionDecodeUnit(Config))
  val EX = Module(new ExecuteUnit(Config))
  val MEM = Module(new MemoryUnit(Config))
  val WB = Module(new WriteBackUnit(Config))

  val vmem = IO(new Bundle{
    val InstData = Input(UInt(32.W))
    val InstAddr = Output(UInt(Config.InstMemAddrWidth.W))
  })

  val BypassIO = IO(Output(new Bypass2ExecuteInterface(Config)))
  val HazardFlag = IO(Output(Bool()))

  if(Config.VirtualInstMem) {
    IFU.vmem.InstData := vmem.InstData
    vmem.InstAddr := IFU.vmem.InstAddr
  } else{
    IFU.vmem.InstData := DontCare
    vmem.InstAddr := DontCare
  }

  if(Config.SimplePipeline) {

    val Bypass = Module(new BypassingUnit(Config))
    val Hazard = Module(new HazardDetectionUnit(Config))

    IFU.In2F.BranchAddr := MEM.M2F.BranchAddr
    IFU.In2F.BranchFlag := MEM.M2F.BranchFlag

    IDU.F2D := RegEnable(IFU.F2D, !Hazard.HazardFlag)
    IDU.Flush := Hazard.HazardFlag | MEM.M2F.BranchFlag
    EX.Flush := MEM.M2F.BranchFlag
    IFU.Flush := MEM.M2F.BranchFlag


    EX.D2E := RegNext(IDU.D2E)
    MEM.E2M := RegNext(EX.E2M)
    WB.M2W := RegNext(MEM.M2W)
    IDU.W2D := WB.W2D

    Bypass.E2B := EX.E2B
    EX.B2E := Bypass.B2E

    Bypass.E2M := (MEM.E2M)
    Bypass.W2D := (IDU.W2D)

    Hazard.F2D := IDU.F2D
    Hazard.D2E := EX.D2E

    val JFlag = IDU.D2E.JFlag | EX.E2M.JFlag | MEM.E2M.JFlag
    IFU.In2F.PCEnable := !(Hazard.HazardFlag | JFlag)

    BypassIO := Bypass.B2E
    HazardFlag := Hazard.HazardFlag
  }

  io_o.m2w := MEM.M2W
  io_o.m2f := MEM.M2F
  io_o.w2d := WB.W2D

  val InstData = IO(Output(UInt(32.W)))
  val InstAddr = IO(Output(UInt(Config.InstMemAddrWidth.W)))

  if(Config.DebugOutput){
    InstData := IDU.F2D.InstData
    InstAddr := IDU.F2D.InstAddr
  }else{
    InstData := DontCare
    InstAddr := DontCare
  }
}
