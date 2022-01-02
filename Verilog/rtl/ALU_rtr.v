`include "defines.v"

/* Register-to-Register ALU 
+--------+---------------------+
| funct3 | computation         |
+--------+---------------------+
| 000    | Addition            |    if (sub_sra==1): Subtraction
| 001    | Signed Less-than    |
| 010    | Unisigned Less-than |
| 011    | Bitwise And         |
| 100    | Bitwise Or          |
| 101    | Bitwise Xor         |
| 110    | Logical Left Shift  |
| 111    | Logical Right Shift |    if (sub_sra==1): Arithmetic Right Shift
+--------+---------------------+
*/

module ALU_rtr(
    input [31:0] i_dataa,
    input [31:0] i_datab,
    input i_sub_sra,
    input [2:0] i_funct3,
    output [31:0]o_datac
);
    reg [31:0]o_datac;
    reg overflow;/* overflow flag */
    
    always@(*)begin
        case(i_funct3)
            `FUNCT_ADD  : o_datac = i_sub_sra ? i_dataa - i_datab
                                    : i_dataa + i_datab;
            `FUNCT_SLT  : o_datac = (i_dataa < i_datab) ? 
                                    4'h1 : 4'h0;
            `FUNCT_SLTU : o_datac = ($unsigend(i_dataa) < $unsigend(i_datab)) ? 
                                    4'h1 : 4'h0;
            `FUNCT_AND  : o_datac = i_dataa & i_datab;
            `FUNCT_OR   : o_datac = i_dataa | i_datab;
            `FUNCT_XOR  : o_datac = i_dataa ^ i_datab;
            `FUNCT_SLL  : o_datac = i_dataa << i_datab[4:0];
            `FUNCT_SRL  : o_datac = i_sub_sra ? i_dataa >> i_datab[4:0]
                                    : i_dataa >>> i_datab[4:0];
            default     : o_datac = 4'h0;
        endcase

        case({i_dataa[31],i_datab[31],o_datac[31]})

            3'b011 : overflow = i_sub_sra;
            3'b100 : overflow = i_sub_sra;
            
            3'b001 : overflow = ~i_sub_sra;
            3'b110 : overflow = ~i_sub_sra;

            default : overflow = 1'b0;

        endcase
    end

endmodule