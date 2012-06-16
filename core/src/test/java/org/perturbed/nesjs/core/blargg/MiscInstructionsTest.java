package org.perturbed.nesjs.core.blargg;

import org.junit.*;
import org.perturbed.nesjs.core.blargg.BlarggTestROMTest;

public class MiscInstructionsTest extends BlarggTestROMTest
{
    @Override
    public String getROMRootPath()
    {
        return "/instr_misc/rom_singles/";
    }

    @Test
    public void test01AbsoluteXWrap() throws Exception
    {
        runTestROM("01-abs_x_wrap.nes");
    }
    
    @Test
    public void test02BranchWrap() throws Exception
    {
        runTestROM("02-branch_wrap.nes");
    }

    /*
     * Does not work at all yet, creates an infinite loop, needs a working PPU status
    @Test
    public void test03DummyReads() throws Exception
    {
        runTestROM("03-dummy_reads.nes");
    }
    */
    
    /*
     * Requires a working APU implementation
     * 
    @Test
    public void test03DummyReadsAPU() throws Exception
    {
        runTestROM("04-dummy_reads_apu.nes");
    }
    */
}
