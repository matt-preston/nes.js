package org.perturbed.nesjs.core.blargg;

import org.junit.Ignore;
import org.junit.Test;

public class InterruptsTest extends BlarggTestROMTest {

  @Override
  public String getROMRootPath() {
    return "/cpu_interrupts_v2/rom_singles/";
  }

  @Test
  public void test01CliLatency() throws Exception {
    runTestROM("1-cli_latency.nes");
  }

  @Test
  @Ignore("Not implemented NMI yet")
  public void test02NmiAndBrk() throws Exception {
    runTestROM("2-nmi_and_brk.nes");
  }

  @Test
  @Ignore("Not implemented NMI yet")
  public void test03NmiAndIrq() throws Exception {
    runTestROM("3-nmi_and_irq.nes");
  }

  @Test
  @Ignore("Not implemented DMA yet")
  public void test04IrqAndDma() throws Exception {
    runTestROM("4-irq_and_dma.nes");
  }

  @Test
  @Ignore("Doesn't work yet, not sure why")
  public void test05BranchDelayIrq() throws Exception {
    runTestROM("5-branch_delays_irq.nes");
  }
}
