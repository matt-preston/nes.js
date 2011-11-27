package org.nesjs.codegen;

import static org.nesjs.core.Instruction.*;

import java.util.*;

import org.nesjs.core.*;

public enum InstructionType
{
    DEFAULT, 
    READ(ADC, ALR, ANC, AND, ARR, ATX, AXS, BIT, CMP, CPX, CPY, EOR, JMP, JSR, LAX, LDA, LDX, LDY, NOP, ORA, SBC), 
    WRITE(SAX, STA, STX, STY, SXA, SYA), 
    READ_MODIFY_WRITE(ASL, DCP, DEC, INC, ISB, LSR, RLA, ROL, ROR, RRA, SLO, SRE);
    
    private EnumSet<Instruction> instructions;
    
    private InstructionType()
    {        
    }
    
    private InstructionType(Instruction ... anInstructions)
    {
        instructions = EnumSet.of(anInstructions[0], anInstructions);        
    }
    
    private EnumSet<Instruction> getInstructions()
    {
        if(this == DEFAULT && (instructions == null))
        {
            /*
             * Initialise DEFAULT to be all instructions that are not part of one of the other sets
             */
            instructions = EnumSet.allOf(Instruction.class);
            
            instructions.removeAll(READ.instructions);
            instructions.removeAll(WRITE.instructions);
            instructions.removeAll(READ_MODIFY_WRITE.instructions);            
        }
        
        return instructions;
    }
    
    public static InstructionType getInstructionType(String aMnemomic)
    {
        return getInstructionType(Instruction.valueOf(aMnemomic));
    }
    
    public static InstructionType getInstructionType(Instruction anInstruction)
    {
        if(DEFAULT.getInstructions().contains(anInstruction))
        {
            return DEFAULT;
        }
        else if(READ_MODIFY_WRITE.getInstructions().contains(anInstruction))
        {
            return READ_MODIFY_WRITE;
        }
        else if(READ.getInstructions().contains(anInstruction))
        {
            return READ;
        }
        else if(WRITE.getInstructions().contains(anInstruction))
        {
            return WRITE;
        }
        else
        {
            throw new RuntimeException("Unknown instruction [" + anInstruction + "]");
        }
    }
}
