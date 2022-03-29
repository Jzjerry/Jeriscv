`include "./defines.v"

module TopLevel(
);
    
    wire [31:0] op1;
    wire [31:0] op2;
    wire [31:0] op1_jump;
    wire [31:0] op2_jump;
    wire [31:0] inst;
    wire [31:0] inst_addr;
    wire [31:0] reg1_data;
    wire [31:0] reg2_data;

    wire mem_enable;
    wire reg_we;
    wire [4:0] reg_wr_addr;
	 
	 
	 Regfile r(
	   .i_clock,
		.i_readwrite,
		.i_reset,
		.i_readreg1,
		.i_readreg2,
		.i_writereg,
		.i_writedata,
		.o_readdata1,
		.o_readdata2
	 );

    Stage_id u1(
        // to ex
        .o_mem_enable(mem_enable),
        .o_op1(op1),
        .o_op2(op2),
        .o_op1_jump(op1_jump),
        .o_op2_jump(op2_jump),

        .o_inst(inst),
        .o_inst_addr(inst_addr),
        .o_reg1_data(reg1_data),
        .o_reg2_data(reg2_data),
        .o_reg_we(reg_we),
        .o_reg_wr_addr(reg_wr_addr),
        .o_csr_we(csr_we),
        .o_csr_rdata(csr_rdata),
        .o_csr_wr_addr(csr_wr_addr)
    );

    Stage_ex u2(
        // from id
        .i_mem_enable(mem_enable),
        .i_op1(op1),
        .i_op2(op2),
        .i_op1_jump(op1_jump),
        .i_op2_jump(op2_jump),
        .i_inst(inst),
        .i_inst_addr(inst_addr),
        .i_reg1_data(reg1_data),
        .i_reg2_data(reg2_data),
        .i_reg_we(reg_we),
        .i_reg_wr_addr(reg_wr_addr),
        .i_csr_we(csr_we),
        .i_csr_rdata(csr_rdata),
        .i_csr_wr_addr(csr_wr_addr)
    );
	 

endmodule
