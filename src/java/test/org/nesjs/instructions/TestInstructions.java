package org.nesjs.instructions;

import java.io.*;

import org.junit.*;
import org.nesjs.core.*;

public class TestInstructions
{
    @Test
    public void test01Implied() throws Exception
    {
        runTestROM("01-implied.nes");
    }
        
    @Test
    public void test02Immediate() throws Exception
    {
        runTestROM("02-immediate.nes");
    }
    
    @Test
    public void test03ZeroPage() throws Exception
    {
        runTestROM("03-zero_page.nes");
    }
    
    @Test
    public void test04ZeroPageXY() throws Exception
    {
        runTestROM("04-zp_xy.nes");
    }
    
    @Test
    public void test05Absolute() throws Exception
    {
        runTestROM("05-absolute.nes");
    }
    
    @Test
    public void test06AbsoluteXY() throws Exception
    {
        runTestROM("06-abs_xy.nes");
    }
    
    @Test
    public void test07IndirectX() throws Exception
    {
        runTestROM("07-ind_x.nes");
    }
    
    @Test
    public void test08IndirectY() throws Exception
    {
        runTestROM("08-ind_y.nes");
    }
    
    @Test
    public void test09Branches() throws Exception
    {
        runTestROM("09-branches.nes");
    }
    
    @Test
    public void test10Stack() throws Exception
    {
        runTestROM("10-stack.nes");
    }
    
    @Test
    public void test11JumpJsr() throws Exception
    {
        runTestROM("11-jmp_jsr.nes");
    }
    
    @Test
    public void test12Rts() throws Exception
    {
        runTestROM("12-rts.nes");
    }
    
    @Test
    public void test13Rti() throws Exception
    {
        runTestROM("13-rti.nes");
    }
    
    @Test
    public void test14Brk() throws Exception
    {
        runTestROM("14-brk.nes");
    }
    
    @Test
    public void test15Special() throws Exception
    {
        runTestROM("15-special.nes");
    }
    
//-----------------------------------
// Private methods
//-----------------------------------    
    
    private void runTestROM(String aROMName) throws Exception
    {
        InputStream _in = getClass().getResourceAsStream(aROMName);
        Memory _memory = ROM.memoryWithROMInputStream(_in);
        
        MOS6502 _6502 = new MOS6502(_memory);
        
        _6502.reset();

        do
        {
            _6502.step();
        }
        while(!isTestFinished(_memory));
        
        String _message = getNullTerminatedString(_memory, 0x6004);
        System.out.println(_message);
        
        Assert.assertEquals(0x00, _memory.readByte(0x6000));
        Assert.assertTrue(_message.contains("Passed"));
    }
    
    private boolean isTestFinished(Memory aMemory)
    {
        int _6001 = aMemory.readByte(0x6001);
        int _6002 = aMemory.readByte(0x6002);
        int _6003 = aMemory.readByte(0x6003);
        
        boolean _testRunning = _6001 == 0xDE && _6002 == 0xB0 && _6003 == 0x61;  
        
        if(_testRunning)
        {
            int _6000 = aMemory.readByte(0x6000);
            
            if(_6000 == 0x80)
            {
                // Test in progress
                return false;
            }
            
            return true;
        }
        
        return false;
    }
    
    private String getNullTerminatedString(Memory aMemory, int aStartAddress)
    {
        StringBuilder _builder = new StringBuilder();
        
        int _byte;
        
        while((_byte = aMemory.readByte(aStartAddress++)) != 0x00)
        {
            _builder.append((char) _byte);
        }
        
        return _builder.toString();
    }
}
