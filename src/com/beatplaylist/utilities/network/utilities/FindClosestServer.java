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

package com.beatplaylist.utilities.network.utilities;

import java.io.BufferedReader;

import com.beatplaylist.Options;
import com.beatplaylist.utilities.Utilities;
import com.beatplaylist.utilities.data.Data;
import com.beatplaylist.utilities.filemanager.FileUtilities;
import com.beatplaylist.utilities.filemanager.OperatingSystem;

public class FindClosestServer {

	private static FindClosestServer instance = new FindClosestServer();

	public static FindClosestServer getInstance() {
		return instance;
	}

	private String country = "United States";
	// private List<String> countries = Arrays.asList("New Zealand", "Australia", "United States", "Canada", "Mexico");

	// United States, Australia, New Zealand, Canada, Mexico.

	public void selectServer() {
		this.country = Utilities.getInstance().getCountry();

		Data.getInstance().country = this.country;
		getMacAddress();

		ServerUtilities.getInstance().setReadWriteHost("connect.beatplaylist.com", "connect.beatplaylist.com");
		ServerUtilities.getInstance().setServerLocation("America");

		ServerUtilities.getInstance().setImageHost("upload.beatplaylist.com");
		ServerUtilities.getInstance().setImageLocation("America");

		if (Options.test_mode) {
			ServerUtilities.getInstance().setReadWriteHost("localhost", "localhost");
			ServerUtilities.getInstance().setImageHost("localhost");
			ServerUtilities.getInstance().setServerLocation("localhost");
			ServerUtilities.getInstance().setImageLocation("localhost");
		}
	}

	public void getMacAddress() {
		if (FileUtilities.getInstance().getOperatingSystem() == OperatingSystem.WINDOWS) {
			try {
				Process process = Runtime.getRuntime().exec("getmac /fo csv /nh");
				BufferedReader in = new BufferedReader(new java.io.InputStreamReader(process.getInputStream()));
				String line = in.readLine();
				String[] result = line.split(",");

				Data.getInstance().macAddress = result[0].replace('"', ' ').trim();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}