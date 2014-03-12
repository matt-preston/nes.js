package org.perturbed.nesjs.core.blargg;

import org.junit.Ignore;
import org.junit.Test;

public class PPUTest extends BlarggTestROMTest {

  @Override
  public String getROMRootPath() {
    return "/ppu_tests/";
  }

  @Test
  @Ignore("Don't know how to handle 1 CHR ROM units")
  public void testPaletteRAM() throws Exception {
    runTestROM("palette_ram.nes");
  }

  @Test
  @Ignore("Don't know how to handle 1 CHR ROM units")
  public void testPowerUpPalette() throws Exception {
    runTestROM("power_up_palette.nes");
  }

  @Test
  @Ignore("Don't know how to handle 1 CHR ROM units")
  public void testSpriteRAM() throws Exception {
    runTestROM("sprite_ram.nes");
  }

  @Test
  @Ignore("Don't know how to handle 1 CHR ROM units")
  public void testVBLClearTime() throws Exception {
    runTestROM("vbl_clear_time.nes");
  }

  @Test
  @Ignore("Don't know how to handle 1 CHR ROM units")
  public void testVRAMAccess() throws Exception {
    runTestROM("vram_access.nes");
  }
}
