package org.perturbed.nesjs.core.client;

import java.io.*;

public class ROM
{
    private int[] bytes;
    
    public ROM(int[] aBytes)
    {
        bytes = aBytes;
    }
    
    public Memory toMemory() throws IOException
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
        
        int _romCount   = _header[4];
        //int _vromCount  = _header[5] * 2;
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
        if(_romCount > 2)
        {
            throw new IOException("Don't know how to handle " + _romCount + " rom banks");
        }
        
         // Load PRG-ROM banks:
        int _offset = 16;
        int _bufferLength = bytes.length;

        int[][] _prom = new int[_romCount][16384];
        
        for (int _i = 0; _i < _romCount; _i++)
        {
            for (int _j = 0; _j < 16384; _j++)
            {
                if (_offset + _j >= _bufferLength)
                {
                    break;
                }

                _prom[_i][_j] = bytes[_offset + _j];
            }
            _offset += 16384;
        }

        Memory _memory = new Memory();
        
        if (_romCount > 1) 
        {
            loadRomBank(_prom[0], _memory.prom, 0x8000);
            loadRomBank(_prom[1], _memory.prom, 0xC000);
        }
        else
        {
            loadRomBank(_prom[0], _memory.prom, 0x8000);
            loadRomBank(_prom[0], _memory.prom, 0xC000);
        }
        
        return _memory;
    }
    
    private void loadRomBank(int[] aBank, int[] aMemory, int anAddress)
    {
        System.arraycopy(aBank, 0, aMemory, anAddress, 16384);
    }
}
