`timescale 1ns/100ps

`include "defines.v"

module id_tb;

reg [6:0] opcode;
reg [2:0] funct3;
reg [6:0] funct7;
reg [4:0] rd;
reg [4:0] rs1;
reg [4:0] rs2;

wire [31:0] id_i_inst;

assign id_i_inst = {funct7, rs2, rs1, funct3, rd, opcode};

reg [31:0] id_i_inst_addr;

initial
begin            
    $dumpfile("./sim/id/wave.vcd");
    $dumpvars(0, id_tb);
end

initial
begin
    // zero
    opcode <= 0;
    funct3 <= 0;
    funct7 <= 0;
    rd <= 5'b00001;
    rs1 <= 5'b00010;
    rs2 <= 5'b00100;
    id_i_inst_addr <= 0;
    #10;
    // NOP
    opcode <= `RV32I_OPCODE_NOP;
    #10;
    opcode <= `RV32I_OPCODE_TYPE_I;
    funct3 <= `FUNCT3_ADDI;
    #10;
    opcode <= `RV32I_OPCODE_TYPE_R;
    funct3 <= `FUNCT3_AND;
    #10;
    opcode <= `RV32I_OPCODE_TYPE_S;
    funct3 <= `FUNCT3_SW;
    #10;
    opcode <= `RV32I_OPCODE_TYPE_SB;
    funct3 <= `FUNCT3_BNE;
    #10;
    opcode <= `RV32I_OPCODE_LUI;
    #10;
    opcode <= `RV32I_OPCODE_AUIPC;
    #10;
    opcode <= `RV32I_OPCODE_JAL;
    #10;
    opcode <= `RV32I_OPCODE_JALR;
    funct3 <= 3'b000;
    #10;
    $stop;
end

wire [5:0] reg_wr_addr;
wire [31:0] op1;
wire [31:0] op2;
wire [31:0] op1_jump;
wire [31:0] op2_jump;

Stage_id U1(
    .i_rst(1'b0),
    .i_inst(id_i_inst),
    .i_inst_addr(id_i_inst_addr),
    .i_reg1_data(32'hFFFF),
    .i_reg2_data(32'hFFFF),
    .o_reg_we(reg_we),
    .o_reg_wr_addr(reg_wr_addr[5:0]),
    .o_op1(op1[31:0]),
    .o_op2(op2[31:0]),
    .o_op1_jump(op1_jump[31:0]),
    .o_op2_jump(op2_jump[31:0])
);


endmodule