package org.perturbed.nesjs.core.client;


public class ObjectAttributeMemory implements Memory2
{
    private final int[] bytes = new int[256];
    @Override
    public int readByte(int anAddress) {
        return bytes[anAddress] & 0xFF;
    }

    @Override
    public void writeByte(int anAddress, int aByte) {
        bytes[anAddress] = aByte & 0xFF;
    }
}
