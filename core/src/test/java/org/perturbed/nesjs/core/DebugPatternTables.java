package org.perturbed.nesjs.core;

import java.io.IOException;

import org.perturbed.nesjs.core.client.PPUMemory;
import org.perturbed.nesjs.core.client.ROM;
import org.perturbed.nesjs.core.client.Utils;
import org.perturbed.nesjs.core.nestest.NESTestTest;

public class DebugPatternTables {

  public static void main(String[] args) throws Exception {
    DebugPatternTables d = new DebugPatternTables();

    d.debug(NESTestTest.class, "/nestest/nestest.nes");
  }

  public void debug(Class<?> clazz, String romResource) throws IOException {
    ROM rom = ResourceROMLoader.loadROMResource(clazz, romResource);

    PPUMemory memory = rom.getPPUMemory();

    for (int tile = 0; tile < 256; tile++) {
      System.out.println("Tile: " + (tile + 1));

      for (int i = 0; i < 16; i++) {
        int chrOffset = (tile * 16) + i;

        String binary = Utils.toBinaryString(memory.chrMem[chrOffset]).replace('0', ' ');

        System.out.println(Utils.toHexString(chrOffset) + ":  " + binary);

        if (i == 7) {
          System.out.println();
        }
      }

      System.out.println();
    }
  }
}
