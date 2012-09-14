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

    @Test
    @Ignore("Not implemented dummy reads in CPU yet")
    public void test03DummyReads() throws Exception
    {
        runTestROM("03-dummy_reads.nes");
    }
    
    @Test
    @Ignore("Not implemented dummy reads in APU yet")
    public void test04DummyReadsAPU() throws Exception
    {
        runTestROM("04-dummy_reads_apu.nes");
    }
}
