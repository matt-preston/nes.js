package org.nesjs.core;

public class Memory
{
    public static final int[] lowMem = new int[0x0800];
    public static final int[] prom   = new int[0x10000];
    
    public static final int readSignedByte(int anAddress)
    {
        int _byte = readUnsignedByte(anAddress);

        if (_byte < 0x80)
        {
            return _byte;
        } 
        else
        {
            return _byte - 256;
        }
    }
    
    public static final int readWord(int anAddress)
    {
        return readUnsignedByte(anAddress) | (readUnsignedByte(anAddress + 1) << 8);
    }
    
    public static final int readUnsignedByte(int anAddress)
    {
        // Mask to 16 bit
        int _address = anAddress & 0xFFFF;

        if(_address < 0x2000)
        {
            // Low memory 2KB (mirrored 3 times)
            return lowMem[_address & 0x7FF] & 0xFF; // TODO, mask to a byte in the ROM loading?
        }
        else if (_address > 0x4017)
        {
            // Program ROM
            return prom[_address] & 0xFF;  // TODO, mask to a byte in the ROM loading?
        }

        System.out.println("Don't know how to read from memory address [" + _address + "]");

        return 0;  // TODO
    }
    
    public static final void writeUnsignedByte(byte aByte, int anAddress)
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
}
