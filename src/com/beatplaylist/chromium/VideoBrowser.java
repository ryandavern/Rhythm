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

import com.beatplaylist.Options;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.settings.Settings;
import com.beatplaylist.utilities.data.Data;
import com.beatplaylist.utilities.data.StoredURL;
import com.beatplaylist.utilities.events.SongPlayEvent;
import com.beatplaylist.utilities.events.chromium.ChromiumVideoUpdate;
import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.browser.callback.InjectCssCallback;
import com.teamdev.jxbrowser.browser.callback.InjectJsCallback;
import com.teamdev.jxbrowser.browser.event.BrowserBecameUnresponsive;
import com.teamdev.jxbrowser.browser.event.FullScreenEntered;
import com.teamdev.jxbrowser.browser.event.FullScreenExited;
import com.teamdev.jxbrowser.cookie.Cookie;
import com.teamdev.jxbrowser.cookie.CookieStore;
import com.teamdev.jxbrowser.js.JsObject;
import com.teamdev.jxbrowser.navigation.event.FrameDocumentLoadFinished;
import com.teamdev.jxbrowser.profile.Profile;
import com.teamdev.jxbrowser.view.javafx.BrowserView;

import javafx.application.Platform;

public class VideoBrowser {

	public BrowserView web_view;
	public Browser web_engine;
	public boolean finishedFirstLoad = false;

	public FullscreenHandler fullscreenHandler;

	public void init() {
		this.web_engine = EngineBrowser.getInstance().engine.newBrowser();
		this.web_view = BrowserView.newInstance(this.web_engine);

		// Profile profile = EngineBrowser.getInstance().engine.profiles().defaultProfile();
		// CookieStore cookieStore = profile.cookieStore();

		// cookieStore.set(Cookie.newBuilder("https://www.youtube.com").name("PREF").value("f1=50000000&fms2=10000&fms1=10000&f6=8").path("/").build());
		// cookieStore.persist();

		// this.web_engine.setFullScreenHandler(new FullscreenHandler(this.web_view));
		this.fullscreenHandler = new FullscreenHandler(getWebView());

		configureBrowser();
	}

	private void configureBrowser() {
		// We unfortunately have to display the browser instance in the user interface to enable title loading properly. WebView is removed from the background pane after first page load.
		this.web_view.setMinSize(5, 5);
		this.web_view.setMaxSize(5, 5);
		// this.web_view.minWidthProperty().bind(GUIManager.getInstance().contentManager.contentPane.widthProperty().divide(1.8));
		// this.web_view.maxWidthProperty().bind(GUIManager.getInstance().contentManager.contentPane.widthProperty().divide(1.8));
		// this.web_view.minHeightProperty().bind(GUIManager.getInstance().contentManager.contentPane.heightProperty().divide(1.7));
		// this.web_view.maxHeightProperty().bind(GUIManager.getInstance().contentManager.contentPane.heightProperty().divide(1.7));

		this.web_engine.navigation().loadUrl("https://www.youtube.com/");

		GUIManager.getInstance().backgroundPane.getChildren().add(this.web_view);

		if (!Options.actAsDefaultBrowser)
			listen();
	}

	public boolean added = false;

	private void listen() {
		this.web_engine.set(InjectJsCallback.class, params -> {
			if (!BrowserManager.getInstance().baseJSFile.isEmpty())
				params.frame().executeJavaScript(BrowserManager.getInstance().baseJSFile);
			return InjectJsCallback.Response.proceed();
		});
		this.web_engine.set(InjectCssCallback.class, params -> {
			return InjectCssCallback.Response.inject(BrowserManager.getInstance().watchCSS);
		});
		this.web_engine.on(FullScreenEntered.class, event -> {
			this.fullscreenHandler.onFullScreenEnter();
		});
		this.web_engine.on(FullScreenExited.class, event -> {
			this.fullscreenHandler.onFullScreenExit();
		});
		this.web_engine.navigation().on(FrameDocumentLoadFinished.class, event -> {
			if (this.getWebEngine().url().isEmpty() || this.getWebEngine().url().equals("about:blank"))
				return;

			String url = BrowserManager.getInstance().getVideoURL();

			if (!url.contains("youtube.com")) {
				this.getWebEngine().navigation().loadUrl("https://youtube.com/");
			} else if (url.contains("?v=")) {
				StoredURL.getInstance().lastInjectedURL = url;

				JsObject window = this.getWebEngine().mainFrame().get().executeJavaScript("window");
				window.putProperty("beatplaylist", new ChromiumVideoUpdate());

				this.getWebEngine().mainFrame().get().executeJavaScript(BrowserManager.getInstance().beatplaylistScripts.replace("var customVolume = 0.00;", "var customVolume = " + Data.getInstance().volumeLevel + ";").replace("var volumeEqualization = false;", "var volumeEqualization = " + Settings.getInstance().hasLoudnessEqualization() + ";"));

				Platform.runLater(() -> {
					new SongPlayEvent();
				});
			}
		});
		this.web_engine.on(BrowserBecameUnresponsive.class, event -> {
			System.out.println("Unresponsive");
		});
	}

	public BrowserView getWebView() {
		return this.web_view;
	}

	public Browser getWebEngine() {
		return this.web_engine;
	}
}