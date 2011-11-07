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
                    System.out.printf("case %s: %s; break;\n", Utils.toHexString(_next.getOpcode()), getMethodName(_next));    
                }
            }
            else
            {
                System.out.printf("case %s: %s; break;\n", Utils.toHexString(_next.getOpcode()), getMethodName(_next));
            }
        }
        
        _r.close();
        
        System.out.println("\n\n\n===========================================\n\n\n");
        
        _r = new OpcodeDefinitionReader();
        OpcodeDefinition _next = null;
        
        while((_next = _r.next()) != null)
        {
            System.out.printf("private final void %s\n", getMethodName(_next));
            System.out.printf("{\n");
            System.out.printf("    throw new RuntimeException(\"opcode not implemented [%s %s]);\n", _next.getMnemonic(), _next.getAddressingMode());
            System.out.printf("}\n");
            System.out.println("");
        }
        
        _r.close();
    }
    
    private static String getMethodName(OpcodeDefinition anOpcode)
    {
        return String.format("opcode_%s%s()", anOpcode.getMnemonic(), getMethodSuffix(anOpcode.getAddressingMode()));
    }
    
    private static String getMethodSuffix(AddressingMode anAddressingMode)
    {
        return "_" + anAddressingMode.name().toLowerCase();
    }
}
