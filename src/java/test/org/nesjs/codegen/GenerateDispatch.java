package org.nesjs.codegen;

import java.util.*;

import org.nesjs.core.*;

public class GenerateDispatch
{
    public static void main(String[] anArgs) throws Exception 
    {
        OpcodeDefinitionReader _r = new OpcodeDefinitionReader();
        
        List<OpcodeDefinition> _opcodes = _r.allOpcodeDefinitions();
        
        for(int _index = 0; _index < _opcodes.size(); _index++)
        {
            OpcodeDefinition _next = _opcodes.get(_index);
            
            if(_index < _opcodes.size() - 2)
            {
                OpcodeDefinition _following = _opcodes.get(_index + 1);
                
                if(_next.getMnemonic().equals(_following.getMnemonic()) && _next.getAddressingMode().equals(_following.getAddressingMode()))
                {
                    System.out.printf("case %s:\n", Utils.toHexString(_next.getOpcode()));
                }
                else
                {
                    System.out.printf("case %s: %s; break;\n", Utils.toHexString(_next.getOpcode()), getCallMethodName(_next));    
                }
            }
            else
            {
                System.out.printf("case %s: %s; break;\n", Utils.toHexString(_next.getOpcode()), getCallMethodName(_next));
            }
        }
        
        _r.close();
        
        System.out.println("\n\n\n===========================================\n\n\n");
        
        _r = new OpcodeDefinitionReader();
        OpcodeDefinition _next = null;
        
        while((_next = _r.next()) != null)
        {
            System.out.printf("private final void %s\n", getDefinitionMethodName(_next));
            System.out.printf("{\n");
            System.out.printf("    throw new RuntimeException(\"opcode not implemented [%s]);\n", _next.getMnemonic());
            System.out.printf("}\n");
            System.out.println("");
        }
        
        _r.close();
    }    
    
    private static String getCallMethodName(OpcodeDefinition anOpcode)
    {
        AddressingMode _mode = anOpcode.getAddressingMode();
        
        if(isMethodRequiresAddressParameter(_mode))
        {
            return String.format("opcode_%s(%s())", anOpcode.getMnemonic(), getAddressingMethod(anOpcode.getAddressingMode()));
        }
        else
        {
            // no address to read
            return String.format("opcode_%s_%s()", anOpcode.getMnemonic(), anOpcode.getAddressingMode().name().toLowerCase());
        }
    }
    
    private static String getDefinitionMethodName(OpcodeDefinition anOpcode)
    {
        AddressingMode _mode = anOpcode.getAddressingMode();
        
        if(isMethodRequiresAddressParameter(_mode))
        {
            return String.format("opcode_%s(int anAddress)", anOpcode.getMnemonic());
        }
        else
        {
            // no address to read
            return String.format("opcode_%s_%s()", anOpcode.getMnemonic(), anOpcode.getAddressingMode().name().toLowerCase());            
        }
    }
    
    private static boolean isMethodRequiresAddressParameter(AddressingMode anAddressingMode)
    {
        EnumSet<AddressingMode> _noParams = EnumSet.of(AddressingMode.IMPLIED, AddressingMode.ACCUMULATOR, AddressingMode.RELATIVE);
        
        return !_noParams.contains(anAddressingMode);
    }
    
    
    private static String getAddressingMethod(AddressingMode anAddressingMode)
    {
        switch(anAddressingMode)
        {
            case IMMEDIATE:   return "immediate";
            case ZERO_PAGE:   return "zeroPage";
            case ZERO_PAGE_X: return "zeroPageX";
            case ZERO_PAGE_Y: return "zeroPageY";
            case RELATIVE:    return "relative";
            case ABSOLUTE:    return "absolute";
            case ABSOLUTE_X:  return "absoluteX";
            case ABSOLUTE_Y:  return "absoluteY";
            case INDIRECT:    return "indirect";
            case INDIRECT_X:  return "indirectX";
            case INDIRECT_Y:  return "indirectY";
        }
        
        throw new RuntimeException("Unhandled mode: " + anAddressingMode);
    }    
}
