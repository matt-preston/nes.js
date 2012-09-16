package org.perturbed.nesjs.core.client;

// TODO implement mirroring & buffering etc...
public class PPUMemory implements Memory2
{
    public final int[] chrMem = new int[Constants._16K];

    @Override
    public int readByte(int anAddress)
    {
        return chrMem[anAddress] & 0xFF;
    }

    @Override
    public void writeByte(int anAddress, int aByte)
    {
        chrMem[anAddress] = aByte & 0xFF;
    }
}
