package org.perturbed.nesjs.core.codegen;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.perturbed.nesjs.core.client.Utils;

public class OpcodeDefinitionReader {

  private BufferedReader reader;

  private Pattern pattern =
      Pattern.compile("^(\\w{3})\\*?\\s+.*\\s+(\\w{2})\\s*(\\d)?$", Pattern.DOTALL);

  public OpcodeDefinitionReader() throws IOException {
    reader = new BufferedReader(new InputStreamReader(new FileInputStream("6502.txt"), "UTF8"));
  }

  /**
   * Will return the next opcode definition, or null if there are none left
   */
  public OpcodeDefinition next() throws IOException {
    String line;

    while ((line = reader.readLine()) != null) {
      line = line.trim();

      Matcher matcher = pattern.matcher(line);

      if (matcher.matches()) {
        String mnemonic = matcher.group(1);
        String opcodeString = matcher.group(2);
        String cyclesString = matcher.group(3);

        if (cyclesString == null) {
          cyclesString = "0";
        }

        int opcode = Integer.parseInt(opcodeString, 16);
        int cycles = Integer.parseInt(cyclesString);

        AddressingMode addressingMode = getAddressingMode(line);

        boolean official = !line.contains("*");

        return new OpcodeDefinition(opcode, mnemonic, addressingMode, official, cycles);
      }
    }

    return null;
  }

  public List<OpcodeDefinition> allOpcodeDefinitions() throws IOException {
    ArrayList<OpcodeDefinition> results = new ArrayList<OpcodeDefinition>();

    OpcodeDefinition next;

    while ((next = next()) != null) {
      results.add(next);
    }

    return results;
  }

  public void close() {
    if (reader != null) {
      try {
        reader.close();
      } catch (IOException anExc) {
      }

      reader = null;
    }
  }

  private AddressingMode getAddressingMode(String definitionString) {
    if (definitionString.contains("#")) {
      return AddressingMode.IMMEDIATE;
    } else if (definitionString.contains("~")) {
      return AddressingMode.RELATIVE;
    } else if (definitionString.contains("aaaa)")) {
      return AddressingMode.INDIRECT;
    } else if (definitionString.contains(",X)")) {
      return AddressingMode.INDIRECT_X;
    } else if (definitionString.contains("),Y")) {
      return AddressingMode.INDIRECT_Y;
    } else if (definitionString.contains("aaaa ")) {
      return AddressingMode.ABSOLUTE;
    } else if (definitionString.contains("aaaa,X")) {
      return AddressingMode.ABSOLUTE_X;
    } else if (definitionString.contains("aaaa,Y")) {
      return AddressingMode.ABSOLUTE_Y;
    } else if (definitionString.contains("aa ")) {
      return AddressingMode.ZERO_PAGE;
    } else if (definitionString.contains("aa,X")) {
      return AddressingMode.ZERO_PAGE_X;
    } else if (definitionString.contains("aa,Y")) {
      return AddressingMode.ZERO_PAGE_Y;
    } else if (definitionString.contains(" A ")) {
      return AddressingMode.ACCUMULATOR;
    }

    return AddressingMode.IMPLIED;
  }

  public static void main(String[] args) throws Exception {
    OpcodeDefinitionReader reader = new OpcodeDefinitionReader();

    List<OpcodeDefinition> opcodes = reader.allOpcodeDefinitions();

    for (OpcodeDefinition def : opcodes) {
      System.out.println(def);
    }
  }
}

enum AddressingMode {
  IMPLIED,
  ACCUMULATOR,
  IMMEDIATE,
  ZERO_PAGE,
  ZERO_PAGE_X,
  ZERO_PAGE_Y,
  RELATIVE,
  ABSOLUTE,
  ABSOLUTE_X,
  ABSOLUTE_Y,
  INDIRECT,
  INDIRECT_X,
  INDIRECT_Y;
}

class OpcodeDefinition {

  private int opcode;
  private String mnemonic;
  private AddressingMode addressingMode;
  private boolean official;
  private int cycles;

  public OpcodeDefinition(int opcode, String mnemonic, AddressingMode addressingMode,
      boolean official, int cycles) {
    this.opcode = opcode;
    this.mnemonic = mnemonic;
    this.addressingMode = addressingMode;
    this.official = official;
    this.cycles = cycles;
  }

  public int getOpcode() {
    return opcode;
  }

  public String getMnemonic() {
    return mnemonic;
  }

  public AddressingMode getAddressingMode() {
    return addressingMode;
  }

  public boolean isOfficial() {
    return official;
  }

  public int getCycles() {
    return cycles;
  }

  @Override
  public String toString() {
    if (official) {
      return String.format("%s [%s] official:   %s [%d]", Utils.toHexString(getOpcode()),
          getMnemonic(), getAddressingMode().name(), getCycles());
    } else {
      return String.format("%s [%s] unofficial: %s [%d]", Utils.toHexString(getOpcode()),
          getMnemonic(), getAddressingMode().name(), getCycles());
    }
  }
}
