package org.perturbed.nesjs.core.client;


public interface Memory {

  int readByte(int address);

  void writeByte(int address, int byteValue);
}
