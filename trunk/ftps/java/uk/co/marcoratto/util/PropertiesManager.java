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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

/**
 * La classe gestisce il file di Properties dell'infrastruttura <code>infrastruttura.properties</code>. &egrave; un'implementazione del Pattern Singleton, per cui il file &egrave; letto solo al momento della creazione dell'istanza della classe.
 * <BR>Mentre il nome del file di properties &egrave; fisso (<b>infrastruttura.properties</b>), il path &egrave; determinato come segue:
 * <BR>- in prima istanza si cerca la variabile di <i>JVM</i> con nome <b>infrastruttura_properties_path</b>;
 * <BR>- se la variabile non viene trovata, si cerca il path tramite una variabile di ambiente del <code>Context</code> sempre con nome <b>infrastruttura_properties_path</b>;
 * <BR>- se anche la variabile del <code>Context</code> non esiste, il path di default <b>/</b> (root).
 * @author Marco Ratto
 */
public class PropertiesManager {
	
	private static Logger logger = Logger.getLogger(PropertiesManager.class);
		
	private final static String USER_HOME = System.getProperty("user.home") + "/.ftps";
	
	private static PropertiesManager instance = null;

	/**
	   * Contenitore per tutte le properties contenute nel file.
	   */
	  private Properties prop = null;

	  // nome di default del file di properties
	  public static final String DEFAULT_PROPERTIES_FILE_NAME = "ftps_config_file.properties";

	 // nome della property contenente il nome del file di properties
	  private static final String FILENAME_PROPERTYNAME = "ftps_config_file";
	
  /**
   * Costruttore della classe. Acquisisce i parametri dal file di Properties.
   * @exception java.io.IOException*/
  protected PropertiesManager() throws PropertiesManagerException {
    this.prop = new Properties();
    readFileProperties();
  }

  /**
   * Costruttore della classe. Acquisisce i parametri dal file di Properties
   * identificato da propertiesPath
   * @param propertiesPath File
   * @throws IOException
   */
  protected PropertiesManager(File propertiesPath) throws PropertiesManagerException {
    super();
    readFileProperties(propertiesPath);
  }
  
  public void readFileProperties() throws PropertiesManagerException {
        try {
            //costruzione del nome del file di properties, necessario per leggere da file system.
            String filename = this.getPropertiesFilename();
              // ricerca del file tramite classloader
              logger.info("readFileProperties(" + filename + "): looking for file in classloader...");
              InputStream is = this.getClass().getResourceAsStream(filename);
              if (is != null) {  // OK, file trovato
            	logger.info("readFileProperties(" + filename + "): looking for file in classloader... OK");
        		this.readFileProperties(is);
              } else {
                // se non trovato si cerca nel file system
		       	  logger.info("readFileProperties(" + filename + "): looking for file in file system...");
		          is = new FileInputStream(new File(filename));
		          if (is != null) {
			          logger.info("readFileProperties(" + filename + "): looking for file in file system... OK");
			          this.readFileProperties(is);
		          }
              }
        } catch (FileNotFoundException e) {
          throw new PropertiesManagerException(e);
        }
    }

  /**
   * Acquisisce dal file di properties tutti i parametri, valorizzando l'attributo <code>prop</code>.
   * <BR>Il file di properties &egrave; passato in formato <code>String</code>;
   * <BR>il path pu&ograve; essere relativo al Classloader oppure assoluto sul file system.
   * <BR>Prima si cerca nel classloader e poi nel file system.
   * @param String filename
   * @exception java.io.IOException
   */
  public void readFileProperties(String filename) throws PropertiesManagerException {
    try {
        // ricerca del file tramite classloader
        InputStream is = this.getClass().getResourceAsStream(filename);
        // se non trovato si cerca nel file system
        if (is == null) {
            is = new FileInputStream(filename);    	
        }
		prop.load(is);
	} catch (IOException e) {
		throw new PropertiesManagerException(e);
	}
  }



  /**
   * Acquisisce dal file di properties tutti i parametri, valorizzando l'attributo <code>prop</code>.
   * <BR>Il file di properties &egrave; passato in formato <code>InputStream</code>.
   * @param InputStream is
   * @exception java.io.IOException
   */
  public void readFileProperties(InputStream is) throws PropertiesManagerException {
    logger.info("readFileProperties(InputStream): loading file...");
    try {
		prop.load(is);
	} catch (IOException e) {
		throw new PropertiesManagerException(e);
	}
	logger.info("readFileProperties(InputStream): loading file... OK");
  }

  /**
   * Acquisisce dal file di properties tutti i parametri, valorizzando l'attributo <code>prop</code>.
   * <BR>Il file di properties &egrave; passato in formato <code>File</code> ed il suo pathname deve essere assoluto partendo dalla root delle classi.
   * @exception java.io.IOException
   * @param File propFile
   */
  public void readFileProperties(File propFile) throws PropertiesManagerException {
    InputStream is = null;
	try {
		is = new FileInputStream(propFile);
	    prop.load(is);
	} catch (FileNotFoundException e) {
		throw new PropertiesManagerException(e);
	} catch (IOException e) {
		throw new PropertiesManagerException(e);
	}
  }

  /**
   * Acquisisce dal file di properties tutti i parametri, valorizzando l'attributo <code>prop</code>.
   * <BR>Il file di properties &egrave; passato in formato <code>URL</code>.
   * @exception java.io.IOException
   * @param URL propUrl
   */
  public void readFileProperties(URL url) throws PropertiesManagerException {
    logger.info("readFileProperties(URL): opening connection...");
    URLConnection connection = null;
    InputStream inputStream = null;
	try {
		connection = (URLConnection) url.openConnection();
	    if (connection != null) {
	    	logger.info("readFileProperties(URL): opening connection... OK");	    	
	    } else {
	    	logger.info("readFileProperties(URL): connection null");	    	
	    }
	    logger.info("readFileProperties(URL): getting inputStream...");
	    inputStream = connection.getInputStream();
	    logger.info("readFileProperties(URL): getting inputStream... OK");
	    this.readFileProperties(inputStream); // prop.load(inputStream);
	} catch (IOException e) {
		throw new PropertiesManagerException(e);
	} finally {
	    try {
			inputStream.close();
		} catch (IOException e) {
			logger.warn(e);
		}		
	}
  }

  /**
   * Il metodo ritorna la stringa contenente il <i>path</i> ed il nome del file di <code>properties</code>.
   * Il nome del file di properties &egrave; determinato come segue:
   * <BR>- in prima istanza si cerca il path nella variabile di <i>JVM</i> con nome <b>infrastruttura_properties_path</b>;
   * <BR>- se la variabile non viene trovata, si cerca il path tramite una variabile di ambiente del <code>Context</code> sempre con nome <b>infrastruttura_properties_path</b>;
   * <BR>- se anche la variabile del <code>Context</code> non esiste, il path di default &egrave; <b>/</b> (root del classloader).
   * <BR>- poi si cerca il filename nella variabile di <i>JVM</i> con nome <b>infrastruttura_properties_filename</b>;
   * <BR>- se la variabile non viene trovata, si cerca il filename tramite una variabile di ambiente del <code>Context</code> sempre con nome <b>infrastruttura_properties_filename</b>;
   * <BR>- se anche la variabile del <code>Context</code> non esiste, il filename di default &egrave; <b>infrastruttura.properties</b>.
   * @return String filename
   */
  protected String getPropertiesFilename() {
    String filename = null;
    // ricerca del filename...
    try {
	  // ricerca del filepath come parametro della JVM (-d ...)
	  filename = System.getProperty(FILENAME_PROPERTYNAME);
	  logger.info("Try to read from -D" + PropertiesManager.FILENAME_PROPERTYNAME + "=" + filename);
	  if (filename != null) {
		  logger.info("OK");
	  } else {
		File f = new File(USER_HOME, DEFAULT_PROPERTIES_FILE_NAME);
		logger.info("Try to read from file " + f.getAbsolutePath());
		if (f.exists()) {
			filename = f.getAbsolutePath();
			logger.info("OK");
		}
	  }			
		logger.info("filename=" + filename);
    } catch (Throwable e) {
      // in caso di errore di assegna il default
      filename = DEFAULT_PROPERTIES_FILE_NAME;
      logger.error(e.getMessage(), e);
    }
    return filename;
  }

  public void reset() {
	  instance = null;
  }
  
  public static PropertiesManager getInstance() throws PropertiesManagerException {
    if (instance == null) {
      synchronized(PropertiesManager.class) {
        if (instance == null) {
          instance = new PropertiesManager();
        }
      }
    }
    return instance;
  }

  /**
   * Il metodo restituisce il valore della property identificata dal parametro <code>key</code>. Se la property non esiste, il metodo ritorna <code>null</code>.
   * @param key String
   * @return String
   */
  public String getProperty(String key) {
    String value = prop.getProperty(key);
    logger.info(key + "=" + value);
    return value;
  }

  /**
   * Il metodo restituisce il valore della property identificata dal parametro <code>key</code>. Se la property non esiste, il metodo ritorna il parametro <code>defaultValue</code>.
   * @param key String
   * @param defaultValue String
   * @return String
   */
  public String getProperty(String key, String defaultValue) {
	String value = prop.getProperty(key, defaultValue);
	logger.info(key + "=" + value);
	return value;
  }
  
  public int getProperty(String key, int defaultValue) {
	    return Integer.parseInt(this.getProperty(key, String.valueOf(defaultValue)), 10);
  }
  
  public boolean getProperty(String key, boolean defaultValue) {
	    return this.getProperty(key, String.valueOf(defaultValue)).equalsIgnoreCase("true");
}  
  
  protected String getDefaultFileName() {
    return DEFAULT_PROPERTIES_FILE_NAME;
  }

  protected String getFilenamePropertyName() {
    return FILENAME_PROPERTYNAME;
  }

}
