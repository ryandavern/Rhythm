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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class FileLoader {

	private static FileLoader instance = new FileLoader();

	public static FileLoader getInstance() {
		return instance;
	}

	public void loadSettings() {
		JSONParser parser = new JSONParser();
		try {
			String directory = getDirectory();
			if (!new File(directory).exists())
				new File(directory).mkdir();

			File file = new File(directory, "settings.txt");
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (file.length() == 0) {
				FileUtilities.getInstance().settingsData = new JSONObject();
				return;
			}
			FileReader fileReader = new FileReader(directory + "settings.txt");
			JSONObject jsonObject = (JSONObject) parser.parse(fileReader);
			FileUtilities.getInstance().settingsData = jsonObject;
			fileReader.close();
		} catch (ParseException | IOException e) {
			e.printStackTrace();
			FileUtilities.getInstance().settingsData = new JSONObject();
		}
	}

	public JSONObject loadPlaylistColorSettings() {
		JSONParser parser = new JSONParser();
		try {
			String directory = getDirectory();
			if (!new File(directory).exists())
				new File(directory).mkdir();

			File file = new File(directory, "playlistColors.json");
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (file.length() == 0) {
				return new JSONObject();
			}
			FileReader fileReader = new FileReader(directory + "playlistColors.json");
			JSONObject jsonObject = (JSONObject) parser.parse(fileReader);
			fileReader.close();
			return jsonObject;
		} catch (ParseException | IOException e) {
			e.printStackTrace();
			return new JSONObject();
		}
	}

	public void set(String key, String value) {
		FileUtilities.getInstance().settingsData.put(key, value);
		saveSettings();
	}

	public void saveSettings() {
		try {
			FileWriter file = new FileWriter(getDirectory() + "settings.txt");

			file.write(FileUtilities.getInstance().settingsData.toJSONString());
			file.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getDirectory() {
		String directory = "";
		if (FileUtilities.getInstance().getOperatingSystem() == OperatingSystem.WINDOWS)
			directory = FileUtilities.getInstance().getAppData() + "\\BeatPlaylist" + File.separator;
		else if (FileUtilities.getInstance().getOperatingSystem() == OperatingSystem.MACOS)
			directory = File.separator + "Applications" + File.separator + "Rhythm.app" + File.separator + "Contents" + File.separator + "Settings" + File.separator;
		return directory;
	}
}