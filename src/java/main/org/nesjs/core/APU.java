package org.nesjs.core;

public class APU
{
    public void clock(int aCPUCycles)
    {
        // Clock the frame counter        
    }
    
    public void writeRegister(int anAddress, int aByte)
    {
        System.out.printf("Write to [%s] with [%s]\n", Utils.toHexString(anAddress), Utils.toBinaryString(aByte));        
    }
    
    public int getStatusRegister()
    {        
        return 0;
    }
}
