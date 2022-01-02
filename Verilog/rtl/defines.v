
`define SOMETHING

// PC Register Defines
`define CPU_RESET_ADDR 0
`define HOLD_PC 3'b001

// Regfile Defines
`define REG_WRITE_EN 1'b1

// ALU R-R Defines
`define FUNCT_ADD  3'b000
`define FUNCT_SLT  3'b001
`define FUNCT_SLTU 3'b010
`define FUNCT_AND  3'b011
`define FUNCT_OR   3'b100
`define FUNCT_XOR  3'b101
`define FUNCT_SLL  3'b110
`define FUNCT_SRL  3'b111

`define FUNCT_SUB_SRA 7'b0100000

