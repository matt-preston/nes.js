package org.perturbed.nesjs.core.blargg;

import org.perturbed.nesjs.core.client.CPUMemory;
import org.perturbed.nesjs.core.client.MOS6502;
import org.perturbed.nesjs.core.client.PPUMemory;
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

    private static final int MAX_LOOPS = 200;

	private ROM rom;
	
    public BlarggTestROM(ROM aROM)
    {
        rom = aROM;	
    }
    
    public void runTestToCompletion(TestLogger aLogger)
    {
        CPUMemory _cpuMemory = rom.getCPUMemory();
        PPUMemory _ppuMemory = rom.getPPUMemory();
        
        MOS6502 _6502 = new MOS6502(_cpuMemory, _ppuMemory);
        
        boolean _hasTestResetCPU = false;

        int loops = 0;

        do
        {
            _6502.execute(100000);

            if(!_hasTestResetCPU && isTestNeedsReset(_cpuMemory))
            {
                _hasTestResetCPU = true;
                
                String _message = getNullTerminatedString(_cpuMemory, 0x6004);
                aLogger.println(_message);
                
                // Reset after 100ms, so just spin the CPU for a bit
                _6502.execute(10000);                
                _6502.requestReset();
                
                aLogger.println("RESET");
            }

            loops++;

            if(loops > MAX_LOOPS)
            {
                aLogger.testFailedWithError("Test timed out, too many cycles without finishing the test.", loops);
                return;
            }
        }
        while(!isTestFinished(_cpuMemory));
        
        int _statusByte = _cpuMemory.readByte(0x6000);
        String _message = getNullTerminatedString(_cpuMemory, 0x6004);
        
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
    
    private final boolean isTestNeedsReset(CPUMemory aMemory)
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
    
    private final boolean isTestFinished(CPUMemory aMemory)
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
    
    private final boolean isTestRunning(CPUMemory aMemory)
    {
        return (aMemory.readByte(0x6001) == 0xDE) && 
        	   (aMemory.readByte(0x6002) == 0xB0) && 
        	   (aMemory.readByte(0x6003) == 0x61);
    }
    
    private String getNullTerminatedString(CPUMemory aMemory, int aStartAddress)
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
