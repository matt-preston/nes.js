package org.nesjs;

import java.io.*;

import org.nesjs.core.*;

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
        
        return new ROM(_out.toByteArray());
    }
}
