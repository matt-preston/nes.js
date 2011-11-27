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
        String _hex = Integer.toHexString(aValue).toUpperCase();
        
        if(_hex.length() % 2 == 0)
        {
            return "0x" + _hex;
        }
        else
        {
            return "0x0" + _hex;
        }
    }
}
