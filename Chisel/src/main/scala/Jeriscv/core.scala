package Jeriscv

/* Single Cycle Non-Pipeline Version */

import chisel3._
import chisel3.util._
import Jeriscv.Pipeline._

class core(Config : JeriscvConfig) extends Module{

//  val io_i = IO(Input(new EndToFetchInterface(Config)))
  val io_o = IO(Output(new Bundle{
    val m2w = new Memory2WritebackInterface(Config)
    val m2f = new Memory2FetchInterface(Config)
  }))

  val IFU = Module(new InstructionFetchUnit(Config))
  val IDU = Module(new InstructionDecodeUnit(Config))
  val EX = Module(new ExecuteUnit(Config))
  val MEM = Module(new MemoryUnit(Config))

  val vmem = IO(new Bundle{
    val InstData = Input(UInt(32.W))
    val InstAddr = Output(UInt(Config.InstMemAddrWidth.W))
  })

  val BypassIO = IO(Output(new Bypass2ExecuteInterface(Config)))

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

    IDU.F2D := RegNext(IFU.F2D)

    EX.D2E := RegNext(IDU.D2E)
    MEM.E2M := RegNext(EX.E2M)
    IDU.W2D := (MEM.M2W)

    Bypass.E2B := EX.E2B
    EX.B2E := Bypass.B2E

    Bypass.E2M := (MEM.E2M)
    Bypass.M2W := (IDU.W2D)

    Hazard.F2D := IDU.F2D
    Hazard.D2E := EX.D2E
    Hazard.JFlag := IDU.D2E.JFlag | EX.E2M.JFlag | MEM.E2M.JFlag
    IFU.In2F.PCEnable := ~Hazard.HazardFlag

    BypassIO := Bypass.B2E
  }
  else{
    IFU.In2F.PCEnable := true.B
    IFU.In2F.BranchAddr := (MEM.M2F.BranchAddr)
    IFU.In2F.BranchFlag := (MEM.M2F.BranchFlag)

    IDU.F2D := (IFU.F2D)

    IDU.W2D := (MEM.M2W)
    EX.D2E := (IDU.D2E)
    MEM.E2M := (EX.E2M)

    BypassIO := DontCare
  }
  io_o.m2w := MEM.M2W
  io_o.m2f := MEM.M2F

  val InstData = IO(Output(UInt(32.W)))
  val InstAddr = IO(Output(UInt(Config.InstMemAddrWidth.W)))

  if(Config.DebugOutput){
    InstData := IFU.F2D.InstData
    InstAddr := IFU.F2D.InstAddr
  }else{
    InstData := DontCare
    InstAddr := DontCare
  }
}
