package StateEnum

import spinal.core._
import spinal.lib._


object StateEnum extends SpinalEnum{
  val state0, state1, state2, state3 = newElement()
  defaultEncoding = binaryOneHot
}

