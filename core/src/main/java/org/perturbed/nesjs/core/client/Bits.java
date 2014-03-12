package org.perturbed.nesjs.core.client;

/*
 * TODO, make a simple byte sized BitSet class?
 */
public class Bits {

  // Will hopefully be inlined...
  public static final int getBit(final int byteValue, final int index) {
    return (byteValue >> index) & 1;
  }

  public static final int setBit(final int byteValue, final int value, final int index) {
    return byteValue | (value << index);
  }
}
