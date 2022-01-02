`include "./defines.v"

module PCReg(
    input i_clock,
    input i_reset,
    input i_jump_flag,
    input [31:0] i_jump_addr,
    input [2:0]i_hold_flag,
    output [31:0] o_pc_addr
);
    reg [31:0] o_pc_addr;
    always @(posedge i_clock) begin
        if(i_reset == 1'b1) begin
            o_pc_addr <= 32'b0;
        end
        else if(i_jump_flag == 1'b1) begin
            o_pc_addr <= i_jump_addr;
        end
        else if(i_hold_flag >= `HOLD_PC) begin
            o_pc_addr <= o_pc_addr;
        end
        else begin
            o_pc_addr <= o_pc_addr + 4'h4;
        end
    end
    
endmodule