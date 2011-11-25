package org.nesjs.codegen;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nesjs.core.Utils;

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

public class OpcodeDefinitionReader 
{
	private BufferedReader reader;
	
	private Pattern pattern = Pattern.compile("^(\\w{3})\\*?\\s+.*\\s+(\\w{2})\\s*(\\d)?$", Pattern.DOTALL);
	
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
                String _cyclesString = _matcher.group(3);
                
                if(_cyclesString == null)
                {
                	_cyclesString = "0";
                }
                
                int _opcode = Integer.parseInt(_opcodeString, 16);
                int _cycles = Integer.parseInt(_cyclesString);
                
                AddressingMode _addressingMode = getAddressingMode(_line);
                
                boolean _official = !_line.contains("*");
                
                return new OpcodeDefinition(_opcode, _mnemonic, _addressingMode, _official, _cycles);
            }
        }
        
        return null;
    }
    
    public List<OpcodeDefinition> allOpcodeDefinitions() throws IOException
    {
        ArrayList<OpcodeDefinition> _results = new ArrayList<OpcodeDefinition>();
        
        OpcodeDefinition _next;
        
        while((_next = next()) != null)
        {
            _results.add(_next);
        }
        
        return _results;
    }
    
    public void close()
    {
        if(reader != null)
        {
            try
            {
                reader.close();
            }
            catch(IOException anExc)
            {                
            }
            
            reader = null;
        }
    }
    
    private AddressingMode getAddressingMode(String aDefinitionString)
    {
    	if(aDefinitionString.contains("#"))
    	{
    		return AddressingMode.IMMEDIATE;
    	} 
    	else if(aDefinitionString.contains("~"))
        {
            return AddressingMode.RELATIVE;
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
    	else if(aDefinitionString.contains(" A "))
    	{
    		return AddressingMode.ACCUMULATOR;
    	}
    	
    	return AddressingMode.IMPLIED;
    }
    
    public static void main(String[] anArgs) throws Exception 
	{
        OpcodeDefinitionReader _r = new OpcodeDefinitionReader();
	       
        List<OpcodeDefinition> _opcodes = _r.allOpcodeDefinitions();
	     
	    for (OpcodeDefinition _def : _opcodes) 
	    {
            System.out.println(_def);   		
        }
	}
}

class OpcodeDefinition
{
	private int opcode;  
    private String mnemonic;
    private AddressingMode addressingMode;
    private boolean official;
    private int cycles;
    
    public OpcodeDefinition(int anOpcode, String aMnemonic, AddressingMode anAddressingMode, boolean isOfficial, int aCycles) 
    {
		opcode = anOpcode;
		mnemonic = aMnemonic;
		addressingMode = anAddressingMode;
		official = isOfficial;
		cycles = aCycles;
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
	
	public int getCycles() 
	{
		return cycles;
	}
	
	@Override
	public String toString() 
	{
		if(official)
		    return String.format("%s [%s] official:   %s [%d]", Utils.toHexString(getOpcode()), getMnemonic(), getAddressingMode().name(), getCycles());
		else
			return String.format("%s [%s] unofficial: %s [%d]", Utils.toHexString(getOpcode()), getMnemonic(), getAddressingMode().name(), getCycles());
	}
}
