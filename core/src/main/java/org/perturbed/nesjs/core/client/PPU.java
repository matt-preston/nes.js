package org.perturbed.nesjs.core.client;


public class PPU {

  private MOS6502 cpu;
  private int cycles;
  private Memory objectAttributeMemory;
  private Memory ppuMemory;


  private boolean ppuScrollAndAddressLatch;

  // PPU_CTRL
  private int xScrollNameTableSelection = 0;
  private int yScrollNameTableSelection = 0;
  private int incrementPPUAddressBy_1_32 = 0;
  private int objectPatternTableSelection = 0;
  private int backgroundPatternTableSelection = 0;
  private int spriteSize_8_16 = 0;
  private int generateNMIAtVBLStart = 0;

  // PPU_MASK
  private int disableColour = 0;
  private int showBackgroundLeftColumn = 0;
  private int showObjectsLeftColumn = 0;
  private int enableBackgroundDisplay = 0;
  private int enableObjectsDisplay = 0;
  private int intensifyReds = 0;
  private int intensifyGreens = 0;
  private int intensifyBlues = 0;

  // PPU_STATUS
  private int spriteOverflow = 0;
  private int sprite0Hit = 0;
  private int verticalBlankStarted = 0;

  // OAM_ADDR
  private int objectAttributeMemoryAddress;


  // PPU register latches & counters
  private int registerVT; // Vertical tile index latch
  private int registerHT; // Horizontal tile index latch
  private int registerFV; // Fine vertical scroll latch
  private int registerFH; // Fine horizontal scroll latch
  private int registerV;  // Vertical name table selection latch
  private int registerH;  // Horizontal name table selection latch
  private int registerS;  // Background pattern table selection latch
  private int counterFV;
  private int counterV;
  private int counterH;
  private int counterVT;
  private int counterHT;

  private boolean getPPUSTATUSCalled;

  public void setCPU(final MOS6502 cpu) {
    this.cpu = cpu;
  }

  public void setPPUMemory(final Memory memory) {
    ppuMemory = memory;
  }

  public void init() {
    cycles = 0;
    objectAttributeMemory = new ObjectAttributeMemory();
    ppuScrollAndAddressLatch = true;
    getPPUSTATUSCalled = false;

    registerVT = 0;
    registerHT = 0;
    registerFV = 0;
    registerFH = 0;
    registerV = 0;
    registerH = 0;
    registerS = 0;

    counterFV = 0;
    counterV = 0;
    counterH = 0;
    counterVT = 0;
    counterHT = 0;
  }


  /**
   * 262 scanlines
   *
   * 0..19: nothing 20: dummy 21..260: render (240 lines) 261: dummy
   */
  public void clock(final int clockCycles) {
    if (cpuClocksUntilNMI > -1) {
      cpuClocksUntilNMI--;
      if (cpuClocksUntilNMI == -1) {
        cpu.requestNMI();
      }
    }

    // PPU runs at 3x the clock rate of the CPU
    for (int i = 0; i < clockCycles * 3; i++) {
      int scanline = cycles / 341;
      int pixelOf = cycles % 341;

      // Caution: Reading PPUSTATUS at the exact start of vertical blank will return a 0 in D7
      // but clear the latch anyway, causing the program to miss frames. See NMI for details.
      if (scanline == 0 && pixelOf == 0 && !getPPUSTATUSCalled) {
        verticalBlankStarted = 1;
      } else if (scanline < 20) {
        // vertical blank, do nothing
      } else if (scanline == 20 && pixelOf == 0) {
        // Start of dummy scanline before 'real' scanlines
        verticalBlankStarted = 0;
      } else if (scanline == 261 && pixelOf == 340) {
        // End of last (dummy) scanline
        if (generateNMIAtVBLStart == 1) {
          cpu.requestNMI();
        }

        // Start of new frame
        cycles = -1;
      }

      cycles++;
      getPPUSTATUSCalled = false;
    }
  }

  public void writeRegister(final int address, final int byteValue) {
    assert address > 0x1FFF && address < 0x2008 : "Tried to write to an out of range PPU address";
    assert byteValue <= 0xFF : "Tried to write a value larger than a byte to a PPU address";

    switch (address) {
      case 0x2000:
        setPPUCtrl(byteValue);
        break;
      case 0x2001:
        setPPUMask(byteValue);
        break;
      case 0x2002:
        setPPUStatus(byteValue);
        break;
      case 0x2003:
        setOAMAddr(byteValue);
        break;
      case 0x2004:
        setOAMData(byteValue);
        break;
      case 0x2005:
        setPPUScroll(byteValue);
        break;
      case 0x2006:
        setPPUAddr(byteValue);
        break;
      case 0x2007:
        setPPUData(byteValue);
        break;
    }
  }

  public int readRegister(final int address) {
    assert address > 0x1FFF && address < 0x2008 : "Tried to read from an out of range PPU address";
    assert address == 0x2002 || address == 0x2004 || address == 0x2007 : "Tried to read from an invalid PPU address";

    switch (address) {
      case 0x2002:
        return getPPUStatus();
      case 0x2004:
        return getOAMData();
      case 0x2007:
        return getPPUData();
    }

    throw new IllegalStateException("Invalid state in PPU");
  }

//----------------------------------------------
// Private interface
//----------------------------------------------

  private int getPPUStatus() {
    getPPUSTATUSCalled = true;

    // Bits 0-4 are 'bits previously written to a PPU register'  Do I need to worry about them?
    int status = 0;

    status = Bits.setBit(status, spriteOverflow, 5);
    status = Bits.setBit(status, sprite0Hit, 6);
    status = Bits.setBit(status, verticalBlankStarted, 7);

    verticalBlankStarted = 0; // cleared by the read
    ppuScrollAndAddressLatch = true; // reset the latch

    return status;
  }

  private void setPPUStatus(final int byteValue) {
    // You shouldn't be able to set the PPU status...
  }

  private int cpuClocksUntilNMI = -1;

  private void setPPUCtrl(final int byteValue) {
    boolean wasNMIEnabled = generateNMIAtVBLStart == 1;

    xScrollNameTableSelection = Bits.getBit(byteValue, 0);
    yScrollNameTableSelection = Bits.getBit(byteValue, 1);
    incrementPPUAddressBy_1_32 = Bits.getBit(byteValue, 2);
    objectPatternTableSelection = Bits.getBit(byteValue, 3);
    backgroundPatternTableSelection = Bits.getBit(byteValue, 4);
    spriteSize_8_16 = Bits.getBit(byteValue, 5);
    generateNMIAtVBLStart = Bits.getBit(byteValue, 7);

    registerH = Bits.getBit(byteValue, 0);
    registerV = Bits.getBit(byteValue, 1);
    registerS = Bits.getBit(byteValue, 4);

    if (!wasNMIEnabled && generateNMIAtVBLStart == 1) {
      if (verticalBlankStarted == 1) {
        cpuClocksUntilNMI = 1;
      }
    } else {
      cpuClocksUntilNMI = -1;
    }
  }

  private void setPPUAddr(final int byteValue) {
    if (ppuScrollAndAddressLatch) {
      registerFV = (byteValue >> 4) & 0x3;
      registerV = Bits.getBit(byteValue, 3);
      registerH = Bits.getBit(byteValue, 2);
      registerVT = (registerVT & 7) | ((byteValue & 3) << 3); // ??
    } else {
      registerVT = (registerVT & 0x18) | ((byteValue >> 5) & 0x7); // ??
      registerHT = byteValue & 0x1F;

      // Init counters
      counterFV = registerFV;
      counterV = registerV;
      counterH = registerH;
      counterVT = registerVT;
      counterHT = registerHT;
    }

    ppuScrollAndAddressLatch = !ppuScrollAndAddressLatch;
  }

  private int getPPUData() {
    final int ppuAddress = getPPUAddressFromCounters();
    final int value = ppuMemory.readByte(ppuAddress);

    incrementPPUAddressAndSetCounters(ppuAddress);

    return value;
  }

  private void setPPUData(final int byteValue) {
    final int ppuAddress = getPPUAddressFromCounters();
    ppuMemory.writeByte(ppuAddress, byteValue);

    incrementPPUAddressAndSetCounters(ppuAddress);
  }

  private void setPPUScroll(final int byteValue) {
    if (ppuScrollAndAddressLatch) {
      // horizontal
      registerHT = byteValue >> 3;
      registerFH = byteValue & 0x7;
    } else {
      // vertical
      registerFV = byteValue & 0x7;
      registerVT = byteValue >> 3;
    }

    ppuScrollAndAddressLatch = !ppuScrollAndAddressLatch;
  }

  private void setOAMAddr(final int byteValue) {
    objectAttributeMemoryAddress = byteValue;
  }

  private int getOAMData() {
    return objectAttributeMemory.readByte(objectAttributeMemoryAddress);
  }

  private void setOAMData(final int byteValue) {
    objectAttributeMemory.writeByte(objectAttributeMemoryAddress, byteValue);

    // Increment
    objectAttributeMemoryAddress = (objectAttributeMemoryAddress + 1) & 0xFF; // Should it wrap?
  }

  private void setPPUMask(final int byteValue) {
    disableColour = Bits.getBit(byteValue, 0);
    showBackgroundLeftColumn = Bits.getBit(byteValue, 1);
    showObjectsLeftColumn = Bits.getBit(byteValue, 2);
    enableBackgroundDisplay = Bits.getBit(byteValue, 3);
    enableObjectsDisplay = Bits.getBit(byteValue, 4);
    intensifyReds = Bits.getBit(byteValue, 5);
    intensifyGreens = Bits.getBit(byteValue, 6);
    intensifyBlues = Bits.getBit(byteValue, 7);
  }

  private int getPPUAddressFromCounters() {
    int address = (counterFV & 0x03) << 12;
    address |= (counterV & 0x01) << 11;
    address |= (counterH & 0x01) << 10;
    address |= (counterVT & 0x1F) << 5;
    address |= (counterHT & 0x1F);

    return address & 0x7FFF; // TODO: why is this wrap needed?
  }

  private void setCountersFromPPUAddress(final int address) {
    assert address <= 0xFFFF : "Invalid PPU Address";

    counterFV = (address >> 12) & 0x03;
    counterV = (address >> 11) & 0x01;
    counterH = (address >> 10) & 0x01;
    counterVT = (address >> 5) & 0x1F;
    counterHT = address & 0x1F;
  }

  private void incrementPPUAddressAndSetCounters(final int address) {
    final int incrementBy = incrementPPUAddressBy_1_32 == 1 ? 32 : 1;
    final int newPPUAddress = (address + incrementBy) & 0xFFFF;  // Should it wrap?

    setCountersFromPPUAddress(newPPUAddress);
  }
}
