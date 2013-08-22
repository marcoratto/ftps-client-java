package uk.co.marcoratto.file.maketree;

import org.apache.log4j.Logger;

public class MakeTreeException extends Exception {

	private static Logger logger = Logger.getLogger(MakeTreeException.class);
	
	private static final long serialVersionUID = -8963227593079453L;

	public MakeTreeException(Exception e) {
      super(e);
      logger.error(e);
    }
	
	public MakeTreeException(Throwable t) {
	      super(t);
	      logger.error(t);
    }

   public MakeTreeException(String s) {
      super(s);
      logger.error(s);
   }
}