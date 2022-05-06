package Jeriscv

import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._
import Jeriscv.ISA._

class MDUInterface (width : Int)extends Bundle{
  val op1 = Input(UInt(width.W))
  val op2 = Input(UInt(width.W))
  val funct3 = Input(MULDIVFunct3())
  val result = Output(UInt(width.W))
}

class MDU(Config: JeriscvConfig) extends Module {

  val io = IO(new MDUInterface(Config.RegFileWidth))
  io.result := 0.U
  val mul_rslt = if(Config.HasMul) io.op1 * io.op2 else null
  val div_rslt = if(Config.HasDiv) io.op1 / io.op2 else null
  val rem_rslt = if(Config.HasRem) io.op1 % io.op2 else null
  if(Config.HasMul) {
      switch(io.funct3) {
        is(MULDIVFunct3.mul) {
          io.result := mul_rslt
        }
        is(MULDIVFunct3.mulh) {
          io.result := mul_rslt(63, 32)
        }
        is(MULDIVFunct3.mulhu) {
          io.result := mul_rslt(63, 32)
        }
        is(MULDIVFunct3.mulhsu) {
          io.result := mul_rslt(63, 32)
        }
      }
  }
  if(Config.HasDiv) {
      switch(io.funct3) {
        is(MULDIVFunct3.div) {
          io.result := div_rslt
        }
        is(MULDIVFunct3.divu) {
          io.result := div_rslt
        }
      }
  }
  if(Config.HasRem){
      switch(io.funct3) {
        is(MULDIVFunct3.rem) {
          io.result := rem_rslt
        }
        is(MULDIVFunct3.remu) {
          io.result := rem_rslt
        }
      }
  }
}
