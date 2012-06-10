package org.perturbed.nesjs.core.nestest;

public class ProcessorStatus
{
    /**
     * [0] carry
     * [1] zero
     * [2] interrupt disable
     * [3] decimal - not used 
     * [4] break - probably not used
     * [5] UNUSED - always set
     * [6] overflow
     * [7] negative
     */
    public static final String toString(int aProcessorStatus)
    {
        assert aProcessorStatus > -1 && aProcessorStatus < 0x0100;
        
        StringBuilder _builder = new StringBuilder(); 
        
        _builder.append(append("N", aProcessorStatus, 7));
        _builder.append(append("V", aProcessorStatus, 6));
        _builder.append(append("U", aProcessorStatus, 5));
        _builder.append(append("B", aProcessorStatus, 4));
        _builder.append(append("D", aProcessorStatus, 3));
        _builder.append(append("I", aProcessorStatus, 2));
        _builder.append(append("Z", aProcessorStatus, 1));
        _builder.append(append("C", aProcessorStatus, 0));
     
        return _builder.toString();
    }
    
    private static boolean isBitSet(int anInteger, int aBit)
    {
        return ((anInteger >> aBit) & 1) == 1;
    }
    
    private static String append(String anAppend, int anInteger, int aBit)
    {
        if(isBitSet(anInteger, aBit))
        {
            return anAppend.toUpperCase();
        }
        else
        {
            return anAppend.toLowerCase();
        }
    }
}
