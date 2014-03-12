package org.perturbed.nesjs.core.client;

// TODO implement mirroring & buffering etc...
public class PPUMemory implements Memory {

  public final int[] chrMem = new int[Constants._16K];

  @Override
  public int readByte(int address) {
    return chrMem[address] & 0xFF;
  }

  @Override
  public void writeByte(int address, int byteValue) {
    chrMem[address] = byteValue & 0xFF;
  }
}
