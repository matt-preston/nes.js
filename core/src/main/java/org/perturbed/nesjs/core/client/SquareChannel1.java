package org.perturbed.nesjs.core.client;

public class SquareChannel1 {

  private final int[] lengthLookup = new int[] {
      0x0A, 0xFE,
      0x14, 0x02,
      0x28, 0x04,
      0x50, 0x06,
      0xA0, 0x08,
      0x3C, 0x0A,
      0x0E, 0x0C,
      0x1A, 0x0E,
      0x0C, 0x10,
      0x18, 0x12,
      0x30, 0x14,
      0x60, 0x16,
      0xC0, 0x18,
      0x48, 0x1A,
      0x10, 0x1C,
      0x20, 0x1E
  };

  private boolean enabled;
  private int lengthCounter;
  private boolean lengthCounterHalt;

  public SquareChannel1() {
    enabled = false;
    lengthCounter = 0;
    lengthCounterHalt = true;
  }


  public void writeRegister(int address, int byteValue) {
    if (address == 0x4000) {
      lengthCounterHalt = ((byteValue & 0x20) == 1);

      if (lengthCounterHalt) {
        lengthCounter = 0;
      }
    } else if (address == 0x4003) {
      if (enabled) {
        lengthCounter = lengthLookup[(byteValue & 0xF8) >> 3];
      }
    }
  }

  public void clockLengthCounter() {
    if (!lengthCounterHalt && lengthCounter > 0) {
      lengthCounter--;
    }
  }

  public int getLengthStatus() {
    return ((lengthCounter == 0 || !enabled) ? 0 : 1);
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;

    if (!this.enabled) {
      lengthCounter = 0;
    }
  }
}
