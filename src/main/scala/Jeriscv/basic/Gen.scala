package Jeriscv.basic

import spinal.core._

object GenRegFile {
  def main(args: Array[String]) {
    SpinalConfig(targetDirectory = "./rtl/").generateVerilog(
      new RegFile(64, 32)
    )
  }
}