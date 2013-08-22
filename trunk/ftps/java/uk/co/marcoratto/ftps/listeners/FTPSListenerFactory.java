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

import uk.co.marcoratto.util.PropertiesManager;
import uk.co.marcoratto.util.PropertiesManagerException;

public class FTPSListenerFactory {

	private static Logger logger = Logger.getLogger(FTPSListenerFactory.class);
	
    private static FTPSListenerFactory instance = null;

    private FTPSListenerFactory() {
      logger.debug("ListenerFactory.ListenerFactory();");
    }

    public static FTPSListenerFactory getInstance() {
      if (instance == null) {
        instance = new FTPSListenerFactory();
      } 
      return instance;
    }
    
    public void reset() {
    	instance = null;
    }
    
    public FTPSListener getListener() throws FTPSListenerFactoryException {
    	FTPSListener listener = null;
		try {
			String factory = PropertiesManager.getInstance().getProperty("ftpsListener", null);
			if (factory != null) {
		    	logger.info("factoryString=" + factory);
		    	listener = (FTPSListener) Class.forName(factory).newInstance();
			} else {
				throw new FTPSListenerFactoryException("Why 'ftpsListener' is null ?");
			}
		} catch (PropertiesManagerException e) {
			throw new FTPSListenerFactoryException(e);
		} catch (ClassNotFoundException e) {
			throw new FTPSListenerFactoryException(e);
		} catch (InstantiationException e) {
			throw new FTPSListenerFactoryException(e);
		} catch (IllegalAccessException e) {
			throw new FTPSListenerFactoryException(e);
		}    
    	return listener;
    }

}