package org.nesjs.codegen;

import org.nesjs.core.*;

public class GenerateOpcodeNames
{
    public static void main(String[] anArgs) throws Exception 
    {
        OpcodeDefinitionReader _r = new OpcodeDefinitionReader();
        OpcodeDefinition _o = null;
        
        while((_o = _r.next()) != null)
        {
            System.out.printf("case %s: return \"%s\";\n", Utils.toHexString(_o.getOpcode()), _o.getMnemonic());            
        }
        
        _r.close();        
    }
}
