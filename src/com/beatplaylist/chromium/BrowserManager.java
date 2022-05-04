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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.settings.Settings;
import com.beatplaylist.utilities.Utilities;
import com.beatplaylist.utilities.data.Data;
import com.beatplaylist.utilities.network.post.SendCurrentSong;
import com.beatplaylist.utilities.playlist.PlaylistManager;
import com.teamdev.jxbrowser.browser.Browser;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.util.Duration;

public class BrowserManager {

	private static BrowserManager instance = new BrowserManager();

	public static BrowserManager getInstance() {
		return instance;
	}

	// YouTube search page and watch page javascript
	public String baseJSFile = "", baseJSUrl = "", watchCSS = "", searchCSS = "", beatplaylistScripts = "", searchPageScripts = "", searchPageWatchScripts = "";
	public StringBuilder adblockJS;

	// Load custom javascript from server. We host the javascript on a server so we don't need to update the client application.
	public void loadExternalJavascript() {
		new Thread(() -> {
			loadURLToString("https://beatplaylist.sfo2.cdn.digitaloceanspaces.com/api/css/watch.css", true);
			loadURLToString("https://beatplaylist.sfo2.cdn.digitaloceanspaces.com/api/css/search.css", false);
			loadScript("https://beatplaylist.sfo2.cdn.digitaloceanspaces.com/api/scripts/beatplaylist.js", "VIDEO");
			loadScript("https://beatplaylist.sfo2.cdn.digitaloceanspaces.com/api/scripts/rhythmSearch.js", "SEARCH");
			// loadScript("https://beatplaylist.sfo2.cdn.digitaloceanspaces.com/api/scripts/rhythmSearchWatch.js", "SEARCHVIDEO");

			loadBaseJS("https://beatplaylist.sfo2.digitaloceanspaces.com/api/youtube/default.js");
		}).start();
	}

	private void loadURLToString(String url, boolean watch) {
		try {
			URLConnection connection = new URL(url).openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0");
			connection.connect();

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));

			String inputLine = "", json = "";
			while ((inputLine = in.readLine()) != null)
				json = json + inputLine;
			in.close();

			if (watch) {
				this.watchCSS = json;
			} else {
				this.searchCSS = json;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadScript(String url, String parentStr) {
		try {
			URLConnection connection = new URL(url).openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0");
			connection.connect();

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));

			String inputLine = "", json = "";
			while ((inputLine = in.readLine()) != null)
				json = json + inputLine;
			in.close();

			if (parentStr.equals("VIDEO"))
				this.beatplaylistScripts = json;
			else if (parentStr.equals("SEARCH"))
				this.searchPageScripts = json;
			else if (parentStr.equals("SEARCHVIDEO"))
				this.searchPageWatchScripts = json;

			System.out.println(parentStr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadBaseJS(String url) {
		if (this.baseJSUrl.equals(url)) {
			return;
		}
		this.baseJSUrl = url;

		new Thread(() -> {
			try {
				StringBuilder str = new StringBuilder();
				URLConnection connection = new URL(url).openConnection();
				connection.setRequestProperty("User-Agent", "Mozilla/5.0");
				connection.connect();

				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.defaultCharset()));

				String inputLine = "";
				while ((inputLine = in.readLine()) != null) {
					str.append(inputLine + "\n");
				}
				System.out.println("JS Loaded");
				Platform.runLater(() -> {
					this.baseJSFile = str.toString();
				});
				in.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	public String getVideoURL() {
		try {
			if (getCurrentBrowser().getWebEngine().mainFrame().isPresent()) {
				String url = getCurrentBrowser().getWebEngine().mainFrame().get().executeJavaScript("window.location.href");
				if (url == null || url.isEmpty() || url.equals("about:blank"))
					return "https://www.youtube.com/";
				return url;
			}
		} catch (Exception e) {
			return "";
		}
		return "";
	}

	public String getSearchURL() {
		try {
			if (GUIManager.getInstance().searchBrowser.getWebEngine().mainFrame().isPresent()) {
				String url = GUIManager.getInstance().searchBrowser.getWebEngine().mainFrame().get().executeJavaScript("window.location.href");
				if (url == null)
					return "";
				if (url.isEmpty() || url.equals("about:blank"))
					return "https://www.youtube.com/";
				else
					return url;
			}
		} catch (Exception e) {
			return "";
		}
		return "";
	}

	// Formats a youtube url and gets the ?watch= part of the url.
	public String getIndividualSongURL(String url) {
		// https://www.youtube.com/watch?v=AMCwYdTJ_PE&list=12345
		if (url.contains("&"))
			url = url.split("&")[0];
		return url.split("watch\\?v=")[1];
	}

	public void loadURL(Browser browser, String url) {
		double listenTime = getListenLengthSeconds();
		if (listenTime > 20) {
			SendCurrentSong.send(listenTime);
		}
		listenTime = 0;
		browser.navigation().loadUrl(url + "?vq=hd1080");
	}

	public void executeJavaScript(Browser browser, String toExecute) {
		new Thread(() -> {
			if (browser.mainFrame().isPresent()) {
				browser.mainFrame().get().executeJavaScript(toExecute);
			}
		}).start();
	}

	private double getListenLengthSeconds() {
		if (Data.getInstance().listenLength == -1)
			return 0;
		else {
			return Data.getInstance().listenLength;
		}
	}

	public VideoBrowser getCurrentBrowser() {
		return Data.getInstance().isFirstBrowser ? GUIManager.getInstance().videoBrowser : GUIManager.getInstance().songFadeBrowser;
	}

	public long lastFadeout = -1;

	public VideoBrowser getFadeoutBrowser() {
		if (!Data.getInstance().isFirstBrowser)
			return GUIManager.getInstance().videoBrowser;
		else
			return GUIManager.getInstance().songFadeBrowser;
	}

	public void handleFadeout() {
		BrowserManager.getInstance().lastFadeout = System.currentTimeMillis();
		final Animation animation = new Transition() {
			{
				setCycleDuration(Duration.seconds(Settings.getInstance().getCrossFadeLength() + 2));
			}

			@Override
			protected void interpolate(double value) {
				if (value == 0.9) {
					getFadeoutBrowser().getWebEngine().navigation().loadUrl("https://www.youtube.com/");
				}
				double newVolumeLevel = Data.getInstance().volumeLevel * (1.0 - value);
				if (newVolumeLevel < Data.getInstance().volumeLevel) {
					if (!Settings.getInstance().hasLoudnessEqualization()) {
						getCurrentBrowser().getWebEngine().mainFrame().get().executeJavaScript("document.getElementsByTagName('video')[0].volume = " + newVolumeLevel + ";");
					} else {
						getCurrentBrowser().getWebEngine().mainFrame().get().executeJavaScript("document.getElementById('movie_player').setVolume(" + (newVolumeLevel * 100) + ");");
					}
				}
			}
		};

		animation.play();
	}

	// In Development Fadeout when pause
	public void pauseSong() {
		final Animation animation = new Transition() {
			{
				setCycleDuration(Duration.seconds(1));
			}

			@Override
			protected void interpolate(double value) {
				if (value >= 0.99) {
					GUIManager.getInstance().audioBar.audioBar.pause_play.setState(true);
					YouTube.setVideoPauseState(true);

					// if (Utilities.getInstance().mediaPlayer != null && PlaylistManager.getInstance().current_playlist.isLocalPlaylist())
					// Utilities.getInstance().getMediaPlayer().pause();
					return;
				}
				double newVolumeLevel = Data.getInstance().volumeLevel * (1.0 - value);
				if (newVolumeLevel < Data.getInstance().volumeLevel) {
					if (!Settings.getInstance().hasLoudnessEqualization()) {
						getCurrentBrowser().getWebEngine().mainFrame().get().executeJavaScript("document.getElementsByTagName('video')[0].volume = " + newVolumeLevel + ";");
					} else {
						getCurrentBrowser().getWebEngine().mainFrame().get().executeJavaScript("document.getElementById('movie_player').setVolume(" + (newVolumeLevel * 100) + ");");
					}
				}
			}
		};

		animation.play();
	}
}