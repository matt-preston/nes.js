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
                    printCaseStatement(_next);    
                }
            }
            else
            {
                printCaseStatement(_next);
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
            System.out.printf("    throw new RuntimeException(\"opcode not implemented [%s]\");\n", _next.getMnemonic());
            System.out.printf("}\n");
            System.out.println("");
        }
        
        _r.close();
    }    
    
    private static void printCaseStatement(OpcodeDefinition anOpcode)
    {
        String _comment = "";
        
        if(!anOpcode.isOfficial())
        {
            _comment = " // Unofficial";
        }
        
        System.out.printf("case %s: %s; addCycles(%d); break;%s\n", Utils.toHexString(anOpcode.getOpcode()), getCallMethodName(anOpcode), anOpcode.getCycles(), _comment);        
    }
    
    private static String getCallMethodName(OpcodeDefinition anOpcode)
    {
        AddressingMode _mode = anOpcode.getAddressingMode();
        
        if(isMethodRequiresAddressParameter(_mode))
        {
            OpcodeType _type = OpcodeType.getOpcodeType(anOpcode.getMnemonic());
            
            return String.format("opcode_%s(%s())", anOpcode.getMnemonic(), getAddressingMethod(anOpcode.getAddressingMode(), _type));
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
    
    
    private static String getAddressingMethod(AddressingMode anAddressingMode, OpcodeType anOpcodeType)
    {
        if(anAddressingMode == AddressingMode.INDIRECT)
        {
            return "indirect"; // JMP is the only opcode to support this mode 
        }
        
        
        String _suffix = "";
        
        switch(anOpcodeType)
        {
           case READ:              _suffix = "_R";   break;
           case WRITE:             _suffix = "_W";   break;
           case READ_MODIFY_WRITE: _suffix = "_RMW"; break;
           case DEFAULT:           _suffix = "_GAAAAAH"; break;
        }
        
        String _prefix = "";
        
        switch(anAddressingMode)
        {
            case IMMEDIATE:   _prefix = "immediate"; break;
            case ZERO_PAGE:   _prefix = "zeroPage";  break;
            case ZERO_PAGE_X: _prefix = "zeroPageX"; break;
            case ZERO_PAGE_Y: _prefix = "zeroPageY"; break;
            case RELATIVE:    _prefix = "relative";  break;
            case ABSOLUTE:    _prefix = "absolute";  break;
            case ABSOLUTE_X:  _prefix = "absoluteX"; break;
            case ABSOLUTE_Y:  _prefix = "absoluteY"; break;
            case INDIRECT_X:  _prefix = "indirectX"; break;
            case INDIRECT_Y:  _prefix = "indirectY"; break;
        }
        
        return _prefix + _suffix;
    }    
}
