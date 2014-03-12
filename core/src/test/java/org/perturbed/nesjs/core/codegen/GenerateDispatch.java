package org.perturbed.nesjs.core.codegen;

import java.util.EnumSet;
import java.util.List;

import org.perturbed.nesjs.core.client.Utils;

public class GenerateDispatch {

  public static void main(String[] args) throws Exception {
    OpcodeDefinitionReader reader = new OpcodeDefinitionReader();

    List<OpcodeDefinition> opcodes = reader.allOpcodeDefinitions();

    for (int i = 0; i < opcodes.size(); i++) {
      OpcodeDefinition next = opcodes.get(i);

      if (i < opcodes.size() - 2) {
        OpcodeDefinition following = opcodes.get(i + 1);

        if (next.getMnemonic().equals(following.getMnemonic()) && next.getAddressingMode()
            .equals(following.getAddressingMode())) {
          System.out.printf("case %s:\n", Utils.toHexString(next.getOpcode()));
        } else {
          printCaseStatement(next);
        }
      } else {
        printCaseStatement(next);
      }
    }

    reader.close();

    System.out.println("\n\n\n===========================================\n\n\n");

    reader = new OpcodeDefinitionReader();
    OpcodeDefinition next;

    while ((next = reader.next()) != null) {
      System.out.printf("private final void %s\n", getDefinitionMethodName(next));
      System.out.printf("{\n");
      System.out.printf("    throw new RuntimeException(\"opcode not implemented [%s]\");\n",
          next.getMnemonic());
      System.out.printf("}\n");
      System.out.println("");
    }

    reader.close();
  }

  private static void printCaseStatement(OpcodeDefinition opcode) {
    String comment = "";

    if (!opcode.isOfficial()) {
      comment = " // Unofficial";
    }

    System.out.printf("case %s: %s; addCycles(%d); break;%s\n",
        Utils.toHexString(opcode.getOpcode()), getCallMethodName(opcode), opcode.getCycles(),
        comment);
  }

  private static String getCallMethodName(OpcodeDefinition opcode) {
    AddressingMode mode = opcode.getAddressingMode();

    if (isMethodRequiresAddressParameter(mode)) {
      InstructionType type = InstructionType.getInstructionType(opcode.getMnemonic());

      return String.format("opcode_%s(%s())", opcode.getMnemonic(),
          getAddressingMethod(opcode.getAddressingMode(), type));
    } else {
      // no address to read
      return String.format("opcode_%s_%s()", opcode.getMnemonic(),
          opcode.getAddressingMode().name().toLowerCase());
    }
  }

  private static String getDefinitionMethodName(OpcodeDefinition opcode) {
    AddressingMode _mode = opcode.getAddressingMode();

    if (isMethodRequiresAddressParameter(_mode)) {
      return String.format("opcode_%s(int anAddress)", opcode.getMnemonic());
    } else {
      // no address to read
      return String.format("opcode_%s_%s()", opcode.getMnemonic(),
          opcode.getAddressingMode().name().toLowerCase());
    }
  }

  private static boolean isMethodRequiresAddressParameter(AddressingMode addressingMode) {
    EnumSet<AddressingMode> noParams =
        EnumSet.of(AddressingMode.IMPLIED, AddressingMode.ACCUMULATOR, AddressingMode.RELATIVE);

    return !noParams.contains(addressingMode);
  }


  private static String getAddressingMethod(AddressingMode addressingMode, InstructionType type) {
    if (addressingMode == AddressingMode.INDIRECT) {
      return "indirect"; // JMP is the only opcode to support this mode
    }

    String suffix = "";

    switch (type) {
      case READ:
        suffix = "_R";
        break;
      case WRITE:
        suffix = "_W";
        break;
      case READ_MODIFY_WRITE:
        suffix = "_RMW";
        break;
    }

    String prefix = "";

    switch (addressingMode) {
      case IMMEDIATE:
        prefix = "immediate";
        break;
      case ZERO_PAGE:
        prefix = "zeroPage";
        break;
      case ZERO_PAGE_X:
        prefix = "zeroPageX";
        break;
      case ZERO_PAGE_Y:
        prefix = "zeroPageY";
        break;
      case RELATIVE:
        prefix = "relative";
        break;
      case ABSOLUTE:
        prefix = "absolute";
        break;
      case ABSOLUTE_X:
        prefix = "absoluteX";
        break;
      case ABSOLUTE_Y:
        prefix = "absoluteY";
        break;
      case INDIRECT_X:
        prefix = "indirectX";
        break;
      case INDIRECT_Y:
        prefix = "indirectY";
        break;
    }

    return prefix + suffix;
  }
}
