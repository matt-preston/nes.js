package org.perturbed.nesjs.core.codegen;

import static org.perturbed.nesjs.core.client.Instruction.*;

import java.util.*;

import org.perturbed.nesjs.core.client.*;

public enum InstructionType
{
    DEFAULT, 
    READ(ADC, ALR, ANC, AND, ANE, ARR, ATX, AXS, BIT, CMP, CPX, CPY, EOR, JMP, JSR, LAS, LAX, LDA, LDX, LDY, NOP, ORA, SBC), 
    WRITE(SAX, SHA, SHS, STA, STX, STY, SXA, SYA), 
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
    
    public static InstructionType getInstructionType(String aMnemonic)
    {
        return getInstructionType(Instruction.valueOf(aMnemonic));
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
