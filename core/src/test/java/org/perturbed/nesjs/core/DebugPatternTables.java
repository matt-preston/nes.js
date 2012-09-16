package org.perturbed.nesjs.core;

import java.io.*;

import org.perturbed.nesjs.core.client.*;
import org.perturbed.nesjs.core.nestest.*;

public class DebugPatternTables
{
    public static void main(String[] anArgs) throws Exception
    {
        DebugPatternTables _d = new DebugPatternTables();
        
        _d.debug(NESTestTest.class, "/nestest/nestest.nes");
    }
    
    public void debug(Class<?> aClass, String aROMResource) throws IOException
    {
        ROM _rom = ResourceROMLoader.loadROMResource(aClass, aROMResource);
        
        PPUMemory _memory = _rom.getPPUMemory();
        
        for(int _tile = 0; _tile < 256; _tile++)
        {
            System.out.println("Tile: " + (_tile + 1));
            
            for (int _index = 0; _index < 16; _index++)
            {
                int _chrOffset = (_tile * 16) + _index;
                
                String _binary = Utils.toBinaryString(_memory.chrMem[_chrOffset]).replace('0', ' ');
                
                System.out.println(Utils.toHexString(_chrOffset) + ":  " + _binary);
                
                if(_index == 7)
                {
                    System.out.println();
                }
            }
            
            System.out.println();
        }
    }    
}
