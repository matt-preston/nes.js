package org.nesjs.core;

public class SquareChannel1
{
    private final int[] lengthLookup = new int[]
    {
        0x0A, 0xFE,
        0x14, 0x02,
        0x28, 0x04,
        0x50, 0x06,
        0xA0, 0x08,
        0x3C, 0x0A,
        0x0E, 0x0C,
        0x1A, 0x0E,
        0x0C, 0x10,
        0x18, 0x12,
        0x30, 0x14,
        0x60, 0x16,
        0xC0, 0x18,
        0x48, 0x1A,
        0x10, 0x1C,
        0x20, 0x1E
    };
    
    private boolean enabled;
    private int lengthCounter;
    private boolean lengthCounterHalt;
    
    public SquareChannel1()
    {
        enabled = false;
        lengthCounter = 0;
        lengthCounterHalt = true;
    }
    
    
    public void writeRegister(int anAddress, int aByte)
    {
        if(anAddress == 0x4000)
        {
            lengthCounterHalt = ((aByte & 0x20) == 1);

            if(lengthCounterHalt)
            {
                lengthCounter = 0;
            }
        }
        else if(anAddress == 0x4003)
        {
            if (enabled) 
            {
                lengthCounter = lengthLookup[(aByte & 0xF8) >> 3];
            }
        }
    }
    
    public void clockLengthCounter()
    {
        if(!lengthCounterHalt && lengthCounter > 0)
        {
            lengthCounter--;
        }
    }
    
    public int getLengthStatus()
    {
        return ((lengthCounter == 0 || !enabled) ? 0 : 1);
    }

    public void setEnabled(boolean isEnabled)
    {
        enabled = isEnabled;
        
        if (!enabled) 
        {
            lengthCounter = 0;
        }        
    }
}
