package org.nesjs.core;


/**
 * Only used for debugging
 * 
 * @author Matt
 */
public class Utils
{
    public static final String toHexString(int aValue)
    {
        if(aValue < 0x0100)
        {
            return String.format("0x%02X", aValue);
        }
        else
        {
            return String.format("0x%04X", aValue);
        }
    }
    
    public static final String toBinaryString(int aByte)
    {
        assert aByte < 0x0100;
        
        String _result = Integer.toBinaryString(aByte);
        
        while(_result.length() < 8)
        {
            _result = "0" + _result;
        }
        
        return _result;
    }
}
