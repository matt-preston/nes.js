package org.perturbed.nesjs.core.blargg;

import org.junit.Test;

public class InstructionsTest extends BlarggTestROMTest {

  @Override
  public String getROMRootPath() {
    return "/instr_test-v5/rom_singles/";
  }

  @Test
  public void test01Basics() throws Exception {
    runTestROM("01-basics.nes");
  }

  @Test
  public void test02Implied() throws Exception {
    runTestROM("02-implied.nes");
  }

  @Test
  public void test03Immediate() throws Exception {
    runTestROM("03-immediate.nes");
  }

  @Test
  public void test04ZeroPage() throws Exception {
    runTestROM("04-zero_page.nes");
  }

  @Test
  public void test05ZeroPageXY() throws Exception {
    runTestROM("05-zp_xy.nes");
  }

  @Test
  public void test06Absolute() throws Exception {
    runTestROM("06-absolute.nes");
  }

  @Test
  public void test07AbsoluteXY() throws Exception {
    runTestROM("07-abs_xy.nes");
  }

  @Test
  public void test08IndirectX() throws Exception {
    runTestROM("08-ind_x.nes");
  }

  @Test
  public void test09IndirectY() throws Exception {
    runTestROM("09-ind_y.nes");
  }

  @Test
  public void test10Branches() throws Exception {
    runTestROM("10-branches.nes");
  }

  @Test
  public void test11Stack() throws Exception {
    runTestROM("11-stack.nes");
  }

  @Test
  public void test12JumpJsr() throws Exception {
    runTestROM("12-jmp_jsr.nes");
  }

  @Test
  public void test13Rts() throws Exception {
    runTestROM("13-rts.nes");
  }

  @Test
  public void test14Rti() throws Exception {
    runTestROM("14-rti.nes");
  }

  @Test
  public void test15Brk() throws Exception {
    runTestROM("15-brk.nes");
  }

  @Test
  public void test16Special() throws Exception {
    runTestROM("16-special.nes");
  }
}
