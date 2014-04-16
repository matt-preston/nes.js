package org.perturbed.nesjs.core.blargg;

import org.perturbed.nesjs.core.client.CPUMemory;
import org.perturbed.nesjs.core.client.MOS6502;
import org.perturbed.nesjs.core.client.PPUMemory;
import org.perturbed.nesjs.core.client.ROM;

/**
 * A wrapper that know how to run Blargg's test ROMs and how to interpret pass/fail
 *
 * @author Matt
 */
public class BlarggTestROM {

  public static interface TestLogger {
    void println(String string);
    void testCompletedSuccessfully(String string);
    void testFailedWithError(String message, int status);
  }

  private static final int MAX_LOOPS = 2000;

  private final ROM rom;

  public BlarggTestROM(ROM rom) {
    this.rom = rom;
  }

  public void runTestToCompletion(TestLogger logger) {
    final CPUMemory cpuMemory = rom.getCPUMemory();
    final PPUMemory ppuMemory = rom.getPPUMemory();

    final MOS6502 cpu = new MOS6502(cpuMemory, ppuMemory);

    boolean hasTestResetCPU = false;

    int loops = 0;

    do {
      cpu.execute(100000);

      if (!hasTestResetCPU && isTestNeedsReset(cpuMemory)) {
        hasTestResetCPU = true;

        String message = getNullTerminatedString(cpuMemory, 0x6004);
        logger.println(message);

        // Reset after 100ms, so just spin the CPU for a bit
        cpu.execute(10000);
        cpu.requestReset();

        logger.println("RESET");
      }

      loops++;

      if (loops > MAX_LOOPS) {
        logger.testFailedWithError("Test timed out, too many cycles without finishing.", loops);
        return;
      }
    }
    while (!isTestFinished(cpuMemory));

    int statusByte = cpuMemory.readByte(0x6000);
    String message = getNullTerminatedString(cpuMemory, 0x6004);

    if (statusByte == 0) {
      logger.testCompletedSuccessfully(message);
    } else {
      logger.testFailedWithError(message, statusByte);
    }
  }

  private boolean isTestNeedsReset(CPUMemory memory) {
    if (isTestRunning(memory)) {
      int value = memory.readByte(0x6000);

      if (value == 0x81) {
        // Test needs reset after at least 100ms
        return true;
      }
    }

    return false;
  }

  private boolean isTestFinished(CPUMemory memory) {
    if (isTestRunning(memory)) {
      int value = memory.readByte(0x6000);

      if (value < 0x80) {
        return true;
      }
    }

    return false;
  }

  private boolean isTestRunning(CPUMemory memory) {
    return (memory.readByte(0x6001) == 0xDE) &&
        (memory.readByte(0x6002) == 0xB0) &&
        (memory.readByte(0x6003) == 0x61);
  }

  private String getNullTerminatedString(CPUMemory memory, int startAddress) {
    StringBuilder builder = new StringBuilder();

    int value;

    while ((value = memory.readByte(startAddress++)) != 0x00) {
      builder.append((char) value);
    }

    return builder.toString();
  }
}
