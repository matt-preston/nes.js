package org.nesjs.nestest;

import java.io.*;
import java.util.*;

import junit.framework.ComparisonFailure;

import org.junit.*;
import org.nesjs.core.*;
import static org.nesjs.core.Utils.*;


public class TestNestest
{
    @Test
    public void testNestestRomAndCompareWithLog() throws Exception
    {
        InputStream _in = getClass().getResourceAsStream("nestest.nes");
    	Memory _memory = ROM.memoryWithROMInputStream(_in);
    	
        MOS6502 _6502 = new MOS6502(_memory);
            
        _6502.setRegisterPC(0xC000);

        int _cycles = 0;
        int _stepCount = 1;
        
        for (CPUState _state : getExpectedCpuStates())
        {
            assertHexEquals("PC not valid at step [" + _stepCount + "]", _state.pc, _6502.getRegisterPC());
            assertHexEquals("A not valid at step [" + _stepCount + "]", _state.a, _6502.getRegisterA());
            assertHexEquals("X not valid at step [" + _stepCount + "]", _state.x, _6502.getRegisterX());
            assertHexEquals("Y not valid at step [" + _stepCount + "]", _state.y, _6502.getRegisterY());
            assertPEquals("P not valid at step [" + _stepCount + "]", _state.p, _6502.getRegisterP());
            assertHexEquals("SP not valid at step [" + _stepCount + "]", _state.s, _6502.getRegisterS());
            
            _cycles += (_6502.getCycles() * 3); // it's comparing to PPU cycles, where 1 CPU clock = 3 PPU clocks
            
            _cycles = _cycles % 341; // Each scanline is 341 PPU cycles long
            
            Assert.assertEquals("Cycles not valid at step [" + _stepCount + "]", _state.cycles, _cycles);
            
            _6502.step();            
            _stepCount++;
        }
        
        int _02h = _memory.readByte(0x02);
        int _03h = _memory.readByte(0x03);
        
        /**
         * Should both be 0x00 according to docs, must related to now I initialise the memory, as
         * I put 0xFF in there and it is never modified. 
         */
        Assert.assertEquals(0xFF, _02h); 
        Assert.assertEquals(0xFF, _03h);        
    }
    
    private void assertHexEquals(String aMessage, int anExpected, int anActual)
    {
        if(anExpected != anActual)
        {
            throw new ComparisonFailure(aMessage, toHexString(anExpected), toHexString(anActual));
        }
    }

    private void assertPEquals(String aMessage, int anExpected, int anActual)
    {
        if(anExpected != anActual)
        {
            String _expected = ProcessorStatus.toString(anExpected) + " [" + Integer.toHexString(anExpected) + "]";
            String _actual   = ProcessorStatus.toString(anActual) + " [" + Integer.toHexString(anActual) + "]";
            
            throw new ComparisonFailure(aMessage, _expected, _actual);
        }
    }
    
    private List<CPUState> getExpectedCpuStates() throws Exception
    {
        InputStream _in = getClass().getResourceAsStream("nestest-full.log");
        Scanner _scanner = new Scanner(_in, "UTF-8");
        
        List<CPUState> _states = new ArrayList<CPUState>();
        
        while(_scanner.hasNextLine())
        {
            int _pc = _scanner.nextInt(16);        
            int _op = _scanner.nextInt(16);
            
            _scanner.skip("[^:]+:");
            int _a = _scanner.nextInt(16);
            
            _scanner.skip("\\s+X:");
            int _x = _scanner.nextInt(16);
            
            _scanner.skip("\\s+Y:");
            int _y = _scanner.nextInt(16);
            
            _scanner.skip("\\s+P:");
            int _p = _scanner.nextInt(16);
            
            _scanner.skip("\\s+SP:");
            int _s = _scanner.nextInt(16) + 0x0100;
            
            _scanner.skip("\\s+CYC:");
            int _cycles = _scanner.nextInt();
            
            _states.add(new CPUState(_pc, _op, _s, _a, _x, _y, _p, _cycles));
            
            _scanner.nextLine(); // skip to the end of the line
        }
        
        return _states;
    }
    
    private class CPUState
    {
        public int pc;        
        public int s;
        
        public int a;
        public int x;
        public int y;
        public int p;
        public int cycles;
        
        public CPUState(int aPc, int aOpcode, int aS, int aA, int aX, int aY, int aP, int aCycles)
        {
            pc = aPc;            
            s = aS;
            a = aA;
            x = aX;
            y = aY;
            p = aP;
            cycles = aCycles;
        }        
    }
}
