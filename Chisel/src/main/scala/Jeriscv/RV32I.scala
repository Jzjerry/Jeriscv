package Jeriscv

import chisel3._
import chisel3.experimental._
import chisel3.util._

object InstType extends ChiselEnum{
  val R_Type, I_Type, S_Type, B_Type, U_Type, J_Type, default = Value
}
object ExecuteType extends ChiselEnum{
  val ALUType, BRUType, LSUType, default = Value
}

object RV32I_ALU {

    // R-Type
    def ADD   = BitPat("b0000000?????_?????_000_?????_0110011")
    def SUB   = BitPat("b0100000?????_?????_000_?????_0110011")
    def SLL   = BitPat("b0000000?????_?????_001_?????_0110011")
    def SLT   = BitPat("b0000000?????_?????_010_?????_0110011")
    def SLTU  = BitPat("b0000000?????_?????_011_?????_0110011")
    def XOR   = BitPat("b0000000?????_?????_100_?????_0110011")
    def SRL   = BitPat("b0000000?????_?????_101_?????_0110011")
    def SRA   = BitPat("b01000000?????_?????_101_?????_0110011")
    def OR    = BitPat("b0000000?????_?????_110_?????_0110011")
    def AND   = BitPat("b0000000?????_?????_111_?????_0110011")

    // I-Type
    def ADDI  = BitPat("b????????????_?????_000_?????_0010011")
    def SLLI  = BitPat("b0000000?????_?????_001_?????_0010011")
    def SLTI  = BitPat("b????????????_?????_010_?????_0010011")
    def SLTIU = BitPat("b????????????_?????_011_?????_0010011")
    def XORI  = BitPat("b????????????_?????_100_?????_0010011")
    def SRLI  = BitPat("b0000000?????_?????_101_?????_0010011")
    def ORI   = BitPat("b????????????_?????_110_?????_0010011")
    def ANDI  = BitPat("b????????????_?????_111_?????_0010011")
    def SRAI  = BitPat("b0100000?????_?????_101_?????_0010011")
    def NOP =          "b000000000000_00000_000_00000_0010011".U

    // U-Type
    def AUIPC = BitPat("b????????????????????_?????_0010111")
    def LUI = BitPat("b????????????????????_?????_0110111")


    val table = Array(
      ADD ->    List(InstType.R_Type, ExecuteType.ALUType, ALUFunct3.add),
      SUB ->    List(InstType.R_Type, ExecuteType.ALUType, ALUFunct3.sub),
      SLL ->    List(InstType.R_Type, ExecuteType.ALUType, ALUFunct3.sll),
      SLT ->    List(InstType.R_Type, ExecuteType.ALUType, ALUFunct3.slt),
      SLTU ->   List(InstType.R_Type, ExecuteType.ALUType, ALUFunct3.sltu),
      XOR ->    List(InstType.R_Type, ExecuteType.ALUType, ALUFunct3.xor),
      SRL ->    List(InstType.R_Type, ExecuteType.ALUType, ALUFunct3.srl),
      SRA ->    List(InstType.R_Type, ExecuteType.ALUType, ALUFunct3.sra),
      OR ->     List(InstType.R_Type, ExecuteType.ALUType, ALUFunct3.or),
      AND ->    List(InstType.R_Type, ExecuteType.ALUType, ALUFunct3.and),

      ADDI ->   List(InstType.I_Type, ExecuteType.ALUType, ALUFunct3.add),
      SLLI ->   List(InstType.I_Type, ExecuteType.ALUType, ALUFunct3.sll),
      SLTI ->   List(InstType.I_Type, ExecuteType.ALUType, ALUFunct3.slt),
      SLTIU ->  List(InstType.I_Type, ExecuteType.ALUType, ALUFunct3.sltu),
      XORI ->   List(InstType.I_Type, ExecuteType.ALUType, ALUFunct3.xor),
      SRLI ->   List(InstType.I_Type, ExecuteType.ALUType, ALUFunct3.srl),
      ORI ->    List(InstType.I_Type, ExecuteType.ALUType, ALUFunct3.or),
      ANDI ->   List(InstType.I_Type, ExecuteType.ALUType, ALUFunct3.and),
      SRAI ->   List(InstType.I_Type, ExecuteType.ALUType, ALUFunct3.sra),

      AUIPC ->  List(InstType.U_Type, ExecuteType.ALUType, ALUFunct3.add),
      LUI ->    List(InstType.U_Type, ExecuteType.ALUType, ALUFunct3.add)
    )
}
object RV32I_BRU {
  // I-Type
  def JALR    = BitPat("b????????????_?????_000_?????_1100111")

  // J-Type
  def JAL     = BitPat("b????????????????????_?????_1101111")

  // B-Type
  def BNE     = BitPat("b???????_?????_?????_001_?????_1100011")
  def BEQ     = BitPat("b???????_?????_?????_000_?????_1100011")
  def BLT     = BitPat("b???????_?????_?????_100_?????_1100011")
  def BGE     = BitPat("b???????_?????_?????_101_?????_1100011")
  def BLTU    = BitPat("b???????_?????_?????_110_?????_1100011")
  def BGEU    = BitPat("b???????_?????_?????_111_?????_1100011")

  val table = Array(
    JAL  -> List(InstType.J_Type, ExecuteType.BRUType, BRUFunct3.jal),
    JALR -> List(InstType.I_Type, ExecuteType.BRUType, BRUFunct3.jalr),
    BNE  -> List(InstType.B_Type, ExecuteType.BRUType, BRUFunct3.bne),
    BEQ  -> List(InstType.B_Type, ExecuteType.BRUType, BRUFunct3.beq),
    BLT  -> List(InstType.B_Type, ExecuteType.BRUType, BRUFunct3.blt),
    BGE  -> List(InstType.B_Type, ExecuteType.BRUType, BRUFunct3.bge),
    BLTU -> List(InstType.B_Type, ExecuteType.BRUType, BRUFunct3.bltu),
    BGEU -> List(InstType.B_Type, ExecuteType.BRUType, BRUFunct3.bgeu)
  )
}

object RV32I_LSU{

  def LB      = BitPat("b????????????_?????_000_?????_0000011")
  def LH      = BitPat("b????????????_?????_001_?????_0000011")
  def LW      = BitPat("b????????????_?????_010_?????_0000011")
  def LBU     = BitPat("b????????????_?????_100_?????_0000011")
  def LHU     = BitPat("b????????????_?????_101_?????_0000011")
  def SB      = BitPat("b???????_?????_?????_000_?????_0100011")
  def SH      = BitPat("b???????_?????_?????_001_?????_0100011")
  def SW      = BitPat("b???????_?????_?????_010_?????_0100011")

  val table = Array(
    LB  -> List(InstType.I_Type, ExecuteType.LSUType, LSUFunct3.lb),
    LH  -> List(InstType.I_Type, ExecuteType.LSUType, LSUFunct3.lh),
    LW  -> List(InstType.I_Type, ExecuteType.LSUType, LSUFunct3.lw),
    LBU -> List(InstType.I_Type, ExecuteType.LSUType, LSUFunct3.lbu),
    LHU -> List(InstType.I_Type, ExecuteType.LSUType, LSUFunct3.lhu),
    SB  -> List(InstType.S_Type, ExecuteType.LSUType, LSUFunct3.sb),
    SH  -> List(InstType.S_Type, ExecuteType.LSUType, LSUFunct3.sh),
    SW  -> List(InstType.S_Type, ExecuteType.LSUType, LSUFunct3.sw)
  )

}
