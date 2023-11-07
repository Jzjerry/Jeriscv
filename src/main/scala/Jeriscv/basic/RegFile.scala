package Jeriscv.basic

import spinal.core._

class RegFileReadPort(wordLength : Int, wordCount : Int) extends Bundle{

  val readEn = in Bool()
  val readAddress_a = in UInt(log2Up(wordCount) bits)
  val readAddress_b = in UInt(log2Up(wordCount) bits)
  val readData_a = out UInt(wordLength bits)
  val readData_b = out UInt(wordLength bits)

}

class RegFileWritePort(wordLength : Int, wordCount : Int) extends Bundle{

  val writeEn = in Bool()
  val writeAddress = in UInt(log2Up(wordCount) bits)
  val writeData = in UInt(wordLength bits)

}

class RegFile(wordLength : Int, wordCount : Int) extends Component {

  val RPort = new RegFileReadPort(wordLength, wordCount)
  val WPort = new RegFileWritePort(wordLength, wordCount)

  val mem = Mem(UInt(wordLength bits),wordCount)

  mem.write(
    enable = WPort.writeEn && (WPort.writeAddress =/= 0),
    address = WPort.writeAddress,
    data = WPort.writeData
  )

  RPort.readData_a := (RPort.readAddress_a =/= 0) ?
    mem.readSync(
    enable = RPort.readEn,
    address = RPort.readAddress_a
  ) | U(0, wordLength bits)

  RPort.readData_b := (RPort.readAddress_b =/= 0) ?
    mem.readSync(
    enable = RPort.readEn,
    address = RPort.readAddress_b
  ) | U(0, wordLength bits)
}
