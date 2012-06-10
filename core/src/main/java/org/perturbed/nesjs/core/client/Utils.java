package org.perturbed.nesjs.core.client;


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
