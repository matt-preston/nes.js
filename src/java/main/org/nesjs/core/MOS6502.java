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
        Memory.resetLowMemory();
        
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

            int _opcode = Memory.readByte(pc++);

            System.out.printf(", opcode is: %s [%s]\n", Utils.toHexString(_opcode), Opcodes.name(_opcode));
            
            switch(_opcode)
            {
                case 0x69: opcode_ADC_immediate(); break;
                case 0x65: opcode_ADC_zero_page(); break;
                case 0x75: opcode_ADC_zero_page_X(); break;
                case 0x6D: opcode_ADC_absolute(); break;
                case 0x7D: opcode_ADC_absolute_X(); break;
                case 0x79: opcode_ADC_absolute_Y(); break;
                case 0x61: opcode_ADC_indirect_X(); break;
                case 0x71: opcode_ADC_indirect_Y(); break;
                case 0x29: opcode_AND_immediate(); break;
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
                case 0xC9: opcode_CMP_immediate(); break;
                case 0xC5: opcode_CMP_zero_page(); break;
                case 0xD5: opcode_CMP_zero_page_X(); break;
                case 0xCD: opcode_CMP_absolute(); break;
                case 0xDD: opcode_CMP_absolute_X(); break;
                case 0xD9: opcode_CMP_absolute_Y(); break;
                case 0xC1: opcode_CMP_indirect_X(); break;
                case 0xD1: opcode_CMP_indirect_Y(); break;
                case 0xE0: opcode_CPX_immediate(); break;
                case 0xE4: opcode_CPX_zero_page(); break;
                case 0xEC: opcode_CPX_absolute(); break;
                case 0xC0: opcode_CPY_immediate(); break;
                case 0xC4: opcode_CPY_zero_page(); break;
                case 0xCC: opcode_CPY_absolute(); break;
                case 0xC6: opcode_DEC_zero_page(); break;
                case 0xD6: opcode_DEC_zero_page_X(); break;
                case 0xCE: opcode_DEC_absolute(); break;
                case 0xDE: opcode_DEC_absolute_X(); break;
                case 0xCA: opcode_DEX(); break;
                case 0x88: opcode_DEY(); break;
                case 0x49: opcode_EOR_immediate(); break;
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
                case 0x4C: opcode_JMP_absolute(); break;
                case 0x6C: opcode_JMP_indirect(); break;
                case 0x20: opcode_JSR(); break;
                case 0xA3: opcode_LAX_indirect_X(); break;
                case 0xA9: opcode_LDA_immediate(); break;
                case 0xA5: opcode_LDA_zero_page(); break;
                case 0xB5: opcode_LDA_zero_page_X(); break;
                case 0xAD: opcode_LDA_absolute(); break;
                case 0xBD: opcode_LDA_absolute_X(); break;
                case 0xB9: opcode_LDA_absolute_Y(); break;
                case 0xA1: opcode_LDA_indirect_X(); break;
                case 0xB1: opcode_LDA_indirect_Y(); break;
                case 0xA2: opcode_LDX_immediate(); break;
                case 0xA6: opcode_LDX_zero_page(); break;
                case 0xB6: opcode_LDX_zero_page_Y(); break;
                case 0xAE: opcode_LDX_absolute(); break;
                case 0xBE: opcode_LDX_absolute_Y(); break;
                case 0xA0: opcode_LDY_immediate(); break;
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
                case 0x1A: opcode_NOP(); break;
                case 0x3A: opcode_NOP(); break;
                case 0x5A: opcode_NOP(); break;
                case 0x7A: opcode_NOP(); break;
                case 0xDA: opcode_NOP(); break;
                case 0xFA: opcode_NOP(); break;
                case 0x80: opcode_NOP_immediate(); break;
                case 0x04: opcode_NOP_zero_page(); break;
                case 0x44: opcode_NOP_zero_page(); break;
                case 0x64: opcode_NOP_zero_page(); break;
                case 0x0C: opcode_NOP_absolute(); break;
                case 0x1C: opcode_NOP_absolute_X(); break;
                case 0x3C: opcode_NOP_absolute_X(); break;
                case 0x5C: opcode_NOP_absolute_X(); break;
                case 0x7C: opcode_NOP_absolute_X(); break;
                case 0xDC: opcode_NOP_absolute_X(); break;
                case 0xFC: opcode_NOP_absolute_X(); break;
                case 0x14: opcode_NOP_zero_page_X(); break;
                case 0x34: opcode_NOP_zero_page_X(); break;
                case 0x54: opcode_NOP_zero_page_X(); break;
                case 0x74: opcode_NOP_zero_page_X(); break;
                case 0xD4: opcode_NOP_zero_page_X(); break;
                case 0xF4: opcode_NOP_zero_page_X(); break;
                case 0x09: opcode_ORA_immediate(); break;
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
                case 0xE9: opcode_SBC_immediate(); break;
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
        
        /*
        if(p > 0xFF)
        {
            System.out.println("oops, p is: " + Utils.toHexString(p));
            System.out.println("carry: " + carry);
            System.out.println("interrupt disable: " + interruptDisable);
            System.out.println("decimal: " + decimal);
            System.out.println("brk: " + brk);
            System.out.println("overflow: " + overflow);
            System.out.println("negative: " + negative);
        }
        */
    }

    
//-------------------------------------------------------------
// Stack
//-------------------------------------------------------------    

    private void push(int aByte)
    {
        Memory.writeByte(aByte, sp);
        sp--;
    }
    
    private int pop()
    {
        sp++;
        sp = 0x0100 | (sp & 0xFF);
        return Memory.readByte(sp);
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
// Opcodes
//-------------------------------------------------------------    
    
    private void opcode_ADC_immediate()
    {
        // Add with Carry
        int _value = Addressing.immediate(pc++);
        int _temp = a + _value + carry;
        
        carry = _temp > 0xFF ? 1 : 0;
        not_zero = _temp & 0xFF;
        overflow = ((!(((a ^ _value) & 0x80) != 0) && (((a ^ _temp) & 0x80)) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    private void opcode_ADC_zero_page()
    {
        // Add with Carry
        int _address = Addressing.zeroPage(pc++);
        int _value = Memory.readByte(_address);
        int _temp = a + _value + carry;
        
        carry = _temp > 0xFF ? 1 : 0;
        not_zero = _temp & 0xFF;
        overflow = ((!(((a ^ _value) & 0x80) != 0) && (((a ^ _temp) & 0x80)) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    private void opcode_ADC_zero_page_X()
    {
        // Add with Carry
        int _address = Addressing.zeroPageX(pc++, x);
        int _value = Memory.readByte(_address);
        int _temp = a + _value + carry;
        
        carry = _temp > 0xFF ? 1 : 0;
        not_zero = _temp & 0xFF;
        overflow = ((!(((a ^ _value) & 0x80) != 0) && (((a ^ _temp) & 0x80)) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    private void opcode_ADC_absolute()
    {
        // Add with Carry
        int _address = Addressing.absolute(pc++);
        int _value = Memory.readByte(_address);
        int _temp = a + _value + carry;
        
        pc++;
        
        carry = _temp > 0xFF ? 1 : 0;
        not_zero = _temp & 0xFF;
        overflow = ((!(((a ^ _value) & 0x80) != 0) && (((a ^ _temp) & 0x80)) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    private void opcode_ADC_absolute_X()
    {
        // Add with Carry
        int _address = Addressing.absoluteX(pc++, x);
        int _value = Memory.readByte(_address);
        int _temp = a + _value + carry;
        
        pc++;
        
        carry = _temp > 0xFF ? 1 : 0;
        not_zero = _temp & 0xFF;
        overflow = ((!(((a ^ _value) & 0x80) != 0) && (((a ^ _temp) & 0x80)) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }
    
    private void opcode_ADC_absolute_Y()
    {
        // Add with Carry
        int _address = Addressing.absoluteY(pc++, y);
        int _value = Memory.readByte(_address);
        int _temp = a + _value + carry;
        
        pc++;
        
        carry = _temp > 0xFF ? 1 : 0;
        not_zero = _temp & 0xFF;
        overflow = ((!(((a ^ _value) & 0x80) != 0) && (((a ^ _temp) & 0x80)) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    private void opcode_ADC_indirect_X()
    {
        // Add with Carry
        int _value = Memory.readByte(Addressing.indirectX(pc++, x));
        int _temp = a + _value + carry;
        
        carry = _temp > 0xFF ? 1 : 0;
        not_zero = _temp & 0xFF;
        overflow = ((!(((a ^ _value) & 0x80) != 0) && (((a ^ _temp) & 0x80)) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    private void opcode_ADC_indirect_Y()
    {
        // Add with Carry
        int _value = Memory.readByte(Addressing.indirectY(pc++, y));
        int _temp = a + _value + carry;
        
        carry = _temp > 0xFF ? 1 : 0;
        not_zero = _temp & 0xFF;
        overflow = ((!(((a ^ _value) & 0x80) != 0) && (((a ^ _temp) & 0x80)) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    private void opcode_AND_immediate()
    {
        // Logical AND
        a &= Addressing.immediate(pc++);
        
        negative = (a >> 7) & 1;
        not_zero = a;        
    }

    private void opcode_AND_zero_page()
    {
        // Logical AND
        int _address = Addressing.zeroPage(pc++);
        a &= Memory.readByte(_address);
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }

    private void opcode_AND_zero_page_X()
    {
        // Logical AND
        int _address = Addressing.zeroPageX(pc++, x);
        a &= Memory.readByte(_address);
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }

    private void opcode_AND_absolute()
    {
        // Logical AND
        int _address = Addressing.absolute(pc++);
        a &= Memory.readByte(_address);
        
        pc++;
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }

    private void opcode_AND_absolute_X()
    {
        // Logical AND
        int _address = Addressing.absoluteX(pc++, x);
        a &= Memory.readByte(_address);
        
        pc++;
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }

    private void opcode_AND_absolute_Y()
    {
        // Logical AND
        int _address = Addressing.absoluteY(pc++, y);
        a &= Memory.readByte(_address);
        
        pc++;
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }

    private void opcode_AND_indirect_X()
    {
        // Logical AND
        int _value = Addressing.indirectX(pc++, x);
        a &= Memory.readByte(_value);
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }

    private void opcode_AND_indirect_Y()
    {
        // Logical AND
        int _value = Addressing.indirectY(pc++, y);
        a &= Memory.readByte(_value);
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }

    private void opcode_ASL()
    {
        // Arithmetic Shift Left
        carry = (a >> 7) & 1;
        a = (a << 1) & 0xFF;
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }

    private void opcode_ASL_zero_page()
    {
        // Arithmetic Shift Left
        int _address = Addressing.zeroPage(pc++);
        int _value = Memory.readByte(_address);
        
        carry = (_value >> 7) & 1;
        _value = (_value << 1) & 0xFF;
        
        Memory.writeByte(_value, _address);
        
        negative = (_value >> 7) & 1;
        not_zero = _value;
    }

    private void opcode_ASL_zero_page_X()
    {
        // Arithmetic Shift Left
        int _address = Addressing.zeroPageX(pc++, x);
        int _value = Memory.readByte(_address);
        
        carry = (_value >> 7) & 1;
        _value = (_value << 1) & 0xFF;
        
        Memory.writeByte(_value, _address);
        
        negative = (_value >> 7) & 1;
        not_zero = _value;
    }

    private void opcode_ASL_absolute()
    {
        // Arithmetic Shift Left
        int _address = Addressing.absolute(pc++);
        int _value = Memory.readByte(_address);
        
        pc++;
        
        carry = (_value >> 7) & 1;
        _value = (_value << 1) & 0xFF;
        
        Memory.writeByte(_value, _address);
        
        negative = (_value >> 7) & 1;
        not_zero = _value;
    }

    private void opcode_ASL_absolute_X()
    {
        // Arithmetic Shift Left
        int _address = Addressing.absoluteX(pc++, x);
        int _value = Memory.readByte(_address);
        
        pc++;
        
        carry = (_value >> 7) & 1;
        _value = (_value << 1) & 0xFF;
        
        Memory.writeByte(_value, _address);
        
        negative = (_value >> 7) & 1;
        not_zero = _value;
    }

    private void opcode_BCC()
    {
        // Branch if Carry Clear
        if(carry == 0)
        {
            int _relative = Addressing.relative(pc++);
            pc += _relative;
        }
        else
        {
            pc++;
        }
    }

    private void opcode_BCS()
    {
        // Branch if Carry Set
        if(carry > 0)
        {
            int _relative = Addressing.relative(pc++); 
            pc += _relative;
        }
        else
        {
            pc++;
        }
    }

    private void opcode_BEQ()
    {
        // Branch if Equal
        if(isZeroFlagSet())
        {            
            int _relative = Addressing.relative(pc++);
            pc += _relative;
        }
        else
        {
            pc++;
        }
    }

    private void opcode_BIT_zero_page()
    {
        // Bit Test
        int _address = Addressing.zeroPage(pc++);        
        int _value = Memory.readByte(_address);
        
        negative = (_value >> 7) & 1;
        overflow = (_value >> 6) & 1;
        _value &= a;
        not_zero = _value;     
    }

    private void opcode_BIT_absolute()
    {
        // Bit Test
        int _address = Addressing.absolute(pc++);        
        int _value = Memory.readByte(_address);
        
        pc++;
        
        negative = (_value >> 7) & 1;
        overflow = (_value >> 6) & 1;
        _value &= a;
        not_zero = _value;        
    }

    private void opcode_BMI()
    {
    	// Branch if Minus
    	if(negative != 0)
    	{
    		int _address = Addressing.relative(pc++);
    		pc += _address;
    	}
    	else
    	{
    		pc++;
    	}
    }

    private void opcode_BNE()
    {
        // Branch if Not Equal
        if(!isZeroFlagSet())
        {            
            int _relative = Addressing.relative(pc++);
            pc += _relative;
        }
        else
        {
            pc++;
        }
    }

    private void opcode_BPL()
    {
        // Branch if Positive
        if(negative == 0)
        {
            int _relative = Addressing.relative(pc++);
            pc += _relative;
        }
        else
        {
            pc++;
        }
    }

    private void opcode_BRK()
    {
        throw new RuntimeException("opcode not implemented [opcode_BRK]");
    }

    private void opcode_BVC()
    {
        // Branch if Overflow Clear
        if(overflow == 0)
        {
            int _relative = Addressing.relative(pc++);
            pc += _relative;
        }
        else
        {
            pc++;
        }
    }

    private void opcode_BVS()
    {
        // Branch if Overflow Set
        if(overflow != 0)
        {
            int _relative = Addressing.relative(pc++);
            pc += _relative;
        }
        else
        {
            pc++;
        }
    }

    private void opcode_CLC()
    {
        // Clear Carry Flag
        carry = 0;
    }

    private void opcode_CLD()
    {
        // Clear Decimal Mode
        decimal = 0;
    }

    private void opcode_CLI()
    {
        throw new RuntimeException("opcode not implemented [opcode_CLI]");
    }

    private void opcode_CLV()
    {
        // Clear Overflow Flag
        overflow = 0;
    }

    private void opcode_CMP_immediate()
    {
        // Compare
        int _temp = a - Addressing.immediate(pc++);
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;;
        negative = (_temp >> 7) & 1;        
    }

    private void opcode_CMP_zero_page()
    {
        // Compare
        int _address = Addressing.zeroPage(pc++);
        int _temp = a - Memory.readByte(_address);
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;;
        negative = (_temp >> 7) & 1;
    }

    private void opcode_CMP_zero_page_X()
    {
        // Compare
        int _address = Addressing.zeroPageX(pc++, x);
        int _temp = a - Memory.readByte(_address);
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;;
        negative = (_temp >> 7) & 1;
    }

    private void opcode_CMP_absolute()
    {
        // Compare
        int _address = Addressing.absolute(pc++);
        int _temp = a - Memory.readByte(_address);
        
        pc++;
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;;
        negative = (_temp >> 7) & 1;
    }

    private void opcode_CMP_absolute_X()
    {
        // Compare
        int _address = Addressing.absoluteX(pc++, x);
        int _temp = a - Memory.readByte(_address);
        
        pc++;
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;;
        negative = (_temp >> 7) & 1;
    }

    private void opcode_CMP_absolute_Y()
    {
        // Compare
        int _address = Addressing.absoluteY(pc++, y);
        int _temp = a - Memory.readByte(_address);
        
        pc++;
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;;
        negative = (_temp >> 7) & 1;
    }

    private void opcode_CMP_indirect_X()
    {
        // Compare
        int _address = Addressing.indirectX(pc++, x);
        int _temp = a - Memory.readByte(_address);
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;;
        negative = (_temp >> 7) & 1;
    }

    private void opcode_CMP_indirect_Y()
    {
        // Compare
        int _address = Addressing.indirectY(pc++, y);
        int _temp = a - Memory.readByte(_address);
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;;
        negative = (_temp >> 7) & 1;
    }

    private void opcode_CPX_immediate()
    {
        // Compare X Register
        int _temp = x - Addressing.immediate(pc++);
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;;
        negative = (_temp >> 7) & 1;
    }

    private void opcode_CPX_zero_page()
    {
        // Compare X Register
        int _address = Addressing.zeroPage(pc++);
        int _temp = x - Memory.readByte(_address);
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;;
        negative = (_temp >> 7) & 1;
    }

    private void opcode_CPX_absolute()
    {
        // Compare X Register
        int _address = Addressing.absolute(pc++);
        int _temp = x - Memory.readByte(_address);
        
        pc++;
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;;
        negative = (_temp >> 7) & 1;
    }

    private void opcode_CPY_immediate()
    {
        // Compare Y Register
        int _temp = y - Addressing.immediate(pc++);
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;;
        negative = (_temp >> 7) & 1;
    }

    private void opcode_CPY_zero_page()
    {
        // Compare Y Register
        int _address = Addressing.zeroPage(pc++);
        int _temp = y - Memory.readByte(_address);
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;;
        negative = (_temp >> 7) & 1;
    }

    private void opcode_CPY_absolute()
    {
        // Compare Y Register
        int _address = Addressing.absolute(pc++);
        int _temp = y - Memory.readByte(_address);
        
        pc++;
        
        carry = (_temp >= 0 ? 1:0);
        not_zero = _temp & 0xFF;;
        negative = (_temp >> 7) & 1;
    }

    private void opcode_DEC_zero_page()
    {
        // Decrement Memory
        int _address = Addressing.zeroPage(pc++);
        
        int _value = Memory.readByte(_address) - 1;
        
        Memory.writeByte(_value, _address);
        
        not_zero = _value & 0xFF;
        negative = (_value >> 7) & 1;
    }

    private void opcode_DEC_zero_page_X()
    {
        // Decrement Memory
        int _address = Addressing.zeroPageX(pc++, x);
        
        int _value = Memory.readByte(_address) - 1;
        
        Memory.writeByte(_value, _address);
        
        not_zero = _value & 0xFF;
        negative = (_value >> 7) & 1;
    }

    private void opcode_DEC_absolute()
    {
        // Decrement Memory
        int _address = Addressing.absolute(pc++);
        
        pc++;
        
        int _value = Memory.readByte(_address) - 1;
        
        Memory.writeByte(_value, _address);
        
        not_zero = _value & 0xFF;
        negative = (_value >> 7) & 1;
    }

    private void opcode_DEC_absolute_X()
    {
        // Decrement Memory
        int _address = Addressing.absoluteX(pc++, x);
        
        pc++;
        
        int _value = Memory.readByte(_address) - 1;
        
        Memory.writeByte(_value, _address);
        
        not_zero = _value & 0xFF;
        negative = (_value >> 7) & 1;
    }

    private void opcode_DEX()
    {
        // Decrement X Register
        x = (x - 1) & 0xFF;
        
        not_zero = x & 0xFF;
        negative = (x >> 7) & 1;
    }

    private void opcode_DEY()
    {
        // Decrement Y Register
        y = (y - 1) & 0xFF;
        
        not_zero = y & 0xFF;
        negative = (y >> 7) & 1;
    }

    private void opcode_EOR_immediate()
    {
        // Exclusive OR
        a ^=  Addressing.immediate(pc++);
        
        not_zero = a & 0xFF;;
        negative = (a >> 7) & 1;        
    }

    private void opcode_EOR_zero_page()
    {
        // Exclusive OR
        int _address = Addressing.zeroPage(pc++);
        a ^=  Memory.readByte(_address);
        
        not_zero = a & 0xFF;;
        negative = (a >> 7) & 1;
    }

    private void opcode_EOR_zero_page_X()
    {
        // Exclusive OR
        int _address = Addressing.zeroPageX(pc++, x);
        a ^=  Memory.readByte(_address);
        
        not_zero = a & 0xFF;;
        negative = (a >> 7) & 1;
    }

    private void opcode_EOR_absolute()
    {
        // Exclusive OR
        int _address = Addressing.absolute(pc++);
        a ^=  Memory.readByte(_address);
        
        pc++;
        
        not_zero = a & 0xFF;;
        negative = (a >> 7) & 1;
    }

    private void opcode_EOR_absolute_X()
    {
        // Exclusive OR
        int _address = Addressing.absoluteX(pc++, x);
        a ^=  Memory.readByte(_address);
        
        pc++;
        
        not_zero = a & 0xFF;;
        negative = (a >> 7) & 1;
    }

    private void opcode_EOR_absolute_Y()
    {
        // Exclusive OR
        int _address = Addressing.absoluteY(pc++, y);
        a ^=  Memory.readByte(_address);
        
        pc++;
        
        not_zero = a & 0xFF;;
        negative = (a >> 7) & 1;
    }

    private void opcode_EOR_indirect_X()
    {
        // Exclusive OR
        int _value = Addressing.indirectX(pc++, x);
        a ^=  Memory.readByte(_value);
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    private void opcode_EOR_indirect_Y()
    {
        // Exclusive OR
        int _value = Addressing.indirectY(pc++, y);
        a ^=  Memory.readByte(_value);
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    private void opcode_INC_zero_page()
    {
        // Increment Memory
        int _address = Addressing.zeroPage(pc++);
        
        int _value = Memory.readByte(_address) + 1;
        
        Memory.writeByte(_value, _address);
        
        not_zero = _value & 0xFF;
        negative = (_value >> 7) & 1;
    }

    private void opcode_INC_zero_page_X()
    {
        // Increment Memory
        int _address = Addressing.zeroPageX(pc++, x);
        
        int _value = Memory.readByte(_address) + 1;
        
        Memory.writeByte(_value, _address);
        
        not_zero = _value & 0xFF;
        negative = (_value >> 7) & 1;
    }

    private void opcode_INC_absolute()
    {
        // Increment Memory
        int _address = Addressing.absolute(pc++);
        
        pc++;
        
        int _value = Memory.readByte(_address) + 1;
        
        Memory.writeByte(_value, _address);
        
        not_zero = _value & 0xFF;
        negative = (_value >> 7) & 1;
    }

    private void opcode_INC_absolute_X()
    {
        // Increment Memory
        int _address = Addressing.absoluteX(pc++, x);
        
        pc++;
        
        int _value = Memory.readByte(_address) + 1;
        
        Memory.writeByte(_value, _address);
        
        not_zero = _value & 0xFF;
        negative = (_value >> 7) & 1;
    }

    private void opcode_INX()
    {
       // Increment X Register
        x = (x + 1) & 0xFF;
        
        not_zero = x & 0xFF;
        negative = (x >> 7) & 1;
    }

    private void opcode_INY()
    {
        // Increment Y Register
        y = (y + 1) & 0xFF;
        
        not_zero = y & 0xFF;
        negative = (y >> 7) & 1;        
    }

    private void opcode_JMP_absolute()
    {
        // Jump to target address
        pc = Addressing.absolute(pc);
    }

    private void opcode_JMP_indirect()
    {
        // Jump to target address
        pc = Addressing.indirect(pc);   
    }
    
    private void opcode_JSR()
    {
        // Jump to subroutine
        int _address = Addressing.absolute(pc);
        
        pushWord((pc + 2) - 1);
        
        pc = _address;
    }

    private void opcode_LAX_indirect_X()
    {
        // Load Accumulator and X with memory
        int _address = Addressing.indirectX(pc++, x);
        a = Memory.readByte(_address);
        x = a;
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }
    
    private void opcode_LDA_immediate()
    {
        // Load Accumulator
        a = Addressing.immediate(pc++);
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }

    private void opcode_LDA_zero_page()
    {
    	// Load Accumulator
        int _address = Addressing.zeroPage(pc++);
    	a = Memory.readByte(_address);
    	
    	negative = (a >> 7) & 1;
        not_zero = a;
    }

    private void opcode_LDA_zero_page_X()
    {
        // Load Accumulator
        int _address = Addressing.zeroPageX(pc++, x);
        a = Memory.readByte(_address);
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }

    private void opcode_LDA_absolute()
    {
        // Load Accumulator
        int _address = Addressing.absolute(pc++); 
        a = Memory.readByte(_address);
        
        negative = (a >> 7) & 1;
        not_zero = a;
        
        pc++;
    }

    private void opcode_LDA_absolute_X()
    {
        // Load Accumulator
        int _address = Addressing.absoluteX(pc++, x); 
        a = Memory.readByte(_address);
        
        negative = (a >> 7) & 1;
        not_zero = a;
        
        pc++;
    }

    private void opcode_LDA_absolute_Y()
    {
        // Load Accumulator
        int _address = Addressing.absoluteY(pc++, y); 
        a = Memory.readByte(_address);
        
        negative = (a >> 7) & 1;
        not_zero = a;
        
        pc++;
    }

    private void opcode_LDA_indirect_X()
    {
    	// Load Accumulator
        int _address = Addressing.indirectX(pc++, x);
        a = Memory.readByte(_address);
        
    	negative = (a >> 7) & 1;
        not_zero = a;
    }

    private void opcode_LDA_indirect_Y()
    {
        // Load Accumulator
        int _address = Addressing.indirectY(pc++, y);
        a = Memory.readByte(_address);
        
        negative = (a >> 7) & 1;
        not_zero = a;
    }

    private void opcode_LDX_immediate()
    {
        // Load X with memory
        x = Addressing.immediate(pc++);
        
        negative = (x >> 7) & 1;
        not_zero = x;
    }

    private void opcode_LDX_zero_page()
    {
        // Load X with memory
        int _address = Addressing.zeroPage(pc++);
        x = Memory.readByte(_address);
        
        negative = (x >> 7) & 1;
        not_zero = x;
    }

    private void opcode_LDX_zero_page_Y()
    {
        // Load X with memory
        int _address = Addressing.zeroPageY(pc++, y);
        x = Memory.readByte(_address);
        
        negative = (x >> 7) & 1;
        not_zero = x;
    }

    private void opcode_LDX_absolute()
    {
        // Load X with memory
        int _address = Addressing.absolute(pc++);
        this.x = Memory.readByte(_address);
        
        negative = (x >> 7) & 1;
        not_zero = x;
        
        pc++;
    }

    private void opcode_LDX_absolute_Y()
    {
        // Load X with memory
        int _address = Addressing.absoluteY(pc++, y);
        this.x = Memory.readByte(_address);
        
        negative = (x >> 7) & 1;
        not_zero = x;
        
        pc++;
    }

    private void opcode_LDY_immediate()
    {
        // Load Y Register
        y = Addressing.immediate(pc++);
        
        negative = (y >> 7) & 1;
        not_zero = y;
    }

    private void opcode_LDY_zero_page()
    {
        // Load Y Register
        int _address = Addressing.zeroPage(pc++);
        y = Memory.readByte(_address);
        
        negative = (y >> 7) & 1;
        not_zero = y;
    }

    private void opcode_LDY_zero_page_X()
    {
        // Load Y Register
        int _address = Addressing.zeroPageX(pc++, x);
        y = Memory.readByte(_address);
        
        negative = (y >> 7) & 1;
        not_zero = y;
    }

    private void opcode_LDY_absolute()
    {
        // Load Y Register
        int _address = Addressing.absolute(pc++);
        y = Memory.readByte(_address);
        
        pc++;
        
        negative = (y >> 7) & 1;
        not_zero = y;
    }

    private void opcode_LDY_absolute_X()
    {
        // Load Y Register
        int _address = Addressing.absoluteX(pc++, x);
        y = Memory.readByte(_address);
        
        pc++;
        
        negative = (y >> 7) & 1;
        not_zero = y;
    }

    private void opcode_LSR()
    {
        // Logical Shift Right
        carry = a & 1; // old bit 0       
        a >>= 1;
        
        not_zero = a;
        negative = 0;        
    }

    private void opcode_LSR_zero_page()
    {
        // Logical Shift Right
        int _address = Addressing.zeroPage(pc++);
        int _value = Memory.readByte(_address);
        
        carry = _value & 1; // old bit 0       
        _value >>= 1;
        
        Memory.writeByte(_value, _address);
        
        not_zero = _value;
        negative = 0;
    }

    private void opcode_LSR_zero_page_X()
    {
        // Logical Shift Right
        int _address = Addressing.zeroPageX(pc++, x);
        int _value = Memory.readByte(_address);
        
        carry = _value & 1; // old bit 0       
        _value >>= 1;
        
        Memory.writeByte(_value, _address);
        
        not_zero = _value;
        negative = 0;
    }

    private void opcode_LSR_absolute()
    {
        // Logical Shift Right
        int _address = Addressing.absolute(pc++);
        int _value = Memory.readByte(_address);
        
        pc++;
        
        carry = _value & 1; // old bit 0       
        _value >>= 1;
        
        Memory.writeByte(_value, _address);
        
        not_zero = _value;
        negative = 0;
    }

    private void opcode_LSR_absolute_X()
    {
        // Logical Shift Right
        int _address = Addressing.absoluteX(pc++, x);
        int _value = Memory.readByte(_address);
        
        pc++;
        
        carry = _value & 1; // old bit 0       
        _value >>= 1;
        
        Memory.writeByte(_value, _address);
        
        not_zero = _value;
        negative = 0;
    }

    private void opcode_NOP()
    {
        // No operation
    }
    
    private void opcode_NOP_immediate()
    {
        // No operation
        pc++;
    }
    
    private void opcode_NOP_zero_page()
    {
       // No operation
       pc++;
    }

    private void opcode_NOP_absolute()
    {
        // No operation
        pc = pc + 2;
    }
    
    private void opcode_NOP_absolute_X()
    {
        // No operation
        pc = pc + 2;
    }
    
    private void opcode_NOP_zero_page_X()
    {
        pc++;
    }
    
    private void opcode_ORA_immediate()
    {
        // Logical Inclusive OR
        a |=  Addressing.immediate(pc++);
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1; 
    }

    private void opcode_ORA_zero_page()
    {
        // Logical Inclusive OR
        int _address = Addressing.zeroPage(pc++);
        a |=  Memory.readByte(_address);
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    private void opcode_ORA_zero_page_X()
    {
        // Logical Inclusive OR
        int _address = Addressing.zeroPageX(pc++, x);
        a |=  Memory.readByte(_address);
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    private void opcode_ORA_absolute()
    {
        // Logical Inclusive OR
        int _address = Addressing.absolute(pc++);
        a |=  Memory.readByte(_address);
        
        pc++;
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    private void opcode_ORA_absolute_X()
    {
        // Logical Inclusive OR
        int _address = Addressing.absoluteX(pc++, x);
        a |=  Memory.readByte(_address);
        
        pc++;
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    private void opcode_ORA_absolute_Y()
    {
        // Logical Inclusive OR
        int _address = Addressing.absoluteY(pc++, y);
        a |=  Memory.readByte(_address);
        
        pc++;
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    private void opcode_ORA_indirect_X()
    {
        // Logical Inclusive OR
        int _value = Memory.readByte(Addressing.indirectX(pc++, x));
        a |=  _value;
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    private void opcode_ORA_indirect_Y()
    {
        // Logical Inclusive OR
        int _value = Memory.readByte(Addressing.indirectY(pc++, y));
        a |=  _value;
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    private void opcode_PHA()
    {
        // Push Accumulator
    	push(a);
    }

    private void opcode_PHP()
    {
        // Push Processor Status
        
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

    private void opcode_PLA()
    {
        // Pull Accumulator
        a = pop();
        
        not_zero = a;
        negative = (a >> 7) & 1;        
    }

    private void opcode_PLP()
    {
        // Pull Processor Status
    	int _temp = pop();
    	
    	carry            = (_temp) & 1;
        not_zero         = ((_temp >> 1) & 1) == 1 ? 0 : 1;
        interruptDisable = (_temp >> 2) & 1;
        decimal          = (_temp >> 3) & 1; 
        brk              = 0; // TODO, very unsure about this...
        overflow         = (_temp >> 6) & 1;
        negative         = (_temp >> 7) & 1;        
    }

    private void opcode_ROL()
    {
        // Rotate Left
        int _temp = a;
		int _add = carry;
		
		carry = (_temp >> 7) &1;
		
		a = ((_temp << 1) & 0xFF) + _add;
		
		negative = (a >> 7) & 1;
		not_zero = a & 0xFF;
    }

    private void opcode_ROL_zero_page()
    {
        // Rotate Left
        int _address = Addressing.zeroPage(pc++);
        int _value = Memory.readByte(_address);
        
        int _temp = _value;
        int _add = carry;
        
        carry = (_temp >> 7) &1;
        
        _value = ((_temp << 1) & 0xFF) + _add;
        
        Memory.writeByte(_value, _address);
        
        negative = (_value >> 7) & 1;
        not_zero = _value & 0xFF;
    }

    private void opcode_ROL_zero_page_X()
    {
        // Rotate Left
        int _address = Addressing.zeroPageX(pc++, x);
        int _value = Memory.readByte(_address);
        
        int _temp = _value;
        int _add = carry;
        
        carry = (_temp >> 7) &1;
        
        _value = ((_temp << 1) & 0xFF) + _add;
        
        Memory.writeByte(_value, _address);
        
        negative = (_value >> 7) & 1;
        not_zero = _value & 0xFF;
    }

    private void opcode_ROL_absolute()
    {
        // Rotate Left
        int _address = Addressing.absolute(pc++);
        int _value = Memory.readByte(_address);
        
        pc++;
        
        int _temp = _value;
        int _add = carry;
        
        carry = (_temp >> 7) &1;
        
        _value = ((_temp << 1) & 0xFF) + _add;
        
        Memory.writeByte(_value, _address);
        
        negative = (_value >> 7) & 1;
        not_zero = _value & 0xFF;
    }

    private void opcode_ROL_absolute_X()
    {
        // Rotate Left
        int _address = Addressing.absoluteX(pc++, x);
        int _value = Memory.readByte(_address);
        
        pc++;
        
        int _temp = _value;
        int _add = carry;
        
        carry = (_temp >> 7) &1;
        
        _value = ((_temp << 1) & 0xFF) + _add;
        
        Memory.writeByte(_value, _address);
        
        negative = (_value >> 7) & 1;
        not_zero = _value & 0xFF;
    }

    private void opcode_ROR()
    {
        // Rotate Right
    	int _add = carry << 7;
    	
		carry = a & 1;
		a = (a >> 1) + _add;
		
		negative = (a >> 7) & 1;
		not_zero = a & 0xFF;
    }

    private void opcode_ROR_zero_page()
    {
        // Rotate Right
        int _address = Addressing.zeroPage(pc++);
        int _value = Memory.readByte(_address);
        
        int _add = carry << 7;
        
        carry = _value & 1;
        _value = (_value >> 1) + _add;
        
        Memory.writeByte(_value, _address);
        
        negative = (_value >> 7) & 1;
        not_zero = _value & 0xFF;
    }

    private void opcode_ROR_zero_page_X()
    {
        // Rotate Right
        int _address = Addressing.zeroPageX(pc++, x);
        int _value = Memory.readByte(_address);
        
        int _add = carry << 7;
        
        carry = _value & 1;
        _value = (_value >> 1) + _add;
        
        Memory.writeByte(_value, _address);
        
        negative = (_value >> 7) & 1;
        not_zero = _value & 0xFF;
    }

    private void opcode_ROR_absolute()
    {
        // Rotate Right
        int _address = Addressing.absolute(pc++);
        int _value = Memory.readByte(_address);
        
        pc++;
        
        int _add = carry << 7;
        
        carry = _value & 1;
        _value = (_value >> 1) + _add;
        
        Memory.writeByte(_value, _address);
        
        negative = (_value >> 7) & 1;
        not_zero = _value & 0xFF;
    }

    private void opcode_ROR_absolute_X()
    {
        // Rotate Right
        int _address = Addressing.absoluteX(pc++, x);
        int _value = Memory.readByte(_address);
        
        pc++;
        
        int _add = carry << 7;
        
        carry = _value & 1;
        _value = (_value >> 1) + _add;
        
        Memory.writeByte(_value, _address);
        
        negative = (_value >> 7) & 1;
        not_zero = _value & 0xFF;
    }

    private void opcode_RTI()
    {
        // Return from Interrupt        
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

    private void opcode_RTS()
    {
        // Return from Subroutine
        pc = popWord() + 1;        
    }

    private void opcode_SBC_immediate()
    {
        // Subtract with Carry
        int _value = Addressing.immediate(pc++);
        int _temp = a - _value - (1 - carry);
        
        carry = _temp < 0 ? 0 : 1;
        not_zero = _temp & 0xFF;
        overflow = ((((a ^ _temp) & 0x80) != 0 && ((a ^ _value) & 0x80) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    private void opcode_SBC_zero_page()
    {
        // Subtract with Carry
        int _address = Addressing.zeroPage(pc++);
        int _value = Memory.readByte(_address);
        int _temp = a - _value - (1 - carry);
        
        carry = _temp < 0 ? 0 : 1;
        not_zero = _temp & 0xFF;
        overflow = ((((a ^ _temp) & 0x80) != 0 && ((a ^ _value) & 0x80) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    private void opcode_SBC_zero_page_X()
    {
        // Subtract with Carry
        int _address = Addressing.zeroPageX(pc++, x);
        int _value = Memory.readByte(_address);
        int _temp = a - _value - (1 - carry);
        
        carry = _temp < 0 ? 0 : 1;
        not_zero = _temp & 0xFF;
        overflow = ((((a ^ _temp) & 0x80) != 0 && ((a ^ _value) & 0x80) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    private void opcode_SBC_absolute()
    {
        // Subtract with Carry
        int _address = Addressing.absolute(pc++);
        int _value = Memory.readByte(_address);
        int _temp = a - _value - (1 - carry);
        
        pc++;
        
        carry = _temp < 0 ? 0 : 1;
        not_zero = _temp & 0xFF;
        overflow = ((((a ^ _temp) & 0x80) != 0 && ((a ^ _value) & 0x80) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    private void opcode_SBC_absolute_X()
    {
        // Subtract with Carry
        int _address = Addressing.absoluteX(pc++, x);
        int _value = Memory.readByte(_address);
        int _temp = a - _value - (1 - carry);
        
        pc++;
        
        carry = _temp < 0 ? 0 : 1;
        not_zero = _temp & 0xFF;
        overflow = ((((a ^ _temp) & 0x80) != 0 && ((a ^ _value) & 0x80) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    private void opcode_SBC_absolute_Y()
    {
        // Subtract with Carry
        int _address = Addressing.absoluteY(pc++, y);
        int _value = Memory.readByte(_address);
        int _temp = a - _value - (1 - carry);
        
        pc++;
        
        carry = _temp < 0 ? 0 : 1;
        not_zero = _temp & 0xFF;
        overflow = ((((a ^ _temp) & 0x80) != 0 && ((a ^ _value) & 0x80) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    private void opcode_SBC_indirect_X()
    {
        // Subtract with Carry
        int _address = Addressing.indirectX(pc++, x);
        int _value = Memory.readByte(_address);
        int _temp = a - _value - (1 - carry);
        
        carry = _temp < 0 ? 0 : 1;
        not_zero = _temp & 0xFF;
        overflow = ((((a ^ _temp) & 0x80) != 0 && ((a ^ _value) & 0x80) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    private void opcode_SBC_indirect_Y()
    {
        // Subtract with Carry
        int _address = Addressing.indirectY(pc++, y);
        int _value = Memory.readByte(_address);
        int _temp = a - _value - (1 - carry);
        
        carry = _temp < 0 ? 0 : 1;
        not_zero = _temp & 0xFF;
        overflow = ((((a ^ _temp) & 0x80) != 0 && ((a ^ _value) & 0x80) != 0) ? 1 : 0);
        negative = (_temp >> 7) & 1;
        
        a = _temp & 0xFF;
    }

    private void opcode_SEC()
    {
        // Set the carry flag
        carry = 1;
    }

    private void opcode_SED()
    {
        // Set Decimal Flag
        decimal = 1;  // TODO NOP??
    }

    private void opcode_SEI()
    {
        // Set Interrupt Disable
        interruptDisable = 1;
    }

    private void opcode_STA_zero_page()
    {
        // Store Accumulator
        Memory.writeByte(a, Addressing.zeroPage(pc++));        
    }

    private void opcode_STA_zero_page_X()
    {
        // Store Accumulator
        Memory.writeByte(a, Addressing.zeroPageX(pc++, x));
    }

    private void opcode_STA_absolute()
    {
        // Store Accumulator    	
    	Memory.writeByte(a, Addressing.absolute(pc++));
    	pc++;
    }

    private void opcode_STA_absolute_X()
    {
        // Store Accumulator        
        Memory.writeByte(a, Addressing.absoluteX(pc++, x));
        pc++;
    }

    private void opcode_STA_absolute_Y()
    {
        // Store Accumulator        
        Memory.writeByte(a, Addressing.absoluteY(pc++, y));
        pc++;
    }

    private void opcode_STA_indirect_X()
    {
        // Store Accumulator
        Memory.writeByte(a, Addressing.indirectX(pc++, x));        
    }

    private void opcode_STA_indirect_Y()
    {
        // Store Accumulator
        Memory.writeByte(a, Addressing.indirectY(pc++, y));
    }

    private void opcode_STX_zero_page()
    {
        // Store X register
        Memory.writeByte(x, Addressing.zeroPage(pc++));
    }

    private void opcode_STX_zero_page_Y()
    {
        // Store X register
        Memory.writeByte(x, Addressing.zeroPageY(pc++, y));
    }

    private void opcode_STX_absolute()
    {
        // Store X register
        Memory.writeByte(x, Addressing.absolute(pc++));        
        pc++;
    }

    private void opcode_STY_zero_page()
    {
        // Store Y Register
        int _address = Addressing.zeroPage(pc++);
        Memory.writeByte(y, _address);        
    }

    private void opcode_STY_zero_page_X()
    {
        // Store Y Register
        int _address = Addressing.zeroPageX(pc++, x);
        Memory.writeByte(y, _address);
    }

    private void opcode_STY_absolute()
    {
        // Store Y Register
        int _address = Addressing.absolute(pc++);
        Memory.writeByte(y, _address);
        
        pc++;
    }

    private void opcode_TAX()
    {
        // Transfer Accumulator to X
        x = a;
        
        not_zero = x & 0xFF;
        negative = (x >> 7) & 1;
    }

    private void opcode_TAY()
    {
        // Transfer Accumulator to Y
        y = a;
        
        not_zero = y & 0xFF;
        negative = (y >> 7) & 1;
    }

    private void opcode_TSX()
    {
        //  Transfer Stack Pointer to X
        x = sp & 0xFF; // Only transfer the lower 8 bits
        
        not_zero = x & 0xFF;
        negative = (x >> 7) & 1;
    }

    private void opcode_TXA()
    {
        // Transfer X to Accumulator
        a = x;
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }

    private void opcode_TXS()
    {
        // Transfer X to Stack Pointer
        sp = x | 0x0100;
    }

    private void opcode_TYA()
    {
        // Transfer Y to Accumulator
        a = y;
        
        not_zero = a & 0xFF;
        negative = (a >> 7) & 1;
    }    
}
