package org.perturbed.nesjs.core.client;

import java.io.*;

/**
 * Parser for the iNES ROM format
 * 
 * http://wiki.nesdev.com/w/index.php/INES
 */
public class ROM
{
    private CPUMemory cpuMemory;
    private PPUMemory ppuMemory;
    
    public ROM(int[] aBytes) throws IOException
    {
        init(aBytes);
    }

    public CPUMemory getCPUMemory()
    {
        return cpuMemory;
    }

    public PPUMemory getPPUMemory()
    {
        return ppuMemory;
    }

    private void init(final int[] bytes) throws IOException
    {
        // Read the header
        int[] _header = new int[16];
        for(int _index = 0; _index < 16; _index++)
        {
            _header[_index] = bytes[_index];
        }

        if(!((_header[0] == 0x4E) &&  // N
                (_header[1] == 0x45) &&  // E
                (_header[2] == 0x53) &&  // S
                (_header[3] == 0x1A)))   // <CR>
        {
            // Invalid ROM header.
            throw new IOException("Invalid ROM header");
        }

        // The number of 16 KB PRG ROM units
        int _prgROMUnitCount   = _header[4];

        // The number of CHR ROM in 8 KB units (Value 0 means the board uses CHR RAM)
        int _chrROMUnitCount  = _header[5];

        //int _mirroring  = ((_header[6] & 1) != 0 ? 1 : 0);
        //boolean _batteryRam = (_header[6] & 2) != 0;
        //boolean _trainer    = (_header[6] & 4) != 0;
        //boolean _fourScreen = (_header[6] & 8) != 0;

        int _mapperType = (_header[6] >> 4) | (_header[7] & 0xF0);

        if(_mapperType != 0)
        {
            throw new IOException("Can't handle mapper type [" + _mapperType + "] ROMs");
        }

        /**
         * Mapper 000 should only have 1 or 2 banks
         */
        if(_prgROMUnitCount > 2)
        {
            throw new IOException("Don't know how to handle " + _prgROMUnitCount + " PRG ROM units");
        }

        if(_chrROMUnitCount != 1)
        {
            throw new IOException("Don't know how to handle " + _prgROMUnitCount + " CHR ROM units");
        }

        // Load the PRG ROM
        int[][] _prgROM = new int[_prgROMUnitCount][Constants._16K];
        int _offset = 16; // Starts after the 16 byte header - TODO a trainer may be present before the PRG ROM

        for(int _index = 0; _index < _prgROMUnitCount; _index++)
        {
            copyBytes(_offset, Constants._16K, bytes, _prgROM[_index]);
            _offset += Constants._16K;
        }

        // Load the CHR ROM
        int[] _chrROM = new int[Constants._8K];
        copyBytes(_offset, Constants._8K, bytes, _chrROM);

        cpuMemory = new CPUMemory();
        ppuMemory = new PPUMemory();

        if (_prgROMUnitCount > 1)
        {
            loadRomBank(_prgROM[0], cpuMemory.prgMem, 0x8000);
            loadRomBank(_prgROM[1], cpuMemory.prgMem, 0xC000);
        }
        else
        {
            loadRomBank(_prgROM[0], cpuMemory.prgMem, 0x8000);
            loadRomBank(_prgROM[0], cpuMemory.prgMem, 0xC000);
        }

        loadRomBank(_chrROM, ppuMemory.chrMem, 0x0000);
    }

    
    private final void loadRomBank(int[] aBank, int[] aMemory, int anAddress)
    {
        System.arraycopy(aBank, 0, aMemory, anAddress, aBank.length);
    }
    
    private final void copyBytes(int anOffset, int aLength, int[] bytes, int[] aTarget)
    {
        for(int _index = 0; _index < aLength; _index++)
        {
            aTarget[_index] = bytes[anOffset + _index];
        }
    }
}
