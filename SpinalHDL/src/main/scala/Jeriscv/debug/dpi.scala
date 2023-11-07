package Jeriscv.debug

import spinal.core._

import Jeriscv._

class dpi_mem(DataLen : Int, AddrLen : Int,
              Config : JeriscvConfig) extends BlackBox {

  val raddr = in UInt(AddrLen bits)
  val waddr = in UInt(AddrLen bits)

  val rdata = out Bits(DataLen bits)
  val wdata = in Bits(DataLen bits)
  val enable = in Bool()

  val wmask = in Bits(DataLen/8 bits)

  if(Config.withDPIPort) {
    setInlineVerilog(
      "module dpi_mem(raddr,waddr,rdata,wdata,wmask,enable);\n" +

        "input wire [63:0] raddr;\n" +
        "input wire [63:0] waddr;\n" +
        "input wire [63:0] wdata;\n" +
        "input wire [7:0] wmask;\n" +
        "input wire enable;\n" +
        "output reg [63:0] rdata;\n" +

        "import \"DPI-C\" function void pmem_read(\n" +
        "input longint raddr, output longint rdata);\n" +

        "import \"DPI-C\" function void pmem_write(\n" +
        "input longint waddr, input longint wdata, input byte wmask);\n" +

        "always @(*) begin\n " +
          "pmem_read(raddr, rdata);\n" +
          "pmem_write(waddr, wdata, wmask);\n" +
        "end\n" +
        "endmodule")
  }else{
    setInlineVerilog("module dpi_mem(raddr,waddr,rdata,wdata,wmask);\n" +
      "    input wire [63:0] raddr;\n" +
      "    input wire [63:0] waddr;\n" +
      "    input wire [63:0] wdata;\n" +
      "    input wire [7:0] wmask;\n" +
      "    output reg [63:0] rdata;\n\n" +
      "    /* Dummy */\n\n" +
      "endmodule")
  }
}

class dpi_RegFile extends BlackBox {

  setInlineVerilog(
    "module dpi_RegFile();\n" +
    "reg [63:0] rf [32];\n" +
    "import \"DPI-C\" function void set_gpr_ptr(input logic [63:0] a []);\n" +
    "initial set_gpr_ptr(rf);\n" +
    "endmodule"
  )

}

object dpi {
  def main(args: Array[String]) {
    SpinalConfig(targetDirectory = "./rtl/").generateVerilog(
      new dpi_mem(64, 32, JeriscvConfig(withDPIPort = true))
    )
  }
}

