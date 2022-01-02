`include "./defines.v"

module Regfile(
    input i_clock,
    input i_readwrite,
    input i_reset,
    input [4:0] i_readreg1,
    input [4:0] i_readreg2,
    input [4:0] i_writereg,
    input [31:0] i_writedata,
    output [31:0] o_readdata1,
    output [31:0] o_readdata2
);

    reg [31:0] reg_data [31:0];
    reg [31:0] o_readdata1;
    reg [31:0] o_readdata2;

    // read reg 1
    always @(*) begin
        if(i_reset == 1'b1) begin
            o_readdata1 = `ZERO_WORD;
        end
        else if(i_readreg1 == 0) begin
            o_readdata1 = `ZERO_WORD;
        end
        else if(i_readreg1 == i_writereg && 
        i_readwrite == `REG_WRITE_ENABLE) begin
            o_readdata1 = i_writedata;
        end
        else begin
            o_readdata1 = reg_data[i_readreg1];
        end
    end


    // read reg 2
    always @(*) begin
        if(i_reset == 1'b1) begin
            o_readdata2 = `ZERO_WORD;
        end
        else if(i_readreg2 == 0) begin
            o_readdata2 = `ZERO_WORD;
        end
        else if(i_readreg2 == i_writereg 
        && i_readwrite == `REG_WRITE_ENABLE) begin
            o_readdata2 = i_writedata;
        end
        else begin
            o_readdata2 = reg_data[i_readreg2];
        end
    end

    // write reg
    always @(posedge i_clock) begin
        if(i_reset == 1'b0) begin
            if((i_readwrite == `REG_WRITE_ENABLE) 
            &&(i_writereg!= 32'b0)) begin
                reg_data[i_writereg] <= i_writedata;
            end
        end
    end
    
endmodule