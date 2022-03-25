package Jeriscv

import chisel3._
import chisel3.experimental._
import chisel3.util._

object RV32I extends ChiselEnum{
  val L           = Value("b0000011".U)
  val F           = Value("b0001111".U)
  val I           = Value("b0010011".U)
  val AUIPC       = Value("b0010111".U)
  val S           = Value("b0100011".U)
  val R           = Value("b0110011".U)
  val LUI         = Value("b0110111".U)
  val B           = Value("b1100011".U)
  val JALR        = Value("b1100111".U)
  val JAL         = Value("b1101111".U)
  val SYS         = Value("b1110011".U)
}

