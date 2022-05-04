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
import com.beatplaylist.gui.module.layout.sidebar.TabType;
import com.beatplaylist.gui.utilities.playlist.SongTitle;
import com.beatplaylist.utilities.data.StoredURL;
import com.beatplaylist.utilities.events.chromium.BrowserAddToPlaylistEvent;
import com.beatplaylist.utilities.events.chromium.SearchBrowserEvent;
import com.beatplaylist.utilities.events.chromium.SearchWatchBrowserEvent;
import com.beatplaylist.utilities.network.post.LinkGoogleAccount;
import com.beatplaylist.utilities.network.post.LinkSocialNetworkingAccount;
import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.browser.callback.InjectCssCallback;
import com.teamdev.jxbrowser.browser.callback.InjectJsCallback;
import com.teamdev.jxbrowser.browser.event.BrowserBecameUnresponsive;
import com.teamdev.jxbrowser.browser.event.TitleChanged;
import com.teamdev.jxbrowser.js.JsObject;
import com.teamdev.jxbrowser.navigation.event.FrameDocumentLoadFinished;
import com.teamdev.jxbrowser.navigation.event.NavigationFinished;
import com.teamdev.jxbrowser.view.javafx.BrowserView;

import javafx.application.Platform;

// Displayed on the Browse Page.
public class SearchBrowser {

	public BrowserView web_view;
	public Browser web_engine;

	public SearchBrowser() {
		this.web_engine = EngineBrowser.getInstance().engine.newBrowser();
		this.web_view = BrowserView.newInstance(this.web_engine);

		this.web_engine.audio().mute();

		// Profile profile = EngineBrowser.getInstance().engine.profiles().defaultProfile();
		// CookieStore cookieStore = profile.cookieStore();

		// cookieStore.set(Cookie.newBuilder("https://www.youtube.com").name("PREF").value("f1=50000000&fms2=10000&fms1=10000&f6=8").path("/").build());
		// cookieStore.persist();

		configureBrowser();
	}

	public void configureBrowser() {
		// https://www.w3schools.com/jsref/prop_nav_useragent.asp
		// this.web_engine.setUserAgent("Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Mobile Safari/537.36");

		// this.web_view.setMinWidth(600);
		// this.web_view.setMinHeight(550);
		this.web_view.setMinWidth(1000);
		this.web_view.setMinHeight(550);
		if (!Options.actAsDefaultBrowser)
			listen();
	}

	private void listen() {
		this.web_engine.set(InjectJsCallback.class, params -> {
			if (!BrowserManager.getInstance().baseJSFile.isEmpty())
				params.frame().executeJavaScript(BrowserManager.getInstance().baseJSFile);

			return com.teamdev.jxbrowser.browser.callback.InjectJsCallback.Response.proceed();
		});
		this.web_engine.set(InjectCssCallback.class, params -> {
			return InjectCssCallback.Response.inject(BrowserManager.getInstance().searchCSS);
		});
		this.web_engine.navigation().on(FrameDocumentLoadFinished.class, event -> {
			if (this.getWebEngine().url().isEmpty() || this.getWebEngine().url().equals("about:blank"))
				return;

			String url = BrowserManager.getInstance().getSearchURL();
			if (url.contains("/results") || url.contains("/feed") || url.contains("/c/") || url.contains("/user")) {
				System.out.println("Injected Search JS");
				JsObject window = this.getWebEngine().mainFrame().get().executeJavaScript("window");
				window.putProperty("popup", new BrowserAddToPlaylistEvent());
				window.putProperty("searchBrowser", new SearchBrowserEvent());
				if (!BrowserManager.getInstance().searchPageScripts.isEmpty())
					getWebEngine().mainFrame().get().executeJavaScript(BrowserManager.getInstance().searchPageScripts);
			}
			if (url.contains("watch")) {
				BrowserManager.getInstance().getCurrentBrowser().getWebEngine().navigation().loadUrl(url);
				GUIManager.getInstance().searchBrowser.getWebEngine().navigation().loadUrl(StoredURL.getInstance().lastBrowseURL);
				GUIManager.getInstance().audioBar.audioBar.setLoading(true);

				if (!StoredURL.getInstance().lastSearchInjectedURL.equals(url)) {
					StoredURL.getInstance().lastSearchInjectedURL = url;

					JsObject window = this.getWebEngine().mainFrame().get().executeJavaScript("window");
					window.putProperty("beatplaylist", new SearchWatchBrowserEvent(url));

					this.getWebEngine().mainFrame().get().executeJavaScript(BrowserManager.getInstance().searchPageScripts);
				}
				return;
			}

			if (!url.contains("?v="))
				StoredURL.getInstance().lastBrowseURL = url;

			if (StoredURL.getInstance().currentSearchURL.equals(url)) {
				double arraySize = this.getWebEngine().mainFrame().get().executeJavaScript("document.getElementsByClassName('play-song-button').length;");
				if (arraySize > 0) {
					return;
				}
			}

			StoredURL.getInstance().currentSearchURL = url;

			if (!url.startsWith("https://www.youtube.com/") && !url.startsWith("https://accounts.google.com/") && !url.startsWith("https://beatplaylist.com/"))
				this.getWebEngine().navigation().loadUrl("https://www.youtube.com/");
			else {
				if (!url.contains("watch")) {

					// if (url.contains("/user/") && !url.contains("/videos"))
					// this.getWebEngine().loadURL(url + "/videos");
					if (url.contains("/playlist?")) {
						if (GUIManager.getInstance().topBar.titleBar.isYouTubePlaylist(url)) {
							Platform.runLater(() -> {
								GUIManager.getInstance().sideBar.sideBarTab.changeTab(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.OPEN_PLAYLIST_VIEW), url);
							});
							getWebEngine().navigation().goBack();
						}
					}
					if (url.contains("/results") || url.contains("/feed") || url.contains("/c/") || url.contains("/user")) {
						double arraySize = this.getWebEngine().mainFrame().get().executeJavaScript("document.querySelectorAll('[id=video-title]').length;");

						this.getWebEngine().mainFrame().get().executeJavaScript("document.getElementsByClassName('add-to-playlist-button').innerHTML='';");
						this.getWebEngine().mainFrame().get().executeJavaScript("document.getElementsByClassName('customElement').innerHTML='';");

						for (int i = 0; i < arraySize; i++) {
							this.getWebEngine().mainFrame().get().executeJavaScript("var div" + i + " = document.createElement('div');" + //
									"div" + i + ".className = 'customElement';" + //
									"div" + i + ".style.float = 'left';" + //
									"div" + i + ".style.marginTop = '5px';" + //
									"document.getElementsByClassName('text-wrapper style-scope ytd-video-renderer')[" + i + "].appendChild(div" + i + ");");

							this.getWebEngine().mainFrame().get().executeJavaScript("var playButton" + i + " = document.createElement('button');" + //
									"playButton" + i + ".innerHTML = 'Play';" + //
									"playButton" + i + ".style.backgroundColor = '#262626';" + //
									"playButton" + i + ".style.color = '#FFFFFF';" + //
									"playButton" + i + ".style.border = 'none';" + //
									"playButton" + i + ".style.borderRadius = '5px';" + //
									"playButton" + i + ".style.padding = '5px';" + //
									"playButton" + i + ".style.cursor = 'pointer';" + //
									"playButton" + i + ".style.width = '150px';" + //
									"playButton" + i + ".style.outline = 'none';" + //
									"playButton" + i + ".style.marginRight = '10px';" + //
									"playButton" + i + ".className = 'play-song-button';" + //
									"playButton" + i + ".addEventListener('mouseenter', function() {playButton" + i + ".style.color = '#2ECC71';}, false);" + //
									"playButton" + i + ".addEventListener('mouseleave', function() {playButton" + i + ".style.color = '#FFFFFF';}, false);" + //
									"playButton" + i + ".addEventListener('click', function() {window.popup.playSong(document.querySelectorAll('[id=video-title]')[" + i + "].href);}, false);" + //
									"document.getElementsByClassName('customElement')[" + i + "].appendChild(playButton" + i + ");");

							this.getWebEngine().mainFrame().get().executeJavaScript("var button" + i + " = document.createElement('button');" + //
									"button" + i + ".innerHTML = 'Add to Playlist';" + //
									"button" + i + ".style.backgroundColor = '#262626';" + //
									"button" + i + ".style.color = '#FFFFFF';" + //
									"button" + i + ".style.border = 'none';" + //
									"button" + i + ".style.borderRadius = '5px';" + //
									"button" + i + ".style.padding = '5px';" + //
									"button" + i + ".style.cursor = 'pointer';" + //
									"button" + i + ".style.width = '150px';" + //
									"button" + i + ".style.outline = 'none';" + //
									"button" + i + ".className = 'add-to-playlist-button';" + //
									"button" + i + ".addEventListener('mouseenter', function() {button" + i + ".style.color = '#2ECC71';}, false);" + //
									"button" + i + ".addEventListener('mouseleave', function() {button" + i + ".style.color = '#FFFFFF';}, false);" + //
									"button" + i + ".addEventListener('click', function() {window.popup.openPlaylist(document.querySelectorAll('[id=video-title]')[" + i + "].href);}, false);" + //
									"document.getElementsByClassName('customElement')[" + i + "].appendChild(button" + i + ");");

							String songTitle = this.getWebEngine().mainFrame().get().executeJavaScript("document.querySelectorAll(\"yt-formatted-string.style-scope.ytd-video-renderer\")[" + i + "].innerText;");
							if (songTitle != null)
								this.getWebEngine().mainFrame().get().executeJavaScript("document.querySelectorAll('yt-formatted-string.style-scope.ytd-video-renderer')[" + i + "].innerText = '" + SongTitle.formatSongTitle(songTitle) + "';");
						}
					}
				}
			}
		});
		this.web_engine.on(TitleChanged.class, event -> {
			if (event.browser().url().contains("https://beatplaylist.com/api/v1/google/auth")) {
				event.browser().mainFrame().get().document().ifPresent(document -> {
					document.documentElement().ifPresent(documentElement -> documentElement.findElementsByTagName("div").forEach(element -> {
						String accessToken = documentElement.findElementsByClassName("accessToken").get(0).innerText();
						String refreshToken = documentElement.findElementsByClassName("refreshToken").get(0).innerText();
						LinkGoogleAccount.send(accessToken, refreshToken);

						BrowserManager.getInstance().loadURL(this.getWebEngine(), "https://youtube.com/");
					}));
				});
			} else if (event.browser().url().contains("https://beatplaylist.com/api/v1/instagram/auth")) {
				event.browser().mainFrame().get().document().ifPresent(document -> {
					document.documentElement().ifPresent(documentElement -> documentElement.findElementsByTagName("div").forEach(element -> {
						String accessToken = documentElement.findElementsByClassName("accessToken").get(0).innerText();
						LinkSocialNetworkingAccount.send(accessToken, "INSTAGRAM");
						BrowserManager.getInstance().loadURL(this.getWebEngine(), "https://youtube.com/");
					}));
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