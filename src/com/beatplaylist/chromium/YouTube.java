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

package com.beatplaylist.chromium;

public class YouTube {

	public static boolean isPaused() {
		try {
			if (BrowserManager.getInstance().getVideoURL().contains("watch")) {
				boolean paused = BrowserManager.getInstance().getCurrentBrowser().getWebEngine().mainFrame().get().executeJavaScript("isPaused();");
				return paused;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	public static double getDuration() {
		if (BrowserManager.getInstance().getCurrentBrowser() == null || BrowserManager.getInstance().getCurrentBrowser().getWebEngine() == null || BrowserManager.getInstance().getCurrentBrowser().getWebEngine().mainFrame() == null || BrowserManager.getInstance().getCurrentBrowser().getWebEngine().mainFrame().get() == null) {
			return 0;
		}
		if (BrowserManager.getInstance().getVideoURL().contains("watch")) {
			try {
				double total_duration = BrowserManager.getInstance().getCurrentBrowser().getWebEngine().mainFrame().get().executeJavaScript("getTotalDuration();");
				// System.out.println("Total Duration: " + total_duration);
				return total_duration;

			} catch (NumberFormatException e) {
				return 0;
			}
		}
		return 0;
	}

	public static void setCurrentTime(double time) {
		// An issue caused by YouTube will cause a freeze issue with the song playing, when setting the currentTime after a certain amount of seconds.
		// A user must either pause / play the video to fix this issue or change the time again to fix it. The issue will occur again when a user changes the song time again.
		// A workaround is pausing then playing the video.
		BrowserManager.getInstance().getCurrentBrowser().getWebEngine().mainFrame().get().executeJavaScript("setCurrentTime(" + time + ");");
	}

	public static void setVideoPauseState(boolean pause) {
		if (BrowserManager.getInstance().getCurrentBrowser() != null && BrowserManager.getInstance().getVideoURL().contains("watch")) {
			if (pause)
				BrowserManager.getInstance().getCurrentBrowser().getWebEngine().mainFrame().get().executeJavaScript("if(document.getElementsByTagName('video')[0] != null)document.getElementsByTagName('video')[0].pause();");
			else
				BrowserManager.getInstance().getCurrentBrowser().getWebEngine().mainFrame().get().executeJavaScript("if(document.getElementsByTagName('video')[0] != null)document.getElementsByTagName('video')[0].play();");
		}
	}

	public static String getTitle() {
		if (BrowserManager.getInstance().getVideoURL().contains("watch")) {
			try {
				String title = BrowserManager.getInstance().getCurrentBrowser().getWebEngine().mainFrame().get().executeJavaScript("getTitle();");
				return title;
			} catch (Exception e) {
				e.printStackTrace();
				return "None";
			}
		}
		return "None";
	}

	// Format currentTime / totalDuration time.
	public static String getFormattedTime(int seconds) {
		int MINUTES_IN_AN_HOUR = 60;
		int SECONDS_IN_A_MINUTE = 60;

		int minutes = seconds / SECONDS_IN_A_MINUTE;
		seconds -= minutes * SECONDS_IN_A_MINUTE;

		int hours = minutes / MINUTES_IN_AN_HOUR;
		minutes -= hours * MINUTES_IN_AN_HOUR;

		if (hours > 0) {
			if (hours > 9)
				return "0" + hours + ":0" + minutes + ":" + seconds;
			else
				return hours + ":0" + minutes + ":" + seconds;

		} else if (minutes == 0) {
			if (seconds < 10)
				return "00:0" + seconds;
			else
				return "00:" + seconds;

		} else if (minutes > 0 && minutes < 60) {
			if (minutes < 10 && seconds < 10)
				return "0" + minutes + ":0" + seconds;
			else if (minutes < 10 && seconds >= 10)
				return "0" + minutes + ":" + seconds;
			else
				return minutes + ":" + seconds;

		} else {
			return minutes + ":" + seconds;
		}
	}
}