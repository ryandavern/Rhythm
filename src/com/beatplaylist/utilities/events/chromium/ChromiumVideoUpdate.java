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

import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.module.layout.audiobar.AudioUtilities;
import com.beatplaylist.gui.utilities.playlist.SongTitle;
import com.beatplaylist.utilities.CustomColor;
import com.beatplaylist.utilities.data.Data;
import com.beatplaylist.utilities.data.StoredURL;
import com.beatplaylist.utilities.network.get.GetHasLikedSong;
import com.beatplaylist.utilities.playlist.LyricManager;
import com.teamdev.jxbrowser.js.JsAccessible;

import javafx.application.Platform;

public class ChromiumVideoUpdate {

	@JsAccessible
	public void onVideoPauseStateChange(boolean isPaused) {
		Platform.runLater(() -> {
			Data.getInstance().isPaused = isPaused;
			GUIManager.getInstance().audioBar.audioListener.audioBar.pause_play.setState(isPaused);
		});
	}

	@JsAccessible
	public void onVideoTimeChange(double currentTime, double totalDuration) {
		Platform.runLater(() -> {
			GUIManager.getInstance().audioBar.audioListener.updateCurrentTimeAndTotalDuration(currentTime, totalDuration);
			AudioUtilities.doesSongMeetMetadataThreshold(currentTime, totalDuration);
			Data.getInstance().listenLength++;
			GUIManager.getInstance().audioBar.audioListener.setNowPlaying(totalDuration);
			// GUIManager.getInstance().audioBar.audioListener.findArtistProfileImage(totalDuration);
			if (GUIManager.getInstance().audioBar.audioListener.audioBar.pause_play.isPaused()) {
				GUIManager.getInstance().audioBar.audioListener.audioBar.pause_play.setState(false);
			}
			if (GUIManager.getInstance().audioBar.nowPlayingButton.isDisabled()) {
				GUIManager.getInstance().audioBar.nowPlayingButton.setDisable(false);
				GUIManager.getInstance().audioBar.nowPlayingButton.setStyle("-fx-opacity: 1; -fx-background-color: " + CustomColor.RHYTHM.getColorHex() + "; -fx-cursor: hand; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-border-radius: 20 20 0 0; -fx-background-radius: 20 20 0 0;");
			}
		});
	}

	@JsAccessible
	public void onVideoEnd() {
		Platform.runLater(() -> {
			GUIManager.getInstance().audioBar.audioListener.endVideo();
		});
	}

	@JsAccessible
	public void onPageLoad(String title, String ucid, String artistImageURL) {
		System.out.println(title);
		Platform.runLater(() -> {
			System.out.println("UCID: " + ucid);
			System.out.println("Artist Image: " + artistImageURL);
			// Enable now playing button.
			if (GUIManager.getInstance().audioBar.nowPlayingButton.isDisabled()) {
				GUIManager.getInstance().audioBar.nowPlayingButton.setDisable(false);
				GUIManager.getInstance().audioBar.nowPlayingButton.setStyle("-fx-opacity: 1; -fx-background-color: " + CustomColor.RHYTHM.getColorHex() + "; -fx-cursor: hand; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-border-radius: 20 20 0 0; -fx-background-radius: 20 20 0 0;");
			}
			// Handle Title Change
			String currentTitle = title.replace("- YouTube", "").trim();
			System.out.println("Title: " + currentTitle);

			AudioUtilities.onTitleChange(currentTitle);

			if (!GUIManager.getInstance().audioBar.audioBar.addToPlaylistImage.getHBox().isVisible()) {
				GUIManager.getInstance().audioBar.audioBar.addToPlaylistImage.getHBox().setVisible(true);
			}

			if (!GUIManager.getInstance().audioBar.audioBar.likeMusicImage.getHBox().isVisible()) {
				GUIManager.getInstance().audioBar.audioBar.likeMusicImage.getHBox().setVisible(true);
			}

			// Determine if user has liked this song
			GetHasLikedSong.send(GUIManager.getInstance().videoBrowser.getWebEngine().url());

			// Get genius url
			LyricManager.getInstance().getLyrics("https://genius.com/" + SongTitle.getReplacedTitle(currentTitle, true) + "-lyrics");

			GUIManager.getInstance().audioBar.audioListener.setArtistProfileImage(artistImageURL, ucid);
		});
	}

	@JsAccessible
	public void onMixLoad(String array) {
		Platform.runLater(() -> {
			System.out.println("Test");
			System.out.println(array);
		});
	}

	@JsAccessible
	public void onDurationChange(double total, double lastTotal) {
		System.out.println(total + ":" + lastTotal);
	}
}