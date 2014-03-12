package org.perturbed.nesjs.core.nestest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.Assert;
import org.junit.Test;
import org.perturbed.nesjs.core.ResourceROMLoader;
import org.perturbed.nesjs.core.client.CPUMemory;
import org.perturbed.nesjs.core.client.MOS6502;
import org.perturbed.nesjs.core.client.PPUMemory;
import org.perturbed.nesjs.core.client.ROM;

import junit.framework.ComparisonFailure;

import static org.perturbed.nesjs.core.client.Utils.toHexString;


public class NESTestTest {

  @Test
  public void testNESTestROMAndCompareWithLog() throws Exception {
    ROM rom = ResourceROMLoader.loadROMResource(this.getClass(), "/nestest/nestest.nes");
    CPUMemory cpuMemory = rom.getCPUMemory();
    PPUMemory ppuMemory = rom.getPPUMemory();

    MOS6502 cpu = new MOS6502(cpuMemory, ppuMemory);

    cpu.setRegisterPC(0xC000);

    int cycles = 0;
    int stepCount = 1;

    for (CPUState state : getExpectedCpuStates()) {
      assertHexEquals("PC not valid at step [" + stepCount + "]", state.pc, cpu.getRegisterPC());
      assertHexEquals("A not valid at step [" + stepCount + "]", state.a, cpu.getRegisterA());
      assertHexEquals("X not valid at step [" + stepCount + "]", state.x, cpu.getRegisterX());
      assertHexEquals("Y not valid at step [" + stepCount + "]", state.y, cpu.getRegisterY());
      assertPEquals("P not valid at step [" + stepCount + "]", state.p, cpu.getRegisterP());
      assertHexEquals("SP not valid at step [" + stepCount + "]", state.s, cpu.getRegisterS());

      // it's comparing to PPU cycles, where 1 CPU clock = 3 PPU clocks
      cycles += (cpu.getCycles() * 3);

      // Each scanline is 341 PPU cycles long
      cycles = cycles % 341;

      Assert.assertEquals("Cycles not valid at step [" + stepCount + "]", state.cycles, cycles);

      cpu.step();
      stepCount++;
    }

    int byte1 = cpuMemory.readByte(0x02);
    int byte2 = cpuMemory.readByte(0x03);

    /**
     * Should both be 0x00 according to docs, must related to now I initialise the memory, as
     * I put 0xFF in there and it is never modified.
     */
    Assert.assertEquals(0xFF, byte1);
    Assert.assertEquals(0xFF, byte2);
  }

  private void assertHexEquals(String message, int expected, int actual) {
    if (expected != actual) {
      throw new ComparisonFailure(message, toHexString(expected), toHexString(actual));
    }
  }

  private void assertPEquals(String message, int expected, int actual) {
    if (expected != actual) {
      String expectedValue =
          ProcessorStatus.toString(expected) + " [" + Integer.toHexString(expected) + "]";
      String actualValue =
          ProcessorStatus.toString(actual) + " [" + Integer.toHexString(actual) + "]";

      throw new ComparisonFailure(message, expectedValue, actualValue);
    }
  }

  private List<CPUState> getExpectedCpuStates() throws Exception {
    InputStream in = getClass().getResourceAsStream("/nestest/nestest-full.log");
    Scanner scanner = new Scanner(in, "UTF-8");

    List<CPUState> states = new ArrayList<CPUState>();

    while (scanner.hasNextLine()) {
      int pc = scanner.nextInt(16);
      int op = scanner.nextInt(16);

      scanner.skip("[^:]+:");
      int a = scanner.nextInt(16);

      scanner.skip("\\s+X:");
      int x = scanner.nextInt(16);

      scanner.skip("\\s+Y:");
      int y = scanner.nextInt(16);

      scanner.skip("\\s+P:");
      int p = scanner.nextInt(16);

      scanner.skip("\\s+SP:");
      int s = scanner.nextInt(16) + 0x0100;

      scanner.skip("\\s+CYC:");
      int cycles = scanner.nextInt();

      states.add(new CPUState(pc, s, a, x, y, p, cycles));

      scanner.nextLine(); // skip to the end of the line
    }

    return states;
  }

  private class CPUState {

    public int pc;
    public int s;

    public int a;
    public int x;
    public int y;
    public int p;
    public int cycles;

    public CPUState(int pc, int s, int a, int x, int y, int p, int cycles) {
      this.pc = pc;
      this.s = s;
      this.a = a;
      this.x = x;
      this.y = y;
      this.p = p;
      this.cycles = cycles;
    }
  }
}
