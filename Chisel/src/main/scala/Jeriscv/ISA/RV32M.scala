package Jeriscv.ISA

import Jeriscv._
import chisel3._
import chisel3.experimental._
import chisel3.util._

object MULDIVFunct3 extends ChiselEnum{
  val mul, mulh, mulhsu, mulhu, div, divu, rem, remu, default = Value
}

object RV32M {
  // R-Type
  def MUL    = BitPat("b0000001?????_?????_000_?????_0110011")
  def MULH   = BitPat("b0000001?????_?????_001_?????_0110011")
  def MULHSU = BitPat("b0000001?????_?????_010_?????_0110011")
  def MULHU  = BitPat("b0000001?????_?????_011_?????_0110011")
  def DIV    = BitPat("b0000001?????_?????_100_?????_0110011")
  def DIVU   = BitPat("b0000001?????_?????_101_?????_0110011")
  def REM    = BitPat("b0000001?????_?????_110_?????_0110011")
  def REMU   = BitPat("b0000001?????_?????_111_?????_0110011")

  val table = Array(
    MUL     ->    List(InstType.R_Type, ExecuteType.MDUType, MULDIVFunct3.mul),
    MULH    ->    List(InstType.R_Type, ExecuteType.MDUType, MULDIVFunct3.mulh),
    MULHSU  ->    List(InstType.R_Type, ExecuteType.MDUType, MULDIVFunct3.mulhsu),
    MULHU   ->    List(InstType.R_Type, ExecuteType.MDUType, MULDIVFunct3.mulhu),
    DIV     ->    List(InstType.R_Type, ExecuteType.MDUType, MULDIVFunct3.div),
    DIVU    ->    List(InstType.R_Type, ExecuteType.MDUType, MULDIVFunct3.divu),
    REM     ->    List(InstType.R_Type, ExecuteType.MDUType, MULDIVFunct3.rem),
    REMU    ->    List(InstType.R_Type, ExecuteType.MDUType, MULDIVFunct3.remu),
  )

}
