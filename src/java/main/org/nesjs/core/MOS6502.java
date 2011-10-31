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
    
    public void init()
    {
    }
    
    public void reset()
    {
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
         *       ON reset, the pc and p (?) should be pushed onto the stack.
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

            System.out.print("PC now at: " + Utils.toHexString(pc));

            int _opcode = Memory.readUnsignedByte(pc++);

            System.out.println(", opcode is: " + Utils.toHexString(_opcode));

            switch(_opcode)
            {
                case 0x69: opcode_ADC(); break;
                case 0x65: opcode_ADC_zero_page(); break;
                case 0x75: opcode_ADC_zero_page_X(); break;
                case 0x6D: opcode_ADC_absolute(); break;
                case 0x7D: opcode_ADC_absolute_X(); break;
                case 0x29: opcode_AND(); break;
                case 0x25: opcode_AND_zero_page(); break;
                case 0x35: opcode_AND_zero_page_X(); break;
                case 0x2D: opcode_AND_absolute(); break;
                case 0x3D: opcode_AND_absolute_X(); break;
                case 0x39: opcode_AND_absolute_Y(); break;
                case 0x21: opcode_AND_indirect_X(); break;
                case 0x31: opcode_AND_indirect_Y(); break;
                case 0x0A: opcode_ASL(); break;
                case 0x06: opcode_ASL_zero_page(); break;
                case 0x16: opcode_ASL_zero_page_X(); break;
                case 0x0E: opcode_ASL_absolute(); break;
                case 0x1E: opcode_ASL_absolute_X(); break;
                case 0x90: opcode_BCC(); break;
                case 0xB0: opcode_BCS(); break;
                case 0xF0: opcode_BEQ(); break;
                case 0x24: opcode_BIT_zero_page(); break;
                case 0x2C: opcode_BIT_absolute(); break;
                case 0x30: opcode_BMI(); break;
                case 0xD0: opcode_BNE(); break;
                case 0x10: opcode_BPL(); break;
                case 0x00: opcode_BRK(); break;
                case 0x50: opcode_BVC(); break;
                case 0x70: opcode_BVS(); break;
                case 0x18: opcode_CLC(); break;
                case 0xD8: opcode_CLD(); break;
                case 0x58: opcode_CLI(); break;
                case 0xB8: opcode_CLV(); break;
                case 0xC9: opcode_CMP(); break;
                case 0xC5: opcode_CMP_zero_page(); break;
                case 0xD5: opcode_CMP_zero_page_X(); break;
                case 0xCD: opcode_CMP_absolute(); break;
                case 0xDD: opcode_CMP_absolute_X(); break;
                case 0xD9: opcode_CMP_absolute_Y(); break;
                case 0xC1: opcode_CMP_indirect_X(); break;
                case 0xD1: opcode_CMP_indirect_Y(); break;
                case 0xE0: opcode_CPX(); break;
                case 0xE4: opcode_CPX_zero_page(); break;
                case 0xEC: opcode_CPX_absolute(); break;
                case 0xC0: opcode_CPY(); break;
                case 0xC4: opcode_CPY_zero_page(); break;
                case 0xCC: opcode_CPY_absolute(); break;
                case 0xC6: opcode_DEC_zero_page(); break;
                case 0xD6: opcode_DEC_zero_page_X(); break;
                case 0xCE: opcode_DEC_absolute(); break;
                case 0xDE: opcode_DEC_absolute_X(); break;
                case 0xCA: opcode_DEX(); break;
                case 0x88: opcode_DEY(); break;
                case 0x49: opcode_EOR(); break;
                case 0x45: opcode_EOR_zero_page(); break;
                case 0x55: opcode_EOR_zero_page_X(); break;
                case 0x4D: opcode_EOR_absolute(); break;
                case 0x5D: opcode_EOR_absolute_X(); break;
                case 0x59: opcode_EOR_absolute_Y(); break;
                case 0x41: opcode_EOR_indirect_X(); break;
                case 0x51: opcode_EOR_indirect_Y(); break;
                case 0xE6: opcode_INC_zero_page(); break;
                case 0xF6: opcode_INC_zero_page_X(); break;
                case 0xEE: opcode_INC_absolute(); break;
                case 0xFE: opcode_INC_absolute_X(); break;
                case 0xE8: opcode_INX(); break;
                case 0xC8: opcode_INY(); break;
                case 0x4C: opcode_JMP(); break;
                case 0x6C: opcode_JMP_absolute(); break;
                case 0x20: opcode_JSR(); break;
                case 0xA9: opcode_LDA(); break;
                case 0xA5: opcode_LDA_zero_page(); break;
                case 0xB5: opcode_LDA_zero_page_X(); break;
                case 0xAD: opcode_LDA_absolute(); break;
                case 0xBD: opcode_LDA_absolute_X(); break;
                case 0xB9: opcode_LDA_absolute_Y(); break;
                case 0xA1: opcode_LDA_indirect_X(); break;
                case 0xB1: opcode_LDA_indirect_Y(); break;
                case 0xA2: opcode_LDX(); break;
                case 0xA6: opcode_LDX_zero_page(); break;
                case 0xB6: opcode_LDX_zero_page_Y(); break;
                case 0xAE: opcode_LDX_absolute(); break;
                case 0xBE: opcode_LDX_absolute_Y(); break;
                case 0xA0: opcode_LDY(); break;
                case 0xA4: opcode_LDY_zero_page(); break;
                case 0xB4: opcode_LDY_zero_page_X(); break;
                case 0xAC: opcode_LDY_absolute(); break;
                case 0xBC: opcode_LDY_absolute_X(); break;
                case 0x4A: opcode_LSR(); break;
                case 0x46: opcode_LSR_zero_page(); break;
                case 0x56: opcode_LSR_zero_page_X(); break;
                case 0x4E: opcode_LSR_absolute(); break;
                case 0x5E: opcode_LSR_absolute_X(); break;
                case 0xEA: opcode_NOP(); break;
                case 0x09: opcode_ORA(); break;
                case 0x05: opcode_ORA_zero_page(); break;
                case 0x15: opcode_ORA_zero_page_X(); break;
                case 0x0D: opcode_ORA_absolute(); break;
                case 0x1D: opcode_ORA_absolute_X(); break;
                case 0x19: opcode_ORA_absolute_Y(); break;
                case 0x01: opcode_ORA_indirect_X(); break;
                case 0x11: opcode_ORA_indirect_Y(); break;
                case 0x48: opcode_PHA(); break;
                case 0x08: opcode_PHP(); break;
                case 0x68: opcode_PLA(); break;
                case 0x28: opcode_PLP(); break;
                case 0x2A: opcode_ROL(); break;
                case 0x26: opcode_ROL_zero_page(); break;
                case 0x36: opcode_ROL_zero_page_X(); break;
                case 0x2E: opcode_ROL_absolute(); break;
                case 0x3E: opcode_ROL_absolute_X(); break;
                case 0x6A: opcode_ROR(); break;
                case 0x66: opcode_ROR_zero_page(); break;
                case 0x76: opcode_ROR_zero_page_X(); break;
                case 0x6E: opcode_ROR_absolute(); break;
                case 0x7E: opcode_ROR_absolute_X(); break;
                case 0x40: opcode_RTI(); break;
                case 0x60: opcode_RTS(); break;
                case 0xE9: opcode_SBC(); break;
                case 0xE5: opcode_SBC_zero_page(); break;
                case 0xF5: opcode_SBC_zero_page_X(); break;
                case 0xED: opcode_SBC_absolute(); break;
                case 0xFD: opcode_SBC_absolute_X(); break;
                case 0xF9: opcode_SBC_absolute_Y(); break;
                case 0xE1: opcode_SBC_indirect_X(); break;
                case 0xF1: opcode_SBC_indirect_Y(); break;
                case 0x38: opcode_SEC(); break;
                case 0xF8: opcode_SED(); break;
                case 0x78: opcode_SEI(); break;
                case 0x85: opcode_STA_zero_page(); break;
                case 0x95: opcode_STA_zero_page_X(); break;
                case 0x8D: opcode_STA_absolute(); break;
                case 0x9D: opcode_STA_absolute_X(); break;
                case 0x99: opcode_STA_absolute_Y(); break;
                case 0x81: opcode_STA_indirect_X(); break;
                case 0x91: opcode_STA_indirect_Y(); break;
                case 0x86: opcode_STX_zero_page(); break;
                case 0x96: opcode_STX_zero_page_Y(); break;
                case 0x8E: opcode_STX_absolute(); break;
                case 0x84: opcode_STY_zero_page(); break;
                case 0x94: opcode_STY_zero_page_X(); break;
                case 0x8C: opcode_STY_absolute(); break;
                case 0xAA: opcode_TAX(); break;
                case 0xA8: opcode_TAY(); break;
                case 0xBA: opcode_TSX(); break;
                case 0x8A: opcode_TXA(); break;
                case 0x9A: opcode_TXS(); break;
                case 0x98: opcode_TYA(); break;
                
                default: System.out.println("******* Unhandled opcode [" + _opcode + "]"); break;
            }
            
            // Mask to 16 bit
            pc = pc & 0xFFFF;
            
            setProcessorStatusRegisterFromFlags();
        }
        
        return _clocksRemain;
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
        int _decimal = 0; // not used        
        int _zero = not_zero != 0 ? 0 : 1;
        
        p = carry << 0 | (_zero << 1) | (interruptDisable << 2) | (_decimal << 3) | (brk << 4) | (1 << 5) | (overflow << 6) | (negative << 7);        
    }

    private void push(int aByte)
    {
        Memory.writeUnsignedByte(aByte, sp);
        sp--;
    }
    
//-------------------------------------------------------------
// Opcodes
//-------------------------------------------------------------    
    
    private void opcode_ADC()
    {
        throw new RuntimeException("opcode not implemented [opcode_ADC]");
    }

    private void opcode_ADC_zero_page()
    {
        throw new RuntimeException("opcode not implemented [opcode_ADC_zero_page]");
    }

    private void opcode_ADC_zero_page_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_ADC_zero_page_X]");
    }

    private void opcode_ADC_absolute()
    {
        throw new RuntimeException("opcode not implemented [opcode_ADC_absolute]");
    }

    private void opcode_ADC_absolute_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_ADC_absolute_X]");
    }

    private void opcode_AND()
    {
        throw new RuntimeException("opcode not implemented [opcode_AND]");
    }

    private void opcode_AND_zero_page()
    {
        throw new RuntimeException("opcode not implemented [opcode_AND_zero_page]");
    }

    private void opcode_AND_zero_page_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_AND_zero_page_X]");
    }

    private void opcode_AND_absolute()
    {
        throw new RuntimeException("opcode not implemented [opcode_AND_absolute]");
    }

    private void opcode_AND_absolute_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_AND_absolute_X]");
    }

    private void opcode_AND_absolute_Y()
    {
        throw new RuntimeException("opcode not implemented [opcode_AND_absolute_Y]");
    }

    private void opcode_AND_indirect_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_AND_indirect_X]");
    }

    private void opcode_AND_indirect_Y()
    {
        throw new RuntimeException("opcode not implemented [opcode_AND_indirect_Y]");
    }

    private void opcode_ASL()
    {
        throw new RuntimeException("opcode not implemented [opcode_ASL]");
    }

    private void opcode_ASL_zero_page()
    {
        throw new RuntimeException("opcode not implemented [opcode_ASL_zero_page]");
    }

    private void opcode_ASL_zero_page_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_ASL_zero_page_X]");
    }

    private void opcode_ASL_absolute()
    {
        throw new RuntimeException("opcode not implemented [opcode_ASL_absolute]");
    }

    private void opcode_ASL_absolute_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_ASL_absolute_X]");
    }

    private void opcode_BCC()
    {
        throw new RuntimeException("opcode not implemented [opcode_BCC]");
    }

    private void opcode_BCS()
    {
        throw new RuntimeException("opcode not implemented [opcode_BCS]");
    }

    private void opcode_BEQ()
    {
        throw new RuntimeException("opcode not implemented [opcode_BEQ]");
    }

    private void opcode_BIT_zero_page()
    {
        throw new RuntimeException("opcode not implemented [opcode_BIT_zero_page]");
    }

    private void opcode_BIT_absolute()
    {
        throw new RuntimeException("opcode not implemented [opcode_BIT_absolute]");
    }

    private void opcode_BMI()
    {
        throw new RuntimeException("opcode not implemented [opcode_BMI]");
    }

    private void opcode_BNE()
    {
        throw new RuntimeException("opcode not implemented [opcode_BNE]");
    }

    private void opcode_BPL()
    {
        throw new RuntimeException("opcode not implemented [opcode_BPL]");
    }

    private void opcode_BRK()
    {
        throw new RuntimeException("opcode not implemented [opcode_BRK]");
    }

    private void opcode_BVC()
    {
        throw new RuntimeException("opcode not implemented [opcode_BVC]");
    }

    private void opcode_BVS()
    {
        throw new RuntimeException("opcode not implemented [opcode_BVS]");
    }

    private void opcode_CLC()
    {
        throw new RuntimeException("opcode not implemented [opcode_CLC]");
    }

    private void opcode_CLD()
    {
        throw new RuntimeException("opcode not implemented [opcode_CLD]");
    }

    private void opcode_CLI()
    {
        throw new RuntimeException("opcode not implemented [opcode_CLI]");
    }

    private void opcode_CLV()
    {
        throw new RuntimeException("opcode not implemented [opcode_CLV]");
    }

    private void opcode_CMP()
    {
        throw new RuntimeException("opcode not implemented [opcode_CMP]");
    }

    private void opcode_CMP_zero_page()
    {
        throw new RuntimeException("opcode not implemented [opcode_CMP_zero_page]");
    }

    private void opcode_CMP_zero_page_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_CMP_zero_page_X]");
    }

    private void opcode_CMP_absolute()
    {
        throw new RuntimeException("opcode not implemented [opcode_CMP_absolute]");
    }

    private void opcode_CMP_absolute_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_CMP_absolute_X]");
    }

    private void opcode_CMP_absolute_Y()
    {
        throw new RuntimeException("opcode not implemented [opcode_CMP_absolute_Y]");
    }

    private void opcode_CMP_indirect_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_CMP_indirect_X]");
    }

    private void opcode_CMP_indirect_Y()
    {
        throw new RuntimeException("opcode not implemented [opcode_CMP_indirect_Y]");
    }

    private void opcode_CPX()
    {
        throw new RuntimeException("opcode not implemented [opcode_CPX]");
    }

    private void opcode_CPX_zero_page()
    {
        throw new RuntimeException("opcode not implemented [opcode_CPX_zero_page]");
    }

    private void opcode_CPX_absolute()
    {
        throw new RuntimeException("opcode not implemented [opcode_CPX_absolute]");
    }

    private void opcode_CPY()
    {
        throw new RuntimeException("opcode not implemented [opcode_CPY]");
    }

    private void opcode_CPY_zero_page()
    {
        throw new RuntimeException("opcode not implemented [opcode_CPY_zero_page]");
    }

    private void opcode_CPY_absolute()
    {
        throw new RuntimeException("opcode not implemented [opcode_CPY_absolute]");
    }

    private void opcode_DEC_zero_page()
    {
        throw new RuntimeException("opcode not implemented [opcode_DEC_zero_page]");
    }

    private void opcode_DEC_zero_page_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_DEC_zero_page_X]");
    }

    private void opcode_DEC_absolute()
    {
        throw new RuntimeException("opcode not implemented [opcode_DEC_absolute]");
    }

    private void opcode_DEC_absolute_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_DEC_absolute_X]");
    }

    private void opcode_DEX()
    {
        throw new RuntimeException("opcode not implemented [opcode_DEX]");
    }

    private void opcode_DEY()
    {
        throw new RuntimeException("opcode not implemented [opcode_DEY]");
    }

    private void opcode_EOR()
    {
        throw new RuntimeException("opcode not implemented [opcode_EOR]");
    }

    private void opcode_EOR_zero_page()
    {
        throw new RuntimeException("opcode not implemented [opcode_EOR_zero_page]");
    }

    private void opcode_EOR_zero_page_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_EOR_zero_page_X]");
    }

    private void opcode_EOR_absolute()
    {
        throw new RuntimeException("opcode not implemented [opcode_EOR_absolute]");
    }

    private void opcode_EOR_absolute_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_EOR_absolute_X]");
    }

    private void opcode_EOR_absolute_Y()
    {
        throw new RuntimeException("opcode not implemented [opcode_EOR_absolute_Y]");
    }

    private void opcode_EOR_indirect_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_EOR_indirect_X]");
    }

    private void opcode_EOR_indirect_Y()
    {
        throw new RuntimeException("opcode not implemented [opcode_EOR_indirect_Y]");
    }

    private void opcode_INC_zero_page()
    {
        throw new RuntimeException("opcode not implemented [opcode_INC_zero_page]");
    }

    private void opcode_INC_zero_page_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_INC_zero_page_X]");
    }

    private void opcode_INC_absolute()
    {
        throw new RuntimeException("opcode not implemented [opcode_INC_absolute]");
    }

    private void opcode_INC_absolute_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_INC_absolute_X]");
    }

    private void opcode_INX()
    {
        throw new RuntimeException("opcode not implemented [opcode_INX]");
    }

    private void opcode_INY()
    {
        throw new RuntimeException("opcode not implemented [opcode_INY]");
    }

    private void opcode_JMP()
    {
        pc = Memory.readWord(pc);
    }

    private void opcode_JMP_absolute()
    {
        throw new RuntimeException("opcode not implemented [opcode_JMP_absolute]");
    }

    private void opcode_JSR()
    {
        // Jump to subroutine
        int _address = Memory.readWord(pc);
        
        push((pc >> 8) & 255);
        push(pc & 255);
        
        pc = _address;
    }

    private void opcode_LDA()
    {
        throw new RuntimeException("opcode not implemented [opcode_LDA]");
    }

    private void opcode_LDA_zero_page()
    {
        throw new RuntimeException("opcode not implemented [opcode_LDA_zero_page]");
    }

    private void opcode_LDA_zero_page_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_LDA_zero_page_X]");
    }

    private void opcode_LDA_absolute()
    {
        throw new RuntimeException("opcode not implemented [opcode_LDA_absolute]");
    }

    private void opcode_LDA_absolute_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_LDA_absolute_X]");
    }

    private void opcode_LDA_absolute_Y()
    {
        throw new RuntimeException("opcode not implemented [opcode_LDA_absolute_Y]");
    }

    private void opcode_LDA_indirect_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_LDA_indirect_X]");
    }

    private void opcode_LDA_indirect_Y()
    {
        throw new RuntimeException("opcode not implemented [opcode_LDA_indirect_Y]");
    }

    private void opcode_LDX()
    {
        // Load X with memory
        this.x = Memory.readUnsignedByte(pc++);

        negative = (this.x >> 7) & 1;
        not_zero = this.x;
    }

    private void opcode_LDX_zero_page()
    {
        throw new RuntimeException("opcode not implemented [opcode_LDX_zero_page]");
    }

    private void opcode_LDX_zero_page_Y()
    {
        throw new RuntimeException("opcode not implemented [opcode_LDX_zero_page_Y]");
    }

    private void opcode_LDX_absolute()
    {
        throw new RuntimeException("opcode not implemented [opcode_LDX_absolute]");
    }

    private void opcode_LDX_absolute_Y()
    {
        throw new RuntimeException("opcode not implemented [opcode_LDX_absolute_Y]");
    }

    private void opcode_LDY()
    {
        throw new RuntimeException("opcode not implemented [opcode_LDY]");
    }

    private void opcode_LDY_zero_page()
    {
        throw new RuntimeException("opcode not implemented [opcode_LDY_zero_page]");
    }

    private void opcode_LDY_zero_page_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_LDY_zero_page_X]");
    }

    private void opcode_LDY_absolute()
    {
        throw new RuntimeException("opcode not implemented [opcode_LDY_absolute]");
    }

    private void opcode_LDY_absolute_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_LDY_absolute_X]");
    }

    private void opcode_LSR()
    {
        throw new RuntimeException("opcode not implemented [opcode_LSR]");
    }

    private void opcode_LSR_zero_page()
    {
        throw new RuntimeException("opcode not implemented [opcode_LSR_zero_page]");
    }

    private void opcode_LSR_zero_page_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_LSR_zero_page_X]");
    }

    private void opcode_LSR_absolute()
    {
        throw new RuntimeException("opcode not implemented [opcode_LSR_absolute]");
    }

    private void opcode_LSR_absolute_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_LSR_absolute_X]");
    }

    private void opcode_NOP()
    {
        // No operation
    }

    private void opcode_ORA()
    {
        throw new RuntimeException("opcode not implemented [opcode_ORA]");
    }

    private void opcode_ORA_zero_page()
    {
        throw new RuntimeException("opcode not implemented [opcode_ORA_zero_page]");
    }

    private void opcode_ORA_zero_page_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_ORA_zero_page_X]");
    }

    private void opcode_ORA_absolute()
    {
        throw new RuntimeException("opcode not implemented [opcode_ORA_absolute]");
    }

    private void opcode_ORA_absolute_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_ORA_absolute_X]");
    }

    private void opcode_ORA_absolute_Y()
    {
        throw new RuntimeException("opcode not implemented [opcode_ORA_absolute_Y]");
    }

    private void opcode_ORA_indirect_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_ORA_indirect_X]");
    }

    private void opcode_ORA_indirect_Y()
    {
        throw new RuntimeException("opcode not implemented [opcode_ORA_indirect_Y]");
    }

    private void opcode_PHA()
    {
        throw new RuntimeException("opcode not implemented [opcode_PHA]");
    }

    private void opcode_PHP()
    {
        throw new RuntimeException("opcode not implemented [opcode_PHP]");
    }

    private void opcode_PLA()
    {
        throw new RuntimeException("opcode not implemented [opcode_PLA]");
    }

    private void opcode_PLP()
    {
        throw new RuntimeException("opcode not implemented [opcode_PLP]");
    }

    private void opcode_ROL()
    {
        throw new RuntimeException("opcode not implemented [opcode_ROL]");
    }

    private void opcode_ROL_zero_page()
    {
        throw new RuntimeException("opcode not implemented [opcode_ROL_zero_page]");
    }

    private void opcode_ROL_zero_page_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_ROL_zero_page_X]");
    }

    private void opcode_ROL_absolute()
    {
        throw new RuntimeException("opcode not implemented [opcode_ROL_absolute]");
    }

    private void opcode_ROL_absolute_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_ROL_absolute_X]");
    }

    private void opcode_ROR()
    {
        throw new RuntimeException("opcode not implemented [opcode_ROR]");
    }

    private void opcode_ROR_zero_page()
    {
        throw new RuntimeException("opcode not implemented [opcode_ROR_zero_page]");
    }

    private void opcode_ROR_zero_page_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_ROR_zero_page_X]");
    }

    private void opcode_ROR_absolute()
    {
        throw new RuntimeException("opcode not implemented [opcode_ROR_absolute]");
    }

    private void opcode_ROR_absolute_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_ROR_absolute_X]");
    }

    private void opcode_RTI()
    {
        throw new RuntimeException("opcode not implemented [opcode_RTI]");
    }

    private void opcode_RTS()
    {
        throw new RuntimeException("opcode not implemented [opcode_RTS]");
    }

    private void opcode_SBC()
    {
        throw new RuntimeException("opcode not implemented [opcode_SBC]");
    }

    private void opcode_SBC_zero_page()
    {
        throw new RuntimeException("opcode not implemented [opcode_SBC_zero_page]");
    }

    private void opcode_SBC_zero_page_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_SBC_zero_page_X]");
    }

    private void opcode_SBC_absolute()
    {
        throw new RuntimeException("opcode not implemented [opcode_SBC_absolute]");
    }

    private void opcode_SBC_absolute_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_SBC_absolute_X]");
    }

    private void opcode_SBC_absolute_Y()
    {
        throw new RuntimeException("opcode not implemented [opcode_SBC_absolute_Y]");
    }

    private void opcode_SBC_indirect_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_SBC_indirect_X]");
    }

    private void opcode_SBC_indirect_Y()
    {
        throw new RuntimeException("opcode not implemented [opcode_SBC_indirect_Y]");
    }

    private void opcode_SEC()
    {
        // Set the carry flag
        carry = 1;
    }

    private void opcode_SED()
    {
        throw new RuntimeException("opcode not implemented [opcode_SED]");
    }

    private void opcode_SEI()
    {
        throw new RuntimeException("opcode not implemented [opcode_SEI]");
    }

    private void opcode_STA_zero_page()
    {
        throw new RuntimeException("opcode not implemented [opcode_STA_zero_page]");
    }

    private void opcode_STA_zero_page_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_STA_zero_page_X]");
    }

    private void opcode_STA_absolute()
    {
        throw new RuntimeException("opcode not implemented [opcode_STA_absolute]");
    }

    private void opcode_STA_absolute_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_STA_absolute_X]");
    }

    private void opcode_STA_absolute_Y()
    {
        throw new RuntimeException("opcode not implemented [opcode_STA_absolute_Y]");
    }

    private void opcode_STA_indirect_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_STA_indirect_X]");
    }

    private void opcode_STA_indirect_Y()
    {
        throw new RuntimeException("opcode not implemented [opcode_STA_indirect_Y]");
    }

    private void opcode_STX_zero_page()
    {
        int _address = Memory.readUnsignedByte(pc++);
        Memory.writeUnsignedByte(x, _address);
    }

    private void opcode_STX_zero_page_Y()
    {
        throw new RuntimeException("opcode not implemented [opcode_STX_zero_page_Y]");
    }

    private void opcode_STX_absolute()
    {
        throw new RuntimeException("opcode not implemented [opcode_STX_absolute]");
    }

    private void opcode_STY_zero_page()
    {
        throw new RuntimeException("opcode not implemented [opcode_STY_zero_page]");
    }

    private void opcode_STY_zero_page_X()
    {
        throw new RuntimeException("opcode not implemented [opcode_STY_zero_page_X]");
    }

    private void opcode_STY_absolute()
    {
        throw new RuntimeException("opcode not implemented [opcode_STY_absolute]");
    }

    private void opcode_TAX()
    {
        throw new RuntimeException("opcode not implemented [opcode_TAX]");
    }

    private void opcode_TAY()
    {
        throw new RuntimeException("opcode not implemented [opcode_TAY]");
    }

    private void opcode_TSX()
    {
        throw new RuntimeException("opcode not implemented [opcode_TSX]");
    }

    private void opcode_TXA()
    {
        throw new RuntimeException("opcode not implemented [opcode_TXA]");
    }

    private void opcode_TXS()
    {
        throw new RuntimeException("opcode not implemented [opcode_TXS]");
    }

    private void opcode_TYA()
    {
        throw new RuntimeException("opcode not implemented [opcode_TYA]");
    }    
}