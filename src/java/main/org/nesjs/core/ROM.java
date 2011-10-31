package org.nesjs.core;

import java.io.*;

public class ROM
{
    public static final void initMemoryWithFile(String aFile) throws Exception
    {
        byte[] _bytes = toByteArray(aFile);
        
         // Read the header
        byte[] _header = new byte[16];
        for(int _index = 0; _index < 16; _index++)
        {
            _header[_index] = _bytes[_index];
        }

        if(!((_header[0] == 0x4E) &&  // N
             (_header[1] == 0x45) &&  // E
             (_header[2] == 0x53) &&  // S
             (_header[3] == 0x1A)))   // <CR>
        {
            // Invalid ROM header.
            System.out.println("Invalid ROM header");
        }
        
        int _romCount   = _header[4];
        //int _vromCount  = _header[5] * 2;
        //int _mirroring  = ((_header[6] & 1) != 0 ? 1 : 0);
        //boolean _batteryRam = (_header[6] & 2) != 0;
        //boolean _trainer    = (_header[6] & 4) != 0;
        //boolean _fourScreen = (_header[6] & 8) != 0;
        //int _mapperType = (_header[6] >> 4) | (_header[7] & 0xF0);
        
         // Load PRG-ROM banks:
        int _offset = 16;
        int _bufferLength = _bytes.length;

        int[][] _prom = new int[_romCount][16384];
        
        for (int _i = 0; _i < _romCount; _i++)
        {
            for (int _j = 0; _j < 16384; _j++)
            {
                if (_offset + _j >= _bufferLength)
                {
                    break;
                }

                _prom[_i][_j] = _bytes[_offset + _j];
            }
            _offset += 16384;
        }

        // Load prom into memory - the default mapper uses only a single bank, so
        // load it into both locations
        loadRomBank(_prom[0], 0x8000);
        loadRomBank(_prom[0], 0xC000);
    }
    
    private static void loadRomBank(int[] aBank, int anAddress)
    {
        System.arraycopy(aBank, 0, Memory.prom, anAddress, 16384);
    }
    
    private static byte[] toByteArray(String aFilename) throws Exception
    {
        FileInputStream _fin = new FileInputStream(aFilename);
        
        int _byte;
        
        ByteArrayOutputStream _buffer = new ByteArrayOutputStream();
        
        while((_byte = _fin.read()) > -1)
        {
            _buffer.write(_byte);
        }
        
        return _buffer.toByteArray();
    }
}
