package uk.co.marcoratto.ftps;

import org.apache.log4j.Logger;

public class FTPSException extends Exception {

	private static Logger logger = Logger.getLogger(FTPSException.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -8081682431340732511L;

	public FTPSException(String s) {
		super(s);
		logger.error(s);		
	}

	public FTPSException(Throwable t) {
		super(t);
		logger.error(t.getMessage(), t);
	}

	public FTPSException(String s, Exception e) {
		super(s, e);
		logger.error(s, e);
	}

	public FTPSException(Exception e) {
		super(e);
		logger.error(e.getMessage(), e);
	}
	
}
