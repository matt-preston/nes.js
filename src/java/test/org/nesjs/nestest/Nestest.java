package org.nesjs.nestest;

import org.nesjs.core.*;

public class Nestest
{
    public static void main(String[] args) throws Exception
    {
        ROM.initMemoryWithFile("nestest.nes");
        
        MOS6502 _6502 = new MOS6502();
        
        _6502.init();
        _6502.reset();
        
        _6502.pc = 0xC000;
        
        _6502.step();
    }
}
