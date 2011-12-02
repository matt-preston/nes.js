package org.nesjs.core;

/**
 * All instructions implemented in the CPU emulator
 * 
 * Not needed or used at runtime.
 * 
 * @author Matt
 */
public enum Instruction
{
    ADC, ALR, ANC, AND, ANE, ARR, ASL, ATX, AXS, BCC, BCS, BEQ, BIT, BMI, BNE, BPL,
    BRK, BVC, BVS, CLC, CLD, CLI, CLV, CMP, CPX, CPY, DCP, DEC, DEX, DEY, EOR, INC,
    INX, INY, ISB, JMP, JSR, LAS, LAX, LDA, LDX, LDY, LSR, NOP, ORA, PHA, PHP, PLA,
    PLP, RLA, ROL, ROR, RRA, RTI, RTS, SAX, SBC, SEC, SED, SEI, SHA, SHS, SLO, SRE,
    STA, STX, STY, SXA, SYA, TAX, TAY, TSX, TXA, TXS, TYA;
}
