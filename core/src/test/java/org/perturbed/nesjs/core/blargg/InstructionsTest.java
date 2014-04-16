package org.perturbed.nesjs.core.blargg;

import org.junit.Test;

public class InstructionsTest extends BlarggTestROMTest {

  @Override
  public String getROMRootPath() {
    return "/instr_test-v5/";
  }

  @Test
  public void testAllInstructions() throws Exception {
    runTestROM("all_instrs.nes");
  }

  @Test
  public void testOfficialOnly() throws Exception {
    runTestROM("official_only.nes");
  }

  @Test
  public void test01Basics() throws Exception {
    runTestROM("rom_singles/01-basics.nes");
  }

  @Test
  public void test02Implied() throws Exception {
    runTestROM("rom_singles/02-implied.nes");
  }

  @Test
  public void test03Immediate() throws Exception {
    runTestROM("rom_singles/03-immediate.nes");
  }

  @Test
  public void test04ZeroPage() throws Exception {
    runTestROM("rom_singles/04-zero_page.nes");
  }

  @Test
  public void test05ZeroPageXY() throws Exception {
    runTestROM("rom_singles/05-zp_xy.nes");
  }

  @Test
  public void test06Absolute() throws Exception {
    runTestROM("rom_singles/06-absolute.nes");
  }

  @Test
  public void test07AbsoluteXY() throws Exception {
    runTestROM("rom_singles/07-abs_xy.nes");
  }

  @Test
  public void test08IndirectX() throws Exception {
    runTestROM("rom_singles/08-ind_x.nes");
  }

  @Test
  public void test09IndirectY() throws Exception {
    runTestROM("rom_singles/09-ind_y.nes");
  }

  @Test
  public void test10Branches() throws Exception {
    runTestROM("rom_singles/10-branches.nes");
  }

  @Test
  public void test11Stack() throws Exception {
    runTestROM("rom_singles/11-stack.nes");
  }

  @Test
  public void test12JumpJsr() throws Exception {
    runTestROM("rom_singles/12-jmp_jsr.nes");
  }

  @Test
  public void test13Rts() throws Exception {
    runTestROM("rom_singles/13-rts.nes");
  }

  @Test
  public void test14Rti() throws Exception {
    runTestROM("rom_singles/14-rti.nes");
  }

  @Test
  public void test15Brk() throws Exception {
    runTestROM("rom_singles/15-brk.nes");
  }

  @Test
  public void test16Special() throws Exception {
    runTestROM("rom_singles/16-special.nes");
  }
}
