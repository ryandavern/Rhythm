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

package com.beatplaylist.utilities.events.chromium;

import com.beatplaylist.chromium.BrowserManager;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.module.layout.sidebar.TabType;
import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;
import com.beatplaylist.utilities.playlist.PlaylistManager;
import com.teamdev.jxbrowser.js.JsAccessible;

import javafx.application.Platform;

public class SearchBrowserEvent {

	public SearchBrowserEvent() {
	}

	@JsAccessible
	public void loadSong(String url) {
		System.out.println("SEARCH LOAD CAUGHT: " + url);
		if (url.contains("/playlist")) {
			Platform.runLater(() -> {
				GUIManager.getInstance().sideBar.sideBarTab.changeTab(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.OPEN_PLAYLIST_VIEW), url);
			});
			return;
		}
		PlaylistManager.getInstance().current_playlist = null;
		// if (url.contains("&list"))
		// BrowserManager.getInstance().getCurrentBrowser().getWebEngine().navigation().loadUrl(url.split("&list")[0]);
		// else
		Platform.runLater(() -> {
			Notification.getInstance().createNotification("Song", "Song is now playing!", AlertType.SUCCESS);
		});
		BrowserManager.getInstance().getCurrentBrowser().getWebEngine().navigation().loadUrl(url);
		GUIManager.getInstance().audioBar.audioBar.setLoading(true);
	}

	@JsAccessible
	public void printURL(String url) {
		System.out.println("load click " + url);
	}

	@JsAccessible
	public void onAfterPageLoad() {
		System.out.println("INSERTED AFTER LOAD");
	}
}