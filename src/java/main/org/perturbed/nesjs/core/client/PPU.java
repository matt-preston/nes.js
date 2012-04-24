package org.perturbed.nesjs.core.client;

public class PPU
{
    public void writeRegister(int anAddress, int aByte)
    {
        //System.out.printf("Don't know how to write [%s] to PPU registers [%s]\n", Utils.toHexString(aByte), Utils.toHexString(anAddress));
    }
    
    public int readRegister(int anAddress)
    {
        //System.out.printf("Don't know how to read from PPU registers [%s]\n", Utils.toHexString(anAddress));
        
        return 0;
    }
}
