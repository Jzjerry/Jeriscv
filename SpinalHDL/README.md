# Jeriscv (Single Cycle Verison, Written in Spinal HDL)

This is the SpinalHDL version for Jeriscv.

The only working design is a single cycle CPU in a single scala file [`Jeriscv/singleCycle/SingleCycleCore.scala`](./src/main/scala/Jeriscv/singleCycle/SingleCycleCore.scala). Other designs are not yet completed and may never be completed. The CPU supports RV64IM, but using the direct implementations of Multiply/Divide, which are basically not practical.

It was written during Fall 2022, I believe it's more concise and readable than the Chisel version.

It was verified using [Difftest](https://xiangshan-doc.readthedocs.io/zh-cn/latest/tools/difftest/) and Verilator, and successfully ran benchmarks (microbench, coremark, dhrystone) and Super Mario Bro on a NES simulator.