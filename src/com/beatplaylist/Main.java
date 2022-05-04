package com.beatplaylist;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.beatplaylist.chromium.BrowserManager;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.LoginGUIManager;
import com.beatplaylist.settings.Settings;
import com.beatplaylist.utilities.KeyboardHookListener;
import com.beatplaylist.utilities.filemanager.FileUtilities;
import com.beatplaylist.utilities.filemanager.OperatingSystem;
import com.beatplaylist.utilities.network.utilities.FindClosestServer;
import com.beatplaylist.utilities.playlist.LyricManager;
import com.beatplaylist.utilities.thread.ThreadManager;
import com.beatplaylist.utilities.update.StartupData;
import com.beatplaylist.utilities.update.UpdateManager;
import com.beatplaylist.utilities.user.UserManager;
import com.install4j.api.launcher.SplashScreen;

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

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

	// Example Age Restricted Video: https://www.youtube.com/watch?v=cqv25qFYKD0

	public static void main(String[] args) {
		System.setProperty("jxbrowser.license.key", "6P835FT5HB12IE4DGMN6PMC2FPT7LDFIVUW9ZJ4AL647WDZMEX23LNE9DDGOK4YEHB07");
		// Load user settings.
		Settings.getInstance().loadSettings();

		if (Settings.getInstance().hidpi()) {
			System.setProperty("prism.allowhidpi", "true");
			System.out.println("HiDPI Enabled");
		} else {
			System.setProperty("prism.allowhidpi", "false");
			System.out.println("HiDPI Disabled");
		}

		try {
			launch(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable t) {
				t.printStackTrace();
			}
		});
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				GUIManager.getInstance().stopApplication();
			}
		});
	}

	@Override
	public void start(Stage stage) {
		try {
			System.out.println("Starting Rhythm with process id: " + ProcessHandle.current().pid());
			ThreadManager.getInstance().initialize();
			// new Thread(() -> {
			// NETTYClient.getInstance().runWebSocketListener();
			// }).start();
			if (!Options.test_mode) {
				UpdateManager.checkVersion();
			}
			// Check for updates.
			StartupData.getInstance().getStartupData();
			// GetGlobalPermissions.send();
			if (FileUtilities.getInstance().getOperatingSystem() == OperatingSystem.WINDOWS) {
				File appData = FileUtilities.getInstance().getAppData();
				if (!new File(appData + "\\BeatPlaylist\\").exists())
					new File(appData + "\\BeatPlaylist\\").mkdir();
				if (!new File(appData + "\\BeatPlaylist\\logs\\").exists())
					new File(appData + "\\BeatPlaylist\\logs\\").mkdir();

				// Handle loggers.
				System.setProperty("jxbrowser.logging.level", "OFF");
				// System.setProperty("jxbrowser.logging.file", FileUtilities.getInstance().getAppData() + "\\BeatPlaylist\\logs\\jxbrowser-error.log");
				// LoggerProvider.setLevel(Level.OFF); // WARNING is another option that I could change to.
				com.teamdev.jxbrowser.logging.Logger.off();
				// Logger.getLogger(GlobalScreen.class.getPackage().getName()).setLevel(Level.OFF);

				if (!Options.test_mode)
					System.setErr(new PrintStream(new FileOutputStream(FileUtilities.getInstance().getAppData() + "\\BeatPlaylist\\logs\\system-error-log.txt")));

				// Hide splash screen from Install4J.
				SplashScreen.hide();

			}

			BrowserManager.getInstance().loadExternalJavascript();

			// Load update logs and delete any update files
			UpdateManager.addAllUpdates();
			UpdateManager.deleteUpdateFile();

			// Load user settings and custom lyrics.
			BrowserManager.getInstance().loadBaseJS("https://beatplaylist.sfo2.digitaloceanspaces.com/api/youtube/default.js");
			LyricManager.getInstance().loadCustomLyrics();

			if (FileUtilities.getInstance().getOperatingSystem() == OperatingSystem.WINDOWS) {
				if (Settings.getInstance().hasMediaKeysEnabled()) {
					// Enable keyboard listen events.
					try {
						org.jnativehook.GlobalScreen.registerNativeHook();
						org.jnativehook.GlobalScreen.addNativeKeyListener(new KeyboardHookListener());
					} catch (org.jnativehook.NativeHookException ex) {
						ex.printStackTrace();
						System.out.println("Global keyboard commands could not connect and were disabled.");
					}
				}
				String path = FileUtilities.getInstance().getAppData().getPath() + "\\Microsoft\\Windows\\Start Menu\\Programs\\Rhythm\\";
				if (new File(path).exists()) {
					if (new File(path + "MacOS Launcher.lnk").exists())
						new File(path + "MacOS Launcher.lnk").delete();
					if (new File(path + "Windows Launcher.lnk").exists())
						new File(path + "Windows Launcher.lnk").delete();
				}
			}

			// Detect server ip address and connect.
			FindClosestServer.getInstance().selectServer();

			// Load UserManager
			UserManager.getInstance().initializeLoginUser();

			// Load login screen.
			LoginGUIManager.getInstance().initializeStage();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		// GUIManager.getInstance().stopApplication();
	}

	private static void redirectLogMessagesToFile(Logger logger, String logFilePath) throws IOException {
		FileHandler fileHandler = new FileHandler(logFilePath);
		fileHandler.setFormatter(new SimpleFormatter());

		// Remove default handlers including console handler
		for (Handler handler : logger.getHandlers())
			logger.removeHandler(handler);
		logger.addHandler(fileHandler);
	}
}