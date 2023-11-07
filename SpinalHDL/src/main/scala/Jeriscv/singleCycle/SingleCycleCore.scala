package Jeriscv.singleCycle

import spinal.core._
import spinal.lib._
import Jeriscv._
import Jeriscv.debug._
import Jeriscv.isa._

class SingleCycleCore(Config : JeriscvConfig) extends Component {

  val isStuck    = Bool()
  val isBadTrap  = Bool()
  val isGoodTrap = Bool()

  val exception = UInt(1 bits)

  exception := 0

  isStuck := False

  /* Instruction Fetch Stage Start */
  val ProgramCounter = new Area{
    val enable = Bool()
    val counter = RegInit(U(Config.ProgramReset,64 bits))
    val next_pc = counter + 4

    enable := ~(isStuck | isGoodTrap | isBadTrap)

    when(enable) {
      counter := next_pc
    }
    def jump(addr : UInt) = {
      counter := addr
    }
  }

  val inst_mem = new dpi_mem(64,64, Config)
  inst_mem.waddr := 0
  inst_mem.wdata := 0
  inst_mem.wmask := 0
  inst_mem.enable := ~clockDomain.readResetWire
  inst_mem.raddr := ProgramCounter.counter
  /* Instruction Fetch Stage End */


  /* RegFile Area Start */

  val RegFile = new Area{
    val mem = Mem(UInt(Config.XLEN bits), 32)
    def readAsync(addr : UInt) = {
      addr.mux(
        U(0) -> U(0),
        default -> mem.readAsync(addr))
    }
    def write(addr : UInt, data : UInt, enable : Bool) = {
      when(addr =/= 0){
        mem.write(addr, data, enable)
      }
    }
  }

  /* RegFile Area End */

  /* Instruction Decode Stage Start */
  val Decoder = new Area{
    val instr = ProgramCounter.counter(2).mux(
      False -> inst_mem.rdata(31 downto 0).asUInt,
      True -> inst_mem.rdata(63 downto 32).asUInt
    )

    // Instruction Type Pre-decode
    val InstrType = InstructionType()
    val ExType = ExecuteType()
    val ALUSel = ALUOpEnum()
    switch(instr){
      // Trap Detection
      for(elem <- Config.isaTable) {
        is(elem._1) {
          InstrType := elem._2.InstrType
          ExType := elem._2.ExType
          ALUSel := elem._2.ALUSel
        }
      }
      default {
        // Default Instruction == NOP
        exception := 1
        InstrType := InstructionType.IType
        ExType := ExecuteType.alu
        ALUSel := ALUOpEnum.add
      }
    }

    // Instruction Trap Detection
    isGoodTrap := instr.asBits === RVDebug.EBREAK
    isBadTrap := instr.asBits === RVDebug.ECALL

    val Op1, Op2 = UInt(Config.XLEN bits)
    val rs1_data = RegFile.readAsync(instr(RiscvFmt.rs1Range))
    val rs2_data = RegFile.readAsync(instr(RiscvFmt.rs2Range))

    // Imm Decode
    val immEX = RiscvFmt.IMM(instr.asBits)
    val immGen =
      InstrType.mux(
        InstructionType.IType       -> RiscvFmt.SignedExtend(immEX.i, Config.XLEN),
        InstructionType.IType_Load  -> RiscvFmt.SignedExtend(immEX.i, Config.XLEN),
        InstructionType.IType_JALR  -> RiscvFmt.SignedExtend(immEX.i, Config.XLEN),
        InstructionType.UType_LUI   -> RiscvFmt.SignedExtend(immEX.u, Config.XLEN),
        InstructionType.UType_AUIPC -> RiscvFmt.SignedExtend(immEX.u, Config.XLEN),
        InstructionType.BType       -> RiscvFmt.SignedExtend(immEX.b, Config.XLEN),
        InstructionType.SType       -> RiscvFmt.SignedExtend(immEX.s, Config.XLEN),
        InstructionType.JType       -> RiscvFmt.SignedExtend(immEX.j, Config.XLEN),
        default                     -> U(0))

    // ALU Src Control Decode
    val Src1Ctrl = Src1Enum()
    val Src2Ctrl = Src2Enum()

    (Src1Ctrl,Src2Ctrl) := InstrType.mux(
      InstructionType.RType -> Cat(Src1Enum.rs1, Src2Enum.rs2),
      InstructionType.IType -> Cat(Src1Enum.rs1, Src2Enum.imm),
      InstructionType.IType_Load -> Cat(Src1Enum.rs1, Src2Enum.imm),
      InstructionType.IType_JALR -> Cat(Src1Enum.rs1, Src2Enum.imm),
      InstructionType.SType -> Cat(Src1Enum.rs1, Src2Enum.imm),
      InstructionType.JType -> Cat(Src1Enum.pc, Src2Enum.imm),
      InstructionType.BType -> Cat(Src1Enum.rs1, Src2Enum.rs2),
      InstructionType.UType_AUIPC -> Cat(Src1Enum.pc, Src2Enum.imm),
      InstructionType.UType_LUI -> Cat(Src1Enum.zero, Src2Enum.imm),
      default     -> Cat(Src1Enum.rs1, Src2Enum.rs2)
    )

    val word_inst_flag = Bool()

    switch(instr){
      if(Config.isRV64I) {
        for(list <- RV64I.TableALU){
          is(list._1){
            word_inst_flag := True
          }
        }
        default{
          word_inst_flag := False
        }
      }
      if(Config.isRV64M) {
        for(list <- RV64M.Table){
          is(list._1){
            word_inst_flag := True
          }
        }
        default{
          word_inst_flag := False
        }
      }
    }

    Op1 := Src1Ctrl.mux(
      Src1Enum.rs1 -> (word_inst_flag ? RiscvFmt.Word2Double(rs1_data) | rs1_data) ,
      Src1Enum.imm -> (word_inst_flag ? RiscvFmt.Word2Double(immGen) | immGen),
      Src1Enum.pc -> ProgramCounter.counter,
      Src1Enum.zero -> U(0)
    )

    Op2 := Src2Ctrl.mux(
      Src2Enum.rs2 -> (word_inst_flag ? RiscvFmt.Word2Double(rs2_data) | rs2_data),
      Src2Enum.imm -> (word_inst_flag ? RiscvFmt.Word2Double(immGen) | immGen)
    )
  }
  /* Instruction Decode Stage End */

  /* Memory Start */
  val Memory = new Area{

    val data_mem = new dpi_mem(64,64, Config)

    // Default
    data_mem.raddr := 0
    data_mem.waddr := 0
    data_mem.wdata := 0
    data_mem.wmask := 0
    data_mem.enable := False

    def read(addr : UInt) : UInt = {
      data_mem.enable := True
      data_mem.raddr := addr
      data_mem.rdata.asUInt
    }
    def write(addr : UInt, wdata : UInt, wmask : UInt) : Unit = {
      data_mem.enable := True
      data_mem.waddr := addr
      data_mem.wdata := B(wdata)
      data_mem.wmask := B(wmask)
    }
  }

  /* Memory End */

  /* Execution Stage Start */
  val Execution = new Area{

    val Result = UInt(Config.XLEN bits)
    val JumpFlag = Bool()
    val BranchResult = Bool()

    BranchResult := False // Default No Branch
    JumpFlag := False     // Default No Jump

    switch(Decoder.ExType){
      is(ExecuteType.alu){
        // ALU Block
        val ShiftOp1 = Decoder.word_inst_flag ? Decoder.Op1(RiscvFmt.WordRange) | Decoder.Op1(RiscvFmt.DoubleRange)
        val shamt = Decoder.word_inst_flag ? Decoder.Op2(RiscvFmt.shamt32Range) | Decoder.Op2(RiscvFmt.shamt64Range)
        Result := Decoder.ALUSel.mux(
          ALUOpEnum.add -> (Decoder.Op1 + Decoder.Op2),
          ALUOpEnum.sub -> (Decoder.Op1 - Decoder.Op2),
          ALUOpEnum.and -> (Decoder.Op1 & Decoder.Op2),
          ALUOpEnum.or  -> (Decoder.Op1 | Decoder.Op2),
          ALUOpEnum.xor -> (Decoder.Op1 ^ Decoder.Op2),
          ALUOpEnum.sra -> (Decoder.word_inst_flag ? U(S(ShiftOp1(RiscvFmt.WordRange)) |>> shamt) | U(S(ShiftOp1) |>> shamt)),
          ALUOpEnum.srl -> (ShiftOp1 |>> shamt),
          ALUOpEnum.sll -> (ShiftOp1 |<< shamt),
          ALUOpEnum.slt -> (Decoder.Op1.asSInt < Decoder.Op2.asSInt).asUInt(Config.XLEN bits),
          ALUOpEnum.sltu -> (Decoder.Op1 < Decoder.Op2).asUInt(Config.XLEN bits),
          default -> U(0)
        )
      }
      is(ExecuteType.bru){
        // BRU Block
        Result := Decoder.ALUSel.mux(
          ALUOpEnum.add -> (Decoder.Op1 + Decoder.Op2), // JALR
          ALUOpEnum.slt -> (Decoder.Op1.asSInt < Decoder.Op2.asSInt).asUInt(Config.XLEN bits), // BGE, BLT
          ALUOpEnum.sltu -> (Decoder.Op1 < Decoder.Op2).asUInt(Config.XLEN bits), // BGEU, BLTU
          ALUOpEnum.sub -> (Decoder.Op1 - Decoder.Op2), // BEQ, BNE
          default -> U(0)
        )

        switch(Decoder.instr){
          is(RV32I.BEQ, RV32I.BGE, RV32I.BGEU){
            BranchResult := Result === 0
          }
          is(RV32I.BNE, RV32I.BLT, RV32I.BLTU){
            BranchResult := Result =/= 0
          }
        }

        JumpFlag := Decoder.InstrType.mux(
          InstructionType.IType_JALR -> True,
          InstructionType.JType -> True,
          InstructionType.BType -> BranchResult,
          default -> False
        )

        val JumpTarget = Decoder.InstrType.mux(
          InstructionType.JType -> (ProgramCounter.counter + Decoder.immGen),  // PC + imm
          InstructionType.BType -> (ProgramCounter.counter + Decoder.immGen),
          InstructionType.IType_JALR -> Result,
          default -> U(Config.ProgramReset)
        )
        when(JumpFlag){ ProgramCounter.jump(JumpTarget) }
      }
      is(ExecuteType.lsu) {
        Result := 0
        val addr = Decoder.ALUSel.mux(
          ALUOpEnum.add -> (Decoder.Op1 + Decoder.Op2),
          default -> U(0)
        )
        when(Decoder.InstrType === InstructionType.IType_Load){
          val rdata = Memory.read(addr) |>> (addr(2 downto 0) << 3) // bytes align
          Result := Decoder.instr.mux(
            RV32I.LB  -> RiscvFmt.SignedExtend(B(rdata(RiscvFmt.ByteRange)),Config.XLEN),
            RV32I.LBU -> U(U"56'h0" ## rdata(RiscvFmt.ByteRange)),
            RV32I.LH  -> RiscvFmt.SignedExtend(B(rdata(RiscvFmt.HalfRange)),Config.XLEN),
            RV32I.LHU -> U(U"48'h0" ## rdata(RiscvFmt.HalfRange)),
            RV32I.LW  -> RiscvFmt.SignedExtend(B(rdata(RiscvFmt.WordRange)),Config.XLEN),
            if(Config.isRV64I) RV64I.LWU -> U(U"32'h0" ## rdata(RiscvFmt.WordRange)) else null,
            if(Config.isRV64I) RV64I.LD -> rdata else null,
            default   -> U(0)
          )
        }.elsewhen(Decoder.InstrType === InstructionType.SType){
          val mask = Decoder.instr.mux(
            RV32I.SW  -> U(0xF),
            RV32I.SH  -> U(0x3),
            RV32I.SB  -> U(0x1),
            if(Config.isRV64I) RV64I.SD -> U(0xFF) else null,
            default   -> U(0)
          ) |<< addr(2 downto 0)
          val wdata = Decoder.rs2_data |<< (addr(2 downto 0) << 3)
          Memory.write(addr, wdata, mask)
        }
      }
      // TODO: Use mul and div IP to implement this
      is(ExecuteType.mlt){
        Result := (Decoder.Op1 * Decoder.Op2)(Config.XLEN - 1 downto 0)
      }
      is(ExecuteType.div){
        Result := Decoder.instr.mux(
          RV32M.DIVU  -> (Decoder.Op1 / Decoder.Op2),
          RV32M.REMU  -> (Decoder.Op1 % Decoder.Op2),
          RV32M.DIV   -> (Decoder.Op1.asSInt / Decoder.Op2.asSInt).asUInt,
          RV32M.REM    -> (Decoder.Op1.asSInt % Decoder.Op2.asSInt).asUInt,
          RV64M.DIVUW  -> (Decoder.Op1 / Decoder.Op2),
          RV64M.REMUW  -> (Decoder.Op1 % Decoder.Op2),
          RV64M.DIVW   -> (Decoder.Op1.asSInt / Decoder.Op2.asSInt).asUInt,
          RV64M.REMW   -> (Decoder.Op1.asSInt % Decoder.Op2.asSInt).asUInt,
          default   -> U(0)
        )
      }
    }
  }
  /* Execution Stage End */

  /* Write Back Stage Start */
  val WriteBackEnable = Decoder.InstrType.mux(
    InstructionType.SType -> False,
    InstructionType.BType -> False,
    default -> True
  )
  val WriteBackData = Decoder.InstrType.mux(
    InstructionType.IType_JALR -> ProgramCounter.next_pc,
    InstructionType.JType -> ProgramCounter.next_pc,
    default -> (Decoder.word_inst_flag ? RiscvFmt.Word2Double(Execution.Result) | Execution.Result)
  )

  RegFile.write(Decoder.instr(RiscvFmt.rdRange), WriteBackData, WriteBackEnable)

  /* Write Back Stage End */

  val npc = (Config.withDebugPort) generate out(new NPCDebugPort)
  if(Config.withDebugPort) {
    npc.instr := Decoder.instr
    npc.pc := ProgramCounter.counter
    npc.ExeRslt := Execution.Result
    npc.GoodTrap := isGoodTrap
    npc.BadTrap := isBadTrap
    npc.Exception := exception
  }
}

object GenSingleCycleCore{
  def main(args: Array[String]) {
    SpinalConfig(targetDirectory = "./npc/rtl/")
      .generateVerilog(new SingleCycleCore(JeriscvConfig(
        withDPIPort = true,
        withDebugPort = true)
      )).printPruned()
  }
}