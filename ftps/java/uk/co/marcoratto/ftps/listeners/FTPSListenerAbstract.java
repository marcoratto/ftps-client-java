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

public abstract class FTPSListenerAbstract implements FTPSListener {
	
    public abstract void onErrorFTPS(Throwable t) throws FTPSListenerException;
    
    public abstract void onStartUploadFTPS(int counter, int totalFiles, File fromUri, String toUri) throws FTPSListenerException;
    public abstract void onEndUploadFTPS(int counter, int totalFiles, File fromUri, String toUri) throws FTPSListenerException;
    
    public abstract void onStartDownloadFTPS(int counter, int totalFiles, String fromUri, String toUri) throws FTPSListenerException;
    public abstract void onEndDownloadFTPS(int counter, int totalFiles, String fromUri, String toUri) throws FTPSListenerException;
    
    public abstract void onStartFTPS() throws FTPSListenerException;     
    public abstract void onEndFTPS(int returnCode) throws FTPSListenerException;

    String getClassName() {
        String FQClassName = getClass().getName();
        int firstChar;
        firstChar = FQClassName.lastIndexOf ('.') + 1;
        if ( firstChar > 0 ) {
          FQClassName = FQClassName.substring ( firstChar );
          }
        return FQClassName;
    }

}
