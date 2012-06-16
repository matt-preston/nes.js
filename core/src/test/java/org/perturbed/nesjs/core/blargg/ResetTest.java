package org.perturbed.nesjs.core.blargg;

import org.junit.*;
import org.perturbed.nesjs.core.blargg.BlarggTestROMTest;

public class ResetTest extends BlarggTestROMTest
{
    @Override
    public String getROMRootPath()
    {
        return "/cpu_reset/";
    }

    @Test
    public void testRegisters() throws Exception
    {
        runTestROM("registers.nes");        
    }
    
    @Test
    public void testRAMAfterReset() throws Exception
    {
        runTestROM("ram_after_reset.nes");
    }
}
