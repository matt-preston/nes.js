package org.perturbed.nesjs.core.instructions;

import org.junit.*;

public class TestInstructions extends BlarggTestCase
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
}
