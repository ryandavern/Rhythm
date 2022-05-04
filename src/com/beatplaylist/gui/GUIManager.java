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

package com.beatplaylist.gui;

import static com.sun.jna.platform.win32.WinUser.GWL_STYLE;

import java.awt.Dimension;
import java.awt.Toolkit;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import com.beatplaylist.Main;
import com.beatplaylist.chromium.EngineBrowser;
import com.beatplaylist.chromium.SearchBrowser;
import com.beatplaylist.chromium.VideoBrowser;
import com.beatplaylist.chromium.YouTube;
import com.beatplaylist.enums.SizeType;
import com.beatplaylist.gui.module.AudioBarManager;
import com.beatplaylist.gui.module.ContentManager;
import com.beatplaylist.gui.module.SideBarManager;
import com.beatplaylist.gui.module.TopBarManager;
import com.beatplaylist.gui.module.layout.sidebar.SideTab;
import com.beatplaylist.gui.module.layout.sidebar.TabType;
import com.beatplaylist.gui.module.page.settings.load_browsers_page;
import com.beatplaylist.settings.Settings;
import com.beatplaylist.utilities.Utilities;
import com.beatplaylist.utilities.control.ControlledTextArea;
import com.beatplaylist.utilities.control.ControlledTextField;
import com.beatplaylist.utilities.data.Data;
import com.beatplaylist.utilities.filemanager.FileUtilities;
import com.beatplaylist.utilities.filemanager.OperatingSystem;
import com.beatplaylist.utilities.network.get.GetAdblock;
import com.beatplaylist.utilities.network.get.GetUserLinkedSocialAccounts;
import com.beatplaylist.utilities.popup.Popup;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GUIManager {

	private static GUIManager instance = new GUIManager();

	public static GUIManager getInstance() {
		return instance;
	}

	public Pane backgroundPane;
	public Stage stage;

	public SideBarManager sideBar;
	public TopBarManager topBar;
	public ContentManager contentManager;
	public AudioBarManager audioBar;

	// Stores the current tab.
	public SideTab currentTab;

	public VideoBrowser videoBrowser, songFadeBrowser;
	public SearchBrowser searchBrowser;

	// Stores last movement of the application user interface. This is checked against popups to stop accidental closing of popups when a popup is open and a user moves the user interface.
	public long applicationLastMoved = System.currentTimeMillis();

	// Padding used in pages
	public double padding = 20, lastYPosition = -1;

	// Initialize Stage and Panes.
	// THIS METHOD SHOULD ONLY BE CALLED ON ONCE.
	public void initializeStage() {
		setInitialModalSize();
		sendLoginRequests();
		this.stage = new Stage();
		this.backgroundPane = new Pane();

		// Set stage scene, stage width and height
		this.stage.setScene(new Scene(this.backgroundPane));
		this.stage.setWidth(Data.getInstance().mainSize.getWidth());
		this.stage.setHeight(Data.getInstance().mainSize.getHeight());

		// Set stage properties
		this.stage.initStyle(StageStyle.UNDECORATED);
		this.stage.setTitle("Rhythm");
		this.stage.setResizable(false); // - We use a custom resize class.
		if (Settings.getInstance().launchMinimized())
			this.stage.setIconified(true);
		// Load module classes and add modal panes to background pane.
		// Modules must be loaded after the stage width and height values are set so the modals can calculate x,y,width and height correctly.
		loadModule();

		this.stage.getIcons().add(new Image(Main.class.getResource("/resources/logo.png").toExternalForm()));
		this.stage.getScene().getStylesheets().add(getClass().getResource("/resources/styles.css").toExternalForm());
		this.stage.show();
		this.stage.toFront();

		// this.stage.setOnHidden(e -> {
		// System.out.println("Forcing shutdown");
		// System.out.println(stage.isShowing());
		// System.exit(0);
		// });

		handleTaskBarMinimizeApplication();
		if (this.videoBrowser == null)
			this.videoBrowser = new VideoBrowser();

		EngineBrowser.getInstance().initializeOptions();

		new load_browsers_page(!EngineBrowser.getInstance().engineOptions.chromiumDir().toFile().exists());

		// if (UserManager.getInstance().user.accountType.isEqualOrLarger(AccountType.DEVELOPER)) {
		// if (Settings.getInstance().crossFadeSongEnabled()) {
		// if (this.songFadeBrowser == null)
		// this.songFadeBrowser = new VideoBrowser();
		// }
		// }
	}

	public void initEngine(boolean async) {
		if (async) {
			new Thread(() -> {
				if (EngineBrowser.getInstance().engine == null) {
					EngineBrowser.getInstance().initializeEngine();
				}
				Platform.runLater(() -> {
					GUIManager.getInstance().sideBar.sideBarTab.enableTabs();
					GUIManager.getInstance().sideBar.profileMenu.enableProfileMenu();
					finishEngineLoad();
				});
			}).start();
		} else {
			if (EngineBrowser.getInstance().engine == null) {
				EngineBrowser.getInstance().initializeEngine();
			}
			finishEngineLoad();
		}
	}

	private void finishEngineLoad() {
		if (this.videoBrowser.web_engine == null)
			this.videoBrowser.init();
		if (this.searchBrowser == null)
			this.searchBrowser = new SearchBrowser();

		this.searchBrowser.getWebEngine().navigation().loadUrl("https://www.youtube.com/feed/trending?bp=4gINGgt5dG1hX2NoYXJ0cw%3D%3D");

		this.backgroundPane.setOnKeyReleased(event -> {
			if (event.getCode() == KeyCode.SPACE) {
				if (event.getTarget() instanceof TextField || event.getTarget() instanceof TextArea || event.getTarget() instanceof com.beatplaylist.utilities.control.TextField || event.getTarget() instanceof ControlledTextArea || event.getTarget() instanceof ControlledTextField) {
					return;
				}
				if (this.audioBar.audioBar.pause_play.isPaused()) {
					this.audioBar.audioBar.pause_play.setState(false);
					YouTube.setVideoPauseState(false);
					// if (Utilities.getInstance().mediaPlayer != null && PlaylistManager.getInstance().current_playlist.isLocalPlaylist())
					// Utilities.getInstance().getMediaPlayer().play();
				} else {
					this.audioBar.audioBar.pause_play.setState(true);
					YouTube.setVideoPauseState(true);

					// if (Utilities.getInstance().mediaPlayer != null && PlaylistManager.getInstance().current_playlist.isLocalPlaylist())
					// Utilities.getInstance().getMediaPlayer().pause();
				}
			}
		});

		this.stage.setOnCloseRequest(event -> {
			event.consume();
			Popup.confirmCloseProgram();
		});
		UIResize.handleHeightResize(GUIManager.getInstance().stage.getHeight());
		GUIManager.getInstance().sideBar.sideBarTab.changeTab(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.PLAYLISTS));
		Popup.showPatchNotes();
		GetUserLinkedSocialAccounts.send();
	}

	// Load sidebar, audiobar, and content pane (content pane includes top bar).
	// THIS METHOD SHOULD ONLY BE CALLED ON ONCE.
	private void loadModule() {
		this.sideBar = new SideBarManager();
		this.topBar = new TopBarManager();
		this.audioBar = new AudioBarManager();
		this.contentManager = new ContentManager();

		this.sideBar.initializeSideBarPane();
		this.topBar.initializeTopBar();
		this.audioBar.initializeAudioBar();

		// ContentManager must be initialized last, otherwise will break as topbar must be initialized before.
		this.contentManager.initializeContentPane();

		// SideBar needs to get configured first.
		// SideBar has a 0, 0 start point. Width = 25% backgroundPane width, Height = 80% backgroundPane height.
		this.sideBar.configure();
		// Then audiobar needs to be configured.
		// AudioBar start point is x:0, y:side bar max-height value.
		this.audioBar.configure();

		// Configure the top bar.
		this.topBar.configure();

		// Configure contentmanager after sidebar so the x value can be calculated correctly (Calculated from side-bar).
		// contentWrapper start point is x:side-bar max-width value.
		// contentWrapper holds the topbar and inner content pane.

		this.contentManager.configure();

		handleBackgroundLayout();
		// Initialize Resize and UI click and drag.
		UIResize.getInstance().addResizeListener(false);

		if (!GUIManager.getInstance().backgroundPane.getChildren().contains(this.audioBar.getNowPlayingButton()))
			GUIManager.getInstance().backgroundPane.getChildren().add(this.audioBar.getNowPlayingButton());

		this.backgroundPane.setOnMouseClicked(event -> {
			this.lastYPosition = this.stage.getY();
		});
		this.backgroundPane.setOnMouseReleased(event -> {
			if (this.lastYPosition != this.stage.getY() && this.stage.getY() <= 0) {
				this.topBar.titleBar.fullscreen();
			}
		});
	}

	// THIS METHOD SHOULD ONLY BE CALLED ON ONCE.
	private void handleBackgroundLayout() {
		// Clear background pane in-case of duplicates.
		this.backgroundPane.getChildren().clear();

		// Add contents to background pane.
		this.backgroundPane.getChildren().addAll(this.sideBar.getSideBarPane(), this.audioBar.getAudioBarPane(), this.contentManager.getContentWrapper());
	}

	// Send login requests.
	private void sendLoginRequests() {
		GetAdblock.send();
	}

	// Set stage width / height variables based on the users screen size.
	private void setInitialModalSize() {
		double size = getScreenInInches();
		if (size < 13)
			Data.getInstance().mainSize = SizeType.SIZE_TYPESMALLEST;
		// 13 inch & 14 inch
		else if (size >= 13 && size < 17)
			Data.getInstance().mainSize = SizeType.SIZE_TYPE1200X620;
		// 15 inch
		else if (size > 17 && size < 19)
			Data.getInstance().mainSize = SizeType.SIZE_TYPE1280X620;
		// 16-19 inch
		else if (size >= 19 && size < 22)
			Data.getInstance().mainSize = SizeType.SIZE_TYPE1280X720;
		// 19-24 inch
		else if (size >= 22 && size <= 24)
			Data.getInstance().mainSize = SizeType.SIZE_TYPE1620X720;
		// 25-29 inch
		else if (size > 24 && size <= 29)
			Data.getInstance().mainSize = SizeType.SIZE_TYPE1620X720; // SIZE_TYPE1920X1080
		// > 29 inch
		else if (size > 29)
			Data.getInstance().mainSize = SizeType.SIZE_TYPE1620X720; // SIZE_TYPE2920X1080
		else
			Data.getInstance().mainSize = SizeType.SIZE_TYPE1620X720;

		// Data.getInstance().mainSize = SizeType.SIZE_TYPE1200X620;
	}

	private double getScreenInInches() {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int pixelPerInch = Toolkit.getDefaultToolkit().getScreenResolution();
		double height = screen.getHeight() / pixelPerInch;
		double width = screen.getWidth() / pixelPerInch;
		double x = Math.pow(height, 2);
		double y = Math.pow(width, 2);
		double diagonal = Math.sqrt(x + y);
		return diagonal;
	}

	public void handleTaskBarMinimizeApplication() {
		// Sourced from http://choudhury.com/blog/2017/02/23/javafx-undecorated-window-task-icon-minimize/.

		this.getStage().iconifiedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.booleanValue()) {
				getStage().setWidth(SceneUtilities.getInstance().current_width);
				getStage().setHeight(SceneUtilities.getInstance().current_height);
				getStage().setIconified(false);
				if (!getStage().isShowing())
					getStage().show();
				else
					getStage().requestFocus();
			}
		});

		if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
			try {
				WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, "Rhythm");
				final User32 user32 = User32.INSTANCE;
				int oldStyle = user32.GetWindowLong(hwnd, GWL_STYLE);
				int newStyle = oldStyle | 0x00021000;
				user32.SetWindowLong(hwnd, GWL_STYLE, newStyle);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Stage getStage() {
		// If stage is null. The only other stage that should exist is the login page stage.
		if (this.stage == null)
			return LoginGUIManager.getInstance().stage;
		return this.stage;
	}

	public Pane getPane() {
		// If background pane is null. The only other pane that should exist is the login page pane.
		if (this.backgroundPane == null)
			return LoginGUIManager.getInstance().backgroundPane;
		return this.backgroundPane;
	}

	// Called from UserManager class when a user logs out
	public void logout() {
		if (Utilities.getInstance().mediaPlayer != null) {
			Utilities.getInstance().mediaPlayer.stop();
			Utilities.getInstance().mediaPlayer.dispose();
			Utilities.getInstance().mediaPlayer = null;
		}
		if (GUIManager.getInstance().videoBrowser != null) {
			GUIManager.getInstance().videoBrowser.getWebEngine().navigation().loadUrl("https://youtube.com/");
		}
		if (FileUtilities.getInstance().getOperatingSystem() == OperatingSystem.WINDOWS) {
			try {
				if (GlobalScreen.isNativeHookRegistered())
					GlobalScreen.unregisterNativeHook();
			} catch (NativeHookException e) {
				e.printStackTrace();
			}
		}
		if (this.stage != null)
			this.stage.close();

		this.sideBar = null;
		this.topBar = null;
		this.contentManager = null;
		this.audioBar = null;
		this.currentTab = null;
		this.backgroundPane = null;
		this.stage = null;
	}

	public void stopApplication() {
		System.out.println("Shutting down...");
		if (GUIManager.getInstance().stage != null)
			GUIManager.getInstance().stage.close();
		GUIManager.getInstance().stage = null;
		Utilities.getInstance().closeServerSocket(Utilities.getInstance().incoming_socket);

		if (Utilities.getInstance().mediaPlayer != null) {
			Utilities.getInstance().mediaPlayer.stop();
			Utilities.getInstance().mediaPlayer.dispose();
			Utilities.getInstance().mediaPlayer = null;
		}
		if (GUIManager.getInstance().videoBrowser != null) {

			if (FileUtilities.getInstance().getOperatingSystem() == OperatingSystem.MACOS) {
				Platform.runLater(() -> {
					disposeBrowsers();
				});
			} else {
				new Thread(() -> {
					disposeBrowsers();
				});
			}
		}
		if (FileUtilities.getInstance().getOperatingSystem() == OperatingSystem.WINDOWS) {
			try {
				if (GlobalScreen.isNativeHookRegistered())
					GlobalScreen.unregisterNativeHook();
			} catch (NativeHookException e) {
				e.printStackTrace();
			}
		}

		System.exit(0);
		Runtime.getRuntime().exit(0);
	}

	private void disposeBrowsers() {
		GUIManager.getInstance().videoBrowser.getWebEngine().audio().mute();
		GUIManager.getInstance().videoBrowser.getWebEngine().close();
		GUIManager.getInstance().searchBrowser.getWebEngine().close();
		EngineBrowser.getInstance().engine.close();
		GUIManager.getInstance().videoBrowser = null;
		GUIManager.getInstance().searchBrowser = null;
		// BrowserCore.shutdown();
	}
}