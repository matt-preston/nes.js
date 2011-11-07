package org.nesjs.codegen;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nesjs.core.Utils;

public class OpcodeDefinitionReader 
{
	// TEST
	public static void main(String[] args) throws Exception 
	{
		OpcodeDefinitionReader _r = new OpcodeDefinitionReader();
		
		OpcodeDefinition _o;
		
		while((_o = _r.next()) != null)
		{
			System.out.println(_o);
		}
	}
	
	
	
	private BufferedReader reader;
	
	private Pattern pattern = Pattern.compile("^(\\w{3})\\s+.*\\s+(\\w{2})$", Pattern.DOTALL);
	
	public OpcodeDefinitionReader() throws IOException
	{		
		reader = new BufferedReader(new InputStreamReader(new FileInputStream("6502.txt"), "UTF8"));
	}
	
	/**
	 * Will return the next opcode definition, or null if there are none left
	 */
    public OpcodeDefinition next() throws IOException
    {
        String _line;
        
        while((_line = reader.readLine()) != null)
        {
            _line = _line.trim();
            
            Matcher _matcher = pattern.matcher(_line);
            
            if(_matcher.matches())
            {
                String _mnemonic = _matcher.group(1);
                String _opcodeString = _matcher.group(2);
                
                int _opcode = Integer.parseInt(_opcodeString, 16);
                
                AddressingMode _addressingMode = getAddressingMode(_line);
                
                return new OpcodeDefinition(_opcode, _mnemonic, _addressingMode, true);
            }
        }
        
        return null;
    }
    
    private AddressingMode getAddressingMode(String aDefinitionString)
    {
    	if(aDefinitionString.contains("#"))
    	{
    		return AddressingMode.IMMEDIATE;
    	}    	
    	else if(aDefinitionString.contains("aaaa "))
    	{
    		return AddressingMode.ABSOLUTE;
    	}
    	else if(aDefinitionString.contains("aaaa,X"))
    	{
    		return AddressingMode.ABSOLUTE_X;
    	}
    	else if(aDefinitionString.contains("aaaa,Y"))
    	{
    		return AddressingMode.ABSOLUTE_Y;
    	}
    	else if(aDefinitionString.contains("aa "))
    	{
    		return AddressingMode.ZERO_PAGE;
    	}
    	else if(aDefinitionString.contains("aa,X"))
    	{
    		return AddressingMode.ZERO_PAGE_X;
    	}
    	else if(aDefinitionString.contains("aa,Y"))
    	{
    		return AddressingMode.ZERO_PAGE_Y;
    	}
    	else if(aDefinitionString.contains("aaaa)"))
    	{
    		return AddressingMode.INDIRECT;
    	}
    	else if(aDefinitionString.contains(",X)"))
    	{
    		return AddressingMode.INDIRECT_X;
    	}
    	else if(aDefinitionString.contains("),Y"))
    	{
    		return AddressingMode.INDIRECT_Y;
    	}
    	else if(aDefinitionString.contains(" A "))
    	{
    		return AddressingMode.ACCUMULATOR;
    	}
    	
    	// TODO, extend document to support identifying RELATIVE
    	
    	return AddressingMode.IMPLIED;
    }
}

enum AddressingMode
{
	IMPLIED,
	ACCUMULATOR,
	IMMEDIATE,
	ZERO_PAGE,
	ZERO_PAGE_X,
	ZERO_PAGE_Y,
	RELATIVE,
	ABSOLUTE,
	ABSOLUTE_X,
	ABSOLUTE_Y,
	INDIRECT,
	INDIRECT_X,
	INDIRECT_Y;
}

class OpcodeDefinition
{
	private int opcode;  
    private String mnemonic;
    private AddressingMode addressingMode;
    private boolean official;
    
    public OpcodeDefinition(int anOpcode, String aMnemonic, AddressingMode anAddressingMode, boolean isOfficial) 
    {
		opcode = anOpcode;
		mnemonic = aMnemonic;
		addressingMode = anAddressingMode;
		official = isOfficial;
	}

	public int getOpcode() 
    {
		return opcode;
	}
    
    public String getMnemonic() 
    {
		return mnemonic;
	}
    
	public AddressingMode getAddressingMode()
	{
	    return addressingMode;	
	}
	
	public boolean isOfficial() 
	{
		return official;
	}
	
	@Override
	public String toString() 
	{
		if(official)
		    return String.format("%s [%s] official:   %s", Utils.toHexString(getOpcode()), getMnemonic(), getAddressingMode().name());
		else
			return String.format("%s [%s] unofficial: %s", Utils.toHexString(getOpcode()), getMnemonic(), getAddressingMode().name());
	}
}