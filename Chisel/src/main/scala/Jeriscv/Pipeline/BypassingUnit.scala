package Jeriscv.Pipeline

import Jeriscv._
import chisel3._
import chisel3.util._
import chisel3.experimental.ChiselEnum


class Bypass2ExecuteInterface(Config : JeriscvConfig) extends Bundle{
  val BypassOp1Flag = Bool()
  val BypassOp2Flag = Bool()
  val BypassOp1Data = UInt(Config.RegFileWidth.W)
  val BypassOp2Data = UInt(Config.RegFileWidth.W)
}

class BypassingUnit(Config : JeriscvConfig) extends Module {

  val E2M = IO(Input(new Execute2MemInterface(Config)))
  val E2B = IO(Input(new Execute2BypassInterface(Config)))
  val W2D = IO(Input(new WriteBack2DecodeInterface(Config)))
  val B2E = IO(Output(new Bypass2ExecuteInterface(Config)))

  B2E.BypassOp1Flag := false.B
  B2E.BypassOp2Flag := false.B
  B2E.BypassOp1Data := 0.U
  B2E.BypassOp2Data := 0.U

  when(E2M.WriteBackEn &&
    (E2M.WriteBackDest =/= 0.U) &&
    (E2M.WriteBackDest === E2B.rs1)){
    B2E.BypassOp1Flag := true.B
    B2E.BypassOp1Data := E2M.ALUResult
  } .elsewhen(W2D.WriteBackEn &&
    (W2D.WriteBackDest =/= 0.U) &&
    (W2D.WriteBackDest === E2B.rs1)){
    B2E.BypassOp1Flag := true.B
    B2E.BypassOp1Data :=  W2D.WriteBackData
  }

  when(E2M.WriteBackEn &&
    (E2M.WriteBackDest =/= 0.U) &&
    (E2M.WriteBackDest === E2B.rs2)){
    B2E.BypassOp2Flag := true.B
    B2E.BypassOp2Data := E2M.ALUResult
  } .elsewhen(W2D.WriteBackEn &&
    (W2D.WriteBackDest =/= 0.U) &&
    (W2D.WriteBackDest === E2B.rs2)){
    B2E.BypassOp2Flag := true.B
    B2E.BypassOp2Data := W2D.WriteBackData
  }

}
