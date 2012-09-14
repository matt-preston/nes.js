package org.perturbed.nesjs.core.client;

interface Mem
{
	int readByte(int anAddress);
	void writeByte(int anAddress, int aByte);
}

public class PPU
{
    private int currentScanline;
    private int currentX;
	private Mem objectAttributeMemory;
    
	private boolean ppuScrollAndAddressLatch;
    
	// PPU_CTRL
    private int xScrollNameTableSelection = 0;
	private int yScrollNameTableSelection = 0;
	private int incrementPPUAddressBy_1_32 = 0;
	private int objectPatternTableSelection = 0;
	private int backgroundPatternTableSelection = 0;
	private int spriteSize_8_16 = 0;
	private int generateNMIAtVBLStart = 0;
	
	// PPU_MASK
	private int disableColour = 0;
	private int showBackgroundLeftColumn = 0;
	private int showObjectsLeftColumn = 0;
	private int enableBackgroundDisplay = 0;
	private int enableObjectsDisplay = 0;
	private int intensifyReds = 0;
	private int intensifyGreens = 0;
	private int intensifyBlues = 0;
	
	// PPU_STATUS
	private int spriteOverflow = 0;
	private int sprite0Hit = 0;
	private int verticalBlankStarted = 0;
	
	// OAM_ADDR
	private int objectAttributeMemoryAddress;
	
	// PPU registers & counters
    private int registerVT; // Vertical tile index counter
    private int registerHT; // Horizontal tile index counter
    private int registerFV; // Fine vertical scroll latch
    private int registerFH; // Fine horizontal scroll latch
    
	
	public void init() 
	{
	    currentScanline = 0;
	    currentX = 0;
	    objectAttributeMemory = new Mem()
	    {
			public int readByte(int anAddress) 
			{
				return 0;
			}

			public void writeByte(int anAddress, int aByte) 
			{
			}	    	
	    };
	    ppuScrollAndAddressLatch = true;
	    
	    registerFH = 0;
	    registerFV = 0;
	    registerHT = 0;
	    registerVT = 0;
	}
	

	public void clock(int aClockCycles)
    {
        // PPU runs at 3x the clock rate of the CPU
		for(int cycles = 0; cycles < aClockCycles * 3; cycles++) 
		{
			/*
		    if(hitSprite0())
		    {
		    	setHitSprite0StatusFlag();
		    }
		    */
			
			currentX++;
		    
		}
    }
    
    public void writeRegister(int anAddress, int aByte)
    {
        assert anAddress > 0x1FFF && anAddress < 0x2008  : "Tried to write to an out of range PPU address";
        assert anAddress != 0x2002 : "Tried to write to the PPUSTATUS register";
        assert aByte <= 0xFF : "Tried to write a value larger than a byte to a PPU address";
        
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

    private int getPPUStatus()
    {
    	// Bits 0-4 are 'bits previously written to a PPU register'  Does I need to worry about them?
    	int status = 0;
    	
    	status = Bits.setBit(status, spriteOverflow, 5);
    	status = Bits.setBit(status, sprite0Hit, 6);
    	status = Bits.setBit(status, verticalBlankStarted, 7);
    	
    	verticalBlankStarted = 0; // cleared by the read
    	ppuScrollAndAddressLatch = true; // reset the latch
    	
        return status;
    }

    private void setPPUCtrl(int aByte)
    {
    	xScrollNameTableSelection = Bits.getBit(aByte, 0);
    	yScrollNameTableSelection = Bits.getBit(aByte, 1);
    	incrementPPUAddressBy_1_32 = Bits.getBit(aByte, 2);
    	objectPatternTableSelection = Bits.getBit(aByte, 3);
    	backgroundPatternTableSelection = Bits.getBit(aByte, 4);
    	spriteSize_8_16 = Bits.getBit(aByte, 5);
    	generateNMIAtVBLStart = Bits.getBit(aByte, 7);
    }

    private void setPPUAddr(int aByte)
    {
//        System.out.println("Write PPUADDR");
    }
    
    private int getPPUData()
    {
//        System.out.println("Read PPUDATA");
        return 0;
    }
    
    private void setPPUData(int aByte)
    {
//        System.out.println("Write PUDATA");
    }

    private void setPPUScroll(int aByte)
    {
    	if(ppuScrollAndAddressLatch) 
        {
        	// horizontal
        	registerHT = aByte >> 3;
            registerFH = aByte & 0x7;
        }
        else
        {
        	// vertical
        	registerFV = aByte & 0x7;
        	registerVT = aByte >> 3;
        }
        
        ppuScrollAndAddressLatch = !ppuScrollAndAddressLatch;
    }

    private void setOAMAddr(int aByte)
    {
        objectAttributeMemoryAddress = aByte;
    }

    private int getOAMData()
    {
    	return objectAttributeMemory.readByte(objectAttributeMemoryAddress);
    }

    private void setOAMData(int aByte)
    {
    	objectAttributeMemory.writeByte(objectAttributeMemoryAddress, aByte);
    	objectAttributeMemoryAddress = (objectAttributeMemoryAddress + 1) & 0xFF;
    }
    
    private void setPPUMask(int aByte)
    {
    	disableColour = Bits.getBit(aByte, 0);
        showBackgroundLeftColumn = Bits.getBit(aByte, 1);
        showObjectsLeftColumn = Bits.getBit(aByte, 2);
        enableBackgroundDisplay = Bits.getBit(aByte, 3);
        enableObjectsDisplay = Bits.getBit(aByte, 4);
        intensifyReds = Bits.getBit(aByte, 5);
        intensifyGreens = Bits.getBit(aByte, 6);
        intensifyBlues = Bits.getBit(aByte, 7);
    }
}
