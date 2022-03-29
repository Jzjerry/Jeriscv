`include "./defines.v"
module Stage_ex(
    input wire rst,

    // from id
    input wire i_mem_enable,
    input wire[31:0] i_op1,
    input wire[31:0] i_op2,
    input wire[31:0] i_op1_jump,
    input wire[31:0] i_op2_jump,

    input wire [31:0] i_inst,
    input wire [31:0] i_inst_addr,
    input wire [31:0] i_reg1_data,
    input wire [31:0] i_reg2_data,
    input wire i_reg_we,
    input wire [4:0] i_reg_wr_addr,
    input wire i_csr_we,
    input wire [31:0] i_csr_rdata,
    input wire [31:0] i_csr_wr_addr,

    // from memory
    input wire [31:0] i_mem_rdata,

    // to memory
    output reg [31:0] o_mem_wdata,
    output reg [`MEM_ADDR_WIDTH] o_mem_waddr,
    output reg [`MEM_ADDR_WIDTH] o_mem_raddr,
    output wire o_mem_req,
    output reg o_mem_we,
    
    // to regs
    output reg [31:0] o_reg_wdata,
    output wire [4:0] o_reg_waddr,
    output wire o_reg_we,

    // to csr regs
    output reg [31:0] o_csr_wdata,
    output wire [31:0] o_csr_waddr,
    output wire o_csr_we,

    // to ctrl
    output reg o_hold_flag,
    output reg o_jump_flag,
    output reg [31:0] o_jump_addr
);
    
    wire[6:0] opcode;
    wire[2:0] funct3;
    wire[6:0] funct7;
    wire[4:0] rd;

    assign opcode = i_inst[6:0];
    assign funct3 = i_inst[14:12];
    assign funct7 = i_inst[31:25];
    assign rd = i_inst[11:7];

    wire [31:0] alu_output;

    ALU_rtr alu_1(
        .i_dataa(i_op1),
        .i_datab(i_op2),
        .i_funct3(funct3),
        .i_sub_sra(funct7==`FUNCT7_SUB_SRA),
        .o_datac(alu_output)
    );

    wire jump_flag;
    wire [31:0] jump_addr;

    ALU_branch alu2(
        .i_op1(i_op1),
        .i_op2(i_op2),
        .i_op1_jump(i_op1_jump),
        .i_op2_jump(i_op2_jump),
        .i_funct3(funct3),
        .o_jump_flag(jump_flag),
        .o_jump_addr(jump_addr)
    );

    // execute block

    reg reg_we;
    reg reg_waddr;
    reg mem_req;

    always@(*) begin
        reg_we = i_reg_we;
        reg_waddr = `ZERO_REG;
        mem_req = 1'b0;
        o_csr_wdata = `ZERO_WORD;
        case (opcode)
            `RV32I_OPCODE_TYPE_I: begin
                o_jump_flag = 1'b0;
                o_hold_flag = 1'b0;
                o_jump_addr = `ZERO_WORD;
                o_mem_wdata = `ZERO_WORD;
                o_mem_raddr = `ZERO_WORD;
                o_mem_waddr = `ZERO_WORD;
                o_mem_we = `WRITE_DISABLE;
                o_reg_wdata = alu_output;
            end
            `RV32I_OPCODE_NOP: begin
                o_jump_flag = 1'b0;
                o_hold_flag = 1'b0;
                o_jump_addr = `ZERO_WORD;
                o_mem_wdata = `ZERO_WORD;
                o_mem_raddr = `ZERO_WORD;
                o_mem_waddr = `ZERO_WORD;
                o_mem_we = `WRITE_DISABLE;
                o_reg_wdata = `ZERO_WORD;
            end
            `RV32I_OPCODE_TYPE_R: begin
                o_jump_flag = 1'b0;
                o_hold_flag = 1'b0;
                o_jump_addr = `ZERO_WORD;
                o_mem_wdata = `ZERO_WORD;
                o_mem_raddr = `ZERO_WORD;
                o_mem_waddr = `ZERO_WORD;
                o_mem_we = `WRITE_DISABLE;
                o_reg_wdata = alu_output;
            end
            // TODO: memory instructions need to be modified
            `RV32I_OPCODE_TYPE_S: begin
                case (funct3)
                    `FUNCT3_SB, `FUNCT3_SH, `FUNCT3_SW: begin
                        o_jump_flag = 1'b0;
                        o_hold_flag = 1'b0;
                        o_jump_addr = `ZERO_WORD;
                        o_reg_wdata = `ZERO_WORD;
                        o_mem_we = `WRITE_ENABLE;
                    end
                    default: begin
                        o_jump_flag = 1'b0;
                        o_hold_flag = 1'b0;
                        o_jump_addr = `ZERO_WORD;
                        o_mem_wdata = `ZERO_WORD;
                        o_mem_raddr = `ZERO_WORD;
                        o_mem_waddr = `ZERO_WORD;
                        o_mem_we = `WRITE_DISABLE;
                    end
                endcase
            end
            // TODO: memory instructions need to be modified
            `RV32I_OPCODE_TYPE_L: begin
                case(funct3)
                `FUNCT3_LB, `FUNCT3_LH, `FUNCT3_LW: begin
                    o_jump_flag = 1'b0;
                    o_hold_flag = 1'b0;
                    o_jump_addr = `ZERO_WORD;
                    o_mem_we = `WRITE_DISABLE;
                    o_mem_wdata = `ZERO_WORD;
                    o_mem_waddr = `ZERO_WORD;
                    o_mem_raddr = `ZERO_WORD;

                    o_reg_wdata = i_mem_rdata;
                end
                default: begin
                    o_jump_flag = 1'b0;
                    o_hold_flag = 1'b0;
                    o_jump_addr = `ZERO_WORD;
                    o_mem_wdata = `ZERO_WORD;
                    o_mem_raddr = `ZERO_WORD;
                    o_mem_waddr = `ZERO_WORD;
                    o_mem_we = `WRITE_DISABLE;
                end
                endcase
            end
            `RV32I_OPCODE_TYPE_SB: begin
                case(funct3)
                    `FUNCT3_BEQ, `FUNCT3_BNE, `FUNCT3_BLT, `FUNCT3_BGE,
                    `FUNCT3_BLTU, `FUNCT3_BGEU: begin
                        o_hold_flag = 1'b0;
                        o_mem_wdata = `ZERO_WORD;
                        o_mem_raddr = `ZERO_WORD;
                        o_mem_waddr = `ZERO_WORD;
                        o_mem_we = `WRITE_DISABLE;

                        o_jump_flag = jump_flag;
                        o_jump_addr = jump_addr;
                    end
                    default: begin
                        o_jump_flag = 1'b0;
                        o_hold_flag = 1'b0;
                        o_jump_addr = `ZERO_WORD;
                        o_mem_wdata = `ZERO_WORD;
                        o_mem_raddr = `ZERO_WORD;
                        o_mem_waddr = `ZERO_WORD;
                        o_mem_we = `WRITE_DISABLE;
                    end
                endcase
            end
            `RV32I_OPCODE_JAL,`RV32I_OPCODE_JALR: begin
                o_hold_flag = 1'b0;
                o_mem_wdata = `ZERO_WORD;
                o_mem_raddr = `ZERO_WORD;
                o_mem_waddr = `ZERO_WORD;
                o_mem_we = `WRITE_DISABLE;

                o_jump_flag = 1'b1;
                o_jump_addr = i_op1_jump + i_op2_jump;
                o_reg_wdata = i_op1 + i_op2;
            end
            `RV32I_OPCODE_LUI,`RV32I_OPCODE_AUIPC: begin
                o_jump_flag = 1'b0;
                o_hold_flag = 1'b0;
                o_jump_addr = `ZERO_WORD;
                o_mem_wdata = `ZERO_WORD;
                o_mem_raddr = `ZERO_WORD;
                o_mem_waddr = `ZERO_WORD;
                o_mem_we = `WRITE_DISABLE;

                o_reg_wdata = i_op1 + i_op2;
            end
            default: begin
                o_jump_flag = 1'b0;
                o_hold_flag = 1'b0;
                o_jump_addr = `ZERO_WORD;
                o_mem_wdata = `ZERO_WORD;
                o_mem_raddr = `ZERO_WORD;
                o_mem_waddr = `ZERO_WORD;
                o_mem_we = `WRITE_DISABLE;
                o_reg_wdata = `ZERO_WORD;
            end
        endcase
    end


endmodule