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

package com.beatplaylist.gui.module.layout.sidebar;

import com.beatplaylist.utilities.user.AccountType;

public enum TabType {

	COMMUNITY_HUB("Hub", "feed", true, true, false, AccountType.DEFAULT), //
	INBOX("Inbox", "inbox", false, true, false, AccountType.DEFAULT), //
	BROWSE("Browse", "browse", true, true, false, AccountType.DEFAULT), //
	PLAYLISTS("Playlists", "playlists", true, true, false, AccountType.DEFAULT), //
	STORE("Store", "store", true, true, false, AccountType.DEVELOPER), //
	PROFILE("Profile", "", true, false, false, AccountType.DEFAULT), //
	SETTINGS("Settings", "", true, false, true, AccountType.DEFAULT), //
	NOTIFICATION("Notifications", "", true, false, false, AccountType.DEFAULT), //

	OPEN_PLAYLIST_VIEW("", "", true, false, false, AccountType.DEFAULT, TabType.PLAYLISTS), //
	NOW_PLAYING("", "", true, false, false, AccountType.DEFAULT), //
	PROFILE_PLAYLIST_VIEW("", "", true, false, false, AccountType.DEFAULT, TabType.PROFILE), //
	PROFILE_FOLLOWING_VIEW("", "", true, false, false, AccountType.DEFAULT, TabType.PROFILE), //
	PROFILE_FOLLOWER_VIEW("", "", true, false, false, AccountType.DEFAULT, TabType.PROFILE), //
	ALL_FEATURED_PLAYLIST_VIEW("", "", true, false, false, AccountType.DEFAULT, TabType.PLAYLISTS),
	BEATPLAYLIST_OFFLINE("", "", true, false, false, AccountType.DEFAULT),
	WALLET_CONNECT("", "", true, false, false, AccountType.DEFAULT);

	private String tabName, tab_icon_url;
	private boolean isEnabled, isSetting, isShownOnSideBar;
	private AccountType account_type;
	private TabType parentTab;

	TabType(String tabName, String tab_icon_url, boolean isEnabled, boolean isShownOnSideBar, boolean isSetting, AccountType account_type) {
		this.tabName = tabName;
		this.tab_icon_url = tab_icon_url;
		this.isEnabled = isEnabled;
		this.isSetting = isSetting;
		this.account_type = account_type;
		this.isShownOnSideBar = isShownOnSideBar;
		this.parentTab = this;
	}

	TabType(String tabName, String tab_icon_url, boolean isEnabled, boolean isShownOnSideBar, boolean isSetting, AccountType account_type, TabType parentTab) {
		this.tabName = tabName;
		this.tab_icon_url = tab_icon_url;
		this.isEnabled = isEnabled;
		this.isSetting = isSetting;
		this.account_type = account_type;
		this.isShownOnSideBar = isShownOnSideBar;
		this.parentTab = parentTab;
	}

	public String getIconURL() {
		return this.tab_icon_url;
	}

	public AccountType getAccountType() {
		return this.account_type;
	}

	public String getName() {
		return this.tabName;
	}

	public boolean isEnabled() {
		return this.isEnabled;
	}

	public boolean isSetting() {
		return this.isSetting;
	}

	public boolean isShownOnSideBar() {
		return this.isShownOnSideBar;
	}

	public TabType getParentTab() {
		return this.parentTab;
	}

	public TabType getTab() {
		return this;
	}
}