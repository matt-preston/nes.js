package org.perturbed.nesjs.core.codegen;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.perturbed.nesjs.core.client.Utils;

public class GenerateOpcodeNames {

  public static void main(String[] args) throws Exception {
    OpcodeDefinitionReader reader = new OpcodeDefinitionReader();

    List<OpcodeDefinition> opcodes = reader.allOpcodeDefinitions();

    SortedSet<String> mnemonics = new TreeSet<String>();

    for (int i = 0; i < opcodes.size(); i++) {
      OpcodeDefinition next = opcodes.get(i);

      mnemonics.add(next.getMnemonic());

      if (i < opcodes.size() - 2) {
        OpcodeDefinition following = opcodes.get(i + 1);

        if (next.getMnemonic().equals(following.getMnemonic())) {
          System.out.printf("case %s:\n", Utils.toHexString(next.getOpcode()));
        } else {
          System.out.printf("case %s: return %s.name();\n", Utils.toHexString(next.getOpcode()),
              next.getMnemonic());
        }
      } else {
        System.out.printf("case %s: return %s.name();\n", Utils.toHexString(next.getOpcode()),
            next.getMnemonic());
      }
    }

    reader.close();

    System.out.println("\n\n\n\n\n");

    for (String mnemonic : mnemonics) {
      System.out.println(mnemonic + ",");
    }
  }
}
