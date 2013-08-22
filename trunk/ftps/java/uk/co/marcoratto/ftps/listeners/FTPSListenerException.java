/*
 * Copyright (C) 2011 Marco Ratto
 *
 * This file is part of the project scp-java-client.
 *
 * scp-java-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 *
 * scp-java-client is free software; you can redistribute it and/or modify
 * it under the terms of the the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package uk.co.marcoratto.ftps.listeners;

import org.apache.log4j.Logger;

public class FTPSListenerException extends Exception {

	private static Logger logger = Logger.getLogger(FTPSListenerException.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -8081682431340732511L;

	public FTPSListenerException(String s) {
		super(s);
	}

	public FTPSListenerException(String s, Exception e) {
		super(s, e);
		logger.error(s, e);
	}

	public FTPSListenerException(Exception e) {
		super(e);
		logger.error(e.getMessage(), e);
	}
	
    public static FTPSListenerException exactlyOne(String[] attrs) {
        return exactlyOne(attrs, null);
    }
	
    public static FTPSListenerException exactlyOne(String[] attrs, String alt) {
        StringBuffer buf = new StringBuffer("Exactly one of ").append(
                '[').append(attrs[0]);
        for (int i = 1; i < attrs.length; i++) {
            buf.append('|').append(attrs[i]);
        }
        buf.append(']');
        if (alt != null) {
            buf.append(" or ").append(alt);
        }
        return new FTPSListenerException(buf.append(" is required.").toString());
    }

}
