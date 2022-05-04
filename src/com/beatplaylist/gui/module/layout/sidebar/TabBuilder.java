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

import java.util.ArrayList;
import java.util.List;

import com.beatplaylist.chromium.BrowserManager;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.module.page.store_page;
import com.beatplaylist.gui.module.page.music.browse_page;
import com.beatplaylist.gui.module.page.music.featured_playlist_page;
import com.beatplaylist.gui.module.page.music.hub_page;
import com.beatplaylist.gui.module.page.music.now_playing_page;
import com.beatplaylist.gui.module.page.music.playlist_page;
import com.beatplaylist.gui.module.page.profile.profile_following_page;
import com.beatplaylist.gui.module.page.profile.profile_page;
import com.beatplaylist.gui.module.page.profile.profile_playlist_page;
import com.beatplaylist.gui.module.page.settings.general_settings_page;
import com.beatplaylist.gui.module.page.settings.server_offline_page;
import com.beatplaylist.gui.module.page.settings.wallet_connect;
import com.beatplaylist.gui.utilities.playlist.PlaylistLoader;
import com.beatplaylist.utilities.playlist.Playlist;
import com.beatplaylist.utilities.user.BaseUser;
import com.beatplaylist.utilities.user.UserManager;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

// Class handles the sidebar tabs.
public class TabBuilder {

	// Playlist scrollpane and vbox contain recently played playlists.
	public ScrollPane playlist_scrollpane;
	public VBox sidebar_vbox, playlist_vbox;

	public boolean gcError = false; // Does jcmd exist?
	public long pid = -1, gcCalls = 0; // Application process id

	// All tabs are stored in list.
	public List<SideTab> tabs = new ArrayList<>();

	public TabBuilder() {
		this.playlist_scrollpane = new ScrollPane() {
			public void requestFocus() {

			}
		};

		this.sidebar_vbox = new VBox(10);
		this.playlist_vbox = new VBox(3);
	}

	private void clearGC() {
		// boolean disableGC = true;
		// if (this.gcError || disableGC) {
		// return;
		// }
		// this.gcCalls++;
		// if (this.gcCalls <= 3) {
		// return;
		// }
		// if (this.pid == -1) {
		// this.pid = ProcessHandle.current().pid();
		// System.out.println("PID: " + this.pid);
		// }
		// try {
		// if (Options.test_mode) {
		// Runtime.getRuntime().exec("jcmd " + this.pid + " GC.run");
		// } else {
		// Runtime.getRuntime().exec(System.getProperty("install4j.appDir") + "//jre//bin//jcmd " + pid + " GC.run");
		// }
		// } catch (IOException e) {
		// this.gcError = true;
		// e.printStackTrace();
		// }
	}

	public void makeTab() {
		this.sidebar_vbox.setPadding(new Insets(this.sidebar_vbox.getPadding().getTop() + 15, this.sidebar_vbox.getPadding().getRight(), this.sidebar_vbox.getPadding().getBottom(), this.sidebar_vbox.getPadding().getLeft() + 45));
		this.sidebar_vbox.setStyle("-fx-background-color: linear-gradient(to top right, #3a3a3a 0%, #211f20 100%);");

		// Bind sidebar to sidebar pane.
		this.sidebar_vbox.minHeightProperty().bind(GUIManager.getInstance().sideBar.sidebarPane.heightProperty().multiply(0.85));
		this.sidebar_vbox.minWidthProperty().bind(GUIManager.getInstance().sideBar.sidebarPane.widthProperty());
		this.sidebar_vbox.maxWidthProperty().bind(GUIManager.getInstance().sideBar.sidebarPane.widthProperty());

		for (TabType tabType : TabType.values()) {
			// if (tabType.isSetting() || !AccountType.isEqualOrLarger(tabType.getAccountType()))
			// continue;

			SideTab tab = new SideTab(tabType);

			this.tabs.add(tab);
			if (tabType.isSetting() || !tabType.isShownOnSideBar())
				continue;
			this.sidebar_vbox.getChildren().add(tab.getTabHBox());
		}
		// PlaylistBuilder.getInstance().displayPlaylists(true);
		// this.sidebar_vbox.getChildren().add(this.playlist_scrollpane);

		GUIManager.getInstance().sideBar.sidebarPane.getChildren().add(this.sidebar_vbox);
	}

	public void disableTabs() {
		for (SideTab tabs : this.tabs) {
			tabs.disableTab();
		}
	}

	public void enableTabs() {
		for (SideTab tabs : this.tabs) {
			tabs.enableTab();
		}
	}

	// Get SideTab class.
	public SideTab getTab(TabType tab) {
		for (SideTab tabs : this.tabs) {
			if (tabs.tab == tab.getTab()) {
				return tabs;
			}
		}
		return null;
	}

	public void changeTab(SideTab sideTab, String... args) {
		// Requests focus away from the web-browsers. For some reason clicking on a web browser and changing page will stop textfields from being usable.
		if (GUIManager.getInstance().backgroundPane.getChildren().contains(GUIManager.getInstance().videoBrowser.web_view))
			GUIManager.getInstance().videoBrowser.web_view.requestFocus();
		GUIManager.getInstance().topBar.titleBar.searchTextfield.requestFocus();

		// Clear content pane.
		GUIManager.getInstance().contentManager.contentPane.getChildren().clear();

		// Re-adjust search bar if previous page was the browse page.
		if (GUIManager.getInstance().currentTab != null && GUIManager.getInstance().currentTab.tab == TabType.BROWSE) {
			GUIManager.getInstance().topBar.adjustToNormalPageHeight();
			GUIManager.getInstance().topBar.titleBar.updateSearchTextFieldKeyPressListener();
		}

		if (BrowserManager.getInstance().getCurrentBrowser().web_view.getMinWidth() != 5) {
			BrowserManager.getInstance().getCurrentBrowser().web_view.minWidthProperty().unbind();
			BrowserManager.getInstance().getCurrentBrowser().web_view.maxWidthProperty().unbind();
			BrowserManager.getInstance().getCurrentBrowser().web_view.minHeightProperty().unbind();
			BrowserManager.getInstance().getCurrentBrowser().web_view.maxHeightProperty().unbind();
			BrowserManager.getInstance().getCurrentBrowser().web_view.setMinSize(5, 5);
			BrowserManager.getInstance().getCurrentBrowser().web_view.setMaxSize(5, 5);
		}

		// Handle tab change style updates.
		sideTab.selectTab();
		// Clear search bar if not browse page.
		if (sideTab.tab != TabType.BROWSE) {
			GUIManager.getInstance().topBar.titleBar.searchTextfield.setText("");
		}

		// Reset UserManager profileUser if loaded page isn't a profile page.
		if (sideTab.tab != TabType.PROFILE) {
			if (UserManager.getInstance().profileUser != null)
				UserManager.getInstance().profileUser = null;
		}

		// Add now playing button to audiobar unless loaded page is now playing page.
		if (!GUIManager.getInstance().backgroundPane.getChildren().contains(GUIManager.getInstance().audioBar.getNowPlayingButton()) && sideTab.tab != TabType.NOW_PLAYING) {
			GUIManager.getInstance().backgroundPane.getChildren().add(GUIManager.getInstance().audioBar.getNowPlayingButton());
		}
		switch (sideTab.tab) {
		case BROWSE:
			new browse_page(args);
			break;
		case COMMUNITY_HUB:
			new hub_page();
			break;
		case INBOX:
			break;
		case NOTIFICATION:
			break;
		case NOW_PLAYING:
			new now_playing_page();
			break;
		case OPEN_PLAYLIST_VIEW:
			PlaylistLoader.getInstance().loadPlaylist(args[0]);
			break;
		case PLAYLISTS:
			new playlist_page();
			break;
		case PROFILE:
			new profile_page();
			break;
		case PROFILE_PLAYLIST_VIEW:
			new profile_playlist_page(args[0]);
			break;
		case SETTINGS:
			new general_settings_page();
			break;
		case STORE:
			new store_page().run();
			break;
		case ALL_FEATURED_PLAYLIST_VIEW:
			new featured_playlist_page();
			break;
		case BEATPLAYLIST_OFFLINE:
			new server_offline_page();
			break;
		case WALLET_CONNECT:
			new wallet_connect();
			break;
		}

		// clearGC();
	}

	public void changeTab(SideTab sideTab, BaseUser baseUser) {
		// Requests focus away from the web-browsers. For some reason clicking on a web browser and changing page will stop textfields from being usable.
		if (GUIManager.getInstance().backgroundPane.getChildren().contains(GUIManager.getInstance().videoBrowser.web_view))
			GUIManager.getInstance().videoBrowser.web_view.requestFocus();
		GUIManager.getInstance().topBar.titleBar.searchTextfield.requestFocus();

		// Clear content pane.
		GUIManager.getInstance().contentManager.contentPane.getChildren().clear();

		// Re-adjust search bar if previous page was the browse page.
		if (GUIManager.getInstance().currentTab != null && GUIManager.getInstance().currentTab.tab == TabType.BROWSE) {
			GUIManager.getInstance().topBar.adjustToNormalPageHeight();
			GUIManager.getInstance().topBar.titleBar.updateSearchTextFieldKeyPressListener();
		}

		if (BrowserManager.getInstance().getCurrentBrowser().web_view.getMinWidth() != 5) {
			BrowserManager.getInstance().getCurrentBrowser().web_view.minWidthProperty().unbind();
			BrowserManager.getInstance().getCurrentBrowser().web_view.maxWidthProperty().unbind();
			BrowserManager.getInstance().getCurrentBrowser().web_view.minHeightProperty().unbind();
			BrowserManager.getInstance().getCurrentBrowser().web_view.maxHeightProperty().unbind();
			BrowserManager.getInstance().getCurrentBrowser().web_view.setMinSize(5, 5);
			BrowserManager.getInstance().getCurrentBrowser().web_view.setMaxSize(5, 5);
		}

		// Handle tab change style updates.
		sideTab.selectTab();
		// Clear search bar if not browse page.
		if (sideTab.tab != TabType.BROWSE) {
			GUIManager.getInstance().topBar.titleBar.searchTextfield.setText("");
		}

		// Reset UserManager profileUser if loaded page isn't a profile page.
		if (sideTab.tab != TabType.PROFILE) {
			if (UserManager.getInstance().profileUser != null)
				UserManager.getInstance().profileUser = null;
		}

		// Add now playing button to audiobar unless loaded page is now playing page.
		if (!GUIManager.getInstance().backgroundPane.getChildren().contains(GUIManager.getInstance().audioBar.getNowPlayingButton()) && sideTab.tab != TabType.NOW_PLAYING) {
			GUIManager.getInstance().backgroundPane.getChildren().add(GUIManager.getInstance().audioBar.getNowPlayingButton());
		}
		switch (sideTab.tab) {
		case PROFILE_FOLLOWING_VIEW:
			new profile_following_page(baseUser, true);
			break;
		case PROFILE_FOLLOWER_VIEW:
			new profile_following_page(baseUser, false);
			break;
		}
	}

	// Load mix or when a user calls back to playlist button on audiobar
	public void loadMix(SideTab sideTab, Playlist playlist) {
		if (GUIManager.getInstance().backgroundPane.getChildren().contains(GUIManager.getInstance().videoBrowser.web_view))
			GUIManager.getInstance().videoBrowser.web_view.requestFocus();
		GUIManager.getInstance().topBar.titleBar.searchTextfield.requestFocus();

		// Clear content pane.
		GUIManager.getInstance().contentManager.contentPane.getChildren().clear();

		// Re-adjust search bar if previous page was the browse page.
		if (GUIManager.getInstance().currentTab != null && GUIManager.getInstance().currentTab.tab == TabType.BROWSE) {
			GUIManager.getInstance().topBar.adjustToNormalPageHeight();
			GUIManager.getInstance().topBar.titleBar.updateSearchTextFieldKeyPressListener();
		}

		if (BrowserManager.getInstance().getCurrentBrowser().web_view.getMinWidth() != 5) {
			BrowserManager.getInstance().getCurrentBrowser().web_view.minWidthProperty().unbind();
			BrowserManager.getInstance().getCurrentBrowser().web_view.maxWidthProperty().unbind();
			BrowserManager.getInstance().getCurrentBrowser().web_view.minHeightProperty().unbind();
			BrowserManager.getInstance().getCurrentBrowser().web_view.maxHeightProperty().unbind();
			BrowserManager.getInstance().getCurrentBrowser().web_view.setMinSize(5, 5);
			BrowserManager.getInstance().getCurrentBrowser().web_view.setMaxSize(5, 5);
		}

		// Handle tab change style updates.
		sideTab.selectTab();
		// Clear search bar if not browse page.
		if (sideTab.tab != TabType.BROWSE) {
			GUIManager.getInstance().topBar.titleBar.searchTextfield.setText("");
		}

		// Reset UserManager profileUser if loaded page isn't a profile page.
		if (sideTab.tab != TabType.PROFILE) {
			if (UserManager.getInstance().profileUser != null)
				UserManager.getInstance().profileUser = null;
		}

		// Add now playing button to audiobar unless loaded page is now playing page.
		if (!GUIManager.getInstance().backgroundPane.getChildren().contains(GUIManager.getInstance().audioBar.getNowPlayingButton()) && sideTab.tab != TabType.NOW_PLAYING) {
			GUIManager.getInstance().backgroundPane.getChildren().add(GUIManager.getInstance().audioBar.getNowPlayingButton());
		}

		PlaylistLoader.getInstance().loadMix(playlist);
	}
}