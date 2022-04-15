package Jeriscv.Pipeline

import Jeriscv._
import chisel3._
import chisel3.util._
import chisel3.experimental.ChiselEnum

class HazardDetectionUnit(Config: JeriscvConfig) extends Module{

  val D2E = IO(Input(new Decode2ExecuteInterface(Config)))
  val F2D = IO(Input(new Fetch2DecodeInterface(Config)))
  val JFlag = IO(Input(Bool()))
  val HazardFlag = IO(Output(Bool()))

  HazardFlag := false.B

  val rs1 = F2D.InstData(19, 15)
  val rs2 = F2D.InstData(24, 20)

  when(D2E.MemoryReadEnable === true.B &&
    (D2E.WriteBackDest  === rs1 || D2E.WriteBackDest === rs2)){
    HazardFlag := true.B
  }.elsewhen(JFlag){
    HazardFlag := true.B
  }
}
