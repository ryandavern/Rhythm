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

import java.nio.file.Paths;

import com.beatplaylist.Options;
import com.beatplaylist.chromium.adblock.AdvertManager;
import com.beatplaylist.chromium.adblock.InterceptRequest;
import com.beatplaylist.chromium.adblock.TermType;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.settings.Settings;
import com.beatplaylist.utilities.filemanager.FileUtilities;
import com.beatplaylist.utilities.filemanager.OperatingSystem;
import com.beatplaylist.utilities.user.UserManager;
import com.teamdev.jxbrowser.browser.callback.InjectJsCallback;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.engine.Language;
import com.teamdev.jxbrowser.engine.ProprietaryFeature;
import com.teamdev.jxbrowser.engine.RenderingMode;
import com.teamdev.jxbrowser.navigation.event.LoadStarted;
import com.teamdev.jxbrowser.net.Scheme;
import com.teamdev.jxbrowser.net.callback.BeforeUrlRequestCallback;

public class EngineBrowser {

	private static EngineBrowser instance = new EngineBrowser();

	public static EngineBrowser getInstance() {
		return instance;
	}

	public Engine engine = null;
	public EngineOptions engineOptions;

	public void initializeOptions() {
		RenderingMode renderingMode = RenderingMode.OFF_SCREEN;

		if (Settings.getInstance().isHardwareAccelerated()) {
			renderingMode = RenderingMode.HARDWARE_ACCELERATED;
		}
		if (FileUtilities.getInstance().getOperatingSystem() == OperatingSystem.WINDOWS) {
			if (Options.test_mode) {
				this.engineOptions = EngineOptions.newBuilder(renderingMode).enableAutoplay().language(Language.ENGLISH_US).enableProprietaryFeature(ProprietaryFeature.AAC) //
						.enableProprietaryFeature(ProprietaryFeature.H_264).enableProprietaryFeature(ProprietaryFeature.WIDEVINE).userDataDir(Paths.get(FileUtilities.getInstance().getAppData().getPath() + "\\BeatPlaylist\\chromium-user")).remoteDebuggingPort(9222).build();
			} else {
				this.engineOptions = EngineOptions.newBuilder(renderingMode).enableAutoplay().language(Language.ENGLISH_US).enableProprietaryFeature(ProprietaryFeature.AAC) //
						.enableProprietaryFeature(ProprietaryFeature.H_264).enableProprietaryFeature(ProprietaryFeature.WIDEVINE).userDataDir(Paths.get(FileUtilities.getInstance().getAppData().getPath() + "\\BeatPlaylist\\chromium-user")).build();
			}
		} else {
			this.engineOptions = EngineOptions.newBuilder(renderingMode).enableAutoplay().language(Language.ENGLISH_US).enableProprietaryFeature(ProprietaryFeature.AAC) //
					.enableProprietaryFeature(ProprietaryFeature.H_264).enableProprietaryFeature(ProprietaryFeature.WIDEVINE).build();
		}
		System.out.println("Chromium Launched with RenderingMode: " + renderingMode.name());
	}

	public void initializeEngine() {
		this.engine = Engine.newInstance(this.engineOptions);

		this.engine.network().set(BeforeUrlRequestCallback.class, params -> {
			if (UserManager.getInstance().getUser().isPremium() && !Settings.getInstance().showVideoAdverts()) {
				String url = params.urlRequest().url();

				if (url.contains("base.js")) {
					// if (Options.test_mode)
					// System.out.println(url);
					BrowserManager.getInstance().loadBaseJS("https://beatplaylist.sfo2.digitaloceanspaces.com/api/youtube/default.js");
					return BeforeUrlRequestCallback.Response.cancel();

				}
				if (url.contains("localhost") || url.contains("accounts.google.com") || url.contains("beatplaylist.com")) {
					return BeforeUrlRequestCallback.Response.proceed();
				}
				if (AdvertManager.getInstance().getAdvertTerms().isEmpty()) {
					System.out.println("No advert blocklist found.");
					return BeforeUrlRequestCallback.Response.proceed();
				}

				boolean blocked = true;

				if (AdvertManager.getInstance().getAdvertTerms().stream().anyMatch(str -> (url.toLowerCase().startsWith(str.getTerm().toLowerCase()) || url.toLowerCase().contains(str.getTerm().toLowerCase())) && str.getTermType() == TermType.BLOCKED)) {
					return BeforeUrlRequestCallback.Response.cancel();
				}
				if (AdvertManager.getInstance().getAdvertTerms().stream().anyMatch(str -> (url.toLowerCase().startsWith(str.getTerm().toLowerCase()) || url.toLowerCase().contains(str.getTerm().toLowerCase())) && str.getTermType() == TermType.ALLOWED)) {
					blocked = false;
					// System.out.println(url);
				}
				if (blocked) {
					return BeforeUrlRequestCallback.Response.cancel();
				}
				return BeforeUrlRequestCallback.Response.proceed();
			} else {
				return BeforeUrlRequestCallback.Response.proceed();
			}
		});
	}

}