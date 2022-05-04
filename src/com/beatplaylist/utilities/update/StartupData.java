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

package com.beatplaylist.utilities.update;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.beatplaylist.Options;
import com.beatplaylist.utilities.data.Data;
import com.beatplaylist.utilities.filemanager.FileUtilities;
import com.beatplaylist.utilities.filemanager.OperatingSystem;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.user.UserManager;
import com.beatplaylist.utilities.validation.PaymentManager;

public class StartupData {

	private static StartupData instance = new StartupData();

	public static StartupData getInstance() {
		return instance;
	}

	// Server status numbers:
	// Status Number 1: Offline
	// Status Number 2: New version release
	// Status Number 3: Maintenance
	// Status Number 4: Online

	public String server_status_number = "4", announcement = "", server_version = "", library_version = "", jre_version = "", update_url = "";
	public Timer timer;
	public boolean pendingUpdate = false, useCustomUpdater = false;

	// Check whether the servers are under maintenance, online or offline.
	// Check to see if there is an announcement message.
	public void getStartupData() {
		try {
			URLConnection connection = new URL("https://rhythm.cc/api/info.json").openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0");
			connection.connect();

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
			String result = "";
			String inputLine;
			while ((inputLine = in.readLine()) != null)
				result += inputLine;
			in.close();
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(result);
			JSONWrapper response = new JSONWrapper((JSONObject) obj);

			this.server_status_number = response.getJSONString("server_status");
			if (Options.test_mode) {
				this.server_status_number = "4";
			}
			this.announcement = response.getJSONString("announcement");

			Data.getInstance().personal_twitter = response.getJSONString("ryan_twitter");
			Data.getInstance().personal_instagram = response.getJSONString("ryan_instagram");

			Data.getInstance().company_twitter = response.getJSONString("company_twitter");
			Data.getInstance().company_instagram = response.getJSONString("company_instagram");

			PaymentManager.getInstance().stripe_publishable_key = response.getJSONString("stripe_publishable_key");

			Iterator<JSONObject> versionIterator = response.getJSONArray("version");
			while (versionIterator.hasNext()) {
				JSONObject object = (JSONObject) versionIterator.next();
				if (object.containsKey("client_version")) {
					this.server_version = object.get("client_version").toString();
				}
				if (object.containsKey("library_version")) {
					this.library_version = object.get("library_version").toString();
				}
				if (object.containsKey("jre_version")) {
					this.jre_version = object.get("jre_version").toString();
				}
			}
			// System.out.println(server_version);
			// System.out.println(library_version);

			if (!this.server_version.equals(UpdateManager.getClientVersion()))
				this.pendingUpdate = true;

			// We can enable / disable the custom auto-updater if an issue occurs.
			this.useCustomUpdater = response.getJSONBoolean("useCustomUpdater");

			// String jreVersion = System.getProperty("java.version");
			// if (jreVersion.equals(this.jre_version)) {
			if (FileUtilities.getInstance().getOperatingSystem() == OperatingSystem.WINDOWS) {
				String bitType = System.getProperty("sun.arch.data.model");
				if (bitType == "32")
					this.update_url = response.getJSONString("windows32BitUpdateURL");
				else
					this.update_url = response.getJSONString("windows64BitUpdateURL");
			} else {
				this.update_url = response.getJSONString("macOSUpdateURL");
			}
			Iterator<JSONObject> updateIterator = response.getJSONArray("fullUpdateRequiredVersions");

			while (updateIterator.hasNext()) {
				JSONWrapper versionObject = new JSONWrapper(updateIterator.next());

				String version = versionObject.getJSONString("version");
				String downloadURL = versionObject.getJSONString("downloadURL");

				if (UpdateManager.getClientVersion().equals(version)) {
					if (FileUtilities.getInstance().getOperatingSystem() == OperatingSystem.WINDOWS) {
						String bitType = System.getProperty("sun.arch.data.model");
						if (bitType == "32")
							this.update_url = downloadURL + "_32.exe";
						else
							this.update_url = downloadURL + "_64.exe";
					} else {
						this.update_url = downloadURL + ".zip";
					}
				}
			}
			System.out.println(this.update_url);
			// } else {
			// UpdateManager.updateJREVersion();
			// System.out.println("Full Installer Update Required " + this.update_url);
			// }
			// this.pendingUpdate = true;
			// this.update_url = response.getJSONString("fullUpdateURL");
			// System.out.println("Full Installer Update Required " + this.update_url);
			// }

		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		this.timer = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				getStartupData();
				if (server_status_number.isEmpty() || server_status_number.equals("4") || server_status_number.equals("1"))
					return;
				UserManager.getInstance().logout();
			}
		};
		this.timer.schedule(task, 0, 300000);
	}
}