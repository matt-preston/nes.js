package org.perturbed.nesjs.core.blargg;

import java.io.IOException;

import junit.framework.Assert;

import org.perturbed.nesjs.core.ResourceROMLoader;
import org.perturbed.nesjs.core.blargg.BlarggTestROM.TestLogger;
import org.perturbed.nesjs.core.client.ROM;
import org.perturbed.nesjs.core.client.Utils;

public abstract class BlarggTestROMTest
{
     public String getROMRootPath()
     {
         return "";
     }

     public void runTestROM(String aROMName) throws IOException
     {
         String _path = getROMRootPath() + aROMName;

    	 ROM _rom = ResourceROMLoader.loadROMResource(this.getClass(), _path);
    	 
    	 BlarggTestROM _romWrapper = new BlarggTestROM(_rom);
    	 
    	 _romWrapper.runTestToCompletion(new TestLogger() 
    	 {		
    		@Override
 			public void println(String aString) 
    		{
 				System.out.println(aString);
 			}
    		 
    		 @Override
			public void testFailedWithError(String aMessage, int aStatus) 
			{
				System.out.println(aMessage);

				Assert.fail("Test failure, invalid status code [" + Utils.toHexString(aStatus) + "]");
			}
			
			@Override
			public void testCompletedSuccessfully(String aString) 
			{
				System.out.println(aString);
				
				Assert.assertTrue(aString.contains("Passed"));
			}			
		});
     }
 }
