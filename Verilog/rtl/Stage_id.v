`include "./defines.v"

module Stage_id(

    input wire i_rst,
    
    // from if_id
    input wire [31:0] i_inst,
    input wire [31:0] i_inst_addr,

    // from regs
    input wire [31:0] i_reg1_data,
    input wire [31:0] i_reg2_data,

    // from csr reg
    input wire [31:0] i_csr_data,

    // from ex
    input wire i_ex_jump_flag,

    // to regs
    output reg [5:0] o_reg1_rd_addr,
    output reg [5:0] o_reg2_rd_addr,

    // to csr reg
    output reg [31:0] o_csr_rd_addr,

    // to ex
    output reg o_mem_enable,
    output reg[31:0] o_op1,
    output reg[31:0] o_op2,
    output reg[31:0] o_op1_jump,
    output reg[31:0] o_op2_jump,

    output reg [31:0] o_inst,
    output reg [31:0] o_inst_addr,
    output reg [31:0] o_reg1_data,
    output reg [31:0] o_reg2_data,
    output reg o_reg_we,
    output reg [5:0] o_reg_wr_addr,
    output reg o_csr_we,
    output reg [31:0] o_csr_rdata,
    output reg [31:0] o_csr_wr_addr
);
    // RISC-V ISA Format
    wire [6:0] opcode = i_inst[6:0];
    wire [2:0] funct3 = i_inst[14:12];
    wire [6:0] funct7 = i_inst[31:25];
    wire [4:0] rd = i_inst[11:7];
    wire [4:0] rs1 = i_inst[19:15];
    wire [4:0] rs2 = i_inst[24:20];

    // Signal pass through Stage_id
    always@(*)begin
        o_inst = i_inst;
        o_inst_addr = i_inst_addr;
        o_reg1_data = i_reg1_data;
        o_reg2_data = i_reg2_data;
        o_csr_rdata = i_csr_data;
    end

    // ISA Decode
    always @(*) begin
        // Initialize
        o_csr_rd_addr = `ZERO_WORD;
        o_csr_wr_addr = `ZERO_WORD;
        o_csr_we = `WRITE_DISABLE;
        o_op1 = `ZERO_WORD;
        o_op2 = `ZERO_WORD;
        o_op1_jump = `ZERO_WORD;
        o_op2_jump = `ZERO_WORD;
        case(opcode)
            // I-Type
            `RV32I_OPCODE_TYPE_I: begin
                case(funct3)
                    `FUNCT3_ADDI, `FUNCT3_SLTI, `FUNCT3_SLTIU, 
                    `FUNCT3_XORI, `FUNCT3_ORI, `FUNCT3_ANDI, 
                    `FUNCT3_SLLI, `FUNCT3_SRI: begin
                        o_reg_we = `REG_WRITE_ENABLE;
                        o_reg_wr_addr = rd;
                        o_reg1_rd_addr = rs1;
                        o_reg2_rd_addr = `ZERO_REG;
                        o_op1 = i_reg1_data;
                        o_op2 = {{20{i_inst[31]}},i_inst[31:20]}; // sign extend
                    end
                    default: begin
                        o_reg_we = `REG_WRITE_DISABLE;
                        o_reg_wr_addr = `ZERO_REG;
                        o_reg1_rd_addr = `ZERO_REG;
                        o_reg2_rd_addr = `ZERO_REG;
                    end
                endcase
            end
            `RV32I_OPCODE_NOP: begin
                o_reg_we = `REG_WRITE_DISABLE;
                o_reg_wr_addr = `ZERO_REG;
                o_reg1_rd_addr = `ZERO_REG;
                o_reg2_rd_addr = `ZERO_REG;
            end
            // R-Type
            `RV32I_OPCODE_TYPE_R: begin
                case(funct7)
                    `FUNCT7_SUB_SRA, `FUNCT7_ADD_SRL:
                    case (funct3)
                        `FUNCT3_ADD_SUB, `FUNCT3_SLL, `FUNCT3_SLT,
                        `FUNCT3_SLTU, `FUNCT3_XOR, `FUNCT3_SR, 
                        `FUNCT3_OR, `FUNCT3_AND: begin
                            o_reg_we = `REG_WRITE_ENABLE;
                            o_reg_wr_addr = rd;
                            o_reg1_rd_addr = rs1;
                            o_reg2_rd_addr = rs2;
                            o_op1 = i_reg1_data;
                            o_op2 = i_reg2_data;
                        end
                        default: begin
                            o_reg_we = `REG_WRITE_DISABLE;
                            o_reg_wr_addr = `ZERO_REG;
                            o_reg1_rd_addr = `ZERO_REG;
                            o_reg2_rd_addr = `ZERO_REG;
                        end
                    endcase
                    `FUNCT7_MUL_DIV:begin
                        // TO DO: RV32M
                        o_reg_we = `REG_WRITE_DISABLE;
                        o_reg_wr_addr = `ZERO_REG;
                        o_reg1_rd_addr = `ZERO_REG;
                        o_reg2_rd_addr = `ZERO_REG;
                    end
                    default:begin
                        o_reg_we = `REG_WRITE_DISABLE;
                        o_reg_wr_addr = `ZERO_REG;
                        o_reg1_rd_addr = `ZERO_REG;
                        o_reg2_rd_addr = `ZERO_REG;
                    end
                endcase
            end
            // S-Type
            `RV32I_OPCODE_TYPE_S: begin
                case(funct3)
                `FUNCT3_SB, `FUNCT3_SH, `FUNCT3_SW: begin
                    o_reg1_rd_addr = rs1;
                    o_reg2_rd_addr = rs2;
                    o_reg_we = `REG_WRITE_DISABLE;
                    o_reg_wr_addr = `ZERO_REG;
                    o_op1 = i_reg1_data;
                    o_op2 = {{20{i_inst[31]}},i_inst[31:25],i_inst[11:7]}; // IMM Memory Address 
                end
                default: begin
                    o_reg_we = `REG_WRITE_DISABLE;
                    o_reg_wr_addr = `ZERO_REG;
                    o_reg1_rd_addr = `ZERO_REG;
                    o_reg2_rd_addr = `ZERO_REG;
                end
                endcase
            end
            // L-Type
            `RV32I_OPCODE_TYPE_L: begin
                case(funct3)
                `FUNCT3_LB, `FUNCT3_LH, `FUNCT3_LW, 
                `FUNCT3_LBU, `FUNCT3_LHU: begin
                    o_reg1_rd_addr = rs1;
                    o_reg2_rd_addr = `ZERO_REG;
                    o_reg_we = `REG_WRITE_ENABLE;
                    o_reg_wr_addr = rd;
                    o_op1 = i_reg1_data;
                    o_op2 = {{20{i_inst[31]}},i_inst[31:20]};
                end
                default:begin
                    o_reg_we = `REG_WRITE_DISABLE;
                    o_reg_wr_addr = `ZERO_REG;
                    o_reg1_rd_addr = `ZERO_REG;
                    o_reg2_rd_addr = `ZERO_REG;
                end
                endcase
            end
            // SB-Type
            `RV32I_OPCODE_TYPE_SB: begin
                case(funct3)
                `FUNCT3_BEQ, `FUNCT3_BNE, `FUNCT3_BLT,
                `FUNCT3_BGE, `FUNCT3_BLTU, `FUNCT3_BGEU: begin
                    o_reg1_rd_addr = rs1;
                    o_reg2_rd_addr = rs2;
                    o_reg_we = `REG_WRITE_DISABLE;
                    o_reg_wr_addr = `ZERO_REG;
                    o_op1 = i_reg1_data;
                    o_op2 = i_reg2_data;
                    o_op1_jump = i_inst_addr;
                    o_op2_jump = {{20{i_inst[31]}},i_inst[7],i_inst[30:25],i_inst[11:8],1'b0}; // B-Immediate
                end
                default:begin
                    o_reg_we = `REG_WRITE_DISABLE;
                    o_reg_wr_addr = `ZERO_REG;
                    o_reg1_rd_addr = `ZERO_REG;
                    o_reg2_rd_addr = `ZERO_REG;
                end
                endcase
            end
            // U-Type
            `RV32I_OPCODE_LUI: begin
                o_reg_we = `REG_WRITE_ENABLE;
                o_reg_wr_addr = rd;
                o_reg1_rd_addr = `ZERO_REG;
                o_reg2_rd_addr = `ZERO_REG;
                o_op1 = {i_inst[31:12],12'b0}; // U-Immediate
                o_op2 = `ZERO_WORD;
            end
            `RV32I_OPCODE_AUIPC: begin
                o_reg_we = `REG_WRITE_ENABLE;
                o_reg_wr_addr = rd;
                o_reg1_rd_addr = `ZERO_REG;
                o_reg2_rd_addr = `ZERO_REG;
                o_op1 = i_inst_addr; 
                o_op2 = {i_inst[31:12],12'b0};// U-Immediate
            end
            // UJ-Type
            `RV32I_OPCODE_JAL: begin
                o_reg_wr_addr = rd;
                o_reg_we = `REG_WRITE_ENABLE;
                o_reg1_rd_addr = `ZERO_REG;
                o_reg2_rd_addr = `ZERO_REG;
                o_op1 = i_inst_addr;
                o_op2 = 32'h4;
                o_op1_jump = i_inst_addr;
                o_op2_jump = {{12{i_inst[31]}},i_inst[19:12],i_inst[20],i_inst[30:21],1'b0}; // J-Immediate
            end
            `RV32I_OPCODE_JALR: begin
                if(funct3==3'b0) begin
                    o_reg_wr_addr = rd;
                    o_reg_we = `REG_WRITE_ENABLE;
                    o_reg1_rd_addr = rs1;
                    o_reg2_rd_addr = `ZERO_REG;
                    o_op1 = i_inst_addr;
                    o_op2 = 32'h4;
                    o_op1_jump = i_reg1_data;
                    o_op2_jump = {{20{i_inst[31]}},i_inst[31:20]}; // I-Immediate
                end
                else begin
                    o_reg_wr_addr = `ZERO_REG;
                    o_reg_we = `REG_WRITE_DISABLE;
                    o_reg1_rd_addr = `ZERO_REG;
                    o_reg2_rd_addr = `ZERO_REG;
                end
                
            end
            default: begin
                o_reg_we = `REG_WRITE_DISABLE;
                o_reg_wr_addr = `ZERO_REG;
                o_reg1_rd_addr = `ZERO_REG;
                o_reg2_rd_addr = `ZERO_REG;
            end
        endcase
    end


endmodule