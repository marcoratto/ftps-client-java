package uk.co.marcoratto.ftps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.SocketException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.KeyManager;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.util.KeyManagerUtils;
import org.apache.commons.net.util.TrustManagerUtils;
import org.apache.log4j.Logger;

import uk.co.marcoratto.file.maketree.MakeTree;
import uk.co.marcoratto.file.maketree.MakeTreeException;
import uk.co.marcoratto.file.maketree.MakeTreeInterface;
import uk.co.marcoratto.ftps.listeners.FTPSListener;
import uk.co.marcoratto.ftps.listeners.FTPSListenerException;
import uk.co.marcoratto.util.RelativePath;
import uk.co.marcoratto.util.Utility;

public class FTPS implements MakeTreeInterface {

	private static Logger logger = Logger.getLogger(FTPS.class);

	public static int DEFAULT_PORT = 990;
	public static String DEFAULT_PROTOCOL = "TLS";
	public static String DEFAULT_TRUST_MANAGER_ALL = "all";
	public static String DEFAULT_TRUST_MANAGER_VALID = "valid";
	public static String DEFAULT_TRUST_MANAGER_NONE = "none";
	public static String DEFAULT_TRUST_MANAGER_CUSTOM = "custom";

	public static String DEFAULT_STORE_TYPE = "JKS";
	
	public static String DEFAULT_CLEAR = "C";
	public static String DEFAULT_SAFE = "S";
	public static String DEFAULT_CONFIDENTIAL = "E";
	public static String DEFAULT_PRIVATE = "P";
	
	private List<File> listOfFiles = new ArrayList<File>();
	
	private String server = null;
	private int port = -1;
	private String fromUri = null;
	private String toUri= null;
	private String protocol = DEFAULT_PROTOCOL;

	private File trustStore = null;
	private String trustStorePassword = null;
	
	private String storeType = null;
	private File keyStore = null;
	private String keyStorePassword = null;
	private String privateKeyAlias = null;
	private String keyPassword = null;

	private boolean implicit = false;
	private int replycode = -1;
	private FTPSClient ftpsClient = null;
	private String trustManager = null;
	private long keepAliveTimeout = -1;
	private int controlKeepAliveReplyTimeout = -1;
	private boolean useEpsvWithIPv4 = false;
	private boolean listHiddenFiles = false;
	private boolean binaryTransfer = true;
	private boolean passive = true;
	private boolean isRecursive = false;
	private boolean delete = false;
	private boolean verbose = false;
	private boolean overwrite = false;
	private String username = null;
	private String wildcards = null;
	private String password = null;
	private boolean askPassword = false;
	private FTPSListener listener = null;

	public FTPS(FTPSListener aListener) {
		this.listener = aListener;
		this.toUri = null;
		this.fromUri = null;
		this.setStoreType(DEFAULT_STORE_TYPE);
	}

	public void execute() throws FTPSException {
		logger.info("fromUri is " + this.fromUri);
		logger.info("toUri is " + this.toUri);
		if (toUri == null) {
			throw new FTPSException("Why the parameter 'toUri' is null ?");
		}
		if (fromUri == null) {
			throw new FTPSException("Why the parameter 'fromUri' is null ?");
		}
		boolean isFromRemote = this.isRemoteUri(this.fromUri);
		boolean isToRemote = this.isRemoteUri(this.toUri);
		logger.info("isFromRemote is " + isFromRemote);
		logger.info("isToRemote is " + isToRemote);
		if (isFromRemote && !isToRemote) {
			logger.info("Download mode");

			String remote = this.parseUri(fromUri);
			
			if ((remote.indexOf("*") != -1)
					|| (remote.indexOf("?") != -1)) {
				logger.info("Wildcards mode");
				
				try {
					remote = new File(remote).getParentFile().getCanonicalFile().toString();
					logger.info("remote=" + remote);
	
					wildcards = new File(fromUri).getName();
					logger.info("wildcards=" + wildcards);
					
					this.connect();
					
					FTPFile[] files;

					files = this.ftpsClient.listFiles(remote, new FTPFileFilter() {

								public boolean accept(FTPFile aFTPFile) {
									if (aFTPFile.isFile() && 
										FilenameUtils.wildcardMatchOnSystem(aFTPFile.getName(), wildcards)) {
										return true;
					                } else {
					                	return false;
					                }
								}										
						}
										);
					
					logger.info("Found " + files.length + " files to download.");
					for (int j=0; j<files.length; j++) {
						
						FTPFile file = files[j];
						String remoteFile = remote + "/" + file.getName();
						
			            logger.info("File do download: " + file.getName());
			            
						File local = new File(toUri);
						logger.info("The local path is " + local.getAbsolutePath());

						if (local.isDirectory()) {
							logger.info("The local path is a directory.");
							local = new File(toUri, file.getName());
							logger.info("The local path changed to " + local.getAbsolutePath());
						} else {
							throw new FTPSException(local + " must to be a directory!");
						}
						
						try {
							this.listener.onStartDownloadFTPS(j+1, files.length, remoteFile, local.getAbsolutePath());
						} catch (FTPSListenerException le) {
							logger.error(le.getMessage(), le);
						}
						
						this.download(remoteFile, local);

						try {
							this.listener.onEndDownloadFTPS(j+1, files.length, remoteFile, local.getAbsolutePath());
						} catch (FTPSListenerException le) {
							logger.error(le.getMessage(), le);
						}
						if (this.delete) {
							this.delete(remoteFile);
						}
			        }
				} catch (FTPSException e) {
					throw e;
				} catch (IOException e) {
					throw new FTPSException(e);
				}
			} else {
				try {
					this.listener.onStartDownloadFTPS(1, 1, fromUri, toUri);
				} catch (FTPSListenerException le) {
					logger.error(le.getMessage(), le);
				}
				
				this.connect();

				File local = new File(toUri);
				logger.info("The local path is " + local.getAbsolutePath());

				if (local.isDirectory()) {
					logger.info("The local path is a directory.");
					local = new File(toUri, new File(remote).getName());
				}

				this.download(remote, local);

				try {
					this.listener.onEndDownloadFTPS(1, 1, fromUri, toUri);
				} catch (FTPSListenerException le) {
					logger.error(le.getMessage(), le);
				}

				if (this.delete) {
					this.delete(remote);
				}

			}

		} else if (!isFromRemote && isToRemote) {
			logger.info("Upload mode");

	    	File local = null;
			try {
				local = new File(fromUri).getCanonicalFile();
			} catch (IOException e) {
				throw new FTPSException(e);
			}
			logger.info("local=" + local);
			
			if (local.isFile()) {
				logger.info("File mode.");
				try {
					this.listener.onStartUploadFTPS(1, 1, local, toUri);
				} catch (FTPSListenerException le) {
					logger.error(le.getMessage(), le);
				}

				String remote = this.parseUri(toUri);
				this.connect();
				this.upload(local, remote);

				try {
					this.listener.onEndUploadFTPS(1, 1, local, toUri);
				} catch (FTPSListenerException le) {
					logger.error(le.getMessage(), le);
				}
				if (this.delete) {
					logger.info("Delete local file " + local.getAbsolutePath());
					local.delete();
				}
			} else if (local.isDirectory()) {
				logger.info("Directory mode.");
				wildcards = "*.*";
				logger.info("wildcards=" + wildcards);

				String remote = parseUri(toUri);
								
				MakeTree mt = new MakeTree(this);
				try {
					mt.searchDirectoryFile(local, wildcards, this.isRecursive);
				} catch (MakeTreeException e) {
					throw new FTPSException(e);
				}

				this.connect();

				logger.info("listOfFiles has " + listOfFiles.size() + " items");
				if (!this.listOfFiles.isEmpty()) {
					for (int j=0; j<this.listOfFiles.size(); j++) {
						File f = this.listOfFiles.get(j);						
						logger.debug("ParentFile is " + f.getParentFile());
						String localRelPath = RelativePath.getRelativePath(local, f.getParentFile());
						logger.debug("localRelPath is " + localRelPath);
						
						this.createRemoteDirectoryTree(remote, localRelPath);
						
						String remoteFile = remote + "/" + localRelPath + ((localRelPath.length() == 0) ? "" : "/") + f.getName();
						try {
							this.listener.onStartUploadFTPS(j + 1, this.listOfFiles.size(), f, remoteFile);
						} catch (FTPSListenerException le) {
							logger.error(le.getMessage(), le);
						}
						this.upload(f, remoteFile);
						try {
							this.listener.onEndUploadFTPS(j + 1, this.listOfFiles.size(), f, remoteFile);
						} catch (FTPSListenerException le) {
							logger.error(le.getMessage(), le);
						}
						if (this.delete) {
							logger.info("Delete local file " + local.getAbsolutePath());
							local.delete();
						}
					}
				}
			} else if ((new File(fromUri).getName().indexOf("*") != -1)
					|| (new File(fromUri).getName().indexOf("?") != -1)) {
				logger.info("Wildcards mode");
				try {
					local = new File(fromUri).getParentFile().getCanonicalFile();
				} catch (IOException e) {
					throw new FTPSException(e);
				}
				logger.info("local is " + local);

				wildcards = new File(fromUri).getName();
				logger.info("wildcards is " + wildcards);
				
				String remote = parseUri(toUri);
				
				MakeTree mt = new MakeTree(this);
				try {
					mt.searchDirectoryFile(local, wildcards, this.isRecursive);
				} catch (MakeTreeException e) {
					throw new FTPSException(e);
				}

				this.connect();

				logger.info("listOfFiles has " + listOfFiles.size() + " items");
				if (!this.listOfFiles.isEmpty()) {
					for (int j=0; j<this.listOfFiles.size(); j++) {
						File f = this.listOfFiles.get(j);						
						logger.debug("ParentFile is " + f.getParentFile());
						String localRelPath = RelativePath.getRelativePath(local, f.getParentFile());
						logger.debug("localRelPath is " + localRelPath);
						
						this.createRemoteDirectoryTree(remote, localRelPath);
						
						String remoteFile = remote + "/" + localRelPath + ((localRelPath.length() == 0) ? "" : "/") + f.getName();
						try {
							this.listener.onStartUploadFTPS(j + 1, this.listOfFiles.size(), f, remoteFile);
						} catch (FTPSListenerException le) {
							logger.error(le.getMessage(), le);
						}
						this.upload(f, remoteFile);
						try {
							this.listener.onEndUploadFTPS(j + 1, this.listOfFiles.size(), f, remoteFile);
						} catch (FTPSListenerException le) {
							logger.error(le.getMessage(), le);
						}
						if (this.delete) {
							logger.info("Delete local file " + local.getAbsolutePath());
							local.delete();
						}
					}
				}
			} else {
				throw new FTPSException(fromUri + " not valid!");
			}
		} else if (isFromRemote && isToRemote) {
			throw new FTPSException("Copying from a remote server to a remote server is not supported.");
		} else {
			throw new FTPSException(
					"'toUri' and 'fromUri' attributes must have syntax like the following: user:password@host:/path");
		}
	}

	public void connect() throws FTPSException {		
		if ((this.trustStore != null) &&
			(this.trustStorePassword != null)) {
			if (!this.trustStore.exists()) {
				throw new FTPSException("trustStore '" + trustStore + "' not exists!");
			}

			System.setProperty("javax.net.ssl.trustStore", this.trustStore.getAbsolutePath());
			logger.info("javax.net.ssl.trustStore set to " + this.trustStore);
			
			System.setProperty("javax.net.ssl.trustStorePassword", this.trustStorePassword);
			logger.info("javax.net.ssl.trustStorePassword set to " + this.trustStorePassword);
		}
		
		this.ftpsClient = new FTPSClient(this.protocol, this.implicit);

		if ((this.keyStore != null) && 
				(this.keyStorePassword != null) &&
				(this.privateKeyAlias != null) && 
				(this.keyPassword != null)) {
			
			if (!this.keyStore.exists()) {
				throw new FTPSException("keyStore '" + keyStore + "' not exists!");
			}
				KeyManager km = null;
				try {
					km = KeyManagerUtils.createClientKeyManager(this.storeType,
						     this.keyStore, 
						     this.keyStorePassword,
						     this.privateKeyAlias, 
						     this.keyPassword);					
					ftpsClient.setKeyManager(km);	
					logger.info("KeyManager initialize");
				} catch (NullPointerException e) {
					throw new FTPSException("privateKeyAlias not found!");
				} catch (IOException e) {
					throw new FTPSException(e);
				} catch (GeneralSecurityException e) {
					throw new FTPSException(e);
				}			
		}
		
		if (this.trustManager.equals(DEFAULT_TRUST_MANAGER_ALL)) {
			ftpsClient.setTrustManager(TrustManagerUtils.getAcceptAllTrustManager());
		} else if (trustManager.equals(DEFAULT_TRUST_MANAGER_VALID)) {
			ftpsClient.setTrustManager(TrustManagerUtils.getValidateServerCertificateTrustManager());
		} else if (trustManager.equals(DEFAULT_TRUST_MANAGER_NONE)) {
			ftpsClient.setTrustManager(null);
		} 
				
		if (keepAliveTimeout >= 0) {
			ftpsClient.setControlKeepAliveTimeout(keepAliveTimeout);
		}
		if (controlKeepAliveReplyTimeout >= 0) {
			ftpsClient
					.setControlKeepAliveReplyTimeout(controlKeepAliveReplyTimeout);
		}

		ftpsClient.setListHiddenFiles(listHiddenFiles);

		// suppress login details
		if (this.verbose) {
			this.ftpsClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));			
		}

		try {
			ftpsClient.connect(server, port);
			this.replycode = ftpsClient.getReplyCode();

			// Set data channel protection to private
			this.ftpsClient.execPROT(DEFAULT_PRIVATE);

			if (!ftpsClient.login(username, password)) {
				throw new FTPSException("Error on login with username '"
						+ this.username + "' and password '" + this.password
						+ "'");
			}
			if (this.binaryTransfer) {
				ftpsClient.setFileType(FTP.BINARY_FILE_TYPE);
			} else {
				ftpsClient.setFileType(FTP.ASCII_FILE_TYPE);
			}
			if (this.passive) {
				ftpsClient.enterLocalPassiveMode();
			} else {
				ftpsClient.enterLocalActiveMode();
			}

			ftpsClient.setUseEPSVwithIPv4(this.useEpsvWithIPv4);

			// check that control connection is working OK
			ftpsClient.noop();
		} catch (FTPSException e) {
			throw e;
		} catch (SocketException e) {
			throw new FTPSException(e);
		} catch (IOException e) {
			throw new FTPSException(e);
		} finally {
			if ((ftpsClient != null)
					&& (!FTPReply.isPositiveCompletion(replycode))) {
				try {
					ftpsClient.disconnect();
				} catch (IOException e) {
					logger.warn(e.getMessage(), e);
				}
			}
		}
	}

	public void download(String remote, File local) throws FTPSException {
		if (this.ftpsClient == null) {
			throw new FTPSException("ftps is null! Do you call before the method 'connect'?");
		}

		OutputStream output = null;
		try {
			if (local.exists() && this.overwrite == false) {
				throw new FTPSException("Local file " + local.getPath() + " already exists!");
			} 
			String modificationTime = this.ftpsClient.getModificationTime(remote);
			if (modificationTime == null) {
				throw new FTPSException("Remote file " + remote + " not found!");
			} 
			output = new FileOutputStream(local);
			this.ftpsClient.retrieveFile(remote, output);

		} catch (FTPSException e) {
			throw e;
		} catch (FileNotFoundException e) {
			throw new FTPSException(e);
		} catch (IOException e) {
			throw new FTPSException(e);
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					logger.warn(e.getMessage(), e);
				}				
			}
		}
	}

	public void delete(String remote) throws FTPSException {
		if (this.ftpsClient == null) {
			throw new FTPSException("ftps is null! Do you call before the method 'connect'?");
		}
		try {
			logger.info("Delete remote file " + remote);
			ftpsClient.deleteFile(remote);
		} catch (IOException e) {
			throw new FTPSException(e);
		} 
	}

	public void disconnect() throws FTPSException {
		if (this.ftpsClient != null) {
			try {
				if (this.ftpsClient.isConnected()) {
					logger.info("Disconnect");
					this.ftpsClient.disconnect();
				}
			} catch (IOException e) {
				throw new FTPSException(e);
			}
		}
	}

	public boolean logout() throws FTPSException {
		if (this.ftpsClient == null) {
			throw new FTPSException(
					"ftps is null! Do you call before the method 'connect'?");
		}
		try {
			logger.info("logout");
			return this.ftpsClient.logout();
		} catch (IOException e) {
			throw new FTPSException(e);
		}
	}

	public void upload(File local, String remote) throws FTPSException {
		if (this.ftpsClient == null) {
			throw new FTPSException(
					"ftps is null! Do you call before the method 'connect'?");
		}
		if (local.length() == 0) {
			throw new FTPSException("Local file is empty!");
		}
		InputStream input = null;
		try {
			String modificationTime = this.ftpsClient.getModificationTime(remote);
			logger.info("modificationTime is " + modificationTime);
			if (modificationTime != null && 
					this.overwrite == false) {
				throw new FTPSException("Remote file " + remote + " already exists!");
			} 
			logger.info("Upload file " + local.getAbsolutePath() + " to " + remote);
			input = new FileInputStream(local);
			ftpsClient.storeFile(remote, input);
		} catch (FTPSException e) {
			throw e;
		} catch (FileNotFoundException e) {
			throw new FTPSException(e);
		} catch (IOException e) {
			throw new FTPSException(e);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					logger.warn(e.getMessage(), e);
				}				
			}
		}
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String value) {
		this.protocol = value;
		logger.info("protocol is " + this.protocol);
	}

	public int getReplycode() {
		return replycode;
	}

	public boolean isImplicit() {
		return implicit;
	}

	public void setImplicit(boolean value) {
		this.implicit = value;
		logger.info("implicit is " + this.implicit);
	}


	public File getTrustStore() {
		return trustStore;
	}

	public void setTrustStore(File value) {
		this.trustStore = value;
		logger.info("trustStore is " + this.trustStore);
	}

	public String getTrustStorePassword() {
		return trustStorePassword;
	}

	public void setTrustStorePassword(String value) {
		this.trustStorePassword = value;
		logger.info("trustStorePassword is " + this.trustStorePassword);
	}

	public File getKeyStore() {
		return keyStore;
	}

	public void setKeyStore(File value) {
		this.keyStore = value;
		logger.info("keyStore is " + this.keyStore);
	}

	public String getKeyStorePassword() {
		return keyStorePassword;
	}

	public void setKeyStorePassword(String value) {
		this.keyStorePassword = value;
		logger.info("keyStorePassword is " + this.keyStorePassword);
	}
	
	public String getPrivateKeyAlias() {
		return this.privateKeyAlias;
	}

	public void setPrivateKeyAlias(String value) {
		this.privateKeyAlias = value;
		logger.info("privateKeyAlias is " + this.privateKeyAlias);
	}

	public String getKeyPassword() {
		return this.keyPassword;
	}

	public void setKeyPassword(String value) {
		this.keyPassword = value;
		logger.info("keyPassword is " + this.keyPassword);
	}
	
	public String getTrustManager() {
		return trustManager;
	}

	public void setTrustManager(String value) {
		this.trustManager = value;
		logger.info("trustManager is " + this.trustManager);
	}

	public String getStoreType() {
		return this.storeType;
	}

	public void setStoreType(String value) {
		this.storeType = value;
		logger.info("storeType is " + this.storeType);
	}
	public long getKeepAliveTimeout() {
		return keepAliveTimeout;
	}

	public void setKeepAliveTimeout(long value) {
		this.keepAliveTimeout = value;
		logger.info("keepAliveTimeout is " + this.keepAliveTimeout);
	}

	public int getControlKeepAliveReplyTimeout() {
		return controlKeepAliveReplyTimeout;
	}

	public void setControlKeepAliveReplyTimeout(int value) {
		this.controlKeepAliveReplyTimeout = value;
		logger.info("controlKeepAliveReplyTimeout is "
				+ this.controlKeepAliveReplyTimeout);
	}

	public boolean isListHiddenFiles() {
		return listHiddenFiles;
	}

	public void setListHiddenFiles(boolean value) {
		this.listHiddenFiles = value;
		logger.info("listHiddenFiles is " + this.listHiddenFiles);
	}

	public boolean isBinaryTransfer() {
		return binaryTransfer;
	}

	public void setBinaryTransfer(boolean value) {
		this.binaryTransfer = value;
		logger.info("binaryTransfer is " + this.binaryTransfer);
	}

	public boolean isPassive() {
		return passive;
	}

	public void setPassive(boolean value) {
		this.passive = value;
		logger.info("passive is " + this.passive);
	}

	public boolean isUseEpsvWithIPv4() {
		return useEpsvWithIPv4;
	}

	public void setUseEpsvWithIPv4(boolean value) {
		this.useEpsvWithIPv4 = value;
		logger.info("useEpsvWithIPv4 is " + this.useEpsvWithIPv4);
	}

	public boolean isRecursive() {
		return isRecursive;
	}

	public void setRecursive(boolean value) {
		this.isRecursive = value;
		logger.info("isRecursive is " + this.isRecursive);
	}

	public void setFromUri(String value) {
		this.fromUri = value;
		logger.info("fromUri is " + this.fromUri);
	}

	public void setToUri(String value) {
		this.toUri = value;
		logger.info("toUri is " + this.toUri);
	}

	public int getPort() {
		return port;
	}

	public void setPort(int value) {
		this.port = value;
		logger.info("port is " + this.port);
	}
	
	public boolean isDelete() {
		return delete;
	}

	public void setDelete(boolean value) {
		this.delete = value;
		logger.info("delete is " + this.delete);
	}

	public boolean isVerbose() {
		return verbose;
	}

	public boolean isOverwrite() {
		return overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
		logger.info("overwrite is " + this.overwrite);
	}
	
	public void setVerbose(boolean value) {		
		this.verbose = value;
		logger.info("verbose is " + this.verbose);
	}

	public void onFileFound(File aFile) throws MakeTreeException {
		logger.info("Added file " + aFile.getName());
		this.listOfFiles.add(aFile);
	}

	public void onDirFound(File aDirectory) throws MakeTreeException {
	}

	private String parseUri(String uri) throws FTPSException {
		logger.info("The uri is " + uri);
		int indexOfAt = uri.indexOf('@');
		int indexOfColon = uri.indexOf(':');

		if (indexOfColon != -1 && indexOfColon < indexOfAt) {
			// user:password@host:/path notation
			// everything upto the last @ before the last : is considered
			// password. (so if the path contains an @ and a : it will not work)
			int indexOfCurrentAt = indexOfAt;
			int indexOfLastColon = uri.lastIndexOf(':');
			while (indexOfCurrentAt > -1 && indexOfCurrentAt < indexOfLastColon) {
				indexOfAt = indexOfCurrentAt;
				indexOfCurrentAt = uri.indexOf('@', indexOfCurrentAt + 1);
			}
			this.username = uri.substring(0, indexOfColon);
			this.password = uri.substring(indexOfColon + 1, indexOfAt);
		} else if (indexOfAt != -1) {
			// no password, will require keyfile
			this.username = uri.substring(0, indexOfAt);
			// this.password = "";
		}
		int indexOfPath = uri.indexOf(':', indexOfAt + 1);
		if (indexOfPath == -1) {
			throw new FTPSException("no remote path in " + uri);
		}

		this.server = uri.substring(indexOfAt + 1, indexOfPath);
		String remotePath = uri.substring(indexOfPath + 1);
		logger.info("The remotePath is " + remotePath);
		
		if ((this.password == null) && 
				(this.askPassword == true)) {

				String pwd = Utility.inputString(this.username + "@" + this.server + "'s password:", null);
				logger.info("pwd is " + pwd);
				if (pwd == null) {
					throw new FTPSException("no password for user "
							+ this.username + " has been "
							+ "given.  Can't authenticate.");
				} else {
					this.password = pwd;
				}
			}

		return remotePath;
	}

	private void createRemoteDirectoryTree(String startDir, String dirTree) throws FTPSException {
			boolean dirExists = true;
			
	        try {
				if (!ftpsClient.changeWorkingDirectory(startDir)) {
				      throw new FTPSException("Unable to change into newly created remote directory '" + startDir + "'.  error='" + ftpsClient.getReplyString()+"'");
				}
			} catch (IOException e) {
				throw new FTPSException(e);
			}

		  //tokenize the string and attempt to change into each directory level.  If you cannot, then start creating.
		  String[] directories = dirTree.split("/");
		  for (String dir : directories ) {
		    if (!dir.isEmpty() ) {
		      if (dirExists) {
		        try {
					dirExists = this.ftpsClient.changeWorkingDirectory(dir);
				    if (!dirExists) {
				    		logger.debug(" Create remote directory " + dir);
					        if (!ftpsClient.makeDirectory(dir)) {
					          throw new FTPSException("Unable to create remote directory '" + dir + "'.  error='" + ftpsClient.getReplyString()+"'");
					        }
					        if (!ftpsClient.changeWorkingDirectory(dir)) {
					          throw new FTPSException("Unable to change into newly created remote directory '" + dir + "'.  error='" + ftpsClient.getReplyString()+"'");
					        }
				    }
				} catch (IOException e) {
					throw new FTPSException(e);
				}
		      }
		    }
		  }     
		}

	private boolean isRemoteUri(String uri) {
		logger.info("Check the uri " + uri);
		boolean isRemote = true;
		File f = new File(uri);
		if (f.getParentFile().exists()) {
			isRemote = false;
		}
		return isRemote;
	}

	public boolean isAskPassword() {
		return askPassword;
	}

	public void setAskPassword(boolean value) {
		this.askPassword = value;
		logger.info("The askPassword is " + askPassword);
	}
	
}
