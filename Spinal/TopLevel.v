// Generator : SpinalHDL v1.6.1    git head : 3bf789d53b1b5a36974196e2d591342e15ddf28c
// Component : TopLevel
// Git hash  : 9237e835a3cb0d46ae59cdd30c770fcc20603c08

`timescale 1ns/1ps 

module TopLevel (
  input               io_enable,
  input               io_reset,
  output              io_flag,
  output     [3:0]    io_o_state,
  input               clk,
  input               reset
);
  localparam StateEnum_state0 = 4'd1;
  localparam StateEnum_state1 = 4'd2;
  localparam StateEnum_state2 = 4'd4;
  localparam StateEnum_state3 = 4'd8;

  reg        [3:0]    stateNext;
  reg        [3:0]    state;
  `ifndef SYNTHESIS
  reg [47:0] io_o_state_string;
  reg [47:0] stateNext_string;
  reg [47:0] state_string;
  `endif


  `ifndef SYNTHESIS
  always @(*) begin
    case(io_o_state)
      StateEnum_state0 : io_o_state_string = "state0";
      StateEnum_state1 : io_o_state_string = "state1";
      StateEnum_state2 : io_o_state_string = "state2";
      StateEnum_state3 : io_o_state_string = "state3";
      default : io_o_state_string = "??????";
    endcase
  end
  always @(*) begin
    case(stateNext)
      StateEnum_state0 : stateNext_string = "state0";
      StateEnum_state1 : stateNext_string = "state1";
      StateEnum_state2 : stateNext_string = "state2";
      StateEnum_state3 : stateNext_string = "state3";
      default : stateNext_string = "??????";
    endcase
  end
  always @(*) begin
    case(state)
      StateEnum_state0 : state_string = "state0";
      StateEnum_state1 : state_string = "state1";
      StateEnum_state2 : state_string = "state2";
      StateEnum_state3 : state_string = "state3";
      default : state_string = "??????";
    endcase
  end
  `endif

  always @(*) begin
    (* parallel_case *)
    case(1) // synthesis parallel_case
      (((state) & StateEnum_state0) == StateEnum_state0) : begin
        stateNext = StateEnum_state1;
      end
      (((state) & StateEnum_state1) == StateEnum_state1) : begin
        stateNext = StateEnum_state2;
      end
      (((state) & StateEnum_state2) == StateEnum_state2) : begin
        stateNext = StateEnum_state0;
      end
      default : begin
        stateNext = StateEnum_state0;
      end
    endcase
  end

  assign io_flag = ((state & StateEnum_state2) != 4'b0000);
  assign io_o_state = state;
  always @(posedge clk or posedge reset) begin
    if(reset) begin
      state <= StateEnum_state0;
    end else begin
      if(io_enable) begin
        if(io_reset) begin
          state <= StateEnum_state0;
        end else begin
          state <= stateNext;
        end
      end
    end
  end


endmodule
