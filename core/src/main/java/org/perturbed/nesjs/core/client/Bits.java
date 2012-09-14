package org.perturbed.nesjs.core.client;

/*
 * TODO, make a simple byte sized BitSet class?
 */
public class Bits {
	
	// Will hopefully be inlined...
	public static final int getBit(final int aByte, final int anIndex) 
	{
	  return (aByte >> anIndex) & 1;	
	}
	
	public static final int setBit(final int aByte, final int value, final int index)
	{
	    return aByte | (value << index);	
	}
}
