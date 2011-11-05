package org.nesjs.core;

/**
 * TODO, sort out the semantics.  Some methods return the address, others, like absolute, 
 *       return the value at the address.  All should return an address - not the value.
 *       
 * @author Matt
 */
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
    	
        return readWordZeroPageWrap(_address);
    }
    
//--------------------------------------
// Addressing utilities   
//--------------------------------------    
    
    private static final int readWord(int anAddress)
    {
        return readWord(anAddress, anAddress + 1);
    }
    
    private static final int readWordZeroPageWrap(int anAddress)
    {
        int _secondAddress = (anAddress + 1) & 0xFF;
        
        return readWord(anAddress, _secondAddress);
    }
    
    private static final int readWord(int anAddress, int aSecondAddress)
    {
        int _byte1 = Memory.readByte(anAddress);
        int _byte2 = (Memory.readByte(aSecondAddress) << 8);
        
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
