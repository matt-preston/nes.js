package org.perturbed.nesjs.core.instructions;

import java.io.IOException;

import junit.framework.Assert;

import org.perturbed.nesjs.core.ResourceROMLoader;
import org.perturbed.nesjs.core.client.BlarggTestROM;
import org.perturbed.nesjs.core.client.BlarggTestROM.TestLogger;
import org.perturbed.nesjs.core.client.ROM;
import org.perturbed.nesjs.core.client.Utils;

public abstract class BlarggTestCase
{
     public void runTestROM(String aROMName) throws IOException
     {
    	 ROM _rom = ResourceROMLoader.loadROMResource(this.getClass(), aROMName);
    	 
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
