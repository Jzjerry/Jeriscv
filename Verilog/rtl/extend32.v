module Extend32
(
    input [15:0] a,
    output reg [31:0] b
);
/* extend signed 16-bit to signed 32-bit */
/* by copilot */

    always @(*) begin
        
        b = a;
        if (a[15] == 1'b1) begin
            b = b | (32'hFFFF_0000);
        end
    end


endmodule
