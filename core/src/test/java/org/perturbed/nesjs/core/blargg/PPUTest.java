package org.perturbed.nesjs.core.blargg;

import org.junit.Ignore;
import org.junit.Test;

@Ignore("Needs a working PPU to visually verify test result")
public class PPUTest extends BlarggTestROMTest {

  @Override
  public String getROMRootPath() {
    return "/ppu_tests/";
  }

  @Test
  public void testPaletteRAM() throws Exception {
    runTestROM("palette_ram.nes");
  }

  @Test
  public void testPowerUpPalette() throws Exception {
    runTestROM("power_up_palette.nes");
  }

  @Test
  public void testSpriteRAM() throws Exception {
    runTestROM("sprite_ram.nes");
  }

  @Test
  public void testVBLClearTime() throws Exception {
    runTestROM("vbl_clear_time.nes");
  }

  @Test
  public void testVRAMAccess() throws Exception {
    runTestROM("vram_access.nes");
  }
}
