package org.perturbed.nesjs.core.codegen;

import java.util.EnumSet;

import org.perturbed.nesjs.core.client.Instruction;

import static org.perturbed.nesjs.core.client.Instruction.ADC;
import static org.perturbed.nesjs.core.client.Instruction.ALR;
import static org.perturbed.nesjs.core.client.Instruction.ANC;
import static org.perturbed.nesjs.core.client.Instruction.AND;
import static org.perturbed.nesjs.core.client.Instruction.ANE;
import static org.perturbed.nesjs.core.client.Instruction.ARR;
import static org.perturbed.nesjs.core.client.Instruction.ASL;
import static org.perturbed.nesjs.core.client.Instruction.ATX;
import static org.perturbed.nesjs.core.client.Instruction.AXS;
import static org.perturbed.nesjs.core.client.Instruction.BIT;
import static org.perturbed.nesjs.core.client.Instruction.CMP;
import static org.perturbed.nesjs.core.client.Instruction.CPX;
import static org.perturbed.nesjs.core.client.Instruction.CPY;
import static org.perturbed.nesjs.core.client.Instruction.DCP;
import static org.perturbed.nesjs.core.client.Instruction.DEC;
import static org.perturbed.nesjs.core.client.Instruction.EOR;
import static org.perturbed.nesjs.core.client.Instruction.INC;
import static org.perturbed.nesjs.core.client.Instruction.ISB;
import static org.perturbed.nesjs.core.client.Instruction.JMP;
import static org.perturbed.nesjs.core.client.Instruction.JSR;
import static org.perturbed.nesjs.core.client.Instruction.LAS;
import static org.perturbed.nesjs.core.client.Instruction.LAX;
import static org.perturbed.nesjs.core.client.Instruction.LDA;
import static org.perturbed.nesjs.core.client.Instruction.LDX;
import static org.perturbed.nesjs.core.client.Instruction.LDY;
import static org.perturbed.nesjs.core.client.Instruction.LSR;
import static org.perturbed.nesjs.core.client.Instruction.NOP;
import static org.perturbed.nesjs.core.client.Instruction.ORA;
import static org.perturbed.nesjs.core.client.Instruction.RLA;
import static org.perturbed.nesjs.core.client.Instruction.ROL;
import static org.perturbed.nesjs.core.client.Instruction.ROR;
import static org.perturbed.nesjs.core.client.Instruction.RRA;
import static org.perturbed.nesjs.core.client.Instruction.SAX;
import static org.perturbed.nesjs.core.client.Instruction.SBC;
import static org.perturbed.nesjs.core.client.Instruction.SHA;
import static org.perturbed.nesjs.core.client.Instruction.SHS;
import static org.perturbed.nesjs.core.client.Instruction.SLO;
import static org.perturbed.nesjs.core.client.Instruction.SRE;
import static org.perturbed.nesjs.core.client.Instruction.STA;
import static org.perturbed.nesjs.core.client.Instruction.STX;
import static org.perturbed.nesjs.core.client.Instruction.STY;
import static org.perturbed.nesjs.core.client.Instruction.SXA;
import static org.perturbed.nesjs.core.client.Instruction.SYA;

public enum InstructionType {
  DEFAULT,
  READ(ADC, ALR, ANC, AND, ANE, ARR, ATX, AXS, BIT, CMP, CPX, CPY, EOR, JMP, JSR, LAS, LAX, LDA,
      LDX, LDY, NOP, ORA, SBC),
  WRITE(SAX, SHA, SHS, STA, STX, STY, SXA, SYA),
  READ_MODIFY_WRITE(ASL, DCP, DEC, INC, ISB, LSR, RLA, ROL, ROR, RRA, SLO, SRE);

  private EnumSet<Instruction> instructions;

  private InstructionType() {
  }

  private InstructionType(Instruction... instructions) {
    this.instructions = EnumSet.of(instructions[0], instructions);
  }

  private EnumSet<Instruction> getInstructions() {
    if (this == DEFAULT && (instructions == null)) {
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

  public static InstructionType getInstructionType(String mnemonic) {
    return getInstructionType(Instruction.valueOf(mnemonic));
  }

  public static InstructionType getInstructionType(Instruction instruction) {
    if (DEFAULT.getInstructions().contains(instruction)) {
      return DEFAULT;
    } else if (READ_MODIFY_WRITE.getInstructions().contains(instruction)) {
      return READ_MODIFY_WRITE;
    } else if (READ.getInstructions().contains(instruction)) {
      return READ;
    } else if (WRITE.getInstructions().contains(instruction)) {
      return WRITE;
    } else {
      throw new RuntimeException("Unknown instruction [" + instruction + "]");
    }
  }
}
