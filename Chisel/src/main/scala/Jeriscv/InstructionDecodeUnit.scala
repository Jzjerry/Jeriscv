package Jeriscv

import chisel3._
import chisel3.util._


class InstructionDecodeUnit(Config : JeriscvConfig) extends Module {
  val F2D = IO(Input(new Fetch2DecodeInterface))

  val inst = F2D.InstData

  // Format Decode
  val funct7 = Wire(F2D.InstData(31, 25))
  val rs2 = Wire(inst(24, 20))
  val rs1 = Wire(inst(19, 15))
  val funct3 = Wire(inst(14, 12))
  val rd = Wire(inst(11, 7))
  val opcode = Wire(inst(6, 0))


  // Immediate Decode
  val I_imm = Wire(Cat(funct7, rs2))
  val S_imm = Wire(Cat(funct7, rd))
  val B_imm = Wire(Cat(inst(31), inst(7), inst(30, 25), inst(11, 8)))
  val U_imm = Wire(inst(31, 12))
  val J_imm = Wire(Cat(inst(31), inst(19, 12), inst(20), inst(30, 21)))

}
