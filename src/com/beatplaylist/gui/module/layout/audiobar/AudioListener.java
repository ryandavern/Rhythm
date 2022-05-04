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

import java.util.Random;

import com.beatplaylist.chromium.BrowserManager;
import com.beatplaylist.chromium.YouTube;
import com.beatplaylist.enums.ShuffleType;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.module.layout.sidebar.TabType;
import com.beatplaylist.gui.utilities.playlist.PlaylistLoader;
import com.beatplaylist.settings.Settings;
import com.beatplaylist.utilities.Utilities;
import com.beatplaylist.utilities.data.Data;
import com.beatplaylist.utilities.data.StoredURL;
import com.beatplaylist.utilities.image.ImageManager;
import com.beatplaylist.utilities.network.post.SendCurrentSong;
import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;
import com.beatplaylist.utilities.playlist.Playlist;
import com.beatplaylist.utilities.playlist.PlaylistManager;
import com.beatplaylist.utilities.playlist.Song;

import javafx.application.Platform;
import javafx.scene.image.ImageView;

public class AudioListener {

	// Stores current song artist profile image url. This is requested by the now_playing_page.
	public ImageView currentArtistProfileImageView;

	// Quicker access to audioBar class.
	public AudioBar audioBar;

	// Used to stop accidental duplicate calls to endVideo function.
	private long lastEnded = -1;

	public AudioListener() {
		this.audioBar = GUIManager.getInstance().audioBar.audioBar;
	}

	public void setArtistProfileImage(String imageURL, String uniqueChannelID) {
		if (this.currentArtistProfileImageView == null) {
			this.currentArtistProfileImageView = new ImageView();
		}
		StoredURL.getInstance().currentArtistProfileImageURL = imageURL;
		ImageManager.getArtistPageImage(this.currentArtistProfileImageView, imageURL);

		StoredURL.getInstance().uniqueChannelID = uniqueChannelID;
	}

	public void setNowPlaying(double totalDuration) {
		String trackName = GUIManager.getInstance().audioBar.audioBar.currentTrackText.getText();
		if (trackName.length() > 35) {
			trackName = trackName.substring(0, 35) + "...";
		}
		if (GUIManager.getInstance().audioBar.audioBar.currentTrackArtistNameText.getText().isEmpty())
			GUIManager.getInstance().audioBar.audioBar.fullCurrentTrack.setText(trackName + "   (" + String.valueOf(YouTube.getFormattedTime((int) totalDuration)) + ")");
		else
			GUIManager.getInstance().audioBar.audioBar.fullCurrentTrack.setText(GUIManager.getInstance().audioBar.audioBar.currentTrackArtistNameText.getText() + " - " + trackName + "   (" + String.valueOf(YouTube.getFormattedTime((int) totalDuration)) + ")");
	}

	public void findArtistProfileImage(double totalDuration) {
		if (GUIManager.getInstance().currentTab.tab == TabType.NOW_PLAYING) {
			if (!StoredURL.getInstance().lastArtistImageURL.equals(GUIManager.getInstance().videoBrowser.getWebEngine().url())) {
				StoredURL.getInstance().lastArtistImageURL = GUIManager.getInstance().videoBrowser.getWebEngine().url();

				if (BrowserManager.getInstance().getCurrentBrowser().getWebEngine().mainFrame().get().executeJavaScript("document.getElementsByTagName('img')[4].currentSrc;") != null) {
					String artist = BrowserManager.getInstance().getCurrentBrowser().getWebEngine().mainFrame().get().executeJavaScript("document.getElementsByTagName('img')[4].currentSrc;");

					if (!StoredURL.getInstance().currentArtistProfileImageURL.equals(artist)) {
						// Artist Image URL.
						String artistString = artist.replace("\\.", "");
						if (!artistString.isEmpty() && !artistString.isBlank()) {
							// System.out.println("ARTIST STRING: " + artistString);
							if (!artistString.isEmpty() && artistString.contains("ggpht.com")) {
								Platform.runLater(() -> {
									if (this.currentArtistProfileImageView == null) {
										this.currentArtistProfileImageView = new ImageView();
									}
									StoredURL.getInstance().currentArtistProfileImageURL = artist;
									ImageManager.getArtistPageImage(this.currentArtistProfileImageView, artistString);
									String uniqueChannelID = BrowserManager.getInstance().getCurrentBrowser().getWebEngine().mainFrame().get().executeJavaScript("ytplayer.config.args[\"ucid\"]");
									if (!uniqueChannelID.isBlank()) {
										StoredURL.getInstance().uniqueChannelID = uniqueChannelID;
										// System.out.println("UCID: " + uniqueChannelID);
									}
								});
							}
						}
					}
				}

				Platform.runLater(() -> {
					String trackName = GUIManager.getInstance().audioBar.audioBar.currentTrackText.getText();
					if (trackName.length() > 35) {
						trackName = trackName.substring(0, 35) + "...";
					}
					GUIManager.getInstance().audioBar.audioBar.fullCurrentTrack.setText(GUIManager.getInstance().audioBar.audioBar.currentTrackArtistNameText.getText() + " - " + trackName + "   (" + String.valueOf(YouTube.getFormattedTime((int) totalDuration)) + ")");
				});
			}
		}
	}

	// End currently playing video and play next in playlist.
	public void endVideo() {
		if (System.currentTimeMillis() - this.lastEnded < 1000)
			return;
		double listenTime = getListenLengthSeconds();
		if (listenTime > 20) {
			SendCurrentSong.send(listenTime);
		}
		listenTime = 0;
		// If user has repeat enabled, set current time to 0 seconds.
		if (Data.getInstance().repeat) {
			YouTube.setCurrentTime(0);
			return;
		}

		// Check if song ended is playing from a playlist.
		// This check will stop an error caused when a user opens BeatPlaylist, and plays a song directly from the Browse tab.
		if (PlaylistManager.getInstance().current_playlist != null) {
			// If user has shuffle enabled, find a random song in the currently playing playlist.
			if (Data.getInstance().shuffle && Settings.getInstance().getShuffleType() == ShuffleType.SONG_RANDOM) {
				int random = getRandomInt(PlaylistManager.getInstance().current_playlist.getSongs().size());
				selectSong(random, PlaylistManager.getInstance().current_playlist);
			} else {
				// If shuffle is not enabled, select the next song in the playlist.
				// System.out.println(PlaylistManager.getInstance().current_playlist.getPosition());
				selectSong(PlaylistManager.getInstance().current_playlist.getPosition() + 1, PlaylistManager.getInstance().current_playlist);
			}
		}

		this.lastEnded = System.currentTimeMillis();
	}

	public void updateCurrentTimeAndTotalDuration(double currentTime, double totalDuration) {
		if (BrowserManager.getInstance().getCurrentBrowser() == null) {
			return;
		}
		this.audioBar.currentDurationText.setText(YouTube.getFormattedTime((int) currentTime) + "/" + YouTube.getFormattedTime((int) totalDuration));
		if (!this.audioBar.songDurationProgress.getProgressBar().isDisabled()) {
			if (currentTime > 0 && currentTime != totalDuration) {
				double progress = (currentTime / totalDuration);
				this.audioBar.songDurationProgress.getProgressBar().setProgress(progress);
				this.audioBar.songDurationProgress.getSlider().setValue(progress * 100);
			}
		}
	}

	public void selectSong(int songIndexInPlaylist, Playlist playlist) {
		double listenTime = getListenLengthSeconds();

		// Send song analytic if user listened to song for longer than 20 seconds.
		if (listenTime > 20) {
			SendCurrentSong.send(listenTime);
		}
		listenTime = 0;

		// Reset listen time.
		Data.getInstance().listenLength = System.currentTimeMillis();

		GUIManager.getInstance().audioBar.audioBar.setLoading(true);

		// If media player is enabled set to null.
		if (Utilities.getInstance().mediaPlayer != null) {
			Utilities.getInstance().mediaPlayer.dispose();
			Utilities.getInstance().mediaPlayer = null;
		}

		Data.getInstance().playing_queue = !PlaylistManager.getInstance().queue_list.isEmpty();

		if (PlaylistManager.getInstance().queue_list.isEmpty()) {
			// Check if playlist equals null and return if no playlist is found.
			if (playlist == null) {
				return;
			}

			// If user reaches the end of the playlist, restart the playlist.
			if (songIndexInPlaylist > playlist.getSongs().size()) {
				songIndexInPlaylist = 0;
			}
			// Set new playlist position
			playlist.setPosition(songIndexInPlaylist);

			if (GUIManager.getInstance().currentTab.tab == TabType.OPEN_PLAYLIST_VIEW) {
				// Check if music is playing from a playlist.
				if (PlaylistManager.getInstance().current_playlist != null) {
					if (PlaylistManager.getInstance().currentPlayingSongView != null)
						PlaylistManager.getInstance().currentPlayingSongView.unselectSong();

					// Get the current displaying playlist url.
					String openPlaylistURL = "";

					if (PlaylistLoader.getInstance().playlistView != null) {
						openPlaylistURL = PlaylistLoader.getInstance().playlistView.playlist.getURL();
					}

					// If the current playlist view is the playlist playing music.
					// This check stops song view glitches that would occur if a user went from the playing playlist to another playlist.
					if (PlaylistManager.getInstance().current_playlist.getURL().equals(openPlaylistURL)) {// Set new songView class.
						if (PlaylistManager.getInstance().storedSongView.size() > playlist.getPosition()) {
							PlaylistManager.getInstance().currentPlayingSongView = PlaylistManager.getInstance().storedSongView.get(playlist.getPosition());
							PlaylistManager.getInstance().currentPlayingSongView.selectSong();
						}
					}
				}
			}

			Song song = null;
			if (playlist.getPosition() < playlist.getSongs().size())
				song = playlist.getSongs().get(playlist.getPosition());
			else
				song = playlist.getSongs().get(0);

			if (song == null) {
				Notification.getInstance().createNotification("Song", "An error has occured while attempting to play the song.", AlertType.ERROR);
				return;
			}
			BrowserManager.getInstance().getCurrentBrowser().getWebEngine().navigation().loadUrl(song.getURL());
		} else {
			// If user has songs added to the queue, play from the queue.
			Song song = PlaylistManager.getInstance().getCurrentQueueSong();

			BrowserManager.getInstance().getCurrentBrowser().getWebEngine().navigation().loadUrl(song.getURL());

			PlaylistManager.getInstance().removeSongFromQueue();
			if (PlaylistManager.getInstance().currentPlayingSongView != null) {
				PlaylistManager.getInstance().currentPlayingSongView.unselectSong();
				PlaylistManager.getInstance().currentPlayingSongView = null;
			}
		}
	}

	private double getListenLengthSeconds() {
		if (Data.getInstance().listenLength == -1)
			return 0;
		else {
			return Data.getInstance().listenLength;
		}
	}

	private int getRandomInt(int max) {
		return new Random().nextInt(max);
	}
}