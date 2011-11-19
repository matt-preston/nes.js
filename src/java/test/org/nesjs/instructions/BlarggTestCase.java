package org.nesjs.instructions;

import java.io.*;

import org.junit.*;
import org.nesjs.core.*;

public class BlarggTestCase
{
     public void runTestROM(String aROMName) throws Exception
     {
         InputStream _in = getClass().getResourceAsStream(aROMName);
         Memory _memory = ROM.memoryWithROMInputStream(_in);
         
         MOS6502 _6502 = new MOS6502(_memory);
         
         _6502.reset();

         do
         {
             _6502.step();
         }
         while(!isTestFinished(_memory));
         
         String _message = getNullTerminatedString(_memory, 0x6004);
         System.out.println(_message);
         
         Assert.assertEquals(0x00, _memory.readByte(0x6000));
         Assert.assertTrue(_message.contains("Passed"));
     }
 
//-----------------------------------
// Private methods
//-----------------------------------     
     
     private boolean isTestFinished(Memory aMemory)
     {
         int _6001 = aMemory.readByte(0x6001);
         int _6002 = aMemory.readByte(0x6002);
         int _6003 = aMemory.readByte(0x6003);
         
         boolean _testRunning = _6001 == 0xDE && _6002 == 0xB0 && _6003 == 0x61;  
         
         if(_testRunning)
         {
             int _6000 = aMemory.readByte(0x6000);
             
             if(_6000 == 0x80)
             {
                 // Test in progress
                 return false;
             }
             
             return true;
         }
         
         return false;
     }
     
     private String getNullTerminatedString(Memory aMemory, int aStartAddress)
     {
         StringBuilder _builder = new StringBuilder();
         
         int _byte;
         
         while((_byte = aMemory.readByte(aStartAddress++)) != 0x00)
         {
             _builder.append((char) _byte);
         }
         
         return _builder.toString();
     }
}
