package org.perturbed.nesjs.core.blargg;

import org.junit.Test;

public class TimingTest extends BlarggTestROMTest {

  @Override
  public String getROMRootPath() {
    return "/instr_timing/";
  }

  @Test
  public void testEverything() throws Exception {
    runTestROM("instr_timing.nes");
  }

  @Test
  public void testInstructionTiming() throws Exception {
    runTestROM("rom_singles/1-instr_timing.nes");
  }

  @Test
  public void testBranchTiming() throws Exception {
    runTestROM("rom_singles/2-branch_timing.nes");
  }
}
