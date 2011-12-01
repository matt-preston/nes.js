package org.nesjs.core;



/**
 * MOS Technology 6502 core
 *
 * Custom sound hardware and a restricted DMA controller on-die
 * 
 * Runs at 1.79 MHz
 * 8 bit, little-endian
 * 16 bit address bus
 *
 * Registers a: accumulator
 *           x, y: index registers
 *           p: processor status
 */
public final class MOS6502
{
    private static final int NMI_VECTOR   = 0xFFFA;
    private static final int RESET_VECTOR = 0xFFFC;
    private static final int IRQ_VECTOR   = 0xFFFE;
    
    private int a, x, y;
    private int s, pc;    
    private int carry, not_zero, interruptDisable, decimal, overflow, negative;
    private boolean resetIRQ;
    
    private int cycles;
    
    private Memory memory;
    private APU apu;

    public MOS6502(Memory aMemory)
    {
    	memory = aMemory;    	
    	memory.resetLowMemory();
    	
    	apu = new APU();    	
    	memory.setAPU(apu);
    	
    	a = 0;
        x = 0;
        y = 0;

        // flags
        carry            = 0;
        not_zero         = 1;
        interruptDisable = 1;
        decimal          = 0;
        overflow         = 0;
        negative         = 0;
        
        resetIRQ = false;
        
        s = 0x01FF - 2;
        pc = readWord(RESET_VECTOR);
    }
    
    public final void reset()
    {
        interruptDisable = 1;
        resetIRQ = true;       
    }
    
    public final int getRegisterA()
    {
        return a;
    }
    
    public final int getRegisterX()
    {
        return x;
    }
    
    public final int getRegisterY()
    {
        return y;
    }
    
    /**
     * Status Register   7  6  5  4  3  2  1  0
     *                   N  V  U  B  D  I  Z  C
     */
    public final int getRegisterP()
    {
        return getRegisterP(0);
    }
    
    public final int getRegisterS()
    {
        return s;
    }
    
    public final int getRegisterPC()
    {
        return pc;
    }
    
    public final void setRegisterPC(int aPC)
    {
        pc = aPC;
    }
    
    public final int getCycles()
    {
        return cycles;
    }
    
    public final void step()
    {
        execute(1);
    }
    
    public final int execute(int aNumberOfCycles)
    {
        int _clocksRemain = aNumberOfCycles;

        while(_clocksRemain > 0)
        {
            if(resetIRQ)
            {
                resetIRQ = false;
                
                pc = readWord(RESET_VECTOR);
                
                /**
                 * On reset, pc (2 bytes) and p are pushed onto the stack.  But the values should not actually
                 * be written to memory.
                 */
                s -= 3;
            }
            
            _clocksRemain--;
            cycles = 0;
            
            int _opcode = memory.readByte(pc++);

            switch(_opcode)
            {
                case 0x69: opcode_ADC(immediate_R()); addCycles(2); break;
                case 0x65: opcode_ADC(zeroPage_R()); addCycles(3); break;
                case 0x75: opcode_ADC(zeroPageX_R()); addCycles(4); break;
                case 0x6D: opcode_ADC(absolute_R()); addCycles(4); break;
                case 0x7D: opcode_ADC(absoluteX_R()); addCycles(4); break;
                case 0x79: opcode_ADC(absoluteY_R()); addCycles(4); break;
                case 0x61: opcode_ADC(indirectX_R()); addCycles(6); break;
                case 0x71: opcode_ADC(indirectY_R()); addCycles(5); break;
                case 0x4B: opcode_ALR(immediate_R()); addCycles(2); break; // Unofficial
                case 0x0B:
                case 0x2B: opcode_ANC(immediate_R()); addCycles(2); break; // Unofficial
                case 0x29: opcode_AND(immediate_R()); addCycles(2); break;
                case 0x25: opcode_AND(zeroPage_R()); addCycles(3); break;
                case 0x35: opcode_AND(zeroPageX_R()); addCycles(4); break;
                case 0x2D: opcode_AND(absolute_R()); addCycles(4); break;
                case 0x3D: opcode_AND(absoluteX_R()); addCycles(4); break;
                case 0x39: opcode_AND(absoluteY_R()); addCycles(4); break;
                case 0x21: opcode_AND(indirectX_R()); addCycles(6); break;
                case 0x31: opcode_AND(indirectY_R()); addCycles(5); break;
                case 0x6B: opcode_ARR(immediate_R()); addCycles(2); break; // Unofficial
                case 0x0A: opcode_ASL_accumulator(); addCycles(2); break;
                case 0x06: opcode_ASL(zeroPage_RMW()); addCycles(5); break;
                case 0x16: opcode_ASL(zeroPageX_RMW()); addCycles(6); break;
                case 0x0E: opcode_ASL(absolute_RMW()); addCycles(6); break;
                case 0x1E: opcode_ASL(absoluteX_RMW()); addCycles(7); break;
                case 0xAB: opcode_ATX(immediate_R()); addCycles(2); break; // Unofficial
                case 0xCB: opcode_AXS(immediate_R()); addCycles(2); break; // Unofficial
                case 0x90: opcode_BCC_relative(); addCycles(2); break;
                case 0xB0: opcode_BCS_relative(); addCycles(2); break;
                case 0xF0: opcode_BEQ_relative(); addCycles(2); break;
                case 0x24: opcode_BIT(zeroPage_R()); addCycles(3); break;
                case 0x2C: opcode_BIT(absolute_R()); addCycles(4); break;
                case 0x30: opcode_BMI_relative(); addCycles(2); break;
                case 0xD0: opcode_BNE_relative(); addCycles(2); break;
                case 0x10: opcode_BPL_relative(); addCycles(2); break;
                case 0x00: opcode_BRK_implied(); addCycles(7); break;
                case 0x50: opcode_BVC_relative(); addCycles(2); break;
                case 0x70: opcode_BVS_relative(); addCycles(2); break;
                case 0x18: opcode_CLC_implied(); addCycles(2); break;
                case 0xD8: opcode_CLD_implied(); addCycles(2); break;
                case 0x58: opcode_CLI_implied(); addCycles(2); break;
                case 0xB8: opcode_CLV_implied(); addCycles(2); break;
                case 0xC9: opcode_CMP(immediate_R()); addCycles(2); break;
                case 0xC5: opcode_CMP(zeroPage_R()); addCycles(3); break;
                case 0xD5: opcode_CMP(zeroPageX_R()); addCycles(4); break;
                case 0xCD: opcode_CMP(absolute_R()); addCycles(4); break;
                case 0xDD: opcode_CMP(absoluteX_R()); addCycles(4); break;
                case 0xD9: opcode_CMP(absoluteY_R()); addCycles(4); break;
                case 0xC1: opcode_CMP(indirectX_R()); addCycles(6); break;
                case 0xD1: opcode_CMP(indirectY_R()); addCycles(5); break;
                case 0xE0: opcode_CPX(immediate_R()); addCycles(2); break;
                case 0xE4: opcode_CPX(zeroPage_R()); addCycles(3); break;
                case 0xEC: opcode_CPX(absolute_R()); addCycles(4); break;
                case 0xC0: opcode_CPY(immediate_R()); addCycles(2); break;
                case 0xC4: opcode_CPY(zeroPage_R()); addCycles(3); break;
                case 0xCC: opcode_CPY(absolute_R()); addCycles(4); break;
                case 0xC7: opcode_DCP(zeroPage_RMW()); addCycles(5); break; // Unofficial
                case 0xD7: opcode_DCP(zeroPageX_RMW()); addCycles(6); break; // Unofficial
                case 0xCF: opcode_DCP(absolute_RMW()); addCycles(6); break; // Unofficial
                case 0xDF: opcode_DCP(absoluteX_RMW()); addCycles(7); break; // Unofficial
                case 0xDB: opcode_DCP(absoluteY_RMW()); addCycles(7); break; // Unofficial
                case 0xC3: opcode_DCP(indirectX_RMW()); addCycles(8); break; // Unofficial
                case 0xD3: opcode_DCP(indirectY_RMW()); addCycles(8); break; // Unofficial
                case 0xC6: opcode_DEC(zeroPage_RMW()); addCycles(5); break;
                case 0xD6: opcode_DEC(zeroPageX_RMW()); addCycles(6); break;
                case 0xCE: opcode_DEC(absolute_RMW()); addCycles(6); break;
                case 0xDE: opcode_DEC(absoluteX_RMW()); addCycles(7); break;
                case 0xCA: opcode_DEX_implied(); addCycles(2); break;
                case 0x88: opcode_DEY_implied(); addCycles(2); break;
                case 0x49: opcode_EOR(immediate_R()); addCycles(2); break;
                case 0x45: opcode_EOR(zeroPage_R()); addCycles(3); break;
                case 0x55: opcode_EOR(zeroPageX_R()); addCycles(4); break;
                case 0x4D: opcode_EOR(absolute_R()); addCycles(4); break;
                case 0x5D: opcode_EOR(absoluteX_R()); addCycles(4); break;
                case 0x59: opcode_EOR(absoluteY_R()); addCycles(4); break;
                case 0x41: opcode_EOR(indirectX_R()); addCycles(6); break;
                case 0x51: opcode_EOR(indirectY_R()); addCycles(5); break;
                case 0xE6: opcode_INC(zeroPage_RMW()); addCycles(5); break;
                case 0xF6: opcode_INC(zeroPageX_RMW()); addCycles(6); break;
                case 0xEE: opcode_INC(absolute_RMW()); addCycles(6); break;
                case 0xFE: opcode_INC(absoluteX_RMW()); addCycles(7); break;
                case 0xE8: opcode_INX_implied(); addCycles(2); break;
                case 0xC8: opcode_INY_implied(); addCycles(2); break;
                case 0xE7: opcode_ISB(zeroPage_RMW()); addCycles(5); break; // Unofficial
                case 0xF7: opcode_ISB(zeroPageX_RMW()); addCycles(6); break; // Unofficial
                case 0xEF: opcode_ISB(absolute_RMW()); addCycles(6); break; // Unofficial
                case 0xFF: opcode_ISB(absoluteX_RMW()); addCycles(7); break; // Unofficial
                case 0xFB: opcode_ISB(absoluteY_RMW()); addCycles(7); break; // Unofficial
                case 0xE3: opcode_ISB(indirectX_RMW()); addCycles(8); break; // Unofficial
                case 0xF3: opcode_ISB(indirectY_RMW()); addCycles(8); break; // Unofficial
                case 0x4C: opcode_JMP(absolute_R()); addCycles(3); break;
                case 0x6C: opcode_JMP(indirect()); addCycles(5); break;
                case 0x20: opcode_JSR(absolute_R()); addCycles(6); break;
                case 0xA7: opcode_LAX(zeroPage_R()); addCycles(3); break; // Unofficial
                case 0xB7: opcode_LAX(zeroPageY_R()); addCycles(4); break; // Unofficial
                case 0xAF: opcode_LAX(absolute_R()); addCycles(4); break; // Unofficial
                case 0xBF: opcode_LAX(absoluteY_R()); addCycles(4); break; // Unofficial
                case 0xA3: opcode_LAX(indirectX_R()); addCycles(6); break; // Unofficial
                case 0xB3: opcode_LAX(indirectY_R()); addCycles(5); break; // Unofficial
                case 0xA9: opcode_LDA(immediate_R()); addCycles(2); break;
                case 0xA5: opcode_LDA(zeroPage_R()); addCycles(3); break;
                case 0xB5: opcode_LDA(zeroPageX_R()); addCycles(4); break;
                case 0xAD: opcode_LDA(absolute_R()); addCycles(4); break;
                case 0xBD: opcode_LDA(absoluteX_R()); addCycles(4); break;
                case 0xB9: opcode_LDA(absoluteY_R()); addCycles(4); break;
                case 0xA1: opcode_LDA(indirectX_R()); addCycles(6); break;
                case 0xB1: opcode_LDA(indirectY_R()); addCycles(5); break;
                case 0xA2: opcode_LDX(immediate_R()); addCycles(2); break;
                case 0xA6: opcode_LDX(zeroPage_R()); addCycles(3); break;
                case 0xB6: opcode_LDX(zeroPageY_R()); addCycles(4); break;
                case 0xAE: opcode_LDX(absolute_R()); addCycles(4); break;
                case 0xBE: opcode_LDX(absoluteY_R()); addCycles(4); break;
                case 0xA0: opcode_LDY(immediate_R()); addCycles(2); break;
                case 0xA4: opcode_LDY(zeroPage_R()); addCycles(3); break;
                case 0xB4: opcode_LDY(zeroPageX_R()); addCycles(4); break;
                case 0xAC: opcode_LDY(absolute_R()); addCycles(4); break;
                case 0xBC: opcode_LDY(absoluteX_R()); addCycles(4); break;
                case 0x4A: opcode_LSR_accumulator(); addCycles(2); break;
                case 0x46: opcode_LSR(zeroPage_RMW()); addCycles(5); break;
                case 0x56: opcode_LSR(zeroPageX_RMW()); addCycles(6); break;
                case 0x4E: opcode_LSR(absolute_RMW()); addCycles(6); break;
                case 0x5E: opcode_LSR(absoluteX_RMW()); addCycles(7); break;
                case 0xEA:
                case 0x1A:
                case 0x3A:
                case 0x5A:
                case 0x7A:
                case 0xDA:
                case 0xFA: opcode_NOP_implied(); addCycles(2); break; // Unofficial
                case 0x80:
                case 0x82:
                case 0x89:
                case 0xC2:
                case 0xE2: opcode_NOP(immediate_R()); addCycles(2); break; // Unofficial
                case 0x04:
                case 0x44:
                case 0x64: opcode_NOP(zeroPage_R()); addCycles(3); break; // Unofficial
                case 0x0C: opcode_NOP(absolute_R()); addCycles(4); break; // Unofficial
                case 0x1C:
                case 0x3C:
                case 0x5C:
                case 0x7C:
                case 0xDC:
                case 0xFC: opcode_NOP(absoluteX_R()); addCycles(4); break; // Unofficial
                case 0x14:
                case 0x34:
                case 0x54:
                case 0x74:
                case 0xD4:
                case 0xF4: opcode_NOP(zeroPageX_R()); addCycles(4); break; // Unofficial
                case 0x09: opcode_ORA(immediate_R()); addCycles(2); break;
                case 0x05: opcode_ORA(zeroPage_R()); addCycles(3); break;
                case 0x15: opcode_ORA(zeroPageX_R()); addCycles(4); break;
                case 0x0D: opcode_ORA(absolute_R()); addCycles(4); break;
                case 0x1D: opcode_ORA(absoluteX_R()); addCycles(4); break;
                case 0x19: opcode_ORA(absoluteY_R()); addCycles(4); break;
                case 0x01: opcode_ORA(indirectX_R()); addCycles(6); break;
                case 0x11: opcode_ORA(indirectY_R()); addCycles(5); break;
                case 0x48: opcode_PHA_implied(); addCycles(3); break;
                case 0x08: opcode_PHP_implied(); addCycles(3); break;
                case 0x68: opcode_PLA_implied(); addCycles(4); break;
                case 0x28: opcode_PLP_implied(); addCycles(4); break;
                case 0x27: opcode_RLA(zeroPage_RMW()); addCycles(5); break; // Unofficial
                case 0x37: opcode_RLA(zeroPageX_RMW()); addCycles(6); break; // Unofficial
                case 0x2F: opcode_RLA(absolute_RMW()); addCycles(6); break; // Unofficial
                case 0x3F: opcode_RLA(absoluteX_RMW()); addCycles(7); break; // Unofficial
                case 0x3B: opcode_RLA(absoluteY_RMW()); addCycles(7); break; // Unofficial
                case 0x23: opcode_RLA(indirectX_RMW()); addCycles(8); break; // Unofficial
                case 0x33: opcode_RLA(indirectY_RMW()); addCycles(8); break; // Unofficial
                case 0x2A: opcode_ROL_accumulator(); addCycles(2); break;
                case 0x26: opcode_ROL(zeroPage_RMW()); addCycles(5); break;
                case 0x36: opcode_ROL(zeroPageX_RMW()); addCycles(6); break;
                case 0x2E: opcode_ROL(absolute_RMW()); addCycles(6); break;
                case 0x3E: opcode_ROL(absoluteX_RMW()); addCycles(7); break;
                case 0x6A: opcode_ROR_accumulator(); addCycles(2); break;
                case 0x66: opcode_ROR(zeroPage_RMW()); addCycles(5); break;
                case 0x76: opcode_ROR(zeroPageX_RMW()); addCycles(6); break;
                case 0x6E: opcode_ROR(absolute_RMW()); addCycles(6); break;
                case 0x7E: opcode_ROR(absoluteX_RMW()); addCycles(7); break;
                case 0x67: opcode_RRA(zeroPage_RMW()); addCycles(5); break; // Unofficial
                case 0x77: opcode_RRA(zeroPageX_RMW()); addCycles(6); break; // Unofficial
                case 0x6F: opcode_RRA(absolute_RMW()); addCycles(6); break; // Unofficial
                case 0x7F: opcode_RRA(absoluteX_RMW()); addCycles(7); break; // Unofficial
                case 0x7B: opcode_RRA(absoluteY_RMW()); addCycles(7); break; // Unofficial
                case 0x63: opcode_RRA(indirectX_RMW()); addCycles(8); break; // Unofficial
                case 0x73: opcode_RRA(indirectY_RMW()); addCycles(8); break; // Unofficial
                case 0x40: opcode_RTI_implied(); addCycles(6); break;
                case 0x60: opcode_RTS_implied(); addCycles(6); break;
                case 0x87: opcode_SAX(zeroPage_W()); addCycles(3); break; // Unofficial
                case 0x97: opcode_SAX(zeroPageY_W()); addCycles(4); break; // Unofficial
                case 0x8F: opcode_SAX(absolute_W()); addCycles(4); break; // Unofficial
                case 0x83: opcode_SAX(indirectX_W()); addCycles(6); break; // Unofficial
                case 0xE9:
                case 0xEB: opcode_SBC(immediate_R()); addCycles(2); break; // Unofficial
                case 0xE5: opcode_SBC(zeroPage_R()); addCycles(3); break;
                case 0xF5: opcode_SBC(zeroPageX_R()); addCycles(4); break;
                case 0xED: opcode_SBC(absolute_R()); addCycles(4); break;
                case 0xFD: opcode_SBC(absoluteX_R()); addCycles(4); break;
                case 0xF9: opcode_SBC(absoluteY_R()); addCycles(4); break;
                case 0xE1: opcode_SBC(indirectX_R()); addCycles(6); break;
                case 0xF1: opcode_SBC(indirectY_R()); addCycles(5); break;
                case 0x38: opcode_SEC_implied(); addCycles(2); break;
                case 0xF8: opcode_SED_implied(); addCycles(2); break;
                case 0x78: opcode_SEI_implied(); addCycles(2); break;
                case 0x07: opcode_SLO(zeroPage_RMW()); addCycles(5); break; // Unofficial
                case 0x17: opcode_SLO(zeroPageX_RMW()); addCycles(6); break; // Unofficial
                case 0x0F: opcode_SLO(absolute_RMW()); addCycles(6); break; // Unofficial
                case 0x1F: opcode_SLO(absoluteX_RMW()); addCycles(7); break; // Unofficial
                case 0x1B: opcode_SLO(absoluteY_RMW()); addCycles(7); break; // Unofficial
                case 0x03: opcode_SLO(indirectX_RMW()); addCycles(8); break; // Unofficial
                case 0x13: opcode_SLO(indirectY_RMW()); addCycles(8); break; // Unofficial
                case 0x47: opcode_SRE(zeroPage_RMW()); addCycles(5); break; // Unofficial
                case 0x57: opcode_SRE(zeroPageX_RMW()); addCycles(6); break; // Unofficial
                case 0x4F: opcode_SRE(absolute_RMW()); addCycles(6); break; // Unofficial
                case 0x5F: opcode_SRE(absoluteX_RMW()); addCycles(7); break; // Unofficial
                case 0x5B: opcode_SRE(absoluteY_RMW()); addCycles(7); break; // Unofficial
                case 0x43: opcode_SRE(indirectX_RMW()); addCycles(8); break; // Unofficial
                case 0x53: opcode_SRE(indirectY_RMW()); addCycles(8); break; // Unofficial
                case 0x85: opcode_STA(zeroPage_W()); addCycles(3); break;
                case 0x95: opcode_STA(zeroPageX_W()); addCycles(4); break;
                case 0x8D: opcode_STA(absolute_W()); addCycles(4); break;
                case 0x9D: opcode_STA(absoluteX_W()); addCycles(5); break;
                case 0x99: opcode_STA(absoluteY_W()); addCycles(5); break;
                case 0x81: opcode_STA(indirectX_W()); addCycles(6); break;
                case 0x91: opcode_STA(indirectY_W()); addCycles(6); break;
                case 0x86: opcode_STX(zeroPage_W()); addCycles(3); break;
                case 0x96: opcode_STX(zeroPageY_W()); addCycles(4); break;
                case 0x8E: opcode_STX(absolute_W()); addCycles(4); break;
                case 0x84: opcode_STY(zeroPage_W()); addCycles(3); break;
                case 0x94: opcode_STY(zeroPageX_W()); addCycles(4); break;
                case 0x8C: opcode_STY(absolute_W()); addCycles(4); break;
                case 0x9E: opcode_SXA(absoluteY_W()); addCycles(5); break; // Unofficial
                case 0x9C: opcode_SYA(absoluteX_W()); addCycles(5); break; // Unofficial
                case 0xAA: opcode_TAX_implied(); addCycles(2); break;
                case 0xA8: opcode_TAY_implied(); addCycles(2); break;
                case 0xBA: opcode_TSX_implied(); addCycles(2); break;
                case 0x8A: opcode_TXA_implied(); addCycles(2); break;
                case 0x9A: opcode_TXS_implied(); addCycles(2); break;
                case 0x98: opcode_TYA_implied(); addCycles(2); break;

                default: throw new RuntimeException("Unhandled opcode [" + Utils.toHexString(_opcode) + "], at [" + Utils.toHexString(pc - 1) + "]");
            }
            
            // Mask to 16 bit
            pc &= 0xFFFF;
            
            assert a >= 0 && a <= 0xFF        : "A out of bounds";
            assert x >= 0 && x <= 0xFF        : "X out of bounds";
            assert y >= 0 && y <= 0xFF        : "Y out of bounds";
            assert s >= 0x0100 && s <= 0x01FF : "S out of bounds";
            
            apu.clock(cycles);
        }
        
        return _clocksRemain;
    }
    
//-------------------------------------------------------------
// Processor status flags
//-------------------------------------------------------------     
    
    private final boolean isZeroFlagSet()
    {
        return not_zero == 0;
    }
    
    private final boolean isCarryFlagSet()
    {
        return carry > 0;
    }
    
    private final boolean isNegativeFlagSet()
    {
        return negative > 0;
    }
    
    private final boolean isOverflowFlagSet()
    {
        return overflow > 0;
    }
    
    private final void setNZFlag(int aValue)
    {
        negative = (aValue >> 7) & 1;
        not_zero = aValue & 0xFF;
    }
    
    public final int getRegisterP(int aBRKValue)
    {
        assert aBRKValue == 0 || aBRKValue == 1;
        
        int _zero = isZeroFlagSet() ? 1 : 0;
        return carry << 0 | (_zero << 1) | (interruptDisable << 2) | (decimal << 3) | (aBRKValue << 4) | (1 << 5) | (overflow << 6) | (negative << 7);
    }
    
//-------------------------------------------------------------
// Addressing
//-------------------------------------------------------------    
    
    private final int immediate_R()
    {
        return pc++;
    }
    
// Zero page
    
    private final int zeroPage_R()
    {        
        return memory.readByte(pc++);
    }
    
    private final int zeroPage_W()
    {        
        return zeroPage_R();
    }

    private final int zeroPage_RMW()
    {        
        return zeroPage_R();
    }
    
// Zero page X
    
    private final int zeroPageX_R()
    {
        return zeroPageIndexed(x);        
    }
    
    private final int zeroPageX_W()
    {
        return zeroPageX_R();        
    }

    private final int zeroPageX_RMW()
    {
        return zeroPageX_R();        
    }
    
// Zero page Y    
    
    private final int zeroPageY_R()
    {
        return zeroPageIndexed(y);        
    }
    
    private final int zeroPageY_W()
    {
        return zeroPageY_R();        
    }
    
// Zero page Indexed helper    
    
    private final int zeroPageIndexed(int aRegister)
    {
        return (memory.readByte(pc++) + aRegister) & 0xFF;        
    } 

// Relative    
    
    private final int relative()
    {
        return pc++; 
    }

// Absolute    
    
    private final int absolute_R()
    {
        int _result = readWord(pc);
        pc += 2;
        return _result;
    }
    
    private final int absolute_W()
    {
        return absolute_R();
    }

    private final int absolute_RMW()
    {
        return absolute_R();
    }
    
// Absolute X
    
    private final int absoluteX_R()
    {
        return absoluteIndexed(x, true);
    }

    private final int absoluteX_W()
    {
        return absoluteIndexed(x, false);
    }
    
    private final int absoluteX_RMW()
    {
        return absoluteX_W();
    }
    
// Absolute Y  

    private final int absoluteY_R()
    {
        return absoluteIndexed(y, true);
    }
    
    private final int absoluteY_W()
    {
        return absoluteIndexed(y, false);
    }

    private final int absoluteY_RMW()
    {
        return absoluteY_W();
    }
    
// Absolute indexed helper    
    
    private final int absoluteIndexed(int aRegister, boolean isPageBoundaryCyclePenalty)
    {
        int _temp = readWord(pc);
        int _result = _temp + aRegister;
        
        if(isPageBoundaryCyclePenalty && isPageCrossed(_temp,_result))
        {
            addCycles(1);
        }
        
        pc += 2;
        return _result & 0xFFFF;
    }

// Indirect    
    
    private final int indirect()
    {
        int _address = readWord(pc++);
        
        if((_address & 0x00FF) == 0xFF)
        {
            // Page boundary bug
            int _msb = _address - 0xFF;            
            return readWord(_address, _msb);               
        }
        else
        {
            return readWord(_address);    
        }
    }    

// Indirect X
    
    private final int indirectX_R()
    {
        int _address = (memory.readByte(pc++) + x) & 0xFF;       
        return readWordZeroPageWrap(_address);
    }
    
    private final int indirectX_W()
    {
        return indirectX_R();
    }
    
    private final int indirectX_RMW()
    {
        return indirectX_R();
    }
    
// Indirect Y
    
    private final int indirectY_R()
    {
        return indirectY(true);
    }
    
    private final int indirectY_W()
    {
        return indirectY(false);
    }
    
    private final int indirectY_RMW()
    {
        return indirectY_W();
    }
    
    private final int indirectY(boolean isPageBoundaryCyclePenalty)
    {
        int _address = memory.readByte(pc++);
        int _temp = readWordZeroPageWrap(_address);        
        int _result = _temp + y;
        
        if(isPageBoundaryCyclePenalty && isPageCrossed(_temp, _result))
        {
            addCycles(1);            
        }
        
        return _result & 0xFFFF;
    }
    
//-------------------------------------------------------------
// Stack
//-------------------------------------------------------------    

    private final void push(int aByte)
    {
        memory.writeByte(aByte, s);
        s = 0x0100 | (--s & 0xFF);
    }
    
    private final int pop()
    {
        s = 0x0100 | (++s & 0xFF);
        return memory.readByte(s);
    }
    
    private final void pushWord(int aWord)
    {
        push((aWord >> 8) & 255);
        push(aWord & 255);
    }
    
    private final int popWord()
    {
        int _byte1 = pop();
        return (pop() << 8) | _byte1;
    }

//-------------------------------------------------------------
// Utils
//-------------------------------------------------------------    
    
    private final int readWord(int anAddress)
    {
        return readWord(anAddress, anAddress + 1);
    }
    
    private final int readWordZeroPageWrap(int anAddress)
    {
        return readWord(anAddress, (anAddress + 1) & 0xFF);
    }
    
    private final int readWord(int anAddress, int aSecondAddress)
    {
        return memory.readByte(anAddress) | (memory.readByte(aSecondAddress) << 8);
    }
    
    private final void branchOnCondition(boolean isBranch)
    {        
        int _address = relative();
        if(isBranch)
        {
            addCycles(1);
            
            int _value = memory.readByte(_address);
            int _signedByte = _value < 0x80 ? _value : _value - 256;
            
            int _temp = pc;
            pc += _signedByte;
            
            if(isPageCrossed(_temp, pc))
            {
                addCycles(1);
            }            
        }
    }
    
    private final void addCycles(int aCycles)
    {
        cycles += aCycles;
    }
    
    public final boolean isPageCrossed(int anAddress1, int anAddress) 
    { 
        //return ((anAddress1 ^ anAddress) & 0x0100) != 0;

        return (((anAddress1 ^ anAddress) & 0xFF00) != 0);
    }
    
//-------------------------------------------------------------
// Opcodes
//-------------------------------------------------------------    
    
    // Add with Carry
    private void opcode_ADC(int anAddress)
    {
        int _value = memory.readByte(anAddress);
        int _temp = a + _value + carry;
        
        overflow = ((!(((a ^ _value) & 0x80) != 0) && (((a ^ _temp) & 0x80)) != 0) ? 1 : 0);
        a = _temp & 0xFF;
        
        carry = _temp > 0xFF ? 1 : 0;
        setNZFlag(_temp);
    }
    
    // Equivalent to AND #i then LSR A.
    private final void opcode_ALR(int anAddress)
    {
        opcode_AND(anAddress);
        opcode_LSR_accumulator();
    }
 
    // AND followed by Copy N (bit 7) to C.
    private final void opcode_ANC(int anAddress)
    {
        opcode_AND(anAddress); 

        carry = (a >> 7) & 1;
    }
    
    // Logical AND
    private void opcode_AND(int anAddress)
    {
        a &= memory.readByte(anAddress);
        setNZFlag(a);
    }

    // AND byte with accumulator, then rotate one bit right in accumulator
    private final void opcode_ARR(int anAddress)
    {
        opcode_AND(anAddress);
        opcode_ROR_accumulator();
        
        /**
         * Adapted from http://nesdev.parodius.com/bbs/viewtopic.php?t=3831&postdays=0&postorder=asc&start=15
         */
        switch (a & 0x60) 
        { 
            case 0x00: carry = 0; overflow = 0; break; // bit 5 and bit 6 clear
            case 0x20: carry = 0; overflow = 1; break; // bit 5 set, bit 6 clear
            case 0x40: carry = 1; overflow = 1; break; // bit 6 set, bit 5 clear
            case 0x60: carry = 1; overflow = 0; break; // bit 5 and bit 6 set
        }
    }    
    
    // Arithmetic Shift Left
    private void opcode_ASL_accumulator()
    {
        carry = (a >> 7) & 1;
        a = (a << 1) & 0xFF;
        
        setNZFlag(a);
    }

    // Arithmetic Shift Left
    private void opcode_ASL(int anAddress)
    {
        int _value = memory.readByte(anAddress);
        
        carry = (_value >> 7) & 1;
        _value = (_value << 1) & 0xFF;
        
        memory.writeByte(_value, anAddress);
        
        setNZFlag(_value);
    }
 
    // AND byte with accumulator, then transfer accumulator to X register.
    private final void opcode_ATX(int anAddress)
    {
        a = 0xFF; // TODO not sure about this, maybe |= value from anAddress? 
        opcode_AND(anAddress);
        x = a;
    }
    
    // AND X register with accumulator and store result in X register, then subtract byte from X register (without borrow).
    private final void opcode_AXS(int anAddress)
    {
        int _value = memory.readByte(anAddress);
        int _temp = (a & x) - _value;
        
        carry = _temp < 0 ? 0 : 1;
        
        x = _temp & 0xFF;
        setNZFlag(x);
    }
    
    // Branch if Carry Clear
    private void opcode_BCC_relative()
    {
        branchOnCondition(!isCarryFlagSet());
    }
 
    // Branch if Carry Set
    private void opcode_BCS_relative()
    {
        branchOnCondition(isCarryFlagSet());
    }

    // Branch if Equal
    private void opcode_BEQ_relative()
    {
        branchOnCondition(isZeroFlagSet());
    }

    // Bit Test
    private void opcode_BIT(int anAddress)
    {
        int _value = memory.readByte(anAddress);
        
        negative = (_value >> 7) & 1;
        overflow = (_value >> 6) & 1;
        _value &= a;
        
        not_zero = _value;
    }

    // Branch if Minus
    private void opcode_BMI_relative()
    {
        branchOnCondition(isNegativeFlagSet());
    }

    // Branch if Not Equal
    private void opcode_BNE_relative()
    {
        branchOnCondition(!isZeroFlagSet());
    }

    // Branch if Positive
    private void opcode_BPL_relative()
    {
        branchOnCondition(!isNegativeFlagSet());
    }

    // Force Interrupt
    private void opcode_BRK_implied()
    {
        pushWord(pc + 1);
        
        opcode_PHP_implied();
        opcode_SEI_implied();
        opcode_JMP(readWord(IRQ_VECTOR));        
    }

    // Branch if Overflow Clear
    private void opcode_BVC_relative()
    {
        branchOnCondition(!isOverflowFlagSet());
    }

    // Branch if Overflow Set
    private void opcode_BVS_relative()
    {
        branchOnCondition(isOverflowFlagSet());
    }
    
    // Clear Carry Flag
    private void opcode_CLC_implied()
    {
        carry = 0;
    }

    // Clear Decimal Mode
    private void opcode_CLD_implied()
    {
        decimal = 0;
    }

    // Clear Interrupt Disable
    private void opcode_CLI_implied()
    {
        interruptDisable = 0;
    }

    // Clear Overflow Flag
    private void opcode_CLV_implied()
    {
        overflow = 0;
    }

    // Compare
    private void opcode_CMP(int anAddress)
    {
        int _temp = a - memory.readByte(anAddress);
        
        carry = (_temp >= 0 ? 1:0);
        setNZFlag(_temp);
    }

    // Compare X Register
    private void opcode_CPX(int anAddress)
    {
        int _temp = x - memory.readByte(anAddress);
        
        carry = (_temp >= 0 ? 1:0);
        setNZFlag(_temp);
    }

    // Compare Y Register
    private void opcode_CPY(int anAddress)
    {
        int _temp = y - memory.readByte(anAddress);
        
        carry = (_temp >= 0 ? 1:0);
        setNZFlag(_temp);
    }
    
    // DEC value then CMP value
    private void opcode_DCP(int anAddress)
    {
        opcode_DEC(anAddress);
        opcode_CMP(anAddress);
    }

    // Decrement Memory
    private void opcode_DEC(int anAddress)
    {
        int _value = (memory.readByte(anAddress) - 1) & 0xFF;
        memory.writeByte(_value, anAddress);
        
        setNZFlag(_value);
    }

    // Decrement X Register
    private void opcode_DEX_implied()
    {
        x = (x - 1) & 0xFF;
        setNZFlag(x);
    }

    // Decrement Y Register
    private void opcode_DEY_implied()
    {
        y = (y - 1) & 0xFF;
        setNZFlag(y);
    }

    // Exclusive OR
    private void opcode_EOR(int anAddress)
    {
        a ^=  memory.readByte(anAddress);
        setNZFlag(a);
    }

    // Increment Memory
    private void opcode_INC(int anAddress)
    {    
        int _value = (memory.readByte(anAddress) + 1) & 0xFF;
        memory.writeByte(_value, anAddress);
        
        setNZFlag(_value);
    }

    // Increment X Register
    private void opcode_INX_implied()
    {
        x = (x + 1) & 0xFF;
        setNZFlag(x);
    }

    // Increment Y Register
    private void opcode_INY_implied()
    {
        y = (y + 1) & 0xFF;
        setNZFlag(y);
    }

    // INC value then SBC value
    private void opcode_ISB(int anAddress)
    {
        opcode_INC(anAddress);
        opcode_SBC(anAddress);
    }
    
    // Jump to target address
    private void opcode_JMP(int anAddress)
    {
        pc = anAddress;
    }

    // Jump to subroutine
    private void opcode_JSR(int anAddress)
    {
        pushWord(pc - 1);        
        pc = anAddress;
    }

    // Load Accumulator and X with memory
    private void opcode_LAX(int anAddress)
    {
        opcode_LDA(anAddress);
        opcode_LDX(anAddress);
    }
    
    // Load Accumulator
    private void opcode_LDA(int anAddress)
    {
        a = memory.readByte(anAddress);
        setNZFlag(a);
    }

    // Load X with memory
    private void opcode_LDX(int anAddress)
    {
        x = memory.readByte(anAddress);
        setNZFlag(x);
    }

    // Load Y Register
    private void opcode_LDY(int anAddress)
    {
        y = memory.readByte(anAddress);
        setNZFlag(y);
    }

    // Logical Shift Right
    private void opcode_LSR_accumulator()
    {
        carry = a & 1; // old bit 0       
        a >>= 1;
        
        not_zero = a;
        negative = 0;
    }

    // Logical Shift Right
    private void opcode_LSR(int anAddress)
    {
        int _value = memory.readByte(anAddress);
        
        carry = _value & 1; // old bit 0       
        _value >>= 1;
        
        memory.writeByte(_value, anAddress);
        
        not_zero = _value;
        negative = 0;
    }

    // No operation
    private void opcode_NOP_implied()
    {        
    }
    
    // No operation
    private void opcode_NOP(int anAddress)
    {
    }
    
    // Logical Inclusive OR
    private void opcode_ORA(int anAddress)
    {
        a |=  memory.readByte(anAddress);
        setNZFlag(a);
    }

    // Push Accumulator
    private void opcode_PHA_implied()
    {
        push(a);
    }

    // Push Processor Status
    private void opcode_PHP_implied()
    {
        push(getRegisterP(1));
    }

    // Pull Accumulator
    private void opcode_PLA_implied()
    {
        a = pop();
        setNZFlag(a);
    }

    // Pull Processor Status
    private void opcode_PLP_implied()
    {
        int _temp = pop();
    	
    	carry            = (_temp) & 1;
        not_zero         = ((_temp >> 1) & 1) == 1 ? 0 : 1;
        interruptDisable = (_temp >> 2) & 1;
        decimal          = (_temp >> 3) & 1; 
        overflow         = (_temp >> 6) & 1;
        negative         = (_temp >> 7) & 1;        
    }

    // ROL value then AND value
    private void opcode_RLA(int anAddress)
    {
        opcode_ROL(anAddress);
        opcode_AND(anAddress);
    }
    
    // Rotate Left
    private void opcode_ROL_accumulator()
    {
		int _add = carry;
		
		carry = (a >> 7) &1;
		
		a = ((a << 1) & 0xFF) + _add;
		
		setNZFlag(a);
    }

    // Rotate Left
    private void opcode_ROL(int anAddress)
    {
        int _value = memory.readByte(anAddress);
        int _add = carry;
        
        carry = (_value >> 7) &1;
        _value = ((_value << 1) & 0xFF) + _add;
        
        memory.writeByte(_value, anAddress);
        
        setNZFlag(_value);
    }

    // Rotate Right
    private void opcode_ROR_accumulator()
    {
        int _add = carry << 7;
    	
		carry = a & 1;
		a = (a >> 1) + _add;
		
		setNZFlag(a);
    }

    // Rotate Right
    private void opcode_ROR(int anAddress)
    {
        int _value = memory.readByte(anAddress);
        int _add = carry << 7;
        
        carry = _value & 1;
        _value = (_value >> 1) + _add;
        
        memory.writeByte(_value, anAddress);
        
        setNZFlag(_value);
    }
    
    // ROR value then ADC value
    private void opcode_RRA(int anAddress)
    {
        opcode_ROR(anAddress);
        opcode_ADC(anAddress);
    }

    // Return from Interrupt
    private void opcode_RTI_implied()
    {
        int _temp = pop();
        
        carry            = (_temp) & 1;
        not_zero         = ((_temp >> 1) & 1) == 1 ? 0 : 1;
        interruptDisable = (_temp >> 2) & 1;
        decimal          = (_temp >> 3) & 1; 
        overflow         = (_temp >> 6) & 1;
        negative         = (_temp >> 7) & 1;
        
        pc = popWord();
    }

    // Return from Subroutine
    private void opcode_RTS_implied()
    {
        pc = popWord() + 1;        
    }

    // Store A and X bitwise
    private void opcode_SAX(int anAddress)
    {
        memory.writeByte(a & x, anAddress);
    }

    // Subtract with Carry
    private void opcode_SBC(int anAddress)
    {
        int _value = memory.readByte(anAddress);
        int _temp = a - _value - (1 - carry);
        
        carry = _temp < 0 ? 0 : 1;
        overflow = ((((a ^ _temp) & 0x80) != 0 && ((a ^ _value) & 0x80) != 0) ? 1 : 0);
        
        setNZFlag(_temp);
        
        a = _temp & 0xFF;
    }

    // Set the carry flag
    private void opcode_SEC_implied()
    {
        carry = 1;
    }

    // Set Decimal Flag
    private void opcode_SED_implied()
    {
        decimal = 1;
    }

    // Set Interrupt Disable
    private void opcode_SEI_implied()
    {
        interruptDisable = 1;
    }

    // ASL value then ORA value
    private void opcode_SLO(int anAddress)
    {
        opcode_ASL(anAddress);
        opcode_ORA(anAddress);
    }
    
    // Equivalent to LSR value then EOR value
    private void opcode_SRE(int anAddress)
    {
        opcode_LSR(anAddress);
        opcode_EOR(anAddress);
    }
    
    // Store Accumulator
    private void opcode_STA(int anAddress)
    {
        memory.writeByte(a, anAddress);        
    }

    // Store X register
    private void opcode_STX(int anAddress)
    {
        memory.writeByte(x, anAddress);
    }

    // Store Y Register
    private void opcode_STY(int anAddress)
    {
        memory.writeByte(y, anAddress);        
    }

    // AND X register with the high byte of the target address of the argument + 1.
    private final void opcode_SXA(int anAddress)
    {
        int _value = (x & ((anAddress >> 8) + 1)) & 0xFF;
        
        /**
         * Ignore the write on page boundary crosses.
         * http://nesdev.parodius.com/bbs/viewtopic.php?t=8107
         */
        if((y + memory.readByte(pc - 2)) <= 0xFF)
        {
            memory.writeByte(_value, anAddress);
        }
    }
    
    // AND Y register with the high byte of the target address of the argument + 1.
    private final void opcode_SYA(int anAddress)
    {
        int _result = (y & (((anAddress) >> 8) + 1)) & 0xFF;
        
        /**
         * Ignore the write on page boundary crosses.
         * http://nesdev.parodius.com/bbs/viewtopic.php?t=8107
         */
        if((x + memory.readByte(pc - 2)) <= 0xFF)
        {
            memory.writeByte(_result, anAddress);
        }
    }

    // Transfer Accumulator to X
    private void opcode_TAX_implied()
    {
        x = a;
        setNZFlag(x);
    }

    // Transfer Accumulator to Y
    private void opcode_TAY_implied()
    {
        y = a;
        setNZFlag(y);
    }

    // Transfer Stack Pointer to X
    private void opcode_TSX_implied()
    {
        x = s & 0xFF; // Only transfer the lower 8 bits        
        setNZFlag(x);
    }
   
    // Transfer X to Accumulator
    private void opcode_TXA_implied()
    {
        a = x;
        setNZFlag(a);
    }

    // Transfer X to Stack Pointer
    private void opcode_TXS_implied()
    {
        s = x | 0x0100;
    }

    // Transfer Y to Accumulator
    private void opcode_TYA_implied()
    {
        a = y;
        setNZFlag(a);
    }    
}
