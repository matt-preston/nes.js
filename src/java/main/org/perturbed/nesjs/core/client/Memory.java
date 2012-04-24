package org.perturbed.nesjs.core.client;


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
    
    private APU apu;
    private PPU ppu;
    
    public void setAPU(APU anAPU)
    {
        apu = anAPU;
        ppu = new PPU(); // TODO
    }
    
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
        assert anAddress >= 0 && anAddress <= 0xFFFF : "Tried to read an out of range address";
        
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
            return ppu.readRegister(anAddress);
        }
        else if (anAddress < 0x4000)
        {
            // Mirrors of PPU registers (0x2000 -> 0x2007) every 8 bytes            
            return ppu.readRegister(0x2000 + (anAddress & 0x7));
        }
        else
        {
            // Must be between 0x4000 and 0x4018, but the only readable register is 0x4015
            assert anAddress == 0x4015;
            
            return apu.getStatusRegister();
        }        
    }
    
    public final void writeByte(int anAddress, int aByte)
    {
        assert anAddress >= 0 && anAddress <= 0xFFFF : "Tried to write an out of range address";
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
            ppu.writeRegister(anAddress, aByte);            
        }
        else if (anAddress < 0x4000)
        {
            // Mirrors of PPU registers (0x2000 -> 0x2007) every 8 bytes
            ppu.writeRegister(0x2000 + (anAddress & 0x7), aByte);
        }
        else
        {
            // Must be between 0x4000 and 0x4018
            assert anAddress > 0x3FFF && anAddress < 0x4018 : "Address out of range for APU";
            
            apu.writeRegister(anAddress, aByte);            
        }
    }
    
    private final int byteAtIndex(int[] aMemory, int anIndex)
    {
        int _byte = aMemory[anIndex];
        
        assert _byte < 0x0100 : "read byte out of range";
        
        return _byte;
    }
}
