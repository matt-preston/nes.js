package org.perturbed.nesjs.core.client;


/**
 * Implements iNes Mapper 000 memory mapping
 *
 * iNES Mapper 000 is used to designate a board with up to 32 KiB PRG ROM, 8 KiB CHR ROM.
 *
 * @author Matt
 */
public class CPUMemory implements Memory {

  public final int[] lowMem = new int[Constants._2K];
  public final int[] prgMem = new int[Constants._64K];

  private APU apu;
  private PPU ppu;

  public void setAPU(APU apu) {
    this.apu = apu;
  }

  public void setPPU(PPU ppu) {
    this.ppu = ppu;
  }

  public void resetLowMemory() {
    for (int i = 0; i < lowMem.length; i++) {
      lowMem[i] = 0xFF;
    }

    lowMem[0x008] = 0xF7;
    lowMem[0x009] = 0xEF;
    lowMem[0x00A] = 0xDF;
    lowMem[0x00F] = 0xBF;
  }


  public final int readByte(int address) {
    assert address >= 0 && address <= 0xFFFF : "Tried to read an out of range address";

    // Mask to 16 bit
    address = address & 0xFFFF;

    if (address < 0x2000) {
      // Low memory 2KB (mirrored 3 times)
      return byteAtIndex(lowMem, address & 0x7FF);
    } else if (address > 0x4017) {
      // Program ROM
      return byteAtIndex(prgMem, address);
    } else if (address < 0x2008) {
      // PPU registers
      return ppu.readRegister(address);
    } else if (address < 0x4000) {
      // Mirrors of PPU registers (0x2000 -> 0x2007) every 8 bytes
      return ppu.readRegister(0x2000 + (address & 0x7));
    } else {
      // Must be between 0x4000 and 0x4018, but the only readable register is 0x4015
      assert address == 0x4015;

      return apu.getStatusRegister();
    }
  }

  public final void writeByte(int address, int byteValue) {
    assert address >= 0 && address <= 0xFFFF : "Tried to write an out of range address";
    assert byteValue < 0x0100 : "Attempting to write out of range byte";

    if (address < 0x2000) {
      // Low memory 2KB (mirrored 3 times)
      lowMem[address & 0x7FF] = byteValue;
    } else if (address > 0x4017) {
      // Program ROM
      prgMem[address] = byteValue;
    } else if (address < 0x2008) {
      // PPU registers
      ppu.writeRegister(address, byteValue);
    } else if (address < 0x4000) {
      // Mirrors of PPU registers (0x2000 -> 0x2007) every 8 bytes
      ppu.writeRegister(0x2000 + (address & 0x7), byteValue);
    } else {
      // Must be between 0x4000 and 0x4018
      assert address > 0x3FFF && address < 0x4018 : "Address out of range for APU";

      apu.writeRegister(address, byteValue);
    }
  }

  private final int byteAtIndex(int[] memory, int index) {
    int _byte = memory[index];

    assert _byte < 0x0100 : "read byte out of range";

    return _byte;
  }
}
