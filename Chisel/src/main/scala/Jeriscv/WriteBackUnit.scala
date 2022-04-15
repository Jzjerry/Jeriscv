package Jeriscv

import chisel3._
import chisel3.util._

class WriteBack2DecodeInterface(Config : JeriscvConfig) extends Bundle{
  val WriteBackData = UInt(Config.RegFileWidth.W)
  val WriteBackEn = Bool()
  val WriteBackDest = UInt(5.W)
}

class WriteBackUnit(Config : JeriscvConfig) extends Module {
  val M2W = IO(Input(new Memory2WritebackInterface(Config)))
  val W2D = IO(Output(new WriteBack2DecodeInterface(Config)))


  W2D.WriteBackData :=
    Mux(M2W.WriteBackSrc === WriteBackType.Mem, M2W.MemoryReadData,
      Mux(M2W.WriteBackSrc === WriteBackType.ALU, M2W.ALUResult,
        Mux(M2W.WriteBackSrc === WriteBackType.NextAddr, M2W.NextAddr, 0.U)))
  W2D.WriteBackEn := M2W.WriteBackEn
  W2D.WriteBackDest := M2W.WriteBackDest
}
