package org.perturbed.nesjs.benchmark.client;

import java.io.*;

import org.perturbed.nesjs.core.client.*;

import com.google.gwt.core.client.*;
import com.google.gwt.user.client.*;

public class Benchmark implements EntryPoint
{
    @Override
    public void onModuleLoad()
    {
        
        try
        {
            runNestestROM();
            
            runImpliedTestROM();
        }
        catch(IOException anExc)
        {
            Window.alert("Got an exception: " + anExc.getMessage());    
        }        
    }
    
    private void runImpliedTestROM() throws IOException
    {
        BinaryResource _resource = ROMs.INSTANCE.impliedTestROM();
        
        int[] _bytes = _resource.getBytes();
        
        ROM _rom = new ROM(_bytes);
        
        long _start = System.currentTimeMillis();
        runTestROM(_rom);
        long _duration = System.currentTimeMillis() - _start;
        
        Window.alert("Implied instructions test took " + _duration + "ms");
    }
    
    private void runNestestROM() throws IOException
    {
        BinaryResource _resource = ROMs.INSTANCE.nestestROM();
        
        int[] _bytes = _resource.getBytes();
        
        ROM _rom = new ROM(_bytes);
        
        Memory _memory = _rom.toMemory();
        
        MOS6502 _6502 = new MOS6502(_memory);
            
        _6502.setRegisterPC(0xC000);
        
        long _start = System.currentTimeMillis();
        _6502.execute(8990);
        long _duration = System.currentTimeMillis() - _start;
        
        Window.alert("Nestest took " + _duration + "ms");        
    }
    
    
    private void runTestROM(ROM aROM) throws IOException
    {
        Memory _memory = aROM.toMemory();
        
        MOS6502 _6502 = new MOS6502(_memory);
        
        boolean _hasTestResetCPU = false;
        
        do
        {
            _6502.step();
            
            if(isTestNeedsReset(_memory) && !_hasTestResetCPU)
            {
                _hasTestResetCPU = true;
                
                String _message = getNullTerminatedString(_memory, 0x6004);
                System.out.println(_message);
                
                // Reset after 100ms, so just spin the CPU for a bit
                for(int _index = 0; _index < 10000; _index++)
                {
                    _6502.step();
                }
                
                _6502.reset();
                System.out.println("RESET");
            }            
        }
        while(!isTestFinished(_memory));
        
        String _message = getNullTerminatedString(_memory, 0x6004);
        System.out.println(_message);
        
        /*
        Assert.assertEquals(0x00, _memory.readByte(0x6000));
        Assert.assertTrue(_message.contains("Passed"));
        */
    }

//-----------------------------------
//Private methods
//-----------------------------------     
    
    private boolean isTestNeedsReset(Memory aMemory)
    {
        if(isTestRunning(aMemory))
        {
            int _6000 = aMemory.readByte(0x6000);
            
            if(_6000 == 0x81)
            {
                // Test needs reset after at least 100ms
                return true;
            }
        }
        
        return false;
    }
    
    private boolean isTestFinished(Memory aMemory)
    {
        if(isTestRunning(aMemory))
        {
            int _6000 = aMemory.readByte(0x6000);
            
            if(_6000 < 0x80)
            {
                return true;
            }            
        }
        
        return false;
    }
    
    private boolean isTestRunning(Memory aMemory)
    {
        int _6001 = aMemory.readByte(0x6001);
        int _6002 = aMemory.readByte(0x6002);
        int _6003 = aMemory.readByte(0x6003);
        
        return _6001 == 0xDE && _6002 == 0xB0 && _6003 == 0x61;
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
