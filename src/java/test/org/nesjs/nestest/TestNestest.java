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
    	Memory _memory = ROM.memoryWithROMFile("nestest.nes");
        MOS6502 _6502 = new MOS6502(_memory);
        
        _6502.reset();        
        _6502.setRegisterPC(0xC000);

        int _stepCount = 0;
        
        for (CPUState _state : getExpectedCpuStates())
        {
            assertHexEquals("PC not valid at step [" + _stepCount + "]", _state.pc, _6502.getRegisterPC());
            assertHexEquals("A not valid at step [" + _stepCount + "]", _state.a, _6502.getRegisterA());
            assertHexEquals("X not valid at step [" + _stepCount + "]", _state.x, _6502.getRegisterX());
            assertHexEquals("Y not valid at step [" + _stepCount + "]", _state.y, _6502.getRegisterY());
            assertPEquals("P not valid at step [" + _stepCount + "]", _state.p, _6502.getRegisterP());
            assertHexEquals("SP not valid at step [" + _stepCount + "]", _state.sp, _6502.getRegisterSP());
            
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
        Scanner _scanner = new Scanner(new File("nestest-simple.log"), "UTF-8");
        
        List<CPUState> _states = new ArrayList<CPUState>();
        
        while(_scanner.hasNextLine())
        {
            int _pc = _scanner.nextInt(16);        
            int _op = _scanner.nextInt(16);
            
            _scanner.skip("\\s+A:");
            int _a = _scanner.nextInt(16);
            
            _scanner.skip("\\s+X:");
            int _x = _scanner.nextInt(16);
            
            _scanner.skip("\\s+Y:");
            int _y = _scanner.nextInt(16);
            
            _scanner.skip("\\s+P:");
            int _p = _scanner.nextInt(16);
            
            _scanner.skip("\\s+SP:");
            int _sp = _scanner.nextInt(16);
            
            _states.add(new CPUState(_pc, _op, _sp, _a, _x, _y, _p));
        }
        
        return _states;
    }
    
    
    private class CPUState
    {
        public int pc;        
        public int sp;
        
        public int a;
        public int x;
        public int y;
        public int p;
        
        public CPUState(int aPc, int aOpcode, int aSp, int aA, int aX, int aY, int aP)
        {
            pc = aPc;            
            sp = aSp;
            a = aA;
            x = aX;
            y = aY;
            p = aP;
        }        
    }
}
