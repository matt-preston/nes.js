package org.nesjs.codegen;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class OpcodeMethods
{
    static int[] ADDRESSING_MODE = new int[]
    {
        /*0x00*/ 0,1,0,1,2,2,2,2,0,10,0,0,3,3,3,3,
        /*0x10*/ 0,4,0,4,5,5,5,5,0,6,0,6,7,7,7,7,
        /*0x20*/ 0,1,0,0,2,2,2,0,0,10,0,0,3,3,3,0,
        /*0x30*/ 0,4,0,0,5,5,5,0,0,6,0,0,7,7,7,0,
        /*0x40*/ 0,1,0,0,2,2,2,0,0,10,0,0,3,3,3,0,
        /*0x50*/ 0,4,0,0,5,5,5,0,0,6,0,0,7,7,7,0,
        /*0x60*/ 0,1,0,0,2,2,2,0,0,10,0,0,9,3,3,0,
        /*0x70*/ 0,4,0,0,5,5,5,0,0,6,0,0,7,7,7,0,
        /*0x80*/ 10,1,0,1,2,2,2,2,0,0,0,0,3,3,3,3,
        /*0x90*/ 0,4,0,0,5,5,8,8,0,6,0,0,0,7,0,0,
        /*0xA0*/ 10,1,10,1,2,2,2,2,0,10,0,0,3,3,3,3,
        /*0xB0*/ 0,4,0,4,5,5,8,8,0,6,0,0,7,7,6,6,
        /*0xC0*/ 10,1,0,1,2,2,2,2,0,10,0,0,3,3,3,3,
        /*0xD0*/ 0,4,0,4,5,5,5,5,0,6,0,6,7,7,7,7,
        /*0xE0*/ 10,1,0,1,2,2,2,2,0,10,0,10,3,3,3,3,
        /*0xF0*/ 0,4,0,4,5,5,5,5,0,6,0,6,7,7,7,7
    };
            
    private static class Opcode
    {
        public String opcode;
        public String function;
        
        public Opcode(String aMnemonic, String anOpcode, String aFunction)
        {
            opcode = anOpcode;
            function = aFunction;
        }        
    }
            
    public static void main(String[] args) throws Exception
    {
        List<Opcode> _opcodes = getOpcodes("6502.txt");
        
        for (Opcode _opcode : _opcodes)
        {
            System.out.printf("                    case 0x%s: %s(); break;\n", _opcode.opcode, _opcode.function);
        }
        
        System.out.println("\n\n");
        
        for (Opcode _opcode : _opcodes)
        {
            System.out.printf("    private void %s()\n", _opcode.function);
            System.out.println("    {");
            System.out.printf("        throw new RuntimeException(\"opcode not implemented [%s]\");\n", _opcode.function);
            System.out.println("    }");
            System.out.println();            
        }
        
        System.out.println("\n\n");
        
        for (Opcode _opcode : _opcodes)
        {
            System.out.printf("                    case 0x%s: return \"%s\";\n", _opcode.opcode, _opcode.function);
        }
    }
            
    private static List<Opcode> getOpcodes(String aFile) throws Exception
    {
        BufferedReader _r = new BufferedReader(new InputStreamReader(new FileInputStream(aFile), "UTF8"));
        
        String _line = null;
        
        Pattern _p = Pattern.compile("^(\\w{3})\\s+.*\\s+(\\w{2})$", Pattern.DOTALL);
        
        List<Opcode> _opcodes = new ArrayList<Opcode>();
        
        while((_line = _r.readLine()) != null)
        {
            _line = _line.trim();
            
            Matcher _matcher = _p.matcher(_line);
            
            if(_matcher.matches())
            {
                String _mnemonic = _matcher.group(1);
                String _opcode   = _matcher.group(2);
                
                int _addressingMode = ADDRESSING_MODE[Integer.parseInt(_opcode, 16)]; 
                
                String _functionSuffix = "";
                
                switch(_addressingMode)
                {
                    case 1:   _functionSuffix = "_indirect_X";   break;
                    case 2:   _functionSuffix = "_zero_page";    break;
                    case 3:   _functionSuffix = "_absolute";     break;
                    case 4:   _functionSuffix = "_indirect_Y";   break;
                    case 5:   _functionSuffix = "_zero_page_X";  break;
                    case 6:   _functionSuffix = "_absolute_Y";   break;
                    case 7:   _functionSuffix = "_absolute_X";   break;
                    case 8:   _functionSuffix = "_zero_page_Y";  break;
                    case 9:   _functionSuffix = "_indirect";     break;
                    case 10:  _functionSuffix = "_immediate";     break;
                }
                
                _opcodes.add(new Opcode(_mnemonic, _opcode, "opcode_" + _mnemonic + _functionSuffix));
            }
        }
        
        return _opcodes;
    }
}
