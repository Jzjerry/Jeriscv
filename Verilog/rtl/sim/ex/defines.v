
`define SOMETHING

`define ZERO_WORD 32'h00000000

`define MEM_ADDR_WIDTH 31:0
`define MEM_DATA_WIDTH 31:0

// PC Register Defines
`define CPU_RESET_ADDR 0
`define HOLD_PC 3'b001

// Memory Defines
`define WRITE_ENABLE 1'b1
`define WRITE_DISABLE 1'b0

// Regfile Defines
`define REG_WRITE_ENABLE 1'b1
`define REG_WRITE_DISABLE 1'b0
`define ZERO_REG 5'b00000

// RISC-V ISA
// Opcode Type Defines(RV32I)

// I-Type
`define RV32I_OPCODE_TYPE_I  7'b0010011
`define RV32I_OPCODE_NOP     7'b0000001
// I-Type Funct3
`define FUNCT3_ADDI  3'b000
`define FUNCT3_SLTI  3'b010
`define FUNCT3_SLTIU 3'b011
`define FUNCT3_SLLI  3'b001
`define FUNCT3_XORI  3'b100
`define FUNCT3_SRI   3'b101 // SRLI SRAI
`define FUNCT3_ORI   3'b110
`define FUNCT3_ANDI  3'b111
// I-Type Funct7
`define FUNCT7_SRAI  7'b0100000
`define FUNCT7_SRLI  7'b0000000

// R-Type
`define RV32I_OPCODE_TYPE_R  7'b0110011
// R-Type Funct7
`define FUNCT7_ADD_SRL  7'b0000000
`define FUNCT7_SUB_SRA  7'b0100000
`define FUNCT7_MUL_DIV  7'b0000001 // TODO: MUL/DIV
// R-Type Funct3
`define FUNCT3_ADD_SUB 3'b000
`define FUNCT3_SLL    3'b001
`define FUNCT3_SLT    3'b010
`define FUNCT3_SLTU   3'b011
`define FUNCT3_XOR    3'b100
`define FUNCT3_SR     3'b101
`define FUNCT3_OR     3'b110
`define FUNCT3_AND    3'b111

// S-Type
`define RV32I_OPCODE_TYPE_S  7'b0100011
// S-Type Funct3
`define FUNCT3_SB  3'b000
`define FUNCT3_SH  3'b001
`define FUNCT3_SW  3'b010

// L-Type
`define RV32I_OPCODE_TYPE_L  7'b0000011
// L-Type Funct3
`define FUNCT3_LB  3'b000
`define FUNCT3_LH  3'b001
`define FUNCT3_LW  3'b010
`define FUNCT3_LBU 3'b100
`define FUNCT3_LHU 3'b101

// SB-Type
`define RV32I_OPCODE_TYPE_SB  7'b1100011
// SB-Type Funct3
`define FUNCT3_BEQ    3'b000
`define FUNCT3_BNE    3'b001
`define FUNCT3_BLT    3'b100
`define FUNCT3_BGE    3'b101
`define FUNCT3_BLTU   3'b110
`define FUNCT3_BGEU   3'b111

// U-Type
`define RV32I_OPCODE_LUI   7'b0110111
`define RV32I_OPCODE_AUIPC 7'b0010111

// UJ-Type
`define RV32I_OPCODE_JAL  7'b1101111
`define RV32I_OPCODE_JALR 7'b1100111

// Fence // TODO: Memory Ordering Fence
`define RV32I_OPCODE_FENCE 7'b0001111

// ECALL and EBREAK // TODO: Enveriment Call and Breakpoints
`define RV32I_OPCODE_ECALL 32'h73
`define RV32I_OPCODE_EBREAK 32'h00100073

// CSR-Type
`define RV32_OPCODE_CSR 7'b1110011 // TODO: CSR