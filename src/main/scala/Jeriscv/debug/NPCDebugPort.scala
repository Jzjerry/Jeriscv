package Jeriscv.debug

import spinal.core._

class NPCDebugPort extends Bundle{
  val instr = UInt(32 bits)
  val pc = UInt(64 bits)
  val ExeRslt = UInt(64 bits)
  val GoodTrap = Bool()
  val BadTrap = Bool()
  val Exception = UInt(1 bits)
}
