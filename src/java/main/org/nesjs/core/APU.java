package org.nesjs.core;


public class APU
{
    private boolean frameSequencerIRQDisabled;
    
    private int frameSequencerMode;
    
    private int frameSequencerDividerPeriod;
    private int frameSequencerDividerCounter;
    
    private int frameCounter;
    private int frameCounterMax;
    
    private boolean frameInterruptFlag;
    
    public APU()
    {
        boolean _pal = false; // Kept as a reminder
        frameSequencerDividerPeriod = _pal ? 16626:14915;
        frameSequencerDividerPeriod *= 24;
        
        frameSequencerDividerCounter = 0;
        
        frameCounter = 0;
        frameCounterMax = 4;
        
        frameInterruptFlag = false;
    }
    
    public void clock(int aCPUCycles)
    {
        if (!frameSequencerIRQDisabled && frameInterruptFlag) 
        {
            //System.out.println("Interrupt CPU");
        }
        
        
        frameSequencerDividerCounter -= aCPUCycles * 48;
        
        if(frameSequencerDividerCounter <= 0)
        {
            frameSequencerClock();
            
            frameSequencerDividerCounter += frameSequencerDividerPeriod;
        }
    }
    
    public void writeRegister(int anAddress, int aByte)
    {
        if(anAddress == 0x4017)
        {
            frameInterruptFlag = false;
            
            frameSequencerMode = (aByte >> 7) & 1;
            frameSequencerIRQDisabled = ((aByte >> 6) & 1) == 1;
            
            if(frameSequencerMode == 0)
            {
                // 4 step sequence
                frameCounterMax = 4;
                frameCounter = 0;
            }
            else
            {
                // 5 step sequence
                frameCounterMax = 5;
                frameCounter = 0;
                frameSequencerClock();
            }
            
            frameSequencerDividerCounter = frameSequencerDividerPeriod;
        }
        else
        {
            System.out.printf("Write to [%s] with [%s]\n", Utils.toHexString(anAddress), Utils.toBinaryString(aByte));    
        }        
    }
    
    public int getStatusRegister()
    {        
        frameInterruptFlag = false;
        
        return 0;
    }
    
    private void frameSequencerClock()
    {
    	if(frameSequencerMode == 0)
    	{
    		// 4 Step sequence
    		if(frameCounter == 1 || frameCounter == 3)
            {
                // clock length counters
                //System.out.println("clock length counter");
            }
            
            // 4 Step sequence
            if(frameCounter == 3)
            {
                // set interrupt flag
                frameInterruptFlag = true;
            }
    	}
    	else
    	{
    		// 5 Step sequence
    		if(frameCounter == 0 || frameCounter == 2)
            {
                // clock length counters
                //System.out.println("clock length counter");
            }
    	}
    	        
        frameCounter++;
        if (frameCounter >= frameCounterMax) 
        {
            frameCounter = 0;
        }
    }
}
