package org.perturbed.nesjs.core.instructions.timing;

import org.junit.*;
import org.perturbed.nesjs.core.instructions.*;

public class TestTiming extends BlarggTestCase
{
    @Test
    public void testInstructionTiming() throws Exception
    {
        runTestROM("1-instr_timing.nes");
    }
    
    @Test
    public void testBranchTiming() throws Exception
    {
        runTestROM("2-branch_timing.nes");
    }
}
