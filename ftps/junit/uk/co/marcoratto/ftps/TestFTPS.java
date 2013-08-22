package uk.co.marcoratto.ftps;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class TestFTPS extends TestBase {
	
	private final static String USER = "rattom";
	private final static String PASS = "rm";
	private final static String SERVER = "192.168.1.3";
	private final static String REMOTE_DIR = "/home/rattom/tmp";
	
	private String remotePath = null;
	
	private FTPS ftps = null;
				
	protected void setUp() {
		super.setUp();
		this.ftps = new FTPS(null);
		this.remotePath = USER + ":" + PASS + "@" + SERVER + ":" + REMOTE_DIR;
	}
	
	protected void tearDown() {
		System.out.println(this.getName() + ".tearDown()");
	}

	public static void main (String[] args) {
		TestRunner.run(suite());
	}
	
	public static Test suite() {
		return new TestSuite(TestFTPS.class);
	}	
			
	public void testBinaryTransfer() {
		System.out.println(this.getClass().getName() + ".testBinaryTransfer()");
		try {			 					
			boolean expected = true;
			this.ftps.setBinaryTransfer(expected);
			boolean actual = this.ftps.isBinaryTransfer();
			System.out.println(actual);
			System.out.println(expected);
			assertEquals(expected, actual);
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}
		
	public void testControlKeepAliveReplyTimeout() {
		System.out.println(this.getClass().getName() + ".testControlKeepAliveReplyTimeout()");
		try {			 					
			int expected = 123;
			this.ftps.setControlKeepAliveReplyTimeout(expected);
			int actual = this.ftps.getControlKeepAliveReplyTimeout();
			System.out.println(actual);
			System.out.println(expected);
			assertEquals(expected, actual);
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}

	public void testPort() {
		System.out.println(this.getClass().getName() + ".testPort()");
		try {			 					
			int expected = 21;
			this.ftps.setPort(expected);
			int actual = this.ftps.getPort();
			System.out.println(actual);
			System.out.println(expected);
			assertEquals(expected, actual);
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}

	public void testKeepAliveTimeout() {
		System.out.println(this.getClass().getName() + ".testKeepAliveTimeout()");
		try {			 					
			long expected = 12345;
			this.ftps.setKeepAliveTimeout(expected);
			long actual = this.ftps.getKeepAliveTimeout();
			System.out.println(actual);
			System.out.println(expected);
			assertEquals(expected, actual);
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}

	public void testImplicit() {
		System.out.println(this.getClass().getName() + ".testImplicit()");
		try {			 					
			boolean expected = true;
			this.ftps.setImplicit(expected);
			
			boolean actual = this.ftps.isImplicit();
			System.out.println(actual);
			System.out.println(expected);
			assertEquals(expected, actual);
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}

	public void testRecursive() {
		System.out.println(this.getClass().getName() + ".testRecursive()");
		try {			 					
			boolean expected = true;
			this.ftps.setRecursive(expected);
			
			boolean actual = this.ftps.isRecursive();
			System.out.println(actual);
			System.out.println(expected);
			assertEquals(expected, actual);
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}
}
