package org.perturbed.nesjs.core.client;


public interface Memory
{
    int readByte(int anAddress);
    void writeByte(int anAddress, int aByte);
}
