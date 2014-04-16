package org.perturbed.nesjs.core.blargg;

import org.junit.Ignore;
import org.junit.Test;

@Ignore("Needs a working PPU to visually verify test result")
public class BranchTimingTest extends BlarggTestROMTest {

  @Override
  public String getROMRootPath() {
    return "/branch_timing/";
  }

  @Test
  public void testBranchBasics() throws Exception {
    runTestROM("1.Branch_Basics.nes");
  }

  @Test
  public void testBackwardBranch() throws Exception {
    runTestROM("2.Backward_Branch.nes");
  }

  @Test
  public void testForwardBranch() throws Exception {
    runTestROM("3.Forward_Branch.nes");
  }
}
