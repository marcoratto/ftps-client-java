package uk.co.marcoratto.ftps;

import java.util.Vector;

import junit.framework.TestCase;

public abstract class TestBase extends TestCase {
	
	protected Runme runme = null;
	protected Vector<String> params = null;

	// MUST be static for the Factory
	protected static Throwable actualThrowable = null;  
	protected static int actualEndTotalFiles;	
	protected static int actualStartTotalFiles;
	protected static int actualReturnCode;

	protected void setUp() {
		System.out.println(this.getName() + ".setUp()");	

		try {
			runme = new Runme();
			this.params = new Vector<String>();
			System.setProperty("ftps_config_file", "./test/res/junit.properties");
			
			actualReturnCode = -1;
			actualEndTotalFiles = -1;
			actualStartTotalFiles = -1;
			actualThrowable = null;

		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 			
	}
	
}
