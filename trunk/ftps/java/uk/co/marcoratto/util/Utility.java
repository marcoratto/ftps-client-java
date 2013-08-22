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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import org.apache.log4j.Logger;

public class Utility {

	private static Logger logger = Logger.getLogger(Utility.class);
	
	public final static String NEWLINE = System.getProperty("line.separator");

	public static String inputString(String msg, String defaultValue) {
		System.out.print(msg);
		String out = defaultValue;
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);
		// read in user input 
		try {
		    out = reader.readLine(); 
		} catch(Exception e) { }
		return out;
	}
	
	public static int stringToInt(String s) throws UtilityException {
		int out = -1;		
		try {
			out = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			throw new UtilityException(e);
		}
		return out;
	}

	public static boolean stringToBoolean(String s) throws UtilityException {
		if (s == null) {
			return false;
		} else {
			return s.trim().equalsIgnoreCase("true");
		}
	}
	
	public static void sleep(int sec) {
		try {
			logger.info("Sleep " + sec + " seconds...");
			Thread.sleep(sec * 1000);
		} catch (InterruptedException e) {
			logger.warn(e.getMessage(), e);
		}
	}

	public static void stringToFile(File file, String encoding, String buffer) throws UtilityException {
		BufferedWriter bw = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new StringReader(buffer));
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding));
			String line = null;
			String space = "";
			while ((line = br.readLine()) != null) {
				bw.write(space);
				bw.write(line);
				space = NEWLINE;
			}
			logger.info("Write " + buffer.length() + " bytes to file.");
			logger.debug("The buffer is:" + NEWLINE + buffer);
		} catch (IOException ioe) {
			throw new UtilityException(ioe);
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (Exception e) {
					// Ignore
				}
			}
		}
	}
	
	public static String fileToString(File file, String encoding) throws UtilityException {
		BufferedReader br = null;
		StringBuffer out = new StringBuffer("");
		try {
			logger.info("Try to read file " + file.getAbsolutePath().toString() + ".");
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
			String line = null;
			String space = "";
			while ((line = br.readLine()) != null) {
				out.append(space);
				out.append(line);
				space = NEWLINE;
			}
			logger.info("Read " + out.length() + " bytes from file.");
			logger.debug("The buffer is:" + NEWLINE + out.toString());
		} catch (IOException ioe) {
			throw new UtilityException(ioe);
		} finally {
			if (br != null) {
				try {
						br.close();
				} catch (Exception e) {
					// Ignore
				}
			}
		}
		return out.toString();
	}	
}
