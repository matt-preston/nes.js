package org.perturbed.nesjs.core.blargg;

import org.perturbed.nesjs.core.client.MOS6502;
import org.perturbed.nesjs.core.client.Memory;
import org.perturbed.nesjs.core.client.ROM;

import java.io.IOException;

/**
 * A wrapper that know how to run Blargg's test ROMs and how to interpret pass/fail
 * 
 * @author Matt
 */
public class BlarggTestROM
{
	public static interface TestLogger
	{
		public void println(String aString);
		
		public void testCompletedSuccessfully(String aString);		
		public void testFailedWithError(String aMessage, int aStatus);
	}
	
	private ROM rom;
	
    public BlarggTestROM(ROM aROM)
    {
        rom = aROM;	
    }
    
    public void runTestToCompletion(TestLogger aLogger) throws IOException
    {
        Memory _memory = rom.toMemory();
        
        MOS6502 _6502 = new MOS6502(_memory);
        
        boolean _hasTestResetCPU = false;
        
        do
        {
            _6502.execute(100000);
            
            if(!_hasTestResetCPU && isTestNeedsReset(_memory))
            {
                _hasTestResetCPU = true;
                
                String _message = getNullTerminatedString(_memory, 0x6004);
                aLogger.println(_message);
                
                // Reset after 100ms, so just spin the CPU for a bit
                _6502.execute(10000);                
                _6502.requestReset();
                
                aLogger.println("RESET");
            }            
        }
        while(!isTestFinished(_memory));
        
        int _statusByte = _memory.readByte(0x6000);
        String _message = getNullTerminatedString(_memory, 0x6004);
        
        if(_statusByte == 0)
        {
            aLogger.testCompletedSuccessfully(_message);
        }
        else
        {
        	aLogger.testFailedWithError(_message, _statusByte);
        }        
    }
       
//-----------------------------------
// Private methods
//-----------------------------------   
    
    private final boolean isTestNeedsReset(Memory aMemory)
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
    
    private final boolean isTestFinished(Memory aMemory)
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
    
    private final boolean isTestRunning(Memory aMemory)
    {
        return (aMemory.readByte(0x6001) == 0xDE) && 
        	   (aMemory.readByte(0x6002) == 0xB0) && 
        	   (aMemory.readByte(0x6003) == 0x61);
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
