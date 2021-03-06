package org.perturbed.nesjs.core.client;

/**
 * Minimal implementation to get the length counter for the first square channel working
 *
 * @author Matt
 */
public class APU {

  private int frameSequencerMode;

  private int frameSequencerDividerPeriod;
  private int frameSequencerDividerCounter;

  private int frameCounter;
  private int frameCounterMax;

  private boolean frameSequencerInterruptFlag;
  private boolean frameSequencerInterruptDisabled;

  private SquareChannel1 square1;

  private MOS6502 cpu;

  public APU() {
    boolean pal = false; // Kept as a reminder
    frameSequencerDividerPeriod = pal ? 16626 : 14915;
    frameSequencerDividerPeriod *= 24;

    frameSequencerDividerCounter = 0;

    frameCounter = 0;
    frameCounterMax = 4;

    frameSequencerInterruptFlag = false;
    frameSequencerInterruptDisabled = false;

    square1 = new SquareChannel1();
  }

  public void setCPU(MOS6502 cpu) {
    this.cpu = cpu;
  }

  public void clock(int cpuCycles) {
    if (!frameSequencerInterruptDisabled && frameSequencerInterruptFlag) {
      cpu.requestIRQ();
    }

    frameSequencerDividerCounter -= cpuCycles * 48;

    if (frameSequencerDividerCounter <= 0) {
      frameSequencerClock();

      frameSequencerDividerCounter += frameSequencerDividerPeriod;
    }
  }

  public void writeRegister(int address, int byteValue) {
    if (address >= 0x4000 && address < 0x4004) {
      square1.writeRegister(address, byteValue);
    } else if (address == 0x4015) {
      // Update the enabled status of Square channel 1
      square1.setEnabled((byteValue & 1) != 0);
    } else if (address == 0x4017) {
      frameSequencerInterruptFlag = false;

      frameSequencerMode = (byteValue >> 7) & 1;
      frameSequencerInterruptDisabled = ((byteValue >> 6) & 1) == 1;

      if (frameSequencerMode == 0) {
        // 4 step sequence
        frameCounterMax = 4;
        frameCounter = 0;
      } else {
        // 5 step sequence
        frameCounterMax = 5;
        frameCounter = 0;
        frameSequencerClock();
      }

      frameSequencerDividerCounter = frameSequencerDividerPeriod;
    } else {
      //System.out.printf("APU: Write to [%s] with [%s]\n", Utils.toHexString(anAddress), Utils.toBinaryString(aByte));
    }
  }

  // Read from 0x4015
  public int getStatusRegister() {
    int frameInterrupt = (frameSequencerInterruptFlag && !frameSequencerInterruptDisabled) ? 1 : 0;

    int result = 0;
    result |= square1.getLengthStatus();
    result |= frameInterrupt << 6;

    frameSequencerInterruptFlag = false;

    return result;
  }

  private void frameSequencerClock() {
    if (frameSequencerMode == 0) {
      // 4 Step sequence
      if (frameCounter == 1 || frameCounter == 3) {
        square1.clockLengthCounter();
      }

      // 4 Step sequence
      if (frameCounter == 3) {
        // set interrupt flag
        frameSequencerInterruptFlag = true;
      }
    } else {
      // 5 Step sequence
      if (frameCounter == 0 || frameCounter == 2) {
        square1.clockLengthCounter();
      }
    }

    frameCounter++;
    if (frameCounter >= frameCounterMax) {
      frameCounter = 0;
    }
  }
}
