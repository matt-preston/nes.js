package org.perturbed.nesjs.core.client;

public class PPU
{
    public void writeRegister(int anAddress, int aByte)
    {
        assert anAddress > 0x1FFF && anAddress < 0x2008  : "Tried to write to an out of range PPU address";
        assert anAddress != 0x2002 : "Tried to write to the PPUSTATUS register";

        switch(anAddress)
        {
            case 0x2000: setPPUCtrl(aByte); break;
            case 0x2001: setPPUMask(aByte); break;
            case 0x2003: setOAMAddr(aByte); break;
            case 0x2004: setOAMData(aByte); break;
            case 0x2005: setPPUScroll(aByte); break;
            case 0x2006: setPPUAddr(aByte); break;
            case 0x2007: setPPUData(aByte); break;
        }
    }

    public int readRegister(int anAddress)
    {
        assert anAddress > 0x1FFF && anAddress < 0x2008  : "Tried to read from an out of range PPU address";
        assert anAddress == 0x2002 || anAddress == 0x2004 || anAddress == 0x2007 : "Tried to read from an invalid PPU address";

        switch(anAddress)
        {
            case 0x2002: return getPPUStatus();
            case 0x2004: return getOAMData();
            case 0x2007: return getPPUData();
        }

        throw new IllegalStateException("Invalid state in PPU");
    }


//----------------------------------------------
// Private interface
//----------------------------------------------

    private int getPPUData()
    {
//        System.out.println("Read PPUDATA");
        return 0;
    }

    private int getOAMData()
    {
//        System.out.println("Read OAMDATA");
        return 0;
    }

    private int getPPUStatus()
    {
//        System.out.println("Read PPUSTATUS");
        return 0;
    }

    private void setPPUCtrl(int aByte)
    {
//        System.out.println("Write PPUCTRL");
    }

    private void setPPUData(int aByte)
    {
//        System.out.println("Write PUDATA");
    }

    private void setPPUAddr(int aByte)
    {
//        System.out.println("Write PPUADDR");
    }

    private void setPPUScroll(int aByte)
    {
//        System.out.println("Write PPUSCROLL");
    }

    private void setOAMData(int aByte)
    {
//        System.out.println("Write OAMDATA");
    }

    private void setOAMAddr(int aByte)
    {
//        System.out.println("Write OAMADDR");
    }

    private void setPPUMask(int aByte)
    {
//        System.out.println("Write PPUMASK");
    }
}
