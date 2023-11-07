package Jeriscv.isa

import Jeriscv._
import spinal.core._


object Src1Enum extends SpinalEnum(binarySequential){
  val rs1, imm, zero, pc = newElement()
}

object Src2Enum extends SpinalEnum(binarySequential){
  val rs2, imm = newElement()
}

object ALUOpEnum extends SpinalEnum(binarySequential){
  val add, sub, sll, slt, sltu, xor, srl, sra, or, and, none = newElement() // XLEN bits-level
  val mlt, div, rem = newElement()
}

object InstructionType extends SpinalEnum(binarySequential){
  val IType, IType_Load, RType, UType_LUI, UType_AUIPC, BType, SType, JType, IType_JALR, ZType = newElement()
}

object ExecuteType extends SpinalEnum(binarySequential){
  val alu, bru, lsu, mlt, div = newElement()
}

object StoreLength extends SpinalEnum(binarySequential){
  val byte, half, word, double = newElement()
}

object RiscvFmt {

  def SignedExtend(data : Bits, Len : Int)= {
    U(S(data).resize(Len))
  }
  def SignedExtend(data : UInt, Len : Int)= {
    U(S(data).resize(Len))
  }
  def Word2Double(data : Bits): UInt ={
    SignedExtend(data(31 downto 0), 64 )
  }
  def Word2Double(data : UInt): UInt ={
    SignedExtend(data(31 downto 0), 64 )
  }

  // from VexRiscv
  def funct7Range = 31 downto 25
  def rdRange = 11 downto 7
  def funct3Range = 14 downto 12
  def rs1Range = 19 downto 15
  def rs2Range = 24 downto 20
  def rs3Range = 31 downto 27
  def csrRange = 31 downto 20

  def shamt64Range = 5 downto 0
  def shamt32Range = 4 downto 0

  // Data Size range
  def ByteRange = 7 downto 0
  def HalfRange = 15 downto 0
  def WordRange = 31 downto 0
  def DoubleRange = 63 downto 0

  case class IMM(instruction  : Bits) extends Area {
    def i = instruction(31 downto 20)
    def h = instruction(31 downto 24)
    def s = instruction(31 downto 25) ## instruction(11 downto 7)
    def b = instruction(31) ## instruction(7) ## instruction(30 downto 25) ## instruction(11 downto 8) ## U"b0"
    def u = instruction(31 downto 12) ## U"x000"
    def j = instruction(31) ## instruction(19 downto 12) ## instruction(20) ## instruction(30 downto 21) ## U"b0"
    def z = instruction(19 downto 15)
  }
}

case class ALUOpInfo
(
  InstrType : InstructionType.E,
  ExType : ExecuteType.E,
  ALUSel: ALUOpEnum.E
)

object RV32I{

  def ADD                = M"0000000----------000-----0110011"
  def SUB                = M"0100000----------000-----0110011"
  def SLL                = M"0000000----------001-----0110011"
  def SLT                = M"0000000----------010-----0110011"
  def SLTU               = M"0000000----------011-----0110011"
  def XOR                = M"0000000----------100-----0110011"
  def SRL                = M"0000000----------101-----0110011"
  def SRA                = M"0100000----------101-----0110011"
  def OR                 = M"0000000----------110-----0110011"
  def AND                = M"0000000----------111-----0110011"

  def ADDI               = M"-----------------000-----0010011"
  def SLLI               = M"000000-----------001-----0010011"
  def SLTI               = M"-----------------010-----0010011"
  def SLTIU              = M"-----------------011-----0010011"
  def XORI               = M"-----------------100-----0010011"
  def SRLI               = M"000000-----------101-----0010011"
  def SRAI               = M"010000-----------101-----0010011"
  def ORI                = M"-----------------110-----0010011"
  def ANDI               = M"-----------------111-----0010011"

  def LB                 = M"-----------------000-----0000011"
  def LH                 = M"-----------------001-----0000011"
  def LW                 = M"-----------------010-----0000011"
  def LBU                = M"-----------------100-----0000011"
  def LHU                = M"-----------------101-----0000011"
  def SB                 = M"-----------------000-----0100011"
  def SH                 = M"-----------------001-----0100011"
  def SW                 = M"-----------------010-----0100011"

  def BNE                = M"-----------------001-----1100011"
  def BEQ                = M"-----------------000-----1100011"
  def BLT                = M"-----------------100-----1100011"
  def BGE                = M"-----------------101-----1100011"
  def BLTU               = M"-----------------110-----1100011"
  def BGEU               = M"-----------------111-----1100011"

  def JAL                = M"-------------------------1101111"
  def JALR               = M"-----------------000-----1100111"

  def AUIPC              = M"-------------------------0010111"
  def LUI                = M"-------------------------0110111"

  val TableALU = Array(
    ADDI  -> ALUOpInfo(InstructionType.IType, ExecuteType.alu, ALUOpEnum.add),
    SLLI  -> ALUOpInfo(InstructionType.IType, ExecuteType.alu, ALUOpEnum.sll),
    SLTI  -> ALUOpInfo(InstructionType.IType, ExecuteType.alu, ALUOpEnum.slt),
    SLTIU -> ALUOpInfo(InstructionType.IType, ExecuteType.alu, ALUOpEnum.sltu),
    XORI  -> ALUOpInfo(InstructionType.IType, ExecuteType.alu, ALUOpEnum.xor),
    SRLI  -> ALUOpInfo(InstructionType.IType, ExecuteType.alu, ALUOpEnum.srl),
    SRAI  -> ALUOpInfo(InstructionType.IType, ExecuteType.alu, ALUOpEnum.sra),
    ORI   -> ALUOpInfo(InstructionType.IType, ExecuteType.alu, ALUOpEnum.or),
    ANDI  -> ALUOpInfo(InstructionType.IType, ExecuteType.alu, ALUOpEnum.and),

    ADD   -> ALUOpInfo(InstructionType.RType, ExecuteType.alu, ALUOpEnum.add),
    SUB   -> ALUOpInfo(InstructionType.RType, ExecuteType.alu, ALUOpEnum.sub),
    SLL   -> ALUOpInfo(InstructionType.RType, ExecuteType.alu, ALUOpEnum.sll),
    SLT   -> ALUOpInfo(InstructionType.RType, ExecuteType.alu, ALUOpEnum.slt),
    SLTU  -> ALUOpInfo(InstructionType.RType, ExecuteType.alu, ALUOpEnum.sltu),
    XOR   -> ALUOpInfo(InstructionType.RType, ExecuteType.alu, ALUOpEnum.xor),
    SRL   -> ALUOpInfo(InstructionType.RType, ExecuteType.alu, ALUOpEnum.srl),
    SRA   -> ALUOpInfo(InstructionType.RType, ExecuteType.alu, ALUOpEnum.sra),
    OR    -> ALUOpInfo(InstructionType.RType, ExecuteType.alu, ALUOpEnum.or),
    AND   -> ALUOpInfo(InstructionType.RType, ExecuteType.alu, ALUOpEnum.and),

    AUIPC -> ALUOpInfo(InstructionType.UType_AUIPC, ExecuteType.alu, ALUOpEnum.add),
    LUI   -> ALUOpInfo(InstructionType.UType_LUI, ExecuteType.alu, ALUOpEnum.add)
  )

  val TableBRU = Array(
    JAL   -> ALUOpInfo(InstructionType.JType, ExecuteType.bru, ALUOpEnum.none),
    JALR  -> ALUOpInfo(InstructionType.IType_JALR, ExecuteType.bru, ALUOpEnum.add),
    BEQ   -> ALUOpInfo(InstructionType.BType, ExecuteType.bru, ALUOpEnum.sub),
    BNE   -> ALUOpInfo(InstructionType.BType, ExecuteType.bru, ALUOpEnum.sub),
    BGE   -> ALUOpInfo(InstructionType.BType, ExecuteType.bru, ALUOpEnum.slt),
    BLT   -> ALUOpInfo(InstructionType.BType, ExecuteType.bru, ALUOpEnum.slt),
    BGEU  -> ALUOpInfo(InstructionType.BType, ExecuteType.bru, ALUOpEnum.sltu),
    BLTU  -> ALUOpInfo(InstructionType.BType, ExecuteType.bru, ALUOpEnum.sltu)
  )

  val TableLS = Array(
    SB    -> ALUOpInfo(InstructionType.SType, ExecuteType.lsu, ALUOpEnum.add),
    SH    -> ALUOpInfo(InstructionType.SType, ExecuteType.lsu, ALUOpEnum.add),
    SW    -> ALUOpInfo(InstructionType.SType, ExecuteType.lsu, ALUOpEnum.add),

    LB    -> ALUOpInfo(InstructionType.IType_Load, ExecuteType.lsu, ALUOpEnum.add),
    LBU   -> ALUOpInfo(InstructionType.IType_Load, ExecuteType.lsu, ALUOpEnum.add),
    LH    -> ALUOpInfo(InstructionType.IType_Load, ExecuteType.lsu, ALUOpEnum.add),
    LHU   -> ALUOpInfo(InstructionType.IType_Load, ExecuteType.lsu, ALUOpEnum.add),
    LW    -> ALUOpInfo(InstructionType.IType_Load, ExecuteType.lsu, ALUOpEnum.add)
  )
}

object RV64I {

  def ADDW               = M"0000000----------000-----0111011"
  def SUBW               = M"0100000----------000-----0111011"
  def SLLW               = M"0000000----------001-----0111011"
  def SRLW               = M"0000000----------101-----0111011"
  def SRAW               = M"0100000----------101-----0111011"

  def ADDIW              = M"-----------------000-----0011011"
  def SLLIW              = M"0000000----------001-----0011011"
  def SRLIW              = M"0000000----------101-----0011011"
  def SRAIW              = M"0100000----------101-----0011011"

  def LWU                = M"-----------------110-----0000011"
  def LD                 = M"-----------------011-----0000011"
  def SD                 = M"-----------------011-----0100011"

  val TableALU = Array(
    ADDIW   -> ALUOpInfo(InstructionType.IType, ExecuteType.alu, ALUOpEnum.add),
    SLLIW   -> ALUOpInfo(InstructionType.IType, ExecuteType.alu, ALUOpEnum.sll),
    SRLIW   -> ALUOpInfo(InstructionType.IType, ExecuteType.alu, ALUOpEnum.srl),
    SRAIW   -> ALUOpInfo(InstructionType.IType, ExecuteType.alu, ALUOpEnum.sra),

    ADDW    -> ALUOpInfo(InstructionType.RType, ExecuteType.alu, ALUOpEnum.add),
    SUBW    -> ALUOpInfo(InstructionType.RType, ExecuteType.alu, ALUOpEnum.sub),
    SLLW    -> ALUOpInfo(InstructionType.RType, ExecuteType.alu, ALUOpEnum.sll),
    SRLW    -> ALUOpInfo(InstructionType.RType, ExecuteType.alu, ALUOpEnum.srl),
    SRAW    -> ALUOpInfo(InstructionType.RType, ExecuteType.alu, ALUOpEnum.sra)
  )

  val TableLS = Array(
    SD    -> ALUOpInfo(InstructionType.SType, ExecuteType.lsu, ALUOpEnum.add),

    LWU   -> ALUOpInfo(InstructionType.IType_Load, ExecuteType.lsu, ALUOpEnum.add),
    LD    -> ALUOpInfo(InstructionType.IType_Load, ExecuteType.lsu, ALUOpEnum.add)
  )

}

object RV32M {
  def MUL                = M"0000001----------000-----0110011"
  def MULH               = M"0000001----------001-----0110011"
  def MULHSU             = M"0000001----------010-----0110011"
  def MULHU              = M"0000001----------011-----0110011"
  def DIV                = M"0000001----------100-----0110011"
  def DIVU               = M"0000001----------101-----0110011"
  def REM                = M"0000001----------110-----0110011"
  def REMU               = M"0000001----------111-----0110011"

  val Table = Array(
    MUL   -> ALUOpInfo(InstructionType.RType, ExecuteType.mlt, ALUOpEnum.mlt),
    DIV   -> ALUOpInfo(InstructionType.RType, ExecuteType.div, ALUOpEnum.div),
    DIVU  -> ALUOpInfo(InstructionType.RType, ExecuteType.div, ALUOpEnum.div),
    REM   -> ALUOpInfo(InstructionType.RType, ExecuteType.div, ALUOpEnum.rem),
    REMU  -> ALUOpInfo(InstructionType.RType, ExecuteType.div, ALUOpEnum.rem)
  )
}

object RV64M {
  def MULW               = M"0000001----------000-----0111011"
  def DIVW               = M"0000001----------100-----0111011"
  def DIVUW              = M"0000001----------101-----0111011"
  def REMW               = M"0000001----------110-----0111011"
  def REMUW              = M"0000001----------111-----0111011"

  val Table = Array(
    MULW   -> ALUOpInfo(InstructionType.RType, ExecuteType.mlt, ALUOpEnum.mlt),
    DIVW   -> ALUOpInfo(InstructionType.RType, ExecuteType.div, ALUOpEnum.div),
    DIVUW  -> ALUOpInfo(InstructionType.RType, ExecuteType.div, ALUOpEnum.div),
    REMW   -> ALUOpInfo(InstructionType.RType, ExecuteType.div, ALUOpEnum.rem),
    REMUW  -> ALUOpInfo(InstructionType.RType, ExecuteType.div, ALUOpEnum.rem)
  )
}

object RVDebug{

  def ECALL              = B"000000000000_00000_000_00000_1110011"
  def EBREAK             = B"000000000001_00000_000_00000_1110011"
  def NOP                = B(0x13, 32 bits)

}

object check{
  def main(args: Array[String]): Unit = {
  }
}