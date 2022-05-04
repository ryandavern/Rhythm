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

package com.beatplaylist.gui.module.layout.audiobar;

import com.beatplaylist.chromium.BrowserManager;
import com.beatplaylist.chromium.YouTube;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.utilities.playlist.SongTitle;
import com.beatplaylist.utilities.CustomColor;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.playlist.PlaylistManager;
import com.beatplaylist.utilities.playlist.Song;

import javafx.application.Platform;

public class AudioUtilities {

	// Has song reached custom end time?
	// Does song have custom start time? If yes, is the current time in song before the set start time?
	public static void doesSongMeetMetadataThreshold(double currentTime, double totalDuration) {
		if (PlaylistManager.getInstance().current_playlist != null && PlaylistManager.getInstance().currentPlayingSongView != null && PlaylistManager.getInstance().currentPlayingSongView.song != null) {

			Song currentlyPlayingSong = PlaylistManager.getInstance().currentPlayingSongView.song;

			if (currentlyPlayingSong.hasReachedSongEndTime(currentTime, totalDuration)) {
				GUIManager.getInstance().audioBar.audioListener.endVideo();
			}

			// Check if currently playing song has a custom set start time and the current time in song is before the set start time.
			if (currentlyPlayingSong.getStartTime() != 0 && currentTime > 1 && currentTime < Double.valueOf(currentlyPlayingSong.getStartTime())) {
				YouTube.setCurrentTime(Double.valueOf(PlaylistManager.getInstance().currentPlayingSongView.song.getStartTime()));
			}
		}
	}

	public static void updatePauseState(boolean isPaused) {
		if (!GUIManager.getInstance().audioBar.nowPlayingButton.isDisabled()) {
			GUIManager.getInstance().audioBar.nowPlayingButton.setDisable(true);
			GUIManager.getInstance().audioBar.nowPlayingButton.setStyle("-fx-opacity: 0.5; -fx-background-color: " + CustomColor.RHYTHM.getColorHex() + "; -fx-cursor: hand; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-border-radius: 20 20 0 0; -fx-background-radius: 20 20 0 0;");
		}
	}

	public static void onTitleChange(String title) {
		JSONWrapper song_data = new JSONWrapper(SongTitle.getSongInformationFromTitle(title));

		if (!song_data.getJSONString("song_name").equals("YouTube")) {

			String trackName = song_data.getJSONString("song_name");
			if (trackName.length() > 50) {
				trackName = trackName.substring(0, 50) + "...";
			}

			GUIManager.getInstance().audioBar.audioBar.currentTrackText.setText(trackName);

			if (!song_data.getJSONString("artist").isEmpty() && !song_data.getJSONString("artist").equals("null"))
				GUIManager.getInstance().audioBar.audioBar.currentTrackArtistNameText.setText(song_data.getJSONString("artist"));
			else {
				BrowserManager.getInstance().getCurrentBrowser().getWebEngine().mainFrame().ifPresent(frame -> {
					String artistName = frame.executeJavaScript("document.getElementsByClassName('yt-simple-endpoint style-scope yt-formatted-string')[0].text;");
					if (artistName == null || (artistName != null && artistName.isEmpty()))
						GUIManager.getInstance().audioBar.audioBar.currentTrackArtistNameText.setText("");
					else {
						Platform.runLater(() -> {
							GUIManager.getInstance().audioBar.audioBar.currentTrackArtistNameText.setText(artistName);
						});
					}
				});
			}
			if (!song_data.getJSONString("featuring").isEmpty() && !song_data.getJSONString("featuring").equals("null"))
				GUIManager.getInstance().audioBar.audioBar.currentTrackFeaturingText.setText("Featuring: " + song_data.getJSONString("featuring"));
			else
				GUIManager.getInstance().audioBar.audioBar.currentTrackFeaturingText.setText("");
			GUIManager.getInstance().audioBar.audioBar.setLoading(false);
		}
	}
}