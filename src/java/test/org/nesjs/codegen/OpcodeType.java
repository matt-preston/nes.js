package org.nesjs.codegen;

import static org.nesjs.core.OpcodeMnemomic.*;

import java.util.*;

import org.nesjs.core.*;

public enum OpcodeType
{
    DEFAULT, 
    READ(ADC, ALR, ANC, AND, ARR, ATX, AXS, BIT, CMP, CPX, CPY, EOR, JMP, JSR, LAX, LDA, LDX, LDY, NOP, ORA, SBC), 
    WRITE(SAX, STA, STX, STY, SXA, SYA), 
    READ_MODIFY_WRITE(ASL, DCP, DEC, INC, ISB, LSR, RLA, ROL, ROR, RRA, SLO, SRE);
    
    private EnumSet<OpcodeMnemomic> opcodes;
    
    private OpcodeType()
    {        
    }
    
    private OpcodeType(OpcodeMnemomic ... aMnemomics)
    {
        opcodes = EnumSet.of(aMnemomics[0], aMnemomics);        
    }
    
    public EnumSet<OpcodeMnemomic> getOpcodes()
    {
        if(this == DEFAULT && (opcodes == null))
        {
            /*
             * Initialise DEFAULT to be all opcodes that are not part of one of the other sets
             */
            opcodes = EnumSet.allOf(OpcodeMnemomic.class);
            
            opcodes.removeAll(READ.opcodes);
            opcodes.removeAll(WRITE.opcodes);
            opcodes.removeAll(READ_MODIFY_WRITE.opcodes);            
        }
        
        return opcodes;
    }
    
    public static OpcodeType getOpcodeType(String aMnemomic)
    {
        return getOpcodeType(OpcodeMnemomic.valueOf(aMnemomic));
    }
    
    public static OpcodeType getOpcodeType(OpcodeMnemomic aMnemomic)
    {
        if(DEFAULT.getOpcodes().contains(aMnemomic))
        {
            return DEFAULT;
        }
        else if(READ_MODIFY_WRITE.getOpcodes().contains(aMnemomic))
        {
            return READ_MODIFY_WRITE;
        }
        else if(READ.getOpcodes().contains(aMnemomic))
        {
            return READ;
        }
        else if(WRITE.getOpcodes().contains(aMnemomic))
        {
            return WRITE;
        }
        else
        {
            throw new RuntimeException("unknown opcode!");
        }
    }
}
