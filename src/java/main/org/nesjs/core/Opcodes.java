package org.nesjs.core;

public class Opcodes
{
    // Debug only
    public static final String name(int anOpcode)
    {
        switch(anOpcode)
        {     
            case 0x69: return "opcode_ADC_immediate";
            case 0x65: return "opcode_ADC_zero_page";
            case 0x75: return "opcode_ADC_zero_page_X";
            case 0x6D: return "opcode_ADC_absolute";
            case 0x7D: return "opcode_ADC_absolute_X";
            case 0x79: return "opcode_ADC_absolute_Y";
            case 0x61: return "opcode_ADC_indirect_X";
            case 0x71: return "opcode_ADC_indirect_Y";
            case 0x29: return "opcode_AND_immediate";
            case 0x25: return "opcode_AND_zero_page";
            case 0x35: return "opcode_AND_zero_page_X";
            case 0x2D: return "opcode_AND_absolute";
            case 0x3D: return "opcode_AND_absolute_X";
            case 0x39: return "opcode_AND_absolute_Y";
            case 0x21: return "opcode_AND_indirect_X";
            case 0x31: return "opcode_AND_indirect_Y";
            case 0x0A: return "opcode_ASL";
            case 0x06: return "opcode_ASL_zero_page";
            case 0x16: return "opcode_ASL_zero_page_X";
            case 0x0E: return "opcode_ASL_absolute";
            case 0x1E: return "opcode_ASL_absolute_X";
            case 0x90: return "opcode_BCC";
            case 0xB0: return "opcode_BCS";
            case 0xF0: return "opcode_BEQ";
            case 0x24: return "opcode_BIT_zero_page";
            case 0x2C: return "opcode_BIT_absolute";
            case 0x30: return "opcode_BMI";
            case 0xD0: return "opcode_BNE";
            case 0x10: return "opcode_BPL";
            case 0x00: return "opcode_BRK";
            case 0x50: return "opcode_BVC";
            case 0x70: return "opcode_BVS";
            case 0x18: return "opcode_CLC";
            case 0xD8: return "opcode_CLD";
            case 0x58: return "opcode_CLI";
            case 0xB8: return "opcode_CLV";
            case 0xC9: return "opcode_CMP_immediate";
            case 0xC5: return "opcode_CMP_zero_page";
            case 0xD5: return "opcode_CMP_zero_page_X";
            case 0xCD: return "opcode_CMP_absolute";
            case 0xDD: return "opcode_CMP_absolute_X";
            case 0xD9: return "opcode_CMP_absolute_Y";
            case 0xC1: return "opcode_CMP_indirect_X";
            case 0xD1: return "opcode_CMP_indirect_Y";
            case 0xE0: return "opcode_CPX_immediate";
            case 0xE4: return "opcode_CPX_zero_page";
            case 0xEC: return "opcode_CPX_absolute";
            case 0xC0: return "opcode_CPY_immediate";
            case 0xC4: return "opcode_CPY_zero_page";
            case 0xCC: return "opcode_CPY_absolute";
            case 0xC7: return "opcode_DCP_zero_page";
            case 0xD7: return "opcode_DCP_zero_page_X";
            case 0xCF: return "opcode_DCP_absolute";
            case 0xDf: return "opcode_DCP_absolute_X";
            case 0xDB: return "opcode_DCP_absolute_Y";
            case 0xC3: return "opcode_DCP_indirect_X";
            case 0xD3: return "opcode_DCP_indirect_Y";
            case 0xC6: return "opcode_DEC_zero_page";
            case 0xD6: return "opcode_DEC_zero_page_X";
            case 0xCE: return "opcode_DEC_absolute";
            case 0xDE: return "opcode_DEC_absolute_X";
            case 0xCA: return "opcode_DEX";
            case 0x88: return "opcode_DEY";
            case 0x49: return "opcode_EOR_immediate";
            case 0x45: return "opcode_EOR_zero_page";
            case 0x55: return "opcode_EOR_zero_page_X";
            case 0x4D: return "opcode_EOR_absolute";
            case 0x5D: return "opcode_EOR_absolute_X";
            case 0x59: return "opcode_EOR_absolute_Y";
            case 0x41: return "opcode_EOR_indirect_X";
            case 0x51: return "opcode_EOR_indirect_Y";
            case 0xE6: return "opcode_INC_zero_page";
            case 0xF6: return "opcode_INC_zero_page_X";
            case 0xEE: return "opcode_INC_absolute";
            case 0xFE: return "opcode_INC_absolute_X";
            case 0xE8: return "opcode_INX";
            case 0xC8: return "opcode_INY";
            case 0xE7: return "opcode_ISB_zero_page";
            case 0xF7: return "opcode_ISB_zero_page_X";
            case 0xEF: return "opcode_ISB_absolute";
            case 0xFF: return "opcode_ISB_absolute_X";
            case 0xFB: return "opcode_ISB_absolute_Y";
            case 0xE3: return "opcode_ISB_indirect_X";
            case 0xF3: return "opcode_ISB_indirect_Y";
            case 0x4C: return "opcode_JMP_absolute";
            case 0x6C: return "opcode_JMP_indirect";
            case 0x20: return "opcode_JSR";
            case 0xA7: return "opcode_LAX_zero_page";
            case 0xB7: return "opcode_LAX_zero_page_Y";
            case 0xAF: return "opcode_LAX_absolute";
            case 0xBF: return "opcode_LAX_absolute_Y";
            case 0xA3: return "opcode_LAX_indirect_X";
            case 0xB3: return "opcode_LAX_indirect_Y";
            case 0xA9: return "opcode_LDA_immediate";
            case 0xA5: return "opcode_LDA_zero_page";
            case 0xB5: return "opcode_LDA_zero_page_X";
            case 0xAD: return "opcode_LDA_absolute";
            case 0xBD: return "opcode_LDA_absolute_X";
            case 0xB9: return "opcode_LDA_absolute_Y";
            case 0xA1: return "opcode_LDA_indirect_X";
            case 0xB1: return "opcode_LDA_indirect_Y";
            case 0xA2: return "opcode_LDX_immediate";
            case 0xA6: return "opcode_LDX_zero_page";
            case 0xB6: return "opcode_LDX_zero_page_Y";
            case 0xAE: return "opcode_LDX_absolute";
            case 0xBE: return "opcode_LDX_absolute_Y";
            case 0xA0: return "opcode_LDY_immediate";
            case 0xA4: return "opcode_LDY_zero_page";
            case 0xB4: return "opcode_LDY_zero_page_X";
            case 0xAC: return "opcode_LDY_absolute";
            case 0xBC: return "opcode_LDY_absolute_X";
            case 0x4A: return "opcode_LSR";
            case 0x46: return "opcode_LSR_zero_page";
            case 0x56: return "opcode_LSR_zero_page_X";
            case 0x4E: return "opcode_LSR_absolute";
            case 0x5E: return "opcode_LSR_absolute_X";
            case 0xEA: return "opcode_NOP";
            case 0x1A: return "opcode_NOP";
            case 0x3A: return "opcode_NOP";
            case 0x5A: return "opcode_NOP";
            case 0x7A: return "opcode_NOP";
            case 0xDA: return "opcode_NOP";
            case 0xFA: return "opcode_NOP";
            case 0x80: return "opcode_NOP_immediate";
            case 0x04: return "opcode_NOP_zero_page";
            case 0x44: return "opcode_NOP_zero_page";
            case 0x64: return "opcode_NOP_zero_page";
            case 0x0C: return "opcode_NOP_absolute";
            case 0x1C: return "opcode_NOP_absolute_X";
            case 0x3C: return "opcode_NOP_absolute_X";
            case 0x5C: return "opcode_NOP_absolute_X";
            case 0x7C: return "opcode_NOP_absolute_X";
            case 0xDC: return "opcode_NOP_absolute_X";
            case 0xFC: return "opcode_NOP_absolute_X";
            case 0x14: return "opcode_NOP_zero_page_X";
            case 0x34: return "opcode_NOP_zero_page_X";
            case 0x54: return "opcode_NOP_zero_page_X";
            case 0x74: return "opcode_NOP_zero_page_X";
            case 0xD4: return "opcode_NOP_zero_page_X";
            case 0xF4: return "opcode_NOP_zero_page_X";
            case 0x09: return "opcode_ORA_immediate";
            case 0x05: return "opcode_ORA_zero_page";
            case 0x15: return "opcode_ORA_zero_page_X";
            case 0x0D: return "opcode_ORA_absolute";
            case 0x1D: return "opcode_ORA_absolute_X";
            case 0x19: return "opcode_ORA_absolute_Y";
            case 0x01: return "opcode_ORA_indirect_X";
            case 0x11: return "opcode_ORA_indirect_Y";
            case 0x48: return "opcode_PHA";
            case 0x08: return "opcode_PHP";
            case 0x68: return "opcode_PLA";
            case 0x28: return "opcode_PLP";
            case 0x2A: return "opcode_ROL";
            case 0x26: return "opcode_ROL_zero_page";
            case 0x36: return "opcode_ROL_zero_page_X";
            case 0x2E: return "opcode_ROL_absolute";
            case 0x3E: return "opcode_ROL_absolute_X";
            case 0x6A: return "opcode_ROR";
            case 0x66: return "opcode_ROR_zero_page";
            case 0x76: return "opcode_ROR_zero_page_X";
            case 0x6E: return "opcode_ROR_absolute";
            case 0x7E: return "opcode_ROR_absolute_X";
            case 0x40: return "opcode_RTI";
            case 0x60: return "opcode_RTS";
            case 0x87: return "opcode_SAX_zero_page";
            case 0x97: return "opcode_SAX_zero_page_Y";
            case 0x8F: return "opcode_SAX_absolute";
            case 0x83: return "opcode_SAX_indirect_X";
            case 0xE9: return "opcode_SBC_immediate";
            case 0xEB: return "opcode_SBC_immediate";
            case 0xE5: return "opcode_SBC_zero_page";
            case 0xF5: return "opcode_SBC_zero_page_X";
            case 0xED: return "opcode_SBC_absolute";
            case 0xFD: return "opcode_SBC_absolute_X";
            case 0xF9: return "opcode_SBC_absolute_Y";
            case 0xE1: return "opcode_SBC_indirect_X";
            case 0xF1: return "opcode_SBC_indirect_Y";
            case 0x38: return "opcode_SEC";
            case 0xF8: return "opcode_SED";
            case 0x78: return "opcode_SEI";
            case 0x07: return "opcode_SLO_zero_page";
            case 0x17: return "opcode_SLO_zero_page_X";
            case 0x0F: return "opcode_SLO_absolute";
            case 0x1F: return "opcode_SLO_absolute_X";
            case 0x1B: return "opcode_SLO_absolute_Y";
            case 0x03: return "opcode_SLO_indirect_X";
            case 0x13: return "opcode_SLO_indirect_Y";
            case 0x85: return "opcode_STA_zero_page";
            case 0x95: return "opcode_STA_zero_page_X";
            case 0x8D: return "opcode_STA_absolute";
            case 0x9D: return "opcode_STA_absolute_X";
            case 0x99: return "opcode_STA_absolute_Y";
            case 0x81: return "opcode_STA_indirect_X";
            case 0x91: return "opcode_STA_indirect_Y";
            case 0x86: return "opcode_STX_zero_page";
            case 0x96: return "opcode_STX_zero_page_Y";
            case 0x8E: return "opcode_STX_absolute";
            case 0x84: return "opcode_STY_zero_page";
            case 0x94: return "opcode_STY_zero_page_X";
            case 0x8C: return "opcode_STY_absolute";
            case 0xAA: return "opcode_TAX";
            case 0xA8: return "opcode_TAY";
            case 0xBA: return "opcode_TSX";
            case 0x8A: return "opcode_TXA";
            case 0x9A: return "opcode_TXS";
            case 0x98: return "opcode_TYA";
        }
        
        return "unknown";
    }
}
