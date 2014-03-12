package org.perturbed.nesjs.core.client;

import java.io.IOException;

/**
 * Parser for the iNES ROM format
 *
 * http://wiki.nesdev.com/w/index.php/INES
 */
public class ROM {

  private CPUMemory cpuMemory;
  private PPUMemory ppuMemory;

  public ROM(int[] bytes) throws IOException {
    init(bytes);
  }

  public CPUMemory getCPUMemory() {
    return cpuMemory;
  }

  public PPUMemory getPPUMemory() {
    return ppuMemory;
  }

  private void init(final int[] bytes) throws IOException {
    // Read the header
    int[] header = new int[16];
    for (int i = 0; i < 16; i++) {
      header[i] = bytes[i];
    }

    if (!((header[0] == 0x4E) &&  // N
          (header[1] == 0x45) &&  // E
          (header[2] == 0x53) &&  // S
          (header[3] == 0x1A))) { // <CR>
      // Invalid ROM header.
      throw new IOException("Invalid ROM header");
    }

    // The number of 16 KB PRG ROM units
    int prgROMUnitCount = header[4];

    // The number of CHR ROM in 8 KB units (Value 0 means the board uses CHR RAM)
    int chrROMUnitCount = header[5];

    //int _mirroring  = ((_header[6] & 1) != 0 ? 1 : 0);
    //boolean _batteryRam = (_header[6] & 2) != 0;
    //boolean _trainer    = (_header[6] & 4) != 0;
    //boolean _fourScreen = (_header[6] & 8) != 0;

    int mapperType = (header[6] >> 4) | (header[7] & 0xF0);

    if (mapperType != 0) {
      throw new IOException("Can't handle mapper type [" + mapperType + "] ROMs");
    }

    /**
     * Mapper 000 should only have 1 or 2 banks
     */
    if (prgROMUnitCount > 2) {
      throw new IOException("Don't know how to handle " + prgROMUnitCount + " PRG ROM units");
    }

    if (chrROMUnitCount != 1) {
      throw new IOException("Don't know how to handle " + prgROMUnitCount + " CHR ROM units");
    }

    // Load the PRG ROM
    int[][] prgROM = new int[prgROMUnitCount][Constants._16K];
    int offset = 16; // Starts after the 16 byte header - TODO a trainer may be present before the PRG ROM

    for (int i = 0; i < prgROMUnitCount; i++) {
      copyBytes(offset, Constants._16K, bytes, prgROM[i]);
      offset += Constants._16K;
    }

    // Load the CHR ROM
    int[] chrROM = new int[Constants._8K];
    copyBytes(offset, Constants._8K, bytes, chrROM);

    cpuMemory = new CPUMemory();
    ppuMemory = new PPUMemory();

    if (prgROMUnitCount > 1) {
      loadRomBank(prgROM[0], cpuMemory.prgMem, 0x8000);
      loadRomBank(prgROM[1], cpuMemory.prgMem, 0xC000);
    } else {
      loadRomBank(prgROM[0], cpuMemory.prgMem, 0x8000);
      loadRomBank(prgROM[0], cpuMemory.prgMem, 0xC000);
    }

    loadRomBank(chrROM, ppuMemory.chrMem, 0x0000);
  }


  private final void loadRomBank(int[] bank, int[] memory, int address) {
    System.arraycopy(bank, 0, memory, address, bank.length);
  }

  private final void copyBytes(int offset, int length, int[] bytes, int[] target) {
    for (int _index = 0; _index < length; _index++) {
      target[_index] = bytes[offset + _index];
    }
  }
}
