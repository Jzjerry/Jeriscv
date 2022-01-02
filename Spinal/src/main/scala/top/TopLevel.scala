package top

import StateEnum._
import spinal.core._
import spinal.lib._

class TopLevel extends Component {
    val io = new Bundle{
        val enable = in Bool()
        val reset = in Bool()
        val flag = out Bool()
        val o_state = out(StateEnum())
    }


    val stateNext = StateEnum()
    val state = Reg(StateEnum()) init(StateEnum.state0)

    when(io.enable){
        when(io.reset){
            state := StateEnum.state0
        } otherwise {
            state := stateNext
        }
    }

    switch(state)
    {
        is(StateEnum.state0)
        {
            stateNext := StateEnum.state1
        }
        is(StateEnum.state1)
        {
            stateNext := StateEnum.state2
        }
        is(StateEnum.state2)
        {
            stateNext := StateEnum.state0
        }
        is(StateEnum.state3)
        {
            stateNext := StateEnum.state0
        }
    }

    io.flag := (state === StateEnum.state2)
    io.o_state := state
}


object TopLevelVerilog {
  def main(args: Array[String]) {
    SpinalVerilog(new TopLevel)
  }
}

