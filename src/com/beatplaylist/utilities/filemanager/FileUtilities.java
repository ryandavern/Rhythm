/**
 *  Disclaimer
 *  This project was created by Ryan Davern.
 *  Start Date: 30/03/2016.
 *  
 *  Copyright (C) 2017 Ryan Davern - All Rights Reserved.
 *  You may not use, distribute, monetize or modify this code under the terms of the Copyright Act 1994.
 *  You may use the compiled program, which can be downloaded at https://www.beatplaylist.com/. Any modified versions or versions uploaded to a different website is against TOS (https://www.beatplaylist.com/terms).
 *  
 *  For more information on the Copyright Act 1994, please visit http://www.legislation.govt.nz/act/public/1994/0143/latest/DLM345634.html.
 */

package com.beatplaylist.utilities.filemanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.simple.JSONObject;

public class FileUtilities {

	private static FileUtilities instance = new FileUtilities();

	public static FileUtilities getInstance() {
		return instance;
	}

	private String app_data_folder = "";
	public JSONObject settingsData;

	public String getAutoUpdateLocation() {
		if (FileUtilities.getInstance().getOperatingSystem() == OperatingSystem.WINDOWS)
			return getAppData() + File.separator + "BeatPlaylist" + File.separator;
		else if (FileUtilities.getInstance().getOperatingSystem() == OperatingSystem.MACOS)
			return System.getProperty("install4j.appDir");
		return "";
	}

	public File getAppData() {
		if (!this.app_data_folder.isEmpty())
			return new File(this.app_data_folder.trim());

		ProcessBuilder builder = new ProcessBuilder(new String[] { "cmd", "/C echo %APPDATA%" });

		BufferedReader br = null;
		try {
			Process start = builder.start();
			br = new BufferedReader(new InputStreamReader(start.getInputStream()));
			String path = br.readLine();
			if (path.endsWith("\"")) {
				path = path.substring(0, path.length() - 1);
			}
			this.app_data_folder = path.trim();
			return new File(path.trim());

		} catch (IOException ex) {
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException ex) {
				}
			}
		}

		return null;
	}

	public OperatingSystem getOperatingSystem() {
		if (System.getProperty("os.name").toLowerCase().startsWith("win"))
			return OperatingSystem.WINDOWS;
		else if (System.getProperty("os.name").toLowerCase().startsWith("mac"))
			return OperatingSystem.MACOS;
		else
			return OperatingSystem.UNKNOWN;
	}

}