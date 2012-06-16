package org.perturbed.nesjs.core.blargg;

import org.junit.*;
import org.perturbed.nesjs.core.blargg.BlarggTestROMTest;

public class PPUInterruptsTest extends BlarggTestROMTest
{
    @Override
    public String getROMRootPath()
    {
        return "/ppu_vbl_nmi/rom_singles/";
    }

    /*
    @Test
    public void testVBLBasics() throws Exception
    {
        runTestROM("01-vbl_basics.nes");
    }
    */

    @Test
    public void testNothing()
    {
        // Just to keep maven happy...
    }
}
