package org.nesjs.core;

import java.io.*;

public class ROM
{
    public static final Memory memoryWithROMInputStream(InputStream anInputStream) throws Exception
    {
        return memoryWithROMBytes(toByteArray(anInputStream));
    }
    
    public static final Memory memoryWithROMFile(String aFile) throws Exception
    {
        return memoryWithROMBytes(toByteArray(aFile));
    }
    
    private static final Memory memoryWithROMBytes(int[] aBytes) throws Exception
    {
        // Read the header
        int[] _header = new int[16];
        for(int _index = 0; _index < 16; _index++)
        {
            _header[_index] = aBytes[_index];
        }

        if(!((_header[0] == 0x4E) &&  // N
             (_header[1] == 0x45) &&  // E
             (_header[2] == 0x53) &&  // S
             (_header[3] == 0x1A)))   // <CR>
        {
            // Invalid ROM header.
            throw new Exception("Invalid ROM header");
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
            throw new Exception("Can't handle mapper type [" + _mapperType + "] ROMs");
        }
        
        /**
         * Mapper 000 should only have 1 or 2 banks
         */
        if(_romCount > 2)
        {
            throw new RuntimeException("Don't know how to handle " + _romCount + " rom banks");
        }
        
         // Load PRG-ROM banks:
        int _offset = 16;
        int _bufferLength = aBytes.length;

        int[][] _prom = new int[_romCount][16384];
        
        for (int _i = 0; _i < _romCount; _i++)
        {
            for (int _j = 0; _j < 16384; _j++)
            {
                if (_offset + _j >= _bufferLength)
                {
                    break;
                }

                _prom[_i][_j] = aBytes[_offset + _j];
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
    
    private static void loadRomBank(int[] aBank, int[] aMemory, int anAddress)
    {
        System.arraycopy(aBank, 0, aMemory, anAddress, 16384);
    }
    
    private static int[] toByteArray(String aFilename) throws Exception
    {
        return toByteArray(new FileInputStream(aFilename));        
    }
    
    private static int[] toByteArray(InputStream anInputStream) throws Exception
    {
        int _byte;
        
        ByteArrayOutputStream _buffer = new ByteArrayOutputStream();
        
        while((_byte = anInputStream.read()) > -1)
        {
            _buffer.write(_byte);
        }
        
        anInputStream.close();
        
        byte[] _bytes = _buffer.toByteArray();
        
        // We need unsigned bytes, so we can't use the normal byte type.
        int[] _result = new int[_bytes.length];
        
        for (int _index = 0; _index < _bytes.length; _index++)
        {
            _result[_index] =  _bytes[_index] & 0xFF;
        }
        
        return _result;
    }
}
