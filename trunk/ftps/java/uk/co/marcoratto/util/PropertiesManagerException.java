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
package uk.co.marcoratto.util;

import org.apache.log4j.Logger;

/**
 * Costituisce la superclasse di tutte le Exception dell'Infrastruttura.
 * <BR>Non ne &egrave; previsto l'utilizzo diretto (istanziazione e/o lancio), ma pu&ograve; essere utilizzata nella gestione del <i>trapping</i>, in modo da poter trattare facilmente tutte le Exception di Infrastruttura.
 * @author Marco Ratto
 */
public class PropertiesManagerException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3348677367947352434L;
	
	private static Logger logger = Logger.getLogger(PropertiesManagerException.class);
	
  public PropertiesManagerException(String s) {
    super(s);
    logger.error(s);
  }

  public PropertiesManagerException(String s, Exception e) {
    super(s, e);
    logger.error(s, e);
  }

  public PropertiesManagerException(Exception e) {
    super(e);
    logger.error(e.getMessage(), e);
  }

  public PropertiesManagerException() {
    super();
    logger.error("");
  }
}
