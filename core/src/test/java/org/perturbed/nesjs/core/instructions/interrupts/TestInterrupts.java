package org.perturbed.nesjs.core.instructions.interrupts;

import org.junit.*;
import org.perturbed.nesjs.core.instructions.*;

public class TestInterrupts extends BlarggTestCase
{
    @Test
    public void test01CliLatency() throws Exception
    {
        runTestROM("1-cli_latency.nes");
    }
    
    /*
    @Test
    public void test02NmiAndBrk() throws Exception
    {
        runTestROM("2-nmi_and_brk.nes");
    }
    
    @Test
    public void test03NmiAndIrq() throws Exception
    {
        runTestROM("3-nmi_and_irq.nes");
    }
    
    @Test
    public void test04IrqAndDma() throws Exception
    {
        runTestROM("4-irq_and_dma.nes");
    }
    
    @Test
    public void test05BranchDelayIrq() throws Exception
    {
        runTestROM("5-branch_delays_irq.nes");
    }
    */
}
