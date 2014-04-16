package org.perturbed.nesjs.core.blargg;

import org.junit.Ignore;
import org.junit.Test;

public class MiscInstructionsTest extends BlarggTestROMTest {

  @Override
  public String getROMRootPath() {
    return "/instr_misc/";
  }

  @Test
  public void testEverything() throws Exception {
    runTestROM("instr_misc.nes");
  }

  @Test
  public void test01AbsoluteXWrap() throws Exception {
    runTestROM("rom_singles/01-abs_x_wrap.nes");
  }

  @Test
  public void test02BranchWrap() throws Exception {
    runTestROM("rom_singles/02-branch_wrap.nes");
  }

  @Test
  public void test03DummyReads() throws Exception {
    runTestROM("rom_singles/03-dummy_reads.nes");
  }

  @Test
  public void test04DummyReadsAPU() throws Exception {
    runTestROM("rom_singles/04-dummy_reads_apu.nes");
  }
}
