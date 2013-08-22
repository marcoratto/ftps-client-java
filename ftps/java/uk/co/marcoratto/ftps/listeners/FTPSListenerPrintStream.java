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

import java.io.File;

import org.apache.log4j.Logger;

public class FTPSListenerPrintStream extends FTPSListenerAbstract {
	
	private static Logger logger = Logger.getLogger(FTPSListenerPrintStream.class);
		
	public void onErrorFTPS(Throwable t) throws FTPSListenerException {
		System.err.println(t.getMessage());
		t.printStackTrace(System.err);
	}

	public void onStartFTPS() throws FTPSListenerException{
		logger.info("Start.");
	}

	public void onEndFTPS(int returnCode) throws FTPSListenerException{
		logger.info("End with return code " + returnCode);
	}
	
	public void onStartUploadFTPS(int counter, int totalFiles, File fromUri, String toUri) throws FTPSListenerException {
		System.out.println("Start upload file " + counter + "/" + totalFiles + " from " + fromUri + " to " + toUri);
	}
	
	public void onEndUploadFTPS(int counter, int totalFiles, File fromUri, String toUri) throws FTPSListenerException {
		System.out.println("End upload file " + counter + "/" + totalFiles + " from " + fromUri + " to " + toUri);
	}

	public void onStartDownloadFTPS(int counter, int totalFiles, String fromUri, String toUri) throws FTPSListenerException{
		System.out.println("Start download from " + fromUri + " to " + toUri);
	}
	
	public void onEndDownloadFTPS(int counter, int totalFiles, String fromUri, String toUri) throws FTPSListenerException{
		System.out.println("End download from " + fromUri + " to " + toUri);
	}

}
