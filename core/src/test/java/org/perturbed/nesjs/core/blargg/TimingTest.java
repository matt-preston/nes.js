package org.perturbed.nesjs.core.blargg;

import org.junit.*;
import org.perturbed.nesjs.core.blargg.BlarggTestROMTest;

public class TimingTest extends BlarggTestROMTest
{
    @Override
    public String getROMRootPath()
    {
        return "/instr_timing/rom_singles/";
    }

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
