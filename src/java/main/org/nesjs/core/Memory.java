package org.nesjs.core;


/**
 * Implements iNes Mapper 000 memory mapping
 *  
 * iNES Mapper 000 is used to designate a board with up to 32 KiB PRG ROM, 8 KiB CHR ROM. 
 *  
 * @author Matt
 */
public class Memory
{
    public final int[] lowMem = new int[0x0800];
    public final int[] prom   = new int[0x10000];
        
    public void resetLowMemory() 
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
    
    
    public final int readByte(int anAddress)
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
        else if (anAddress < 0x2008)
        {
            // PPU registers
            //System.out.printf("Don't know how to read from PPU registers [%s]\n", Utils.toHexString(anAddress));
            
            return 0; // TODO
        }
        else if (anAddress < 0x4000)
        {
            // Mirrors of of 0x2000 every 8 bytes
            //System.out.printf("Don't know how to read from PPU register mirrors [%s]\n", Utils.toHexString(anAddress));
            
            return 0; // TODO
        }
        else
        {
            // Must be between 0x4000 and 0x4018
            assert anAddress > 0x3FFF && anAddress < 0x4018;
            
            // NES APU and I/O registers            
            //System.out.printf("Don't know how to read from APU and I/O registers [%s]\n", Utils.toHexString(anAddress));
            
            return 0; // TODO
        }        
    }
    
    public final void writeByte(int aByte, int anAddress)
    {
        assert aByte < 0x0100 : "Attempting to write out of range byte";
        
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
        else if (anAddress < 0x2008)
        {
            // PPU registers
            //System.out.printf("Don't know how to write [%s] to PPU registers [%s]\n", Utils.toHexString(aByte), Utils.toHexString(anAddress));
        }
        else if (anAddress < 0x4000)
        {
            // Mirrors of of 0x2000 every 8 bytes
            //System.out.printf("Don't know how to write [%s] to PPU register mirrors [%s]\n", Utils.toHexString(aByte), Utils.toHexString(anAddress));
        }
        else
        {
            // Must be between 0x4000 and 0x4018
            assert anAddress > 0x3FFF && anAddress < 0x4018;
            
            // NES APU and I/O registers            
            //System.out.printf("Don't know how to write [%s] to APU and I/O registers [%s]\n", Utils.toHexString(aByte), Utils.toHexString(anAddress));
        }
    }
    
    private final int byteAtIndex(int[] aMemory, int anIndex)
    {
        int _byte = aMemory[anIndex];
        
        assert _byte < 0x0100 : "read byte out of range";
        
        return _byte;
    }
}
