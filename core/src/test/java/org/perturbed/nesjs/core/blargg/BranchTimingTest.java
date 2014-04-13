package org.perturbed.nesjs.core.blargg;

import org.junit.Ignore;
import org.junit.Test;

public class BranchTimingTest extends BlarggTestROMTest {

  @Override
  public String getROMRootPath() {
    return "/branch_timing/";
  }

  @Test
  @Ignore("Needs a working PPU to visually verify test result")
  public void testBranchBasics() throws Exception {
    runTestROM("1.Branch_Basics.nes");
  }

  @Test
  @Ignore("Needs a working PPU to visually verify test result")
  public void testBackwardBranch() throws Exception {
    runTestROM("2.Backward_Branch.nes");
  }

  @Test
  @Ignore("Needs a working PPU to visually verify test result")
  public void testForwardBranch() throws Exception {
    runTestROM("3.Forward_Branch.nes");
  }
}
