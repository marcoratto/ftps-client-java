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
package uk.co.marcoratto.ftps;

import java.io.File;

import org.apache.log4j.Logger;

import uk.co.marcoratto.ftps.listeners.FTPSListener;
import uk.co.marcoratto.ftps.listeners.FTPSListenerException;
import uk.co.marcoratto.ftps.listeners.FTPSListenerFactory;
import uk.co.marcoratto.ftps.listeners.FTPSListenerFactoryException;
import uk.co.marcoratto.util.Utility;
import uk.co.marcoratto.util.UtilityException;

public class Runme {

	private static Logger logger = Logger.getLogger(Runme.class);
	private FTPSListener listener = null;
	private static int retCode = -1;

	public Runme() {
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println(help);
			retCode = 3;
		} else {
			Runme runme = new Runme();
			runme.runme(args);
		}
		System.exit(retCode);
	}

	public void runme(String[] args) {
		try {
			listener = FTPSListenerFactory.getInstance().getListener();
		} catch (FTPSListenerFactoryException e) {
			logger.error(e.getMessage(), e);
		}

		try {
			logger.info("Call listener onStart...");
			listener.onStartFTPS();
		} catch (FTPSListenerException le) {
			logger.error(le.getMessage(), le);
		}			
		try {
			this.execute(args);
		} catch (Throwable t) {
			logger.info("Call listener onError...");
			try {
				listener.onErrorFTPS(t);
			} catch (FTPSListenerException le) {
				logger.error(le.getMessage(), le);
			}			
			retCode = 2;
		}
		logger.info("retCode=" + retCode);
		try {
			logger.info("Call listener onEnd...");
			listener.onEndFTPS(retCode);
		} catch (FTPSListenerException le) {
			logger.error(le.getMessage(), le);
		}			
	}

	public void execute(String[] args) throws FTPSException {
		for (int j = 0; j < args.length; j++) {
			logger.debug("args[" + j + "]=" + args[j]);
			if (args[j].equalsIgnoreCase("-h") == true) {
				System.err.println(help);
				retCode = 4;
				return;
			} else if (args[j].equalsIgnoreCase("-source") == true) {
				if (++j < args.length) {
					this.source = args[j];
				} else {
					throw new FTPSException("Too many args!");
				}
			} else if (args[j].equalsIgnoreCase("-retry") == true) {
				if (++j < args.length) {
					try {
						this.retry = Utility.stringToInt(args[j]);
					} catch (UtilityException e) {
						throw new FTPSException(e);
					}
				} else {
					throw new FTPSException("Too many args!");
				}
				this.delay = 60;
			} else if (args[j].equalsIgnoreCase("-k") == true) {
				if (++j < args.length) {
					try {
						this.keepAliveTimeout = Utility.stringToInt(args[j]);
					} catch (UtilityException e) {
						throw new FTPSException(e);
					}
				} else {
					throw new FTPSException("Too many args!");
				}
			} else if (args[j].equalsIgnoreCase("-w") == true) {
				if (++j < args.length) {
					try {
						this.controlKeepAliveReplyTimeout = Utility.stringToInt(args[j]);
					} catch (UtilityException e) {
						throw new FTPSException(e);
					}
				} else {
					throw new FTPSException("Too many args!");
				}
			} else if (args[j].equalsIgnoreCase("-port") == true) {
				if (++j < args.length) {
					try {
						this.port = Utility.stringToInt(args[j]);
					} catch (UtilityException e) {
						throw new FTPSException(e);
					}
				} else {
					throw new FTPSException("Too many args!");
				}
			} else if (args[j].equalsIgnoreCase("-delay") == true) {
				if (++j < args.length) {
					try {
						this.delay = Utility.stringToInt(args[j]);
					} catch (UtilityException e) {
						throw new FTPSException(e);
					}
				} else {
					throw new FTPSException("Too many args!");
				}
			} else if (args[j].equalsIgnoreCase("-target") == true) {
				if (++j < args.length) {
					this.target = args[j];
				} else {
					throw new FTPSException("Too many args!");
				}
			} else if (args[j].equalsIgnoreCase("-o") == true) {
				this.overwrite = true;
			} else if (args[j].equalsIgnoreCase("-d") == true) {
				this.delete = true;
			} else if (args[j].equalsIgnoreCase("-v") == true) {
				this.verbose = true;
			} else if (args[j].equalsIgnoreCase("-ask") == true) {
				this.askPassword = true;
			} else if (args[j].equalsIgnoreCase("-b") == true) {
				this.binaryTransfer = true;
			} else if (args[j].equalsIgnoreCase("-e") == true) {
				this.useEpsvWithIPv4 = true;
			} else if (args[j].equalsIgnoreCase("-a") == true) {
				this.passive = false;
			} else if (args[j].equalsIgnoreCase("-p") == true) {
				if (++j < args.length) {
					this.protocol = args[j];
				} else {
					throw new FTPSException("Too many args!");
				}
			} else if (args[j].equalsIgnoreCase("-trust") == true) {
				if (++j < args.length) {
					this.trust = args[j];
				} else {
					throw new FTPSException("Too many args!");
				}
			} else if (args[j].equalsIgnoreCase("-keyStore") == true) {
				if (++j < args.length) {
					this.keyStore = new File(args[j]);
					this.keyStorePassword = "";
				} else {
					throw new FTPSException("Too many args!");
				}
			} else if (args[j].equalsIgnoreCase("-keyStorePassword") == true) {
				if (++j < args.length) {
					this.keyStorePassword = args[j];
				} else {
					throw new FTPSException("Too many args!");
				}
			} else if (args[j].equalsIgnoreCase("-privateKeyAlias") == true) {
				if (++j < args.length) {
					this.privateKeyAlias = args[j];
				} else {
					throw new FTPSException("Too many args!");
				}	
			} else if (args[j].equalsIgnoreCase("-keyPassword") == true) {
				if (++j < args.length) {
					this.keyPassword = args[j];
				} else {
					throw new FTPSException("Too many args!");
				}					
			} else if (args[j].equalsIgnoreCase("-trustStore") == true) {
				if (++j < args.length) {
					this.trustStore = new File (args[j]);
					this.trustStorePassword = "";
				} else {
					throw new FTPSException("Too many args!");
				}
			} else if (args[j].equalsIgnoreCase("-trustStorePassword") == true) {
				if (++j < args.length) {					
					this.trustStorePassword = args[j];
				} else {
					throw new FTPSException("Too many args!");
				}			
			} else if (args[j].equalsIgnoreCase("-r") == true) {
				this.recursive = true;
			} else {
				throw new FTPSException("Parameter '" + args[j] + "' unknown!");
			}
		}

		int counterOfRetries = 0;
		while (counterOfRetries <= this.retry) {
			FTPS ftps = new FTPS(this.listener);				
			try {
				ftps.setFromUri(this.source);
				ftps.setToUri(this.target);
				ftps.setPort(this.port);
				ftps.setImplicit(false);
				ftps.setProtocol(FTPS.DEFAULT_PROTOCOL);
				ftps.setTrustManager(this.trust);
				
				ftps.setKeyStore(this.keyStore);
				ftps.setPrivateKeyAlias(this.privateKeyAlias);
				ftps.setKeyPassword(this.keyPassword);
				ftps.setKeyStorePassword(this.keyStorePassword);
				
				ftps.setTrustStore(this.trustStore);
				ftps.setTrustStorePassword(this.trustStorePassword);
				
				ftps.setRecursive(this.recursive);
				ftps.setBinaryTransfer(this.binaryTransfer);	
				ftps.setPassive(this.passive);
				ftps.setKeepAliveTimeout(this.keepAliveTimeout);
				ftps.setControlKeepAliveReplyTimeout(this.controlKeepAliveReplyTimeout);
				ftps.setProtocol(this.protocol);
				ftps.setUseEpsvWithIPv4(this.useEpsvWithIPv4);
				ftps.setVerbose(this.verbose);
				ftps.setDelete(this.delete);
				ftps.setOverwrite(this.overwrite);
				ftps.setAskPassword(this.askPassword);
				ftps.execute();
								
				retCode = 0;
			} catch (Throwable t) {
				logger.warn(t.getMessage());				
				logger.info("counterOfRetries is " + counterOfRetries);
				logger.info("retry is " + retry);
				if (counterOfRetries >= this.retry) {
					if (t instanceof FTPSException) {
						FTPSException ftpse = (FTPSException) t;
						throw ftpse;
					} else {
						throw new FTPSException(t); 						
					}
				}
			} finally {
				if (ftps != null) {
					ftps.disconnect();
				}
			}
			counterOfRetries++;							
			if ((counterOfRetries < this.retry) && (retCode != 0)) {
				Utility.sleep(this.delay);

				// Null the Listener, so you re-instanciate the object
				FTPSListenerFactory.getInstance().reset();
			} 
		}
		logger.info("execute() finished...");
	}
	
	public static int getRetCode() {
		return retCode;
	}
	
	private File trustStore = null;
	private String trustStorePassword = null;
	private File keyStore = null;
	private String keyStorePassword = null;
	private String privateKeyAlias = null;
	private String keyPassword = null;

	private int port = FTPS.DEFAULT_PORT;
	private String source = null;
	private String target = null;
	private String trust = FTPS.DEFAULT_TRUST_MANAGER_NONE;
	private String protocol = FTPS.DEFAULT_PROTOCOL;
	private boolean binaryTransfer = false;
	private boolean passive = true;
	private boolean delete = false;
	private boolean overwrite = false;
	private boolean recursive = false;
	private boolean verbose = false;
	private boolean askPassword = false;
	private boolean useEpsvWithIPv4 = false;
	private int keepAliveTimeout = -1;
	private int controlKeepAliveReplyTimeout = -1;
	
	private int retry = 0;	
	private int delay = 0;

	private static String help = " * Parameters:"
			+ "\n"
			+ "-source\n\tThe file to copy. This can be a local path or a remote path of the form user[:password]@host:/directory/path. :password can be omitted if you use key based authentication or specify the password attribute. The way remote path is recognized is whether it contains @ character or not. This will not work if your localPath contains @ character."
			+ "\n"
			+ "-target\n\tThe directory to copy to. This can be a local path or a remote path of the form user[:password]@host:/directory/path. :password can be omitted if you use key based authentication or specify the password attribute. The way remote path is recognized is whether it contains @ character or not. This will not work if your localPath contains @ character."
			+ "\n"
			+ "-port\n\tThe port to connect to on the remote host (default to 990)."
			+ "\n"
			+ "-trust [all|valid|none]\n\tThis trusts all unknown hosts if set to yes/true (default to none)."
			+ "\n"
			+ "-b\n\tThe file transfer is binary mode (default to false, ASCII)."
			+ "\n"
			+ "-d\n\tDelete remote file (download) or local file (upload) after download or upload (default false)."
			+ "\n"
			+ "-o\n\tOverwrite target file if exists, else error (default false)."
			+ "\n"
			+ "-e\n\tUse EPSV with IPv4 (default false)."
			+ "\n"
			+ "-k secs\n\tUse keep-alive timer, setControlKeepAliveTimeout (default -1)." 
			+ "\n"
			+ "-w msec\n\tWait time for keep-alive reply, setControlKeepAliveReplyTimeout (default 1)."
			+ "\n"
			+ "-t all|valid|none\n\tUse one of the built-in TrustManager implementations (none = JVM default)." 
			+ "\n"
			+ "-retry #\n\t Number of retry in case of errors."  
			+ "\n"
			+ "-r\n\t Traverse recursive the directory and send the files (default false)."
			+ "\n"
			+ "-keyStore\n\tLocation of the file holding the private key."
			+ "\n"
			+ "-keyStorePassword\n\tPassphrase for your private key (default to an empty string)."
			+ "\n"
			+ "-privateKeyAlias\n\tThe alias of your private key."
			+ "\n"
			+ "-keyPassword\n\tThe password of your  private key."			
			+ "\n"
			+ "-trustStore\n\tLocation of the file holding the server certificate."
			+ "\n"
			+ "-trustStorePassword\n\tPassphrase for your TrustStore (default to an empty string)."
			+ "\n\n";

}
