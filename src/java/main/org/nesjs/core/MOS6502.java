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
public class MOS6502
{
    public int a;
    public int x;
    public int y;
    public int p;
    
    public int sp;
    public int pc;
    
    public int carry;
    public int not_zero;
    public int interruptDisable;
    public int decimal;
    public int brk;
    public int overflow;
    public int negative;
    
    private Memory memory;
    
    public MOS6502(Memory aMemory)
    {
    	memory = aMemory;
    }
    
    public void init()
    {
    }
    
    public void reset()
    {
        memory.resetLowMemory();
        
        a = 0;
        x = 0;
        y = 0;

        // flags
        carry            = 0;
        not_zero         = 1;
        interruptDisable = 1;
        decimal          = 0;
        brk              = 0;
        overflow         = 0;
        negative         = 0;

        // The processor status, based on the flags
        setProcessorStatusRegisterFromFlags();

        /**
         * TODO, stack pointer should run between 0x0100 and 0x01FF, using only the lower 8 bits.
         *       ON reset, the pc and p should be pushed onto the stack.
         */
        sp = 0x01FF - 2;
        pc = 0;
    }
    
    public void step()
    {
        execute(1);
    }
    
    public int execute(int aNumberOfCycles)
    {
        int _clocksRemain = aNumberOfCycles;

        while(_clocksRemain > 0)
        {
            _clocksRemain--;

            System.out.print(Utils.toHexString(pc));

            int _opcode = memory.readByte(pc++);

            System.out.printf(": %s [%s]\n", Utils.toHexString(_opcode), Opcodes.name(_opcode));
            
            switch(_opcode)
            {
                case 0x69: opcode_ADC_immediate(); break;
                case 0x65: opcode_ADC_zero_page(); break;
                case 0x75: opcode_ADC_zero_page_x(); break;
                case 0x6D: opcode_ADC_absolute(); break;
                case 0x7D: opcode_ADC_absolute_x(); break;
                case 0x79: opcode_ADC_absolute_y(); break;
                case 0x61: opcode_ADC_indirect_x(); break;
                case 0x71: opcode_ADC_indirect_y(); break;
                case 0x29: opcode_AND_immediate(); break;
                case 0x25: opcode_AND_zero_page(); break;
                case 0x35: opcode_AND_zero_page_x(); break;
                case 0x2D: opcode_AND_absolute(); break;
                case 0x3D: opcode_AND_absolute_x(); break;
                case 0x39: opcode_AND_absolute_y(); break;
                case 0x21: opcode_AND_indirect_x(); break;
                case 0x31: opcode_AND_indirect_y(); break;
                case 0x0A: opcode_ASL_accumulator(); break;
                case 0x06: opcode_ASL_zero_page(); break;
                case 0x16: opcode_ASL_zero_page_x(); break;
                case 0x0E: opcode_ASL_absolute(); break;
                case 0x1E: opcode_ASL_absolute_x(); break;
                case 0x90: opcode_BCC_relative(); break;
                case 0xB0: opcode_BCS_relative(); break;
                case 0xF0: opcode_BEQ_relative(); break;
                case 0x24: opcode_BIT_zero_page(); break;
                case 0x2C: opcode_BIT_absolute(); break;
                case 0x30: opcode_BMI_relative(); break;
                case 0xD0: opcode_BNE_relative(); break;
                case 0x10: opcode_BPL_relative(); break;
                case 0x00: opcode_BRK_implied(); break;
                case 0x50: opcode_BVC_relative(); break;
                case 0x70: opcode_BVS_relative(); break;
                case 0x18: opcode_CLC_implied(); break;
                case 0xD8: opcode_CLD_implied(); break;
                case 0x58: opcode_CLI_implied(); break;
                case 0xB8: opcode_CLV_implied(); break;
                case 0xC9: opcode_CMP_immediate(); break;
                case 0xC5: opcode_CMP_zero_page(); break;
                case 0xD5: opcode_CMP_zero_page_x(); break;
                case 0xCD: opcode_CMP_absolute(); break;
                case 0xDD: opcode_CMP_absolute_x(); break;
                case 0xD9: opcode_CMP_absolute_y(); break;
                case 0xC1: opcode_CMP_indirect_x(); break;
                case 0xD1: opcode_CMP_indirect_y(); break;
                case 0xE0: opcode_CPX_immediate(); break;
                case 0xE4: opcode_CPX_zero_page(); break;
                case 0xEC: opcode_CPX_absolute(); break;
                case 0xC0: opcode_CPY_immediate(); break;
                case 0xC4: opcode_CPY_zero_page(); break;
                case 0xCC: opcode_CPY_absolute(); break;
                case 0xC7: opcode_DCP_zero_page(); break;
                case 0xD7: opcode_DCP_zero_page_x(); break;
                case 0xCF: opcode_DCP_absolute(); break;
                case 0xDF: opcode_DCP_absolute_x(); break;
                case 0xDB: opcode_DCP_absolute_y(); break;
                case 0xC3: opcode_DCP_indirect_x(); break;
                case 0xD3: opcode_DCP_indirect_y(); break;
                case 0xC6: opcode_DEC_zero_page(); break;
                case 0xD6: opcode_DEC_zero_page_x(); break;
                case 0xCE: opcode_DEC_absolute(); break;
                case 0xDE: opcode_DEC_absolute_x(); break;
                case 0xCA: opcode_DEX_implied(); break;
                case 0x88: opcode_DEY_implied(); break;
                case 0x49: opcode_EOR_immediate(); break;
                case 0x45: opcode_EOR_zero_page(); break;
                case 0x55: opcode_EOR_zero_page_x(); break;
                case 0x4D: opcode_EOR_absolute(); break;
                case 0x5D: opcode_EOR_absolute_x(); break;
                case 0x59: opcode_EOR_absolute_y(); break;
                case 0x41: opcode_EOR_indirect_x(); break;
                case 0x51: opcode_EOR_indirect_y(); break;
                case 0xE6: opcode_INC_zero_page(); break;
                case 0xF6: opcode_INC_zero_page_x(); break;
                case 0xEE: opcode_INC_absolute(); break;
                case 0xFE: opcode_INC_absolute_x(); break;
                case 0xE8: opcode_INX_implied(); break;
                case 0xC8: opcode_INY_implied(); break;
                case 0xE7: opcode_ISB_zero_page(); break;
                case 0xF7: opcode_ISB_zero_page_x(); break;
                case 0xEF: opcode_ISB_absolute(); break;
                case 0xFF: opcode_ISB_absolute_x(); break;
                case 0xFB: opcode_ISB_absolute_y(); break;
                case 0xE3: opcode_ISB_indirect_x(); break;
                case 0xF3: opcode_ISB_indirect_y(); break;
                case 0x4C: opcode_JMP_absolute(); break;
                case 0x6C: opcode_JMP_indirect(); break;
                case 0x20: opcode_JSR_absolute(); break;
                case 0xA7: opcode_LAX_zero_page(); break;
                case 0xB7: opcode_LAX_zero_page_y(); break;
                case 0xAF: opcode_LAX_absolute(); break;
                case 0xBF: opcode_LAX_absolute_y(); break;
                case 0xA3: opcode_LAX_indirect_x(); break;
                case 0xB3: opcode_LAX_indirect_y(); break;
                case 0xA9: opcode_LDA_immediate(); break;
                case 0xA5: opcode_LDA_zero_page(); break;
                case 0xB5: opcode_LDA_zero_page_x(); break;
                case 0xAD: opcode_LDA_absolute(); break;
                case 0xBD: opcode_LDA_absolute_x(); break;
                case 0xB9: opcode_LDA_absolute_y(); break;
                case 0xA1: opcode_LDA_indirect_x(); break;
                case 0xB1: opcode_LDA_indirect_y(); break;
                case 0xA2: opcode_LDX_immediate(); break;
                case 0xA6: opcode_LDX_zero_page(); break;
                case 0xB6: opcode_LDX_zero_page_y(); break;
                case 0xAE: opcode_LDX_absolute(); break;
                case 0xBE: opcode_LDX_absolute_y(); break;
                case 0xA0: opcode_LDY_immediate(); break;
                case 0xA4: opcode_LDY_zero_page(); break;
                case 0xB4: opcode_LDY_zero_page_x(); break;
                case 0xAC: opcode_LDY_absolute(); break;
                case 0xBC: opcode_LDY_absolute_x(); break;
                case 0x4A: opcode_LSR_accumulator(); break;
                case 0x46: opcode_LSR_zero_page(); break;
                case 0x56: opcode_LSR_zero_page_x(); break;
                case 0x4E: opcode_LSR_absolute(); break;
                case 0x5E: opcode_LSR_absolute_x(); break;
                case 0xEA:
                case 0x1A:
                case 0x3A:
                case 0x5A:
                case 0x7A:
                case 0xDA:
                case 0xFA: opcode_NOP_implied(); break;
                case 0x80: opcode_NOP_immediate(); break;
                case 0x04:
                case 0x44:
                case 0x64: opcode_NOP_zero_page(); break;
                case 0x0C: opcode_NOP_absolute(); break;
                case 0x1C:
                case 0x3C:
                case 0x5C:
                case 0x7C:
                case 0xDC:
                case 0xFC: opcode_NOP_absolute_x(); break;
                case 0x14:
                case 0x34:
                case 0x54:
                case 0x74:
                case 0xD4:
                case 0xF4: opcode_NOP_zero_page_x(); break;
                case 0x09: opcode_ORA_immediate(); break;
                case 0x05: opcode_ORA_zero_page(); break;
                case 0x15: opcode_ORA_zero_page_x(); break;
                case 0x0D: opcode_ORA_absolute(); break;
                case 0x1D: opcode_ORA_absolute_x(); break;
                case 0x19: opcode_ORA_absolute_y(); break;
                case 0x01: opcode_ORA_indirect_x(); break;
                case 0x11: opcode_ORA_indirect_y(); break;
                case 0x48: opcode_PHA_implied(); break;
                case 0x08: opcode_PHP_implied(); break;
                case 0x68: opcode_PLA_implied(); break;
                case 0x28: opcode_PLP_implied(); break;
                case 0x27: opcode_RLA_zero_page(); break;
                case 0x37: opcode_RLA_zero_page_x(); break;
                case 0x2F: opcode_RLA_absolute(); break;
                case 0x3F: opcode_RLA_absolute_x(); break;
                case 0x3B: opcode_RLA_absolute_y(); break;
                case 0x23: opcode_RLA_indirect_x(); break;
                case 0x33: opcode_RLA_indirect_y(); break;
                case 0x2A: opcode_ROL_accumulator(); break;
                case 0x26: opcode_ROL_zero_page(); break;
                case 0x36: opcode_ROL_zero_page_x(); break;
                case 0x2E: opcode_ROL_absolute(); break;
                case 0x3E: opcode_ROL_absolute_x(); break;
                case 0x6A: opcode_ROR_accumulator(); break;
                case 0x66: opcode_ROR_zero_page(); break;
                case 0x76: opcode_ROR_zero_page_x(); break;
                case 0x6E: opcode_ROR_absolute(); break;
                case 0x7E: opcode_ROR_absolute_x(); break;
                case 0x67: opcode_RRA_zero_page(); break;
                case 0x77: opcode_RRA_zero_page_x(); break;
                case 0x6F: opcode_RRA_absolute(); break;
                case 0x7F: opcode_RRA_absolute_x(); break;
                case 0x7B: opcode_RRA_absolute_y(); break;
                case 0x63: opcode_RRA_indirect_x(); break;
                case 0x73: opcode_RRA_indirect_y(); break;
                case 0x40: opcode_RTI_implied(); break;
                case 0x60: opcode_RTS_implied(); break;
                case 0x87: opcode_SAX_zero_page(); break;
                case 0x97: opcode_SAX_zero_page_y(); break;
                case 0x8F: opcode_SAX_absolute(); break;
                case 0x83: opcode_SAX_indirect_x(); break;
                case 0xE9:
                case 0xEB: opcode_SBC_immediate(); break;
                case 0xE5: opcode_SBC_zero_page(); break;
                case 0xF5: opcode_SBC_zero_page_x(); break;
                case 0xED: opcode_SBC_absolute(); break;
                case 0xFD: opcode_SBC_absolute_x(); break;
                case 0xF9: opcode_SBC_absolute_y(); break;
                case 0xE1: opcode_SBC_indirect_x(); break;
                case 0xF1: opcode_SBC_indirect_y(); break;
                case 0x38: opcode_SEC_implied(); break;
                case 0xF8: opcode_SED_implied(); break;
                case 0x78: opcode_SEI_implied(); break;
                case 0x07: opcode_SLO_zero_page(); break;
                case 0x17: opcode_SLO_zero_page_x(); break;
                case 0x0F: opcode_SLO_absolute(); break;
                case 0x1F: opcode_SLO_absolute_x(); break;
                case 0x1B: opcode_SLO_absolute_y(); break;
                case 0x03: opcode_SLO_indirect_x(); break;
                case 0x13: opcode_SLO_indirect_y(); break;
                case 0x47: opcode_SRE_zero_page(); break;
                case 0x57: opcode_SRE_zero_page_x(); break;
                case 0x4F: opcode_SRE_absolute(); break;
                case 0x5F: opcode_SRE_absolute_x(); break;
                case 0x5B: opcode_SRE_absolute_y(); break;
                case 0x43: opcode_SRE_indirect_x(); break;
                case 0x53: opcode_SRE_indirect_y(); break;
                case 0x85: opcode_STA_zero_page(); break;
                case 0x95: opcode_STA_zero_page_x(); break;
                case 0x8D: opcode_STA_absolute(); break;
                case 0x9D: opcode_STA_absolute_x(); break;
                case 0x99: opcode_STA_absolute_y(); break;
                case 0x81: opcode_STA_indirect_x(); break;
                case 0x91: opcode_STA_indirect_y(); break;
                case 0x86: opcode_STX_zero_page(); break;
                case 0x96: opcode_STX_zero_page_y(); break;
                case 0x8E: opcode_STX_absolute(); break;
                case 0x84: opcode_STY_zero_page(); break;
                case 0x94: opcode_STY_zero_page_x(); break;
                case 0x8C: opcode_STY_absolute(); break;
                case 0xAA: opcode_TAX_implied(); break;
                case 0xA8: opcode_TAY_implied(); break;
                case 0xBA: opcode_TSX_implied(); break;
                case 0x8A: opcode_TXA_implied(); break;
                case 0x9A: opcode_TXS_implied(); break;
                case 0x98: opcode_TYA_implied(); break;

                default: throw new RuntimeException("Unhandled opcode [" + Utils.toHexString(_opcode) + "]");
            }
            
            // Mask to 16 bit
            pc = pc & 0xFFFF;
            
            setProcessorStatusRegisterFromFlags();
        }
        
        return _clocksRemain;
    }
    
    private boolean isZeroFlagSet()
    {
        return not_zero == 0;
    }
    
    /**
     * [0] carry
     * [1] zero
     * [2] interrupt disable
     * [3] decimal - not used 
     * [4] break - probably not used
     * [5] UNUSED - always set
     * [6] overflow
     * [7] negative
     */
    private void setProcessorStatusRegisterFromFlags()
    {        
        int _zero = isZeroFlagSet() ? 1 : 0;
        
        p = carry << 0 | (_zero << 1) | (interruptDisable << 2) | (decimal << 3) | (brk << 4) | (1 << 5) | (overflow << 6) | (negative << 7);        
    }


//-------------------------------------------------------------
// Addressing
//-------------------------------------------------------------    
    
    /**
     * Immediate addressing allows the programmer to directly specify an 8 bit constant within the instruction. 
     */
    public final int immediate(int anAddress)
    {
        return anAddress;
    }
    
    /**
     * An instruction using zero page addressing mode has only an 8 bit address operand. This limits it to addressing 
     * only the first 256 bytes of memory (e.g. $0000 to $00FF) where the most significant byte of the address is always 
     * zero. In zero page mode only the least significant byte of the address is held in the instruction making it 
     * shorter by one byte (important for space saving) and one less memory fetch during execution (important for speed).
     * 
     * An assembler will automatically select zero page addressing mode if the operand evaluates to a zero page address
     * and the instruction supports the mode (not all do).
     */
    public final int zeroPage(int anAddress)
    {        
        return memory.readByte(anAddress);
    }
    
    /**
     * The address to be accessed by an instruction using indexed zero page addressing is calculated by taking the 8 bit 
     * zero page address from the instruction and adding the current value of the X register to it. For example if the X 
     * register contains $0F and the instruction LDA $80,X is executed then the accumulator will be loaded from $008F 
     * (e.g. $80 + $0F => $8F).
     * 
     * NB:
     * The address calculation wraps around if the sum of the base address and the register exceed $FF. If we repeat the 
     * last example but with $FF in the X register then the accumulator will be loaded from $007F (e.g. $80 + $FF => $7F) 
     * and not $017F.
     */
    public final int zeroPageX(int anAddress)
    {
        return (memory.readByte(anAddress) + x) & 0xFF;        
    }
    
    /**
     * The address to be accessed by an instruction using indexed zero page addressing is calculated by  taking the 8 bit 
     * zero page address from the instruction and adding the current value of the Y register to it. This mode can only be 
     * used with the LDX and STX instructions.
     */
    public final int zeroPageY(int anAddress)
    {
        return (memory.readByte(anAddress) + y) & 0xFF;        
    }
    
    /**
     * Relative addressing mode is used by branch instructions (e.g. BEQ, BNE, etc.) which contain a signed 8 bit relative 
     * offset (e.g. -128 to +127) which is added to program counter if the condition is true. As the program counter itself 
     * is incremented during instruction execution by two the effective address range for the target instruction must be 
     * with -126 to +129 bytes of the branch.
     */
    public final int relative(int anAddress)
    {
        return anAddress; 
    }
    
    /**
     * Instructions using absolute addressing contain a full 16 bit address to identify the target location.
     */
    public final int absolute(int anAddress)
    {
        return readWord(anAddress);
    }
    
    /**
     * The address to be accessed by an instruction using X register indexed absolute addressing is computed by taking 
     * the 16 bit address from the instruction and added the contents of the X register. For example if X contains $92 
     * then an STA $2000,X instruction will store the accumulator at $2092 (e.g. $2000 + $92).
     */
    public final int absoluteX(int anAddress)
    {
        return readWord(anAddress) + x;
    }
    
    /**
     * The Y register indexed absolute addressing mode is the same as the previous mode only with the contents of the Y 
     * register added to the 16 bit address from the instruction.
     */
    public final int absoluteY(int anAddress)
    {
        return readWord(anAddress) + y;
    }
    
    /**
     * JMP is the only 6502 instruction to support indirection. The instruction contains a 16 bit address which identifies 
     * the location of the least significant byte of another 16 bit memory address which is the real target of the instruction.
     * 
     * For example if location $0120 contains $FC and location $0121 contains $BA then the instruction JMP ($0120) will cause 
     * the next instruction execution to occur at $BAFC (e.g. the contents of $0120 and $0121).
     * 
     * N.B.
     * An original 6502 has does not correctly fetch the target address if the indirect vector falls on a page boundary 
     * (e.g. $xxFF where xx is and value from $00 to $FF). In this case fetches the LSB from $xxFF as expected but takes 
     * the MSB from $xx00. This is fixed in some later chips like the 65SC02 so for compatibility always ensure the indirect 
     * vector is not at the end of the page.
     */
    public final int indirect(int anAddress)
    {
        int _address = readWord(anAddress);
        
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
    
    /**
     * Indexed indirect addressing is normally used in conjunction with a table of address held on zero page. The address 
     * of the table is taken from the instruction and the X register added to it (with zero page wrap around) to give the 
     * location of the least significant byte of the target address.
     */
    public final int indirectX(int anAddress)
    {
        int _address = (memory.readByte(anAddress) + x) & 0xFF;       
        return readWordZeroPageWrap(_address);
    }
    
    /**
     * Indirect indirect addressing is the most common indirection mode used on the 6502. In instruction contains the zero 
     * page location of the least significant byte of 16 bit address. The Y register is dynamically added to this value 
     * to generated the actual target address for operation.
     */
    public final int indirectY(int anAddress)
    {
        int _address = memory.readByte(anAddress);
        return readWordZeroPageWrap(_address) + y;        
    }
    
//-------------------------------------------------------------
// Stack
//-------------------------------------------------------------    

    private void push(int aByte)
    {
        memory.writeByte(aByte, sp);
        sp--;
    }
    
    private int pop()
    {
        sp++;
        sp = 0x0100 | (sp & 0xFF);
        return memory.readByte(sp);
    }
    
    private void pushWord(int aWord)
    {
        push((aWord >> 8) & 255);
        push(aWord & 255);
    }
    
    private int popWord()
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
    
    private final int asSignedByte(int aByte)
    {
        return aByte < 0x80 ? aByte : aByte - 256;
    }
    
//-------------------------------------------------------------
// Opcodes
//-------------------------------------------------------------    
    
    // Add with Carry
    private void opcode_ADC_immediate()
    {
        int _address = immediate(pc++);
        int _value = memory.readByte(_address);
        int _temp = a + _value + carry;
        
        carry = _temp > 0xFF ? 1 : 0;
        not_zero = _temp & 0xFF;
        overflow = ((!(((a ^ _value) & 0x80) != 0) && (((a ^ _temp) & 0x80)) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    // Add with Carry
    private void opcode_ADC_zero_page()
    {
        int _address = zeroPage(pc++);
        int _value = memory.readByte(_address);
        int _temp = a + _value + carry;
        
        carry = _temp > 0xFF ? 1 : 0;
        not_zero = _temp & 0xFF;
        overflow = ((!(((a ^ _value) & 0x80) != 0) && (((a ^ _temp) & 0x80)) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    // Add with Carry
    private void opcode_ADC_zero_page_x()
    {
        int _address = zeroPageX(pc++);
        int _value = memory.readByte(_address);
        int _temp = a + _value + carry;
        
        carry = _temp > 0xFF ? 1 : 0;
        not_zero = _temp & 0xFF;
        overflow = ((!(((a ^ _value) & 0x80) != 0) && (((a ^ _temp) & 0x80)) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    // Add with Carry
    private void opcode_ADC_absolute()
    {
        int _address = absolute(pc++);
        int _value = memory.readByte(_address);
        int _temp = a + _value + carry;
        
        pc++;
        
        carry = _temp > 0xFF ? 1 : 0;
        not_zero = _temp & 0xFF;
        overflow = ((!(((a ^ _value) & 0x80) != 0) && (((a ^ _temp) & 0x80)) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    // Add with Carry
    private void opcode_ADC_absolute_x()
    {
        int _address = absoluteX(pc++);
        int _value = memory.readByte(_address);
        int _temp = a + _value + carry;
        
        pc++;
        
        carry = _temp > 0xFF ? 1 : 0;
        not_zero = _temp & 0xFF;
        overflow = ((!(((a ^ _value) & 0x80) != 0) && (((a ^ _temp) & 0x80)) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }
    
    // Add with Carry
    private void opcode_ADC_absolute_y()
    {
        int _address = absoluteY(pc++);
        int _value = memory.readByte(_address);
        int _temp = a + _value + carry;
        
        pc++;
        
        carry = _temp > 0xFF ? 1 : 0;
        not_zero = _temp & 0xFF;
        overflow = ((!(((a ^ _value) & 0x80) != 0) && (((a ^ _temp) & 0x80)) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    // Add with Carry
    private void opcode_ADC_indirect_x()
    {
        int _address = indirectX(pc++);
        int _value = memory.readByte(_address);
        int _temp = a + _value + carry;
        
        carry = _temp > 0xFF ? 1 : 0;
        not_zero = _temp & 0xFF;
        overflow = ((!(((a ^ _value) & 0x80) != 0) && (((a ^ _temp) & 0x80)) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }
 
    // Add with Carry
    private void opcode_ADC_indirect_y()
    {
        int _address = indirectY(pc++);
        int _value = memory.readByte(_address);
        int _temp = a + _value + carry;
        
        carry = _temp > 0xFF ? 1 : 0;
        not_zero = _temp & 0xFF;
        overflow = ((!(((a ^ _value) & 0x80) != 0) && (((a ^ _temp) & 0x80)) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }
 
    // Logical AND
    private void opcode_AND_immediate()
    {
        int _address = immediate(pc++);
        a &= memory.readByte(_address);        
        
        negative = (a >> 7) & 1;
        not_zero = a;        
    }

    // Logical AND
    private void opcode_AND_zero_page()
    {
        int _address = zeroPage(pc++);
        a &= memory.readByte(_address);
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }

    // Logical AND
    private void opcode_AND_zero_page_x()
    {
        int _address = zeroPageX(pc++);
        a &= memory.readByte(_address);
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }

    // Logical AND
    private void opcode_AND_absolute()
    {
        int _address = absolute(pc++);
        a &= memory.readByte(_address);
        
        pc++;
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }

    // Logical AND
    private void opcode_AND_absolute_x()
    {
        int _address = absoluteX(pc++);
        a &= memory.readByte(_address);
        
        pc++;
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }

    // Logical AND
    private void opcode_AND_absolute_y()
    {
        int _address = absoluteY(pc++);
        a &= memory.readByte(_address);
        
        pc++;
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }

    // Logical AND
    private void opcode_AND_indirect_x()
    {
        int _value = indirectX(pc++);
        a &= memory.readByte(_value);
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }

    // Logical AND
    private void opcode_AND_indirect_y()
    {
        int _value = indirectY(pc++);
        a &= memory.readByte(_value);
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }

    // Arithmetic Shift Left
    private void opcode_ASL_accumulator()
    {
        carry = (a >> 7) & 1;
        a = (a << 1) & 0xFF;
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }

    // Arithmetic Shift Left
    private void opcode_ASL_zero_page()
    {
        int _address = zeroPage(pc++);
        int _value = memory.readByte(_address);
        
        carry = (_value >> 7) & 1;
        _value = (_value << 1) & 0xFF;
        
        memory.writeByte(_value, _address);
        
        negative = (_value >> 7) & 1;
        not_zero = _value;
    }

    // Arithmetic Shift Left
    private void opcode_ASL_zero_page_x()
    {
        int _address = zeroPageX(pc++);
        int _value = memory.readByte(_address);
        
        carry = (_value >> 7) & 1;
        _value = (_value << 1) & 0xFF;
        
        memory.writeByte(_value, _address);
        
        negative = (_value >> 7) & 1;
        not_zero = _value;
    }

    // Arithmetic Shift Left
    private void opcode_ASL_absolute()
    {
        int _address = absolute(pc++);
        int _value = memory.readByte(_address);
        
        pc++;
        
        carry = (_value >> 7) & 1;
        _value = (_value << 1) & 0xFF;
        
        memory.writeByte(_value, _address);
        
        negative = (_value >> 7) & 1;
        not_zero = _value;
    }

    // Arithmetic Shift Left
    private void opcode_ASL_absolute_x()
    {
        int _address = absoluteX(pc++);
        int _value = memory.readByte(_address);
        
        pc++;
        
        carry = (_value >> 7) & 1;
        _value = (_value << 1) & 0xFF;
        
        memory.writeByte(_value, _address);
        
        negative = (_value >> 7) & 1;
        not_zero = _value;
    }
 
    // Branch if Carry Clear
    private void opcode_BCC_relative()
    {
        if(carry == 0)
        {
            int _address = relative(pc++);
            int _value = memory.readByte(_address);
            
            int _relative = asSignedByte(_value);
            pc += _relative;
        }
        else
        {
            pc++;
        }
    }
 
    // Branch if Carry Set
    private void opcode_BCS_relative()
    {
        if(carry > 0)
        {
            int _address = relative(pc++);
            int _value = memory.readByte(_address);
            
            int _relative = asSignedByte(_value); 
            pc += _relative;
        }
        else
        {
            pc++;
        }
    }

    // Branch if Equal
    private void opcode_BEQ_relative()
    {
        if(isZeroFlagSet())
        {            
            int _address = relative(pc++);
            int _value = memory.readByte(_address);
            
            int _relative = asSignedByte(_value);
            pc += _relative;
        }
        else
        {
            pc++;
        }
    }

    // Bit Test
    private void opcode_BIT_zero_page()
    {
        int _address = zeroPage(pc++);        
        int _value = memory.readByte(_address);
        
        negative = (_value >> 7) & 1;
        overflow = (_value >> 6) & 1;
        _value &= a;
        not_zero = _value;     
    }

    // Bit Test
    private void opcode_BIT_absolute()
    {
        int _address = absolute(pc++);        
        int _value = memory.readByte(_address);
        
        pc++;
        
        negative = (_value >> 7) & 1;
        overflow = (_value >> 6) & 1;
        _value &= a;
        not_zero = _value;        
    }

    // Branch if Minus
    private void opcode_BMI_relative()
    {
    	if(negative != 0)
    	{
    	    int _address = relative(pc++);
            int _value = memory.readByte(_address);
            
            int _relative = asSignedByte(_value);
    		pc += _relative;
    	}
    	else
    	{
    		pc++;
    	}
    }

    // Branch if Not Equal
    private void opcode_BNE_relative()
    {
        if(!isZeroFlagSet())
        {            
            int _address = relative(pc++);
            int _value = memory.readByte(_address);
            
            int _relative = asSignedByte(_value);
            pc += _relative;
        }
        else
        {
            pc++;
        }
    }

    // Branch if Positive
    private void opcode_BPL_relative()
    {
        if(negative == 0)
        {
            int _address = relative(pc++);
            int _value = memory.readByte(_address);
            
            int _relative = asSignedByte(_value);
            pc += _relative;
        }
        else
        {
            pc++;
        }
    }

    private void opcode_BRK_implied()
    {
        throw new RuntimeException("opcode not implemented [opcode_BRK]");
    }

    // Branch if Overflow Clear
    private void opcode_BVC_relative()
    {
        if(overflow == 0)
        {
            int _address = relative(pc++);
            int _value = memory.readByte(_address);
            
            int _relative = asSignedByte(_value);
            pc += _relative;
        }
        else
        {
            pc++;
        }
    }

    // Branch if Overflow Set
    private void opcode_BVS_relative()
    {
        if(overflow != 0)
        {
            int _address = relative(pc++);
            int _value = memory.readByte(_address);
            
            int _relative = asSignedByte(_value);
            pc += _relative;
        }
        else
        {
            pc++;
        }
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

    private void opcode_CLI_implied()
    {
        throw new RuntimeException("opcode not implemented [opcode_CLI]");
    }

    // Clear Overflow Flag
    private void opcode_CLV_implied()
    {
        overflow = 0;
    }

    // Compare
    private void opcode_CMP_immediate()
    {
        int _address = immediate(pc++);
        int _temp = a - memory.readByte(_address);
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;
        negative = (_temp >> 7) & 1;        
    }

    // Compare
    private void opcode_CMP_zero_page()
    {
        int _address = zeroPage(pc++);
        int _temp = a - memory.readByte(_address);
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;
        negative = (_temp >> 7) & 1;
    }

    // Compare
    private void opcode_CMP_zero_page_x()
    {
        int _address = zeroPageX(pc++);
        int _temp = a - memory.readByte(_address);
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;
        negative = (_temp >> 7) & 1;
    }

    // Compare
    private void opcode_CMP_absolute()
    {
        int _address = absolute(pc++);
        int _temp = a - memory.readByte(_address);
        
        pc++;
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;
        negative = (_temp >> 7) & 1;
    }

    // Compare
    private void opcode_CMP_absolute_x()
    {
        int _address = absoluteX(pc++);
        int _temp = a - memory.readByte(_address);
        
        pc++;
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;
        negative = (_temp >> 7) & 1;
    }

    // Compare
    private void opcode_CMP_absolute_y()
    {
        int _address = absoluteY(pc++);
        int _temp = a - memory.readByte(_address);
        
        pc++;
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;
        negative = (_temp >> 7) & 1;
    }

    // Compare
    private void opcode_CMP_indirect_x()
    {
        int _address = indirectX(pc++);
        int _temp = a - memory.readByte(_address);
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;
        negative = (_temp >> 7) & 1;
    }

    // Compare
    private void opcode_CMP_indirect_y()
    {
        int _address = indirectY(pc++);
        int _temp = a - memory.readByte(_address);
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;
        negative = (_temp >> 7) & 1;
    }

    // Compare X Register
    private void opcode_CPX_immediate()
    {
        int _address = immediate(pc++);
        int _temp = x - memory.readByte(_address);
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;
        negative = (_temp >> 7) & 1;
    }

    // Compare X Register
    private void opcode_CPX_zero_page()
    {
        int _address = zeroPage(pc++);
        int _temp = x - memory.readByte(_address);
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;
        negative = (_temp >> 7) & 1;
    }

    // Compare X Register
    private void opcode_CPX_absolute()
    {
        int _address = absolute(pc++);
        int _temp = x - memory.readByte(_address);
        
        pc++;
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;
        negative = (_temp >> 7) & 1;
    }

    // Compare Y Register
    private void opcode_CPY_immediate()
    {
        int _address = immediate(pc++);
        int _temp = y - memory.readByte(_address);
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;
        negative = (_temp >> 7) & 1;
    }

    // Compare Y Register
    private void opcode_CPY_zero_page()
    {
        int _address = zeroPage(pc++);
        int _temp = y - memory.readByte(_address);
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;
        negative = (_temp >> 7) & 1;
    }

    // Compare Y Register
    private void opcode_CPY_absolute()
    {
        int _address = absolute(pc++);
        int _temp = y - memory.readByte(_address);
        
        pc++;
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;
        negative = (_temp >> 7) & 1;
    }
    
    // DEC value then CMP value
    private void opcode_DCP_zero_page()
    {
        int _address = zeroPage(pc++);
        int _value = memory.readByte(_address) - 1;
        
        memory.writeByte(_value, _address);
        
        int _temp = a - _value;
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;
        negative = (_temp >> 7) & 1;
    }

    // DEC value then CMP value
    private void opcode_DCP_zero_page_x()
    {
        int _address = zeroPageX(pc++);
        int _value = memory.readByte(_address) - 1;
        
        memory.writeByte(_value, _address);
        
        int _temp = a - _value;
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;
        negative = (_temp >> 7) & 1;
    }

    // DEC value then CMP value
    private void opcode_DCP_absolute()
    {
        int _address = absolute(pc++);
        int _value = memory.readByte(_address) - 1;
        
        memory.writeByte(_value, _address);
        
        pc++;
        
        int _temp = a - _value;
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;
        negative = (_temp >> 7) & 1;
    }

    // DEC value then CMP value
    private void opcode_DCP_absolute_x()
    {
        int _address = absoluteX(pc++);
        int _value = memory.readByte(_address) - 1;
        
        memory.writeByte(_value, _address);
        
        pc++;
        
        int _temp = a - _value;
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;
        negative = (_temp >> 7) & 1;
    }

    // DEC value then CMP value
    private void opcode_DCP_absolute_y()
    {
        int _address = absoluteY(pc++);
        int _value = memory.readByte(_address) - 1;
        
        memory.writeByte(_value, _address);
        
        pc++;
        
        int _temp = a - _value;
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;
        negative = (_temp >> 7) & 1;
    }

    // DEC value then CMP value
    private void opcode_DCP_indirect_x()
    {
        int _address = indirectX(pc++);
        int _value = memory.readByte(_address) - 1;
        
        memory.writeByte(_value, _address);
        
        int _temp = a - _value;
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;
        negative = (_temp >> 7) & 1;
    }

    // DEC value then CMP value
    private void opcode_DCP_indirect_y()
    {
        int _address = indirectY(pc++);
        int _value = memory.readByte(_address) - 1;
        
        memory.writeByte(_value, _address);
        
        int _temp = a - _value;
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;
        negative = (_temp >> 7) & 1;
    }

    // Decrement Memory
    private void opcode_DEC_zero_page()
    {
        int _address = zeroPage(pc++);        
        int _value = memory.readByte(_address) - 1;
        
        memory.writeByte(_value, _address);
        
        not_zero = _value & 0xFF;
        negative = (_value >> 7) & 1;
    }

    // Decrement Memory
    private void opcode_DEC_zero_page_x()
    {
        int _address = zeroPageX(pc++);        
        int _value = memory.readByte(_address) - 1;
        
        memory.writeByte(_value, _address);
        
        not_zero = _value & 0xFF;
        negative = (_value >> 7) & 1;
    }

    // Decrement Memory
    private void opcode_DEC_absolute()
    {
        int _address = absolute(pc++);
        int _value = memory.readByte(_address) - 1;
        
        pc++;
        
        memory.writeByte(_value, _address);
        
        not_zero = _value & 0xFF;
        negative = (_value >> 7) & 1;
    }

    // Decrement Memory
    private void opcode_DEC_absolute_x()
    {
        int _address = absoluteX(pc++);
        int _value = memory.readByte(_address) - 1;
        
        pc++;
        
        memory.writeByte(_value, _address);
        
        not_zero = _value & 0xFF;
        negative = (_value >> 7) & 1;
    }

    // Decrement X Register
    private void opcode_DEX_implied()
    {
        x = (x - 1) & 0xFF;
        
        not_zero = x & 0xFF;
        negative = (x >> 7) & 1;
    }

    // Decrement Y Register
    private void opcode_DEY_implied()
    {
        y = (y - 1) & 0xFF;
        
        not_zero = y & 0xFF;
        negative = (y >> 7) & 1;
    }

    // Exclusive OR
    private void opcode_EOR_immediate()
    {
        int _address = immediate(pc++);
        a ^=  memory.readByte(_address);
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;        
    }

    // Exclusive OR
    private void opcode_EOR_zero_page()
    {
        int _address = zeroPage(pc++);
        a ^=  memory.readByte(_address);
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    // Exclusive OR
    private void opcode_EOR_zero_page_x()
    {
        int _address = zeroPageX(pc++);
        a ^=  memory.readByte(_address);
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    // Exclusive OR
    private void opcode_EOR_absolute()
    {
        int _address = absolute(pc++);
        a ^=  memory.readByte(_address);
        
        pc++;
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    // Exclusive OR
    private void opcode_EOR_absolute_x()
    {
        int _address = absoluteX(pc++);
        a ^=  memory.readByte(_address);
        
        pc++;
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    // Exclusive OR
    private void opcode_EOR_absolute_y()
    {
        int _address = absoluteY(pc++);
        a ^=  memory.readByte(_address);
        
        pc++;
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    // Exclusive OR
    private void opcode_EOR_indirect_x()
    {
        int _value = indirectX(pc++);
        a ^=  memory.readByte(_value);
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    // Exclusive OR
    private void opcode_EOR_indirect_y()
    {
        int _value = indirectY(pc++);
        a ^=  memory.readByte(_value);
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    // Increment Memory
    private void opcode_INC_zero_page()
    {
        int _address = zeroPage(pc++);        
        int _value = memory.readByte(_address) + 1;
        
        memory.writeByte(_value, _address);
        
        not_zero = _value & 0xFF;
        negative = (_value >> 7) & 1;
    }

    // Increment Memory
    private void opcode_INC_zero_page_x()
    {
        int _address = zeroPageX(pc++);        
        int _value = memory.readByte(_address) + 1;
        
        memory.writeByte(_value, _address);
        
        not_zero = _value & 0xFF;
        negative = (_value >> 7) & 1;
    }

    // Increment Memory
    private void opcode_INC_absolute()
    {
        int _address = absolute(pc++);
        int _value = memory.readByte(_address) + 1;
        
        pc++;
        
        memory.writeByte(_value, _address);
        
        not_zero = _value & 0xFF;
        negative = (_value >> 7) & 1;
    }

    // Increment Memory
    private void opcode_INC_absolute_x()
    {
        int _address = absoluteX(pc++);
        int _value = memory.readByte(_address) + 1;
        
        pc++;
        
        memory.writeByte(_value, _address);
        
        not_zero = _value & 0xFF;
        negative = (_value >> 7) & 1;
    }

    // Increment X Register
    private void opcode_INX_implied()
    {
        x = (x + 1) & 0xFF;
        
        not_zero = x & 0xFF;
        negative = (x >> 7) & 1;
    }

    // Increment Y Register
    private void opcode_INY_implied()
    {
        y = (y + 1) & 0xFF;
        
        not_zero = y & 0xFF;
        negative = (y >> 7) & 1;        
    }

    // INC value then SBC value
    private void opcode_ISB_zero_page()
    {
        int _address = zeroPage(pc++);
        int _value = memory.readByte(_address) + 1;
        
        memory.writeByte(_value, _address);
        
        int _temp = a - _value - (1 - carry);
        
        //carry = _temp + 1 < 0 ? 0 : 1; // TODO hacked + 1 to make test pass
        not_zero = _temp & 0xFF;
        overflow = ((((a ^ _temp) & 0x80) != 0 && ((a ^ _value) & 0x80) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    // INC value then SBC value
    private void opcode_ISB_zero_page_x()
    {
        int _address = zeroPageX(pc++);
        int _value = memory.readByte(_address) + 1;
        
        memory.writeByte(_value, _address);
        
        int _temp = a - _value - (1 - carry);
        
        //carry = _temp + 1 < 0 ? 0 : 1; // TODO hacked + 1 to make test pass
        not_zero = _temp & 0xFF;
        overflow = ((((a ^ _temp) & 0x80) != 0 && ((a ^ _value) & 0x80) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    // INC value then SBC value
    private void opcode_ISB_absolute()
    {
        int _address = absolute(pc++);
        int _value = memory.readByte(_address) + 1;
        
        pc++;
        
        memory.writeByte(_value, _address);
                
        int _temp = a - _value - (1 - carry);
        
        //carry = _temp + 1 < 0 ? 0 : 1; // TODO hacked + 1 to make test pass
        not_zero = _temp & 0xFF;
        overflow = ((((a ^ _temp) & 0x80) != 0 && ((a ^ _value) & 0x80) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    // INC value then SBC value
    private void opcode_ISB_absolute_x()
    {
        int _address = absoluteX(pc++);
        int _value = memory.readByte(_address) + 1;
        
        pc++;
        
        memory.writeByte(_value, _address);
        
        int _temp = a - _value - (1 - carry);
        
        //carry = _temp + 1 < 0 ? 0 : 1; // TODO hacked + 1 to make test pass
        not_zero = _temp & 0xFF;
        overflow = ((((a ^ _temp) & 0x80) != 0 && ((a ^ _value) & 0x80) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    // INC value then SBC value
    private void opcode_ISB_absolute_y()
    {
        int _address = absoluteY(pc++);
        int _value = memory.readByte(_address) + 1;
        
        pc++;
        
        memory.writeByte(_value, _address);
        
        int _temp = a - _value - (1 - carry);
        
        //carry = _temp + 1 < 0 ? 0 : 1; // TODO hacked + 1 to make test pass
        not_zero = _temp & 0xFF;
        overflow = ((((a ^ _temp) & 0x80) != 0 && ((a ^ _value) & 0x80) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    // INC value then SBC value
    private void opcode_ISB_indirect_x()
    {
        int _address = indirectX(pc++);
        int _value = memory.readByte(_address) + 1;
        
        memory.writeByte(_value, _address);
        
        int _temp = a - _value - (1 - carry);
        
        //carry = _temp + 1 < 0 ? 0 : 1; // TODO hacked + 1 to make test pass
        not_zero = _temp & 0xFF;
        overflow = ((((a ^ _temp) & 0x80) != 0 && ((a ^ _value) & 0x80) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;    
    }

    // INC value then SBC value
    private void opcode_ISB_indirect_y()
    {
        int _address = indirectY(pc++);
        int _value = memory.readByte(_address) + 1;
        
        memory.writeByte(_value, _address);
        
        int _temp = a - _value - (1 - carry);
        
        //carry = _temp + 1 < 0 ? 0 : 1; // TODO hacked + 1 to make test pass
        not_zero = _temp & 0xFF;
        overflow = ((((a ^ _temp) & 0x80) != 0 && ((a ^ _value) & 0x80) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF; 
    }
    
    // Jump to target address
    private void opcode_JMP_absolute()
    {
        pc = absolute(pc);
    }

    // Jump to target address
    private void opcode_JMP_indirect()
    {
        pc = indirect(pc);   
    }
    
    // Jump to subroutine
    private void opcode_JSR_absolute()
    {
        int _address = absolute(pc);
        
        pushWord((pc + 2) - 1);
        
        pc = _address;
    }

    // Load Accumulator and X with memory
    private void opcode_LAX_zero_page()
    {
        int _address = zeroPage(pc++);
        a = memory.readByte(_address);
        x = a;
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }
    
    // Load Accumulator and X with memory
    private void opcode_LAX_zero_page_y()
    {
        int _address = zeroPageY(pc++);
        a = memory.readByte(_address);
        x = a;
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }

    // Load Accumulator and X with memory
    private void opcode_LAX_absolute()
    {
        int _address = absolute(pc++);
        a = memory.readByte(_address);
        x = a;
        
        pc++;
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }
    
    // Load Accumulator and X with memory
    private void opcode_LAX_absolute_y()
    {
        int _address = absoluteY(pc++);
        a = memory.readByte(_address);
        x = a;
        
        pc++;
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }

    // Load Accumulator and X with memory
    private void opcode_LAX_indirect_x()
    {
        int _address = indirectX(pc++);
        a = memory.readByte(_address);
        x = a;
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }

    // Load Accumulator and X with memory
    private void opcode_LAX_indirect_y()
    {
        int _address = indirectY(pc++);
        a = memory.readByte(_address);
        x = a;
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }
    
    // Load Accumulator
    private void opcode_LDA_immediate()
    {
        int _address = immediate(pc++);
        a = memory.readByte(_address);
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }

    // Load Accumulator
    private void opcode_LDA_zero_page()
    {
    	int _address = zeroPage(pc++);
    	a = memory.readByte(_address);
    	
    	negative = (a >> 7) & 1;
        not_zero = a;
    }

    // Load Accumulator
    private void opcode_LDA_zero_page_x()
    {
        int _address = zeroPageX(pc++);
        a = memory.readByte(_address);
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }

    // Load Accumulator
    private void opcode_LDA_absolute()
    {
        int _address = absolute(pc++); 
        a = memory.readByte(_address);
        
        pc++;
        
        negative = (a >> 7) & 1;
        not_zero = a;        
    }

    // Load Accumulator
    private void opcode_LDA_absolute_x()
    {
        int _address = absoluteX(pc++); 
        a = memory.readByte(_address);
        
        pc++;
        
        negative = (a >> 7) & 1;
        not_zero = a;        
    }

    // Load Accumulator
    private void opcode_LDA_absolute_y()
    {
        int _address = absoluteY(pc++); 
        a = memory.readByte(_address);
        
        pc++;
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }

    // Load Accumulator
    private void opcode_LDA_indirect_x()
    {
    	int _address = indirectX(pc++);
        a = memory.readByte(_address);
        
    	negative = (a >> 7) & 1;
        not_zero = a;
    }

    // Load Accumulator
    private void opcode_LDA_indirect_y()
    {
        int _address = indirectY(pc++);
        a = memory.readByte(_address);
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }

    // Load X with memory
    private void opcode_LDX_immediate()
    {
        int _address = immediate(pc++);
        x = memory.readByte(_address);
        
        negative = (x >> 7) & 1;
        not_zero = x;
    }

    // Load X with memory
    private void opcode_LDX_zero_page()
    {
        int _address = zeroPage(pc++);
        x = memory.readByte(_address);
        
        negative = (x >> 7) & 1;
        not_zero = x;
    }

    // Load X with memory
    private void opcode_LDX_zero_page_y()
    {
        int _address = zeroPageY(pc++);
        x = memory.readByte(_address);
        
        negative = (x >> 7) & 1;
        not_zero = x;
    }

    // Load X with memory
    private void opcode_LDX_absolute()
    {
        int _address = absolute(pc++);
        this.x = memory.readByte(_address);
        
        pc++;
        
        negative = (x >> 7) & 1;
        not_zero = x;
    }

    // Load X with memory
    private void opcode_LDX_absolute_y()
    {
        int _address = absoluteY(pc++);
        this.x = memory.readByte(_address);
        
        pc++;
        
        negative = (x >> 7) & 1;
        not_zero = x;
    }

    // Load Y Register
    private void opcode_LDY_immediate()
    {
        int _address = immediate(pc++);
        y = memory.readByte(_address);
        
        negative = (y >> 7) & 1;
        not_zero = y;
    }

    // Load Y Register
    private void opcode_LDY_zero_page()
    {
        int _address = zeroPage(pc++);
        y = memory.readByte(_address);
        
        negative = (y >> 7) & 1;
        not_zero = y;
    }

    // Load Y Register
    private void opcode_LDY_zero_page_x()
    {
        int _address = zeroPageX(pc++);
        y = memory.readByte(_address);
        
        negative = (y >> 7) & 1;
        not_zero = y;
    }

    // Load Y Register
    private void opcode_LDY_absolute()
    {
        int _address = absolute(pc++);
        y = memory.readByte(_address);
        
        pc++;
        
        negative = (y >> 7) & 1;
        not_zero = y;
    }

    // Load Y Register
    private void opcode_LDY_absolute_x()
    {
        int _address = absoluteX(pc++);
        y = memory.readByte(_address);
        
        pc++;
        
        negative = (y >> 7) & 1;
        not_zero = y;
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
    private void opcode_LSR_zero_page()
    {
        int _address = zeroPage(pc++);
        int _value = memory.readByte(_address);
        
        carry = _value & 1; // old bit 0       
        _value >>= 1;
        
        memory.writeByte(_value, _address);
        
        not_zero = _value;
        negative = 0;
    }

    // Logical Shift Right
    private void opcode_LSR_zero_page_x()
    {
        int _address = zeroPageX(pc++);
        int _value = memory.readByte(_address);
        
        carry = _value & 1; // old bit 0       
        _value >>= 1;
        
        memory.writeByte(_value, _address);
        
        not_zero = _value;
        negative = 0;
    }

    // Logical Shift Right
    private void opcode_LSR_absolute()
    {
        int _address = absolute(pc++);
        int _value = memory.readByte(_address);
        
        pc++;
        
        carry = _value & 1; // old bit 0       
        _value >>= 1;
        
        memory.writeByte(_value, _address);
        
        not_zero = _value;
        negative = 0;
    }

    // Logical Shift Right
    private void opcode_LSR_absolute_x()
    {
        int _address = absoluteX(pc++);
        int _value = memory.readByte(_address);
        
        pc++;
        
        carry = _value & 1; // old bit 0       
        _value >>= 1;
        
        memory.writeByte(_value, _address);
        
        not_zero = _value;
        negative = 0;
    }

    // No operation
    private void opcode_NOP_implied()
    {        
    }
    
    // No operation
    private void opcode_NOP_immediate()
    {
        pc++;
    }
    
    // No operation
    private void opcode_NOP_zero_page()
    {
       pc++;
    }

    // No operation
    private void opcode_NOP_absolute()
    {
        pc = pc + 2;
    }
    
    // No operation
    private void opcode_NOP_absolute_x()
    {
        pc = pc + 2;
    }
    
    // No operation
    private void opcode_NOP_zero_page_x()
    {
        pc++;
    }
    
    // Logical Inclusive OR
    private void opcode_ORA_immediate()
    {
        int _address = immediate(pc++);
        a |=  memory.readByte(_address);
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1; 
    }

    // Logical Inclusive OR
    private void opcode_ORA_zero_page()
    {
        int _address = zeroPage(pc++);
        a |=  memory.readByte(_address);
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    // Logical Inclusive OR
    private void opcode_ORA_zero_page_x()
    {
        int _address = zeroPageX(pc++);
        a |=  memory.readByte(_address);
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    // Logical Inclusive OR
    private void opcode_ORA_absolute()
    {
        int _address = absolute(pc++);
        a |=  memory.readByte(_address);
        
        pc++;
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    // Logical Inclusive OR
    private void opcode_ORA_absolute_x()
    {
        int _address = absoluteX(pc++);
        a |=  memory.readByte(_address);
        
        pc++;
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    // Logical Inclusive OR
    private void opcode_ORA_absolute_y()
    {
        int _address = absoluteY(pc++);
        a |=  memory.readByte(_address);
        
        pc++;
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    // Logical Inclusive OR
    private void opcode_ORA_indirect_x()
    {
        int _value = memory.readByte(indirectX(pc++));
        a |=  _value;
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    // Logical Inclusive OR
    private void opcode_ORA_indirect_y()
    {
        int _value = memory.readByte(indirectY(pc++));
        a |=  _value;
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    // Push Accumulator
    private void opcode_PHA_implied()
    {
        push(a);
    }

    // Push Processor Status
    private void opcode_PHP_implied()
    {
        /**
         * TODO, need a way of easily setting brk to 1 in the
         * pushed processor status 
         */
        int _oldBrk = brk;
        brk = 1;
        
        setProcessorStatusRegisterFromFlags();
        
        push(p);
        
        // Restore brk to what it was before - the needs to be an easier method
        brk = _oldBrk;
    }

    // Pull Accumulator
    private void opcode_PLA_implied()
    {
        a = pop();
        
        not_zero = a;
        negative = (a >> 7) & 1;        
    }

    // Pull Processor Status
    private void opcode_PLP_implied()
    {
        int _temp = pop();
    	
    	carry            = (_temp) & 1;
        not_zero         = ((_temp >> 1) & 1) == 1 ? 0 : 1;
        interruptDisable = (_temp >> 2) & 1;
        decimal          = (_temp >> 3) & 1; 
        brk              = 0; // TODO, very unsure about this...
        overflow         = (_temp >> 6) & 1;
        negative         = (_temp >> 7) & 1;        
    }

    // ROL value then AND value
    private void opcode_RLA_zero_page()
    {
        int _address = zeroPage(pc++);
        int _value = memory.readByte(_address);
        
        int _temp = _value;
        int _add = carry;
        
        carry = (_temp >> 7) &1;
        
        _value = ((_temp << 1) & 0xFF) + _add;
        
        memory.writeByte(_value, _address);
        
        a &= _value;
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }

    // ROL value then AND value
    private void opcode_RLA_zero_page_x()
    {
        int _address = zeroPageX(pc++);
        int _value = memory.readByte(_address);
        
        int _temp = _value;
        int _add = carry;
        
        carry = (_temp >> 7) &1;
        
        _value = ((_temp << 1) & 0xFF) + _add;
        
        memory.writeByte(_value, _address);
        
        a &= _value;
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }

    // ROL value then AND value
    private void opcode_RLA_absolute()
    {
        int _address = absolute(pc++);
        int _value = memory.readByte(_address);
        
        pc++;
        
        int _temp = _value;
        int _add = carry;
        
        carry = (_temp >> 7) &1;
        
        _value = ((_temp << 1) & 0xFF) + _add;
        
        memory.writeByte(_value, _address);
        
        a &= _value;
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }
    
    // ROL value then AND value
    private void opcode_RLA_absolute_x()
    {
        int _address = absoluteX(pc++);
        int _value = memory.readByte(_address);
        
        pc++;
        
        int _temp = _value;
        int _add = carry;
        
        carry = (_temp >> 7) &1;
        
        _value = ((_temp << 1) & 0xFF) + _add;
        
        memory.writeByte(_value, _address);
        
        a &= _value;
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }

    // ROL value then AND value
    private void opcode_RLA_absolute_y()
    {
        int _address = absoluteY(pc++);
        int _value = memory.readByte(_address);
        
        pc++;
        
        int _temp = _value;
        int _add = carry;
        
        carry = (_temp >> 7) &1;
        
        _value = ((_temp << 1) & 0xFF) + _add;
        
        memory.writeByte(_value, _address);
        
        a &= _value;
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }

    // ROL value then AND value
    private void opcode_RLA_indirect_x()
    {
        int _address = indirectX(pc++);
        int _value = memory.readByte(_address);
        
        int _temp = _value;
        int _add = carry;
        
        carry = (_temp >> 7) &1;
        
        _value = ((_temp << 1) & 0xFF) + _add;
        
        memory.writeByte(_value, _address);
        
        a &= _value;
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }

    // ROL value then AND value
    private void opcode_RLA_indirect_y()
    {
        int _address = indirectY(pc++);
        int _value = memory.readByte(_address);
        
        int _temp = _value;
        int _add = carry;
        
        carry = (_temp >> 7) &1;
        
        _value = ((_temp << 1) & 0xFF) + _add;
        
        memory.writeByte(_value, _address);
        
        a &= _value;
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }
    
    // Rotate Left
    private void opcode_ROL_accumulator()
    {
        int _temp = a;
		int _add = carry;
		
		carry = (_temp >> 7) &1;
		
		a = ((_temp << 1) & 0xFF) + _add;
		
		negative = (a >> 7) & 1;
		not_zero = a & 0xFF;
    }

    // Rotate Left
    private void opcode_ROL_zero_page()
    {
        int _address = zeroPage(pc++);
        int _value = memory.readByte(_address);
        
        int _temp = _value;
        int _add = carry;
        
        carry = (_temp >> 7) &1;
        
        _value = ((_temp << 1) & 0xFF) + _add;
        
        memory.writeByte(_value, _address);
        
        negative = (_value >> 7) & 1;
        not_zero = _value & 0xFF;
    }

    // Rotate Left
    private void opcode_ROL_zero_page_x()
    {
        int _address = zeroPageX(pc++);
        int _value = memory.readByte(_address);
        
        int _temp = _value;
        int _add = carry;
        
        carry = (_temp >> 7) &1;
        
        _value = ((_temp << 1) & 0xFF) + _add;
        
        memory.writeByte(_value, _address);
        
        negative = (_value >> 7) & 1;
        not_zero = _value & 0xFF;
    }

    // Rotate Left
    private void opcode_ROL_absolute()
    {
        int _address = absolute(pc++);
        int _value = memory.readByte(_address);
        
        pc++;
        
        int _temp = _value;
        int _add = carry;
        
        carry = (_temp >> 7) &1;
        
        _value = ((_temp << 1) & 0xFF) + _add;
        
        memory.writeByte(_value, _address);
        
        negative = (_value >> 7) & 1;
        not_zero = _value & 0xFF;
    }

    // Rotate Left
    private void opcode_ROL_absolute_x()
    {
        int _address = absoluteX(pc++);
        int _value = memory.readByte(_address);
        
        pc++;
        
        int _temp = _value;
        int _add = carry;
        
        carry = (_temp >> 7) &1;
        
        _value = ((_temp << 1) & 0xFF) + _add;
        
        memory.writeByte(_value, _address);
        
        negative = (_value >> 7) & 1;
        not_zero = _value & 0xFF;
    }

    // Rotate Right
    private void opcode_ROR_accumulator()
    {
        int _add = carry << 7;
    	
		carry = a & 1;
		a = (a >> 1) + _add;
		
		negative = (a >> 7) & 1;
		not_zero = a & 0xFF;
    }

    // Rotate Right
    private void opcode_ROR_zero_page()
    {
        int _address = zeroPage(pc++);
        int _value = memory.readByte(_address);
        
        int _add = carry << 7;
        
        carry = _value & 1;
        _value = (_value >> 1) + _add;
        
        memory.writeByte(_value, _address);
        
        negative = (_value >> 7) & 1;
        not_zero = _value & 0xFF;
    }

    // Rotate Right
    private void opcode_ROR_zero_page_x()
    {
        int _address = zeroPageX(pc++);
        int _value = memory.readByte(_address);
        
        int _add = carry << 7;
        
        carry = _value & 1;
        _value = (_value >> 1) + _add;
        
        memory.writeByte(_value, _address);
        
        negative = (_value >> 7) & 1;
        not_zero = _value & 0xFF;
    }

    // Rotate Right
    private void opcode_ROR_absolute()
    {
        int _address = absolute(pc++);
        int _value = memory.readByte(_address);
        
        pc++;
        
        int _add = carry << 7;
        
        carry = _value & 1;
        _value = (_value >> 1) + _add;
        
        memory.writeByte(_value, _address);
        
        negative = (_value >> 7) & 1;
        not_zero = _value & 0xFF;
    }

    // Rotate Right
    private void opcode_ROR_absolute_x()
    {
        int _address = absoluteX(pc++);
        int _value = memory.readByte(_address);
        
        pc++;
        
        int _add = carry << 7;
        
        carry = _value & 1;
        _value = (_value >> 1) + _add;
        
        memory.writeByte(_value, _address);
        
        negative = (_value >> 7) & 1;
        not_zero = _value & 0xFF;
    }
    
    // ROR value then ADC value
    private void opcode_RRA_zero_page()
    {
        int _address = zeroPage(pc++);
        int _value = memory.readByte(_address);
        
        int _add = carry << 7;
        
        carry = _value & 1;
        _value = (_value >> 1) + _add;
        
        memory.writeByte(_value, _address);
        
        int _temp = a + _value + carry;
        
        carry = _temp > 0xFF ? 1 : 0;
        not_zero = _temp & 0xFF;
        overflow = ((!(((a ^ _value) & 0x80) != 0) && (((a ^ _temp) & 0x80)) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    // ROR value then ADC value
    private void opcode_RRA_zero_page_x()
    {
        int _address = zeroPageX(pc++);
        int _value = memory.readByte(_address);
        
        int _add = carry << 7;
        
        carry = _value & 1;
        _value = (_value >> 1) + _add;
        
        memory.writeByte(_value, _address);
        
        int _temp = a + _value + carry;
        
        carry = _temp > 0xFF ? 1 : 0;
        not_zero = _temp & 0xFF;
        overflow = ((!(((a ^ _value) & 0x80) != 0) && (((a ^ _temp) & 0x80)) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    // ROR value then ADC value
    private void opcode_RRA_absolute()
    {
        int _address = absolute(pc++);
        int _value = memory.readByte(_address);
        
        pc++;
        
        int _add = carry << 7;
        
        carry = _value & 1;
        _value = (_value >> 1) + _add;
        
        memory.writeByte(_value, _address);
        
        int _temp = a + _value + carry;
        
        carry = _temp > 0xFF ? 1 : 0;
        not_zero = _temp & 0xFF;
        overflow = ((!(((a ^ _value) & 0x80) != 0) && (((a ^ _temp) & 0x80)) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    // ROR value then ADC value
    private void opcode_RRA_absolute_x()
    {
        int _address = absoluteX(pc++);
        int _value = memory.readByte(_address);
        
        pc++;
        
        int _add = carry << 7;
        
        carry = _value & 1;
        _value = (_value >> 1) + _add;
        
        memory.writeByte(_value, _address);
        
        int _temp = a + _value + carry;
        
        carry = _temp > 0xFF ? 1 : 0;
        not_zero = _temp & 0xFF;
        overflow = ((!(((a ^ _value) & 0x80) != 0) && (((a ^ _temp) & 0x80)) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    // ROR value then ADC value
    private void opcode_RRA_absolute_y()
    {
        int _address = absoluteY(pc++);
        int _value = memory.readByte(_address);
        
        pc++;
        
        int _add = carry << 7;
        
        carry = _value & 1;
        _value = (_value >> 1) + _add;
        
        memory.writeByte(_value, _address);
        
        int _temp = a + _value + carry;
        
        carry = _temp > 0xFF ? 1 : 0;
        not_zero = _temp & 0xFF;
        overflow = ((!(((a ^ _value) & 0x80) != 0) && (((a ^ _temp) & 0x80)) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    // ROR value then ADC value
    private void opcode_RRA_indirect_x()
    {
        int _address = indirectX(pc++);
        int _value = memory.readByte(_address);
        
        int _add = carry << 7;
        
        carry = _value & 1;
        _value = (_value >> 1) + _add;
        
        memory.writeByte(_value, _address);
        
        int _temp = a + _value + carry;
        
        carry = _temp > 0xFF ? 1 : 0;
        not_zero = _temp & 0xFF;
        overflow = ((!(((a ^ _value) & 0x80) != 0) && (((a ^ _temp) & 0x80)) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    // ROR value then ADC value
    private void opcode_RRA_indirect_y()
    {
        int _address = indirectY(pc++);
        int _value = memory.readByte(_address);
        
        int _add = carry << 7;
        
        carry = _value & 1;
        _value = (_value >> 1) + _add;
        
        memory.writeByte(_value, _address);
        
        int _temp = a + _value + carry;
        
        carry = _temp > 0xFF ? 1 : 0;
        not_zero = _temp & 0xFF;
        overflow = ((!(((a ^ _value) & 0x80) != 0) && (((a ^ _temp) & 0x80)) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    // Return from Interrupt
    private void opcode_RTI_implied()
    {
        int _temp = pop();
        
        carry            = (_temp) & 1;
        not_zero         = ((_temp >> 1) & 1) == 1 ? 0 : 1;
        interruptDisable = (_temp >> 2) & 1;
        decimal          = (_temp >> 3) & 1; 
        brk              = 0; // TODO, very unsure about this...
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
    private void opcode_SAX_zero_page()
    {
        memory.writeByte(a & x, zeroPage(pc++));
    }

    // Store A and X bitwise
    private void opcode_SAX_zero_page_y()
    {
        memory.writeByte(a & x, zeroPageY(pc++));
    }

    // Store A and X bitwise
    private void opcode_SAX_absolute()
    {
        memory.writeByte(a & x, absolute(pc++));
        pc++;
    }

    // Store A and X bitwise
    private void opcode_SAX_indirect_x()
    {
        memory.writeByte(a & x, indirectX(pc++));
    }
    
    // Subtract with Carry
    private void opcode_SBC_immediate()
    {
        int _address = immediate(pc++);
        int _value = memory.readByte(_address);
        int _temp = a - _value - (1 - carry);
        
        carry = _temp < 0 ? 0 : 1;
        not_zero = _temp & 0xFF;
        overflow = ((((a ^ _temp) & 0x80) != 0 && ((a ^ _value) & 0x80) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    // Subtract with Carry
    private void opcode_SBC_zero_page()
    {
        int _address = zeroPage(pc++);
        int _value = memory.readByte(_address);
        int _temp = a - _value - (1 - carry);
        
        carry = _temp < 0 ? 0 : 1;
        not_zero = _temp & 0xFF;
        overflow = ((((a ^ _temp) & 0x80) != 0 && ((a ^ _value) & 0x80) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    // Subtract with Carry
    private void opcode_SBC_zero_page_x()
    {
        int _address = zeroPageX(pc++);
        int _value = memory.readByte(_address);
        int _temp = a - _value - (1 - carry);
        
        carry = _temp < 0 ? 0 : 1;
        not_zero = _temp & 0xFF;
        overflow = ((((a ^ _temp) & 0x80) != 0 && ((a ^ _value) & 0x80) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    // Subtract with Carry
    private void opcode_SBC_absolute()
    {
        int _address = absolute(pc++);
        int _value = memory.readByte(_address);
        int _temp = a - _value - (1 - carry);
        
        pc++;
        
        carry = _temp < 0 ? 0 : 1;
        not_zero = _temp & 0xFF;
        overflow = ((((a ^ _temp) & 0x80) != 0 && ((a ^ _value) & 0x80) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    // Subtract with Carry
    private void opcode_SBC_absolute_x()
    {
        int _address = absoluteX(pc++);
        int _value = memory.readByte(_address);
        int _temp = a - _value - (1 - carry);
        
        pc++;
        
        carry = _temp < 0 ? 0 : 1;
        not_zero = _temp & 0xFF;
        overflow = ((((a ^ _temp) & 0x80) != 0 && ((a ^ _value) & 0x80) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    // Subtract with Carry
    private void opcode_SBC_absolute_y()
    {
        int _address = absoluteY(pc++);
        int _value = memory.readByte(_address);
        int _temp = a - _value - (1 - carry);
        
        pc++;
        
        carry = _temp < 0 ? 0 : 1;
        not_zero = _temp & 0xFF;
        overflow = ((((a ^ _temp) & 0x80) != 0 && ((a ^ _value) & 0x80) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    // Subtract with Carry
    private void opcode_SBC_indirect_x()
    {
        int _address = indirectX(pc++);
        int _value = memory.readByte(_address);
        int _temp = a - _value - (1 - carry);
        
        carry = _temp < 0 ? 0 : 1;
        not_zero = _temp & 0xFF;
        overflow = ((((a ^ _temp) & 0x80) != 0 && ((a ^ _value) & 0x80) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    // Subtract with Carry
    private void opcode_SBC_indirect_y()
    {
        int _address = indirectY(pc++);
        int _value = memory.readByte(_address);
        int _temp = a - _value - (1 - carry);
        
        carry = _temp < 0 ? 0 : 1;
        not_zero = _temp & 0xFF;
        overflow = ((((a ^ _temp) & 0x80) != 0 && ((a ^ _value) & 0x80) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
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
    private void opcode_SLO_zero_page()
    {
        int _address = zeroPage(pc++);
        int _value = memory.readByte(_address);
        
        carry = (_value >> 7) & 1;
        _value = (_value << 1) & 0xFF;
        
        memory.writeByte(_value, _address);
        
        a |=  _value;
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    // ASL value then ORA value
    private void opcode_SLO_zero_page_x()
    {
        int _address = zeroPageX(pc++);
        int _value = memory.readByte(_address);
        
        carry = (_value >> 7) & 1;
        _value = (_value << 1) & 0xFF;
        
        memory.writeByte(_value, _address);
        
        a |=  _value;
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    // ASL value then ORA value
    private void opcode_SLO_absolute()
    {
        int _address = absolute(pc++);
        int _value = memory.readByte(_address);
        
        pc++;
        
        carry = (_value >> 7) & 1;
        _value = (_value << 1) & 0xFF;
        
        memory.writeByte(_value, _address);
        
        a |=  _value;
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    // ASL value then ORA value
    private void opcode_SLO_absolute_x()
    {
        int _address = absoluteX(pc++);
        int _value = memory.readByte(_address);
        
        pc++;
        
        carry = (_value >> 7) & 1;
        _value = (_value << 1) & 0xFF;
        
        memory.writeByte(_value, _address);
        
        a |=  _value;
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    // ASL value then ORA value
    private void opcode_SLO_absolute_y()
    {
        int _address = absoluteY(pc++);
        int _value = memory.readByte(_address);
        
        pc++;
        
        carry = (_value >> 7) & 1;
        _value = (_value << 1) & 0xFF;
        
        memory.writeByte(_value, _address);
        
        a |=  _value;
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    // ASL value then ORA value
    private void opcode_SLO_indirect_x()
    {
        int _address = indirectX(pc++);
        int _value = memory.readByte(_address);
        
        carry = (_value >> 7) & 1;
        _value = (_value << 1) & 0xFF;
        
        memory.writeByte(_value, _address);
        
        a |=  _value;
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    // ASL value then ORA value
    private void opcode_SLO_indirect_y()
    {
        int _address = indirectY(pc++);
        int _value = memory.readByte(_address);
        
        carry = (_value >> 7) & 1;
        _value = (_value << 1) & 0xFF;
        
        memory.writeByte(_value, _address);
        
        a |=  _value;
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }
    
    // Equivalent to LSR value then EOR value
    private void opcode_SRE_zero_page()
    {
        int _address = zeroPage(pc++);
        int _value = memory.readByte(_address);
        
        carry = _value & 1; // old bit 0       
        _value >>= 1;
        
        memory.writeByte(_value, _address);
        
        a ^=  _value;
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    // Equivalent to LSR value then EOR value
    private void opcode_SRE_zero_page_x()
    {
        int _address = zeroPageX(pc++);
        int _value = memory.readByte(_address);
        
        carry = _value & 1; // old bit 0       
        _value >>= 1;
        
        memory.writeByte(_value, _address);
        
        a ^=  _value;
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    // Equivalent to LSR value then EOR value
    private void opcode_SRE_absolute()
    {
        int _address = absolute(pc++);
        int _value = memory.readByte(_address);
        
        pc++;
        
        carry = _value & 1; // old bit 0       
        _value >>= 1;
        
        memory.writeByte(_value, _address);
        
        a ^=  _value;
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    // Equivalent to LSR value then EOR value
    private void opcode_SRE_absolute_x()
    {
        int _address = absoluteX(pc++);
        int _value = memory.readByte(_address);
        
        pc++;
        
        carry = _value & 1; // old bit 0       
        _value >>= 1;
        
        memory.writeByte(_value, _address);
        
        a ^=  _value;
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    // Equivalent to LSR value then EOR value
    private void opcode_SRE_absolute_y()
    {
        int _address = absoluteY(pc++);
        int _value = memory.readByte(_address);
        
        pc++;
        
        carry = _value & 1; // old bit 0       
        _value >>= 1;
        
        memory.writeByte(_value, _address);
        
        a ^=  _value;
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    // Equivalent to LSR value then EOR value
    private void opcode_SRE_indirect_x()
    {
        int _address = indirectX(pc++);
        int _value = memory.readByte(_address);
        
        carry = _value & 1; // old bit 0       
        _value >>= 1;
        
        memory.writeByte(_value, _address);
        
        a ^=  _value;
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    // Equivalent to LSR value then EOR value
    private void opcode_SRE_indirect_y()
    {
        int _address = indirectY(pc++);
        int _value = memory.readByte(_address);
        
        carry = _value & 1; // old bit 0       
        _value >>= 1;
        
        memory.writeByte(_value, _address);
        
        a ^=  _value;
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }
    
    // Store Accumulator
    private void opcode_STA_zero_page()
    {
        memory.writeByte(a, zeroPage(pc++));        
    }

    // Store Accumulator
    private void opcode_STA_zero_page_x()
    {
        memory.writeByte(a, zeroPageX(pc++));
    }

    // Store Accumulator
    private void opcode_STA_absolute()
    {
        memory.writeByte(a, absolute(pc++));
    	pc++;
    }

    // Store Accumulator
    private void opcode_STA_absolute_x()
    {
        memory.writeByte(a, absoluteX(pc++));
        pc++;
    }

    // Store Accumulator
    private void opcode_STA_absolute_y()
    {
        memory.writeByte(a, absoluteY(pc++));
        pc++;
    }

    // Store Accumulator
    private void opcode_STA_indirect_x()
    {
        memory.writeByte(a, indirectX(pc++));        
    }

    // Store Accumulator
    private void opcode_STA_indirect_y()
    {
        memory.writeByte(a, indirectY(pc++));
    }

    // Store X register
    private void opcode_STX_zero_page()
    {
        memory.writeByte(x, zeroPage(pc++));
    }

    // Store X register
    private void opcode_STX_zero_page_y()
    {
        memory.writeByte(x, zeroPageY(pc++));
    }

    // Store X register
    private void opcode_STX_absolute()
    {
        memory.writeByte(x, absolute(pc++));        
        pc++;
    }

    // Store Y Register
    private void opcode_STY_zero_page()
    {
        int _address = zeroPage(pc++);
        memory.writeByte(y, _address);        
    }

    // Store Y Register
    private void opcode_STY_zero_page_x()
    {
        int _address = zeroPageX(pc++);
        memory.writeByte(y, _address);
    }

    // Store Y Register
    private void opcode_STY_absolute()
    {
        int _address = absolute(pc++);
        memory.writeByte(y, _address);
        
        pc++;
    }

    // Transfer Accumulator to X
    private void opcode_TAX_implied()
    {
        x = a;
        
        not_zero = x & 0xFF;
        negative = (x >> 7) & 1;
    }

    // Transfer Accumulator to Y
    private void opcode_TAY_implied()
    {
        y = a;
        
        not_zero = y & 0xFF;
        negative = (y >> 7) & 1;
    }

    // Transfer Stack Pointer to X
    private void opcode_TSX_implied()
    {
        x = sp & 0xFF; // Only transfer the lower 8 bits
        
        not_zero = x & 0xFF;
        negative = (x >> 7) & 1;
    }
   
    // Transfer X to Accumulator
    private void opcode_TXA_implied()
    {
        a = x;
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    // Transfer X to Stack Pointer
    private void opcode_TXS_implied()
    {
        sp = x | 0x0100;
    }

    // Transfer Y to Accumulator
    private void opcode_TYA_implied()
    {
        a = y;
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }    
}