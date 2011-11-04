package org.nesjs.core;

public class Addressing
{
    /**
     * Instructions using absolute addressing contain a full 16 bit address 
     * to identify the target location.
     * 
     * @param anAddress
     * @return
     */
    public static final int absolute(int anAddress)
    {
        return readWord(anAddress);
    }
    
    /**
     * Immediate addressing allows the programmer to directly specify an 
     * 8 bit constant within the instruction. 
     * 
     * It is indicated by a '#' symbol followed by an numeric expression.
     * 
     * @param anAddress
     * @return
     */
    public static final int immediate(int anAddress)
    {
        return Memory.readByte(anAddress);
    }
    
    /**
     * An instruction using zero page addressing mode has only an 8 bit address 
     * operand. This limits it to addressing only the first 256 bytes of memory 
     * (e.g. $0000 to $00FF) where the most significant byte of the address is 
     * always zero. In zero page mode only the least significant byte of the address 
     * is held in the instruction making it shorter by one byte (important for space saving) 
     * and one less memory fetch during execution (important for speed).
     * 
     * An assembler will automatically select zero page addressing mode if the operand 
     * evaluates to a zero page address and the instruction supports the mode (not all do).
     * 
     * @param anAddress
     */
    public static final int zeroPage(int anAddress)
    {
        return Memory.readByte(anAddress);
    }
    
    /**
     * Relative addressing mode is used by branch instructions (e.g. BEQ, BNE, etc.) which contain 
     * a signed 8 bit relative offset (e.g. -128 to +127) which is added to program counter if the 
     * condition is true. As the program counter itself is incremented during instruction execution 
     * by two the effective address range for the target instruction must be with -126 to +129 bytes 
     * of the branch.
     * 
     * @param anAddress
     * @return
     */
    public static final int relative(int anAddress)
    {
        return readSignedByte(anAddress);
    }
    
    /**
     * Indexed indirect addressing is normally used in conjunction with a table of address held on zero page. 
     * The address of the table is taken from the instruction and the X register added to it (with zero page 
     * wrap around) to give the location of the least significant byte of the target address.
     * 
     * @param anAddress
     * @return
     */
    public static final int indirectX(int anAddress, int anX)
    {
    	int _address = (Memory.readByte(anAddress) + anX) & 0xFF;
    	
    	int _address2 = readWord(_address);
    	
    	System.out.printf("      arg: [%s]\n", Utils.toHexString(Memory.readByte(anAddress)));
    	System.out.printf("        x: [%s]\n", Utils.toHexString(anX));
    	System.out.printf("address 1: [%s]\n", Utils.toHexString(_address));
    	System.out.printf("address 2: [%s]\n", Utils.toHexString(_address2)); // Wrong for last LDA_indirect_X in test
    	                                                                      // Should be 0x0400
    	int _byte1 = Memory.readByte(_address);            // byte from 0xFF seems ok, should be 0x00
        int _byte2 = (Memory.readByte(_address + 1) << 8); //  <----- The value in _address + 1 (0x0100) is 0xFF, it should be 0x04.   Why is it not?
    	
        System.out.printf(">   byte1: [%s]\n", Utils.toHexString(_byte1)); // should be 0x00
        System.out.printf(">   byte2: [%s]\n", Utils.toHexString(_byte2)); // should be 0x0400
        
        System.out.printf("susp addr: [%s]\n",   Utils.toHexString(_address + 1));  // The suspect address
        
        System.out.printf("exp value: [%s]\n",   Utils.toHexString(Memory.readByte(0x0400)));    // Is 0x5D
    	System.out.printf("act value: [%s]\n\n", Utils.toHexString(Memory.readByte(_address2))); // Should be 0x5D  	
    	
		return _address2;
    }
    
//--------------------------------------
// Addressing utilities   
//--------------------------------------    
    
    private static final int readWord(int anAddress)
    {
        int _byte1 = Memory.readByte(anAddress);
        int _byte2 = (Memory.readByte(anAddress + 1) << 8);
        
        //System.out.printf("byte 1: %s\n", Utils.toHexString(_byte1));
        //System.out.printf("byte 2: %s\n", Utils.toHexString(_byte2));
        
        return _byte1 | _byte2;
    }
    
    private static final int readSignedByte(int anAddress)
    {
        int _byte = Memory.readByte(anAddress);

        if (_byte < 0x80)
        {
            return _byte;
        } 
        else
        {
            return _byte - 256;
        }
    }
}
