package org.perturbed.nesjs.core;

import java.io.*;

import org.perturbed.nesjs.core.client.*;

public class ResourceROMLoader
{
    public static ROM loadROMResource(Class<?> aClass, String aResourceName) throws IOException
    {
        InputStream _in = aClass.getResourceAsStream(aResourceName);
        ByteArrayOutputStream _out = new ByteArrayOutputStream();
        
        int _next;
        while((_next = _in.read()) != -1)
        {
            _out.write(_next);
        }
        
        _in.close();
        
        byte[] _buffer = _out.toByteArray();
        int[] _bytes = new int[_buffer.length];
        
        for(int _index = 0; _index < _buffer.length; _index++)
        {
            _bytes[_index] = _buffer[_index] & 0xFF;
        }
        
        return new ROM(_bytes);
    }
}
