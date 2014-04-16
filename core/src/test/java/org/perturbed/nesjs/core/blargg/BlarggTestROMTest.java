package org.perturbed.nesjs.core.blargg;

import java.io.IOException;

import org.perturbed.nesjs.core.ResourceROMLoader;
import org.perturbed.nesjs.core.blargg.BlarggTestROM.TestLogger;
import org.perturbed.nesjs.core.client.ROM;
import org.perturbed.nesjs.core.client.Utils;

import junit.framework.Assert;

public abstract class BlarggTestROMTest {

  public String getROMRootPath() {
    return "";
  }

  public void runTestROM(String romName) throws IOException {
    String path = getROMRootPath() + romName;

    ROM rom = ResourceROMLoader.loadROMResource(this.getClass(), path);

    BlarggTestROM romWrapper = new BlarggTestROM(rom);

    romWrapper.runTestToCompletion(new TestLogger() {
      @Override
      public void println(String string) {
        System.out.println(string);
      }

      @Override
      public void testFailedWithError(String message, int status) {
        System.out.println(message);
        Assert.fail("Test failure, invalid status code [" + Utils.toHexString(status) + "]");
      }

      @Override
      public void testCompletedSuccessfully(String string) {
        System.out.println(string);
        Assert.assertTrue(string.toLowerCase().contains("passed"));
      }
    });
  }
}
