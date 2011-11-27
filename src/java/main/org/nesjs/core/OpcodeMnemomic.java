package org.nesjs.core;

// All opcode mnemonics implemented in the CPU emulator
public enum OpcodeMnemomic
{
    ADC, ALR, ANC, AND, ARR, ASL, ATX, AXS, BCC, BCS, BEQ, BIT, BMI, BNE, BPL, BRK,
    BVC, BVS, CLC, CLD, CLI, CLV, CMP, CPX, CPY, DCP, DEC, DEX, DEY, EOR, INC, INX,
    INY, ISB, JMP, JSR, LAX, LDA, LDX, LDY, LSR, NOP, ORA, PHA, PHP, PLA, PLP, RLA,
    ROL, ROR, RRA, RTI, RTS, SAX, SBC, SEC, SED, SEI, SLO, SRE, STA, STX, STY, SXA,
    SYA, TAX, TAY, TSX, TXA, TXS, TYA;
}
