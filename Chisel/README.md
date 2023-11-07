# Jeriscv (Pipeline Verison, Written in Chisel)

This is the Chisel version for Jeriscv.

Although it gets 5-stage pipeline, it only supports RV32I, which is the minimal ISA.

It was written during Spring 2022, with a pretty bad Chisel coding style (that's why I never updated it).

It was not rigorously verified, but I believe it passed all `riscv-tests` for rv32i.

I implemented it on an Intel FPGA, at least it successfully controlled some LEDs.