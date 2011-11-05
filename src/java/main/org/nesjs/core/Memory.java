package org.nesjs.core;


public class Memory
{
    public static final int[] lowMem = new int[0x0800];
    public static final int[] prom   = new int[0x10000];
        
    public static void resetLowMemory() 
    {
        for (int _index = 0; _index < lowMem.length; _index++) 
        {
            lowMem[_index] = 0xFF;
        }
        
        lowMem[0x008] = 0xF7;
        lowMem[0x009] = 0xEF;
        lowMem[0x00A] = 0xDF;
        lowMem[0x00F] = 0xBF;
    }
    
    
    public static final int readByte(int anAddress)
    {
        // Mask to 16 bit
        int _address = anAddress & 0xFFFF;

        if(_address < 0x2000)
        {
            // Low memory 2KB (mirrored 3 times)            
            return byteAtIndex(lowMem, _address & 0x7FF);
        }
        else if (_address > 0x4017)
        {
            // Program ROM
            return byteAtIndex(prom, _address);
        }

        System.out.println("Don't know how to read from memory address [" + _address + "]");

        return 0;  // TODO
    }
    
    public static final void writeByte(int aByte, int anAddress)
    {
        if (anAddress < 0x2000)
        {
            // Low memory 2KB (mirrored 3 times)
            lowMem[anAddress & 0x7FF] = aByte;
        }
        else if (anAddress > 0x4017)
        {
            // Program ROM
            prom[anAddress] = aByte;
        }
        else
        {
            System.out.println("Don't know how to write to memory at address [" + anAddress + "]");
        }
    }
    
    private static final int byteAtIndex(int[] aMemory, int anIndex)
    {
        /**
         * Mask to the lowest byte
         * TODO - do this during ROM loading instead?
         */
        return aMemory[anIndex] & 0xFF;
    }
}
