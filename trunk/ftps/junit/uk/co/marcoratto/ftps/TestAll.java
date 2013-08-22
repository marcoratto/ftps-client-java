package uk.co.marcoratto.ftps;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestAll {

	public static void main (String[] args) {
		junit.textui.TestRunner.run (suite());
	}

	public static Test suite ( ) {
		TestSuite suite = new TestSuite("JUnit Test All");		
		suite.addTest(TestFTPS.suite());
		suite.addTest(TestRunme.suite());		
	    return suite;
	}
}