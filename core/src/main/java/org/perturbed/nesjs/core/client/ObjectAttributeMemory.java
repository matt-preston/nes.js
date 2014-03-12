package org.perturbed.nesjs.core.client;


public class ObjectAttributeMemory implements Memory {

  private final int[] bytes = new int[256];

  @Override
  public int readByte(int address) {
    return bytes[address] & 0xFF;
  }

  @Override
  public void writeByte(int address, int byteValue) {
    bytes[address] = byteValue & 0xFF;
  }
}
