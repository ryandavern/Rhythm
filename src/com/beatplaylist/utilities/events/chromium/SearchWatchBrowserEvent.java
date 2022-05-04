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
import com.beatplaylist.utilities.data.StoredURL;
import com.teamdev.jxbrowser.js.JsAccessible;

import javafx.application.Platform;

public class SearchWatchBrowserEvent {

	public String url;

	public SearchWatchBrowserEvent(String url) {
		this.url = url;
	}

	@JsAccessible
	public void onVideoTimeChange() {
		GUIManager.getInstance().searchBrowser.getWebEngine().navigation().loadUrl(StoredURL.getInstance().lastBrowseURL);
		
		BrowserManager.getInstance().getCurrentBrowser().getWebEngine().navigation().loadUrl(this.url);
		GUIManager.getInstance().audioBar.audioBar.setLoading(true);
		Platform.runLater(() -> {
			GUIManager.getInstance().sideBar.sideBarTab.changeTab(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.NOW_PLAYING), "");
		});
	}
}