package org.perturbed.nesjs.core.codegen;

import java.util.*;

import org.perturbed.nesjs.core.client.*;

public class GenerateOpcodeNames
{
    public static void main(String[] anArgs) throws Exception 
    {
        OpcodeDefinitionReader _r = new OpcodeDefinitionReader();
        
        List<OpcodeDefinition> _opcodes = _r.allOpcodeDefinitions();
        
        SortedSet<String> _mnemonics = new TreeSet<String>();
        
        for(int _index = 0; _index < _opcodes.size(); _index++)
        {
            OpcodeDefinition _next = _opcodes.get(_index);
            
            _mnemonics.add(_next.getMnemonic());
            
            if(_index < _opcodes.size() - 2)
            {
                OpcodeDefinition _following = _opcodes.get(_index + 1);
                
                if(_next.getMnemonic().equals(_following.getMnemonic()))
                {
                    System.out.printf("case %s:\n", Utils.toHexString(_next.getOpcode()));
                }
                else
                {
                    System.out.printf("case %s: return %s.name();\n", Utils.toHexString(_next.getOpcode()), _next.getMnemonic());    
                }
            }
            else
            {
                System.out.printf("case %s: return %s.name();\n", Utils.toHexString(_next.getOpcode()), _next.getMnemonic());
            }
        }
        
        _r.close();
        
        System.out.println("\n\n\n\n\n");
        
        for (String _mnemonic : _mnemonics)
        {
            System.out.println(_mnemonic + ",");
        }        
    }
}
