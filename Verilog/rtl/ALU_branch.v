`include "defines.v"

module ALU_branch(
    input [31:0] i_op1,
    input [31:0] i_op2,
    input [31:0] i_op1_jump,
    input [31:0] i_op2_jump,
    input [2:0] i_funct3,
    output reg [31:0]o_jump_addr,
    output reg o_jump_flag
);

    always @(*) begin
        case(i_funct3)
            `FUNCT3_BEQ: begin
                o_jump_flag = i_op1 == i_op2 ? 1'b1 : 1'b0;
            end
            `FUNCT3_BNE: begin
                o_jump_flag = i_op1 != i_op2 ? 1'b1 : 1'b0;
            end
            `FUNCT3_BLT: begin
                o_jump_flag = i_op1 < i_op2 ? 1'b1 : 1'b0;
            end
            `FUNCT3_BGE: begin
                o_jump_flag = i_op1 > i_op2 ? 1'b1 : 1'b0;
            end
            `FUNCT3_BLTU: begin
                o_jump_flag = $unsigned(i_op1) < $unsigned(i_op2) ? 1'b1 : 1'b0;
            end
            `FUNCT3_BGEU: begin
                o_jump_flag = $unsigned(i_op1) > $unsigned(i_op2) ? 1'b1 : 1'b0;
            end
            default:begin
                o_jump_flag = 1'b0;
            end
        endcase
        o_jump_addr = {32{o_jump_flag}}&(i_op1_jump + i_op2_jump);
    end


endmodule