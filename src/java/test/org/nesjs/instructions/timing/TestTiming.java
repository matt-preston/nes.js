package org.nesjs.instructions.timing;

import org.junit.*;
import org.nesjs.instructions.*;

public class TestTiming extends BlarggTestCase
{
    @Test
    public void testInstructionTiming() throws Exception
    {
        System.out.println("start test");
        runTestROM("1-instr_timing.nes");
    }
}
