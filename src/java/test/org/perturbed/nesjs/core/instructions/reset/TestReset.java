package org.perturbed.nesjs.core.instructions.reset;

import org.junit.*;
import org.perturbed.nesjs.core.instructions.*;

public class TestReset extends BlarggTestCase
{
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
