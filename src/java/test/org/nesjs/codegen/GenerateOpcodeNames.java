package org.nesjs.codegen;

import java.util.*;

import org.nesjs.core.*;

public class GenerateOpcodeNames
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
                
                if(_next.getMnemonic().equals(_following.getMnemonic()))
                {
                    System.out.printf("case %s:\n", Utils.toHexString(_next.getOpcode()));
                }
                else
                {
                    System.out.printf("case %s: return \"%s\";\n", Utils.toHexString(_next.getOpcode()), _next.getMnemonic());    
                }
            }
            else
            {
                System.out.printf("case %s: return \"%s\";\n", Utils.toHexString(_next.getOpcode()), _next.getMnemonic());
            }
        }
        
        _r.close();        
    }
}
