package org.nesjs.codegen;

import org.nesjs.core.*;

public class GenerateDispatch
{
    public static void main(String[] anArgs) throws Exception 
    {
        OpcodeDefinitionReader _r = new OpcodeDefinitionReader();
        OpcodeDefinition _o = null;
        
        while((_o = _r.next()) != null)
        {
            System.out.printf("case %s: %s; break;\n", Utils.toHexString(_o.getOpcode()), getMethodName(_o));            
        }
        
        _r.close();
        
        System.out.println("\n\n\n===========================================\n\n\n");
        
        _r = new OpcodeDefinitionReader();
        _o = null;
        
        while((_o = _r.next()) != null)
        {
            System.out.printf("private final void %s\n", getMethodName(_o));
            System.out.printf("{\n");
            System.out.printf("    throw new RuntimeException(\"opcode not implemented [%s %s]);\n", _o.getMnemonic(), _o.getAddressingMode());
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
        String _result = "_" + anAddressingMode.name().toLowerCase(); 
        
        _result = _result.replaceAll("x", "X");
        _result = _result.replaceAll("y", "Y");
        
        return  _result;        
    }
}
