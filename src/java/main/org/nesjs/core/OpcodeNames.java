package org.nesjs.core;

import static org.nesjs.core.Instruction.*;

// Debug only, not needed for actual emulation 
public class OpcodeNames
{
    // Debug only
    public static final String name(int anOpcode)
    {
        switch(anOpcode)
        {     
            case 0x69:
            case 0x65:
            case 0x75:
            case 0x6D:
            case 0x7D:
            case 0x79:
            case 0x61:
            case 0x71: return ADC.name();
            case 0x4B: return ALR.name();
            case 0x0B:
            case 0x2B: return ANC.name();
            case 0x29:
            case 0x25:
            case 0x35:
            case 0x2D:
            case 0x3D:
            case 0x39:
            case 0x21:
            case 0x31: return AND.name();
            case 0x6B: return ARR.name();
            case 0x0A:
            case 0x06:
            case 0x16:
            case 0x0E:
            case 0x1E: return ASL.name();
            case 0xAB: return ATX.name();
            case 0xCB: return AXS.name();
            case 0x90: return BCC.name();
            case 0xB0: return BCS.name();
            case 0xF0: return BEQ.name();
            case 0x24:
            case 0x2C: return BIT.name();
            case 0x30: return BMI.name();
            case 0xD0: return BNE.name();
            case 0x10: return BPL.name();
            case 0x00: return BRK.name();
            case 0x50: return BVC.name();
            case 0x70: return BVS.name();
            case 0x18: return CLC.name();
            case 0xD8: return CLD.name();
            case 0x58: return CLI.name();
            case 0xB8: return CLV.name();
            case 0xC9:
            case 0xC5:
            case 0xD5:
            case 0xCD:
            case 0xDD:
            case 0xD9:
            case 0xC1:
            case 0xD1: return CMP.name();
            case 0xE0:
            case 0xE4:
            case 0xEC: return CPX.name();
            case 0xC0:
            case 0xC4:
            case 0xCC: return CPY.name();
            case 0xC7:
            case 0xD7:
            case 0xCF:
            case 0xDF:
            case 0xDB:
            case 0xC3:
            case 0xD3: return DCP.name();
            case 0xC6:
            case 0xD6:
            case 0xCE:
            case 0xDE: return DEC.name();
            case 0xCA: return DEX.name();
            case 0x88: return DEY.name();
            case 0x49:
            case 0x45:
            case 0x55:
            case 0x4D:
            case 0x5D:
            case 0x59:
            case 0x41:
            case 0x51: return EOR.name();
            case 0xE6:
            case 0xF6:
            case 0xEE:
            case 0xFE: return INC.name();
            case 0xE8: return INX.name();
            case 0xC8: return INY.name();
            case 0xE7:
            case 0xF7:
            case 0xEF:
            case 0xFF:
            case 0xFB:
            case 0xE3:
            case 0xF3: return ISB.name();
            case 0x4C:
            case 0x6C: return JMP.name();
            case 0x20: return JSR.name();
            case 0xA7:
            case 0xB7:
            case 0xAF:
            case 0xBF:
            case 0xA3:
            case 0xB3: return LAX.name();
            case 0xA9:
            case 0xA5:
            case 0xB5:
            case 0xAD:
            case 0xBD:
            case 0xB9:
            case 0xA1:
            case 0xB1: return LDA.name();
            case 0xA2:
            case 0xA6:
            case 0xB6:
            case 0xAE:
            case 0xBE: return LDX.name();
            case 0xA0:
            case 0xA4:
            case 0xB4:
            case 0xAC:
            case 0xBC: return LDY.name();
            case 0x4A:
            case 0x46:
            case 0x56:
            case 0x4E:
            case 0x5E: return LSR.name();
            case 0xEA:
            case 0x1A:
            case 0x3A:
            case 0x5A:
            case 0x7A:
            case 0xDA:
            case 0xFA:
            case 0x80:
            case 0x82:
            case 0x89:
            case 0xC2:
            case 0xE2:
            case 0x04:
            case 0x44:
            case 0x64:
            case 0x0C:
            case 0x1C:
            case 0x3C:
            case 0x5C:
            case 0x7C:
            case 0xDC:
            case 0xFC:
            case 0x14:
            case 0x34:
            case 0x54:
            case 0x74:
            case 0xD4:
            case 0xF4: return NOP.name();
            case 0x09:
            case 0x05:
            case 0x15:
            case 0x0D:
            case 0x1D:
            case 0x19:
            case 0x01:
            case 0x11: return ORA.name();
            case 0x48: return PHA.name();
            case 0x08: return PHP.name();
            case 0x68: return PLA.name();
            case 0x28: return PLP.name();
            case 0x27:
            case 0x37:
            case 0x2F:
            case 0x3F:
            case 0x3B:
            case 0x23:
            case 0x33: return RLA.name();
            case 0x2A:
            case 0x26:
            case 0x36:
            case 0x2E:
            case 0x3E: return ROL.name();
            case 0x6A:
            case 0x66:
            case 0x76:
            case 0x6E:
            case 0x7E: return ROR.name();
            case 0x67:
            case 0x77:
            case 0x6F:
            case 0x7F:
            case 0x7B:
            case 0x63:
            case 0x73: return RRA.name();
            case 0x40: return RTI.name();
            case 0x60: return RTS.name();
            case 0x87:
            case 0x97:
            case 0x8F:
            case 0x83: return SAX.name();
            case 0xE9:
            case 0xEB:
            case 0xE5:
            case 0xF5:
            case 0xED:
            case 0xFD:
            case 0xF9:
            case 0xE1:
            case 0xF1: return SBC.name();
            case 0x38: return SEC.name();
            case 0xF8: return SED.name();
            case 0x78: return SEI.name();
            case 0x07:
            case 0x17:
            case 0x0F:
            case 0x1F:
            case 0x1B:
            case 0x03:
            case 0x13: return SLO.name();
            case 0x47:
            case 0x57:
            case 0x4F:
            case 0x5F:
            case 0x5B:
            case 0x43:
            case 0x53: return SRE.name();
            case 0x85:
            case 0x95:
            case 0x8D:
            case 0x9D:
            case 0x99:
            case 0x81:
            case 0x91: return STA.name();
            case 0x86:
            case 0x96:
            case 0x8E: return STX.name();
            case 0x84:
            case 0x94:
            case 0x8C: return STY.name();
            case 0x9E: return SXA.name();
            case 0x9C: return SYA.name();
            case 0xAA: return TAX.name();
            case 0xA8: return TAY.name();
            case 0xBA: return TSX.name();
            case 0x8A: return TXA.name();
            case 0x9A: return TXS.name();
            case 0x98: return TYA.name();
        }
        
        return "unknown";
    }
}
