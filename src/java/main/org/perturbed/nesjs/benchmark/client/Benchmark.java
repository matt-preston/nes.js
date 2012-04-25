package org.perturbed.nesjs.benchmark.client;

import java.io.IOException;

import org.perturbed.nesjs.core.client.BlarggTestROM;
import org.perturbed.nesjs.core.client.BlarggTestROM.TestLogger;
import org.perturbed.nesjs.core.client.MOS6502;
import org.perturbed.nesjs.core.client.Memory;
import org.perturbed.nesjs.core.client.ROM;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;

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
        int _cycles = 0;
        
        for(int _index = 0; _index < 9000; _index++)
        {
        	_6502.step();
        	_cycles += _6502.getCycles();
        }
        
        long _duration = System.currentTimeMillis() - _start;
        
        double _factor = 1000.0 / _duration;
        
        double _frequency = _factor * _cycles;
    
        String _c = NumberFormat.getDecimalFormat().format(_cycles);
        String _f = NumberFormat.getDecimalFormat().format(_frequency);
        
        Window.alert("Nestest took " + _duration + "ms to execute " + _c + " clock cycles - > " + _f + " cycles per second");        
    }
    
    
    private void runTestROM(ROM aROM) throws IOException
    {
        BlarggTestROM _wrapper = new BlarggTestROM(aROM);
        
        _wrapper.runTestToCompletion(new TestLogger() 
        {
			@Override
			public void testFailedWithError(String aMessage, int aStatus) 
			{
			}
			
			@Override
			public void testCompletedSuccessfully(String aString) 
			{
				System.out.println(aString);
			}
			
			@Override
			public void println(String aString) 
			{			
			}
		});        
    }
}