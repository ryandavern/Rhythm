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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.beatplaylist.Options;
import com.beatplaylist.utilities.filemanager.FileUtilities;
import com.beatplaylist.utilities.filemanager.OperatingSystem;
import com.install4j.api.context.UserCanceledException;
import com.install4j.api.launcher.ApplicationLauncher;
import com.install4j.api.launcher.SplashScreen;
import com.install4j.api.launcher.SplashScreen.ConnectionException;
import com.install4j.api.update.ApplicationDisplayMode;
import com.install4j.api.update.UpdateChecker;
import com.install4j.api.update.UpdateSchedule;
import com.install4j.api.update.UpdateScheduleRegistry;

public class UpdateManager {

	public static HashMap<String, UpdateCategory> updates = new HashMap<>();
	private static String date = "01/05/2022", client_version = "0.9.6";

	public static void addAllUpdates() {
		// New Feature
		addUpdate("", UpdateCategory.NEW_FEATURE);
		addUpdate("", UpdateCategory.NEW_FEATURE);
		addUpdate("", UpdateCategory.NEW_FEATURE);
		// addUpdate("", UpdateCategory.NEW_FEATURE);
		// addUpdate("", UpdateCategory.NEW_FEATURE);

		// Improvements
		addUpdate("", UpdateCategory.IMPROVEMENT);

		// Bug Fix
		addUpdate("Wallet connect page now displays correct earned $RHYTHM.", UpdateCategory.BUG_FIX);
		addUpdate("Browse page fixes.", UpdateCategory.BUG_FIX);
		addUpdate("'Claim Rewards' button has been disabled during testing.", UpdateCategory.BUG_FIX);

		// Coming Soon
		addUpdate("Rhythm Hub - Suggested Playlists", UpdateCategory.COMING_SOON);
		addUpdate("Verified NFT profile pictures", UpdateCategory.COMING_SOON);
		addUpdate("Cross-project playlist competitions", UpdateCategory.COMING_SOON);
		// addUpdate("", UpdateCategory.COMING_SOON);
		// addUpdate("Sync playlists created on your YouTube account to your BeatPlaylist account when you login.", UpdateCategory.COMING_SOON);
		// addUpdate("Sync playlists you follow on BeatPlaylist to your YouTube account.", UpdateCategory.COMING_SOON);
		// addUpdate("Add an option to disable playlist syncing in the playlist create popup.", UpdateCategory.COMING_SOON);
		// addUpdate("Group messaging.", UpdateCategory.COMING_SOON);
		// addUpdate("Post Q&A's and polls.", UpdateCategory.COMING_SOON);
		// addUpdate("Post multiple images.", UpdateCategory.COMING_SOON);
		// addUpdate("", "Highlight text.", UpdateCategory.COMING_SOON);
		// addUpdate("Link your Snapchat and Facebook account to your BeatPlaylist account.", UpdateCategory.COMING_SOON);
		// addUpdate("Import your Spotify and Apple Music playlists directly into your BeatPlaylist account.", UpdateCategory.COMING_SOON);
	}

	private static void addUpdate(String description, UpdateCategory category) {
		UpdateManager.updates.put(description, category);
	}

	public static void deleteUpdateFile() {
		if (StartupData.getInstance().server_version.equals(UpdateManager.client_version)) {
			// Delete auto-update file.
			String extension = ".exe";
			if (FileUtilities.getInstance().getOperatingSystem() == OperatingSystem.MACOS)
				extension = ".dmg";

			File file = new File(FileUtilities.getInstance().getAutoUpdateLocation() + "/update" + extension);

			if (file.exists())
				file.delete();
		}
	}

	public static void checkVersion() {
		if (Options.test_mode) {
			return;
		}
		try {
			UpdateScheduleRegistry.setUpdateSchedule(UpdateSchedule.ON_EVERY_START);
			if (UpdateChecker.getUpdateDescriptor("https://assets.beatplaylist.com/download/updates.xml", ApplicationDisplayMode.GUI).getPossibleUpdateEntry() != null && UpdateScheduleRegistry.checkAndReset()) {
				SplashScreen.hide();
				ApplicationLauncher.launchApplication("661", null, false, new ApplicationLauncher.Callback() {
					public void exited(int paramAnonymousInt) {
						System.exit(0);
					}

					public void prepareShutdown() {
						System.exit(0);
					}
				});
			}

		} catch (IOException | UserCanceledException | ConnectionException localIOException) {
			localIOException.printStackTrace();
		}
	}

	public static void updateJREVersion() {
		if (Options.test_mode)
			return;
		try {
			UpdateScheduleRegistry.setUpdateSchedule(UpdateSchedule.ON_EVERY_START);
			if (UpdateChecker.getUpdateDescriptor("https://assets.beatplaylist.com/download/updates.xml", ApplicationDisplayMode.GUI).getPossibleUpdateEntry() != null && UpdateScheduleRegistry.checkAndReset()) {
				ApplicationLauncher.launchApplication("661", null, false, new ApplicationLauncher.Callback() {
					public void exited(int exitCode) {
						System.exit(0);
					}

					public void prepareShutdown() {
						System.exit(0);
					}
				});
			}
		} catch (IOException | UserCanceledException localIOException) {
			localIOException.printStackTrace();
		}
	}

	public static String getClientVersion() {
		return client_version;
	}

	public static String getDate() {
		return date;
	}
}