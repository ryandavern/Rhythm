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

import com.beatplaylist.Main;
import com.beatplaylist.chromium.BrowserManager;
import com.beatplaylist.chromium.YouTube;
import com.beatplaylist.enums.FontType;
import com.beatplaylist.enums.ResourceIcon;
import com.beatplaylist.enums.ShuffleType;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.module.layout.sidebar.TabType;
import com.beatplaylist.gui.utilities.playlist.PlaylistLoader;
import com.beatplaylist.gui.utilities.playlist.SongOrder;
import com.beatplaylist.gui.utilities.playlist.SongTitle;
import com.beatplaylist.settings.Settings;
import com.beatplaylist.utilities.CustomColor;
import com.beatplaylist.utilities.CustomToolTip;
import com.beatplaylist.utilities.Utilities;
import com.beatplaylist.utilities.control.ControlledProgressBar;
import com.beatplaylist.utilities.control.Tooltip;
import com.beatplaylist.utilities.cooldown.CooldownManager;
import com.beatplaylist.utilities.data.Data;
import com.beatplaylist.utilities.events.UpdateSongLikeEvent;
import com.beatplaylist.utilities.image.ImageBuilder;
import com.beatplaylist.utilities.image.PausePlay;
import com.beatplaylist.utilities.network.post.LikeOrUnlikeSong;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;
import com.beatplaylist.utilities.playlist.PlaylistManager;
import com.beatplaylist.utilities.popup.MediaTooltip;
import com.beatplaylist.utilities.popup.Popup;
import com.beatplaylist.utilities.popup.control.PopupHBox;
import com.beatplaylist.utilities.popup.control.PopupVBox;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class AudioBar {

	// Volume and song duration sliders.
	public ControlledProgressBar songDurationProgress, volumeProgress;

	public ImageView soundImage;
	public ImageBuilder skipImage, previousImage, repeatImage, shuffleImage, addToPlaylistImage, likeMusicImage, backToPlayingPlaylistImage;

	// Pause and Play button class.
	public PausePlay pause_play;

	// Current Playing Text
	public Text currentDurationText, currentTrackText, currentTrackArtistNameText, currentTrackFeaturingText;
	public Text fullCurrentTrack; // Accessed in now playing page.

	public PopupVBox vbox, center_vbox, right_vbox, left_vbox, trackLeftVBox, track_vbox;
	public PopupHBox pausePlayHBox, track_hbox, sound_hbox, right_hbox;

	public AudioBar() {
		this.vbox = new PopupVBox(2);
		this.center_vbox = new PopupVBox(5);
		this.right_vbox = new PopupVBox(1);
		this.left_vbox = new PopupVBox(1);
		this.track_vbox = new PopupVBox(1);
		this.trackLeftVBox = new PopupVBox(3);

		this.pausePlayHBox = new PopupHBox(25);
		this.track_hbox = new PopupHBox(20);
		this.right_hbox = new PopupHBox(3);
		this.sound_hbox = new PopupHBox(3);

		this.songDurationProgress = new ControlledProgressBar();
		this.volumeProgress = new ControlledProgressBar();

		this.soundImage = new ImageView(new Image(Main.class.getResource("/resources/icons/audioBar/volume_2.png").toExternalForm(), 20, 20, false, false));

		this.skipImage = new ImageBuilder(new Image(Main.class.getResource(ResourceIcon.AUDIO_BAR.getPath() + "skip.png").toExternalForm(), 16, 16, false, false), new Image(Main.class.getResource(ResourceIcon.AUDIO_BAR.getPath() + "skip-hover.png").toExternalForm(), 16, 16, false, false));
		this.previousImage = new ImageBuilder(new Image(Main.class.getResource(ResourceIcon.AUDIO_BAR.getPath() + "previous.png").toExternalForm(), 16, 16, false, false), new Image(Main.class.getResource(ResourceIcon.AUDIO_BAR.getPath() + "previous-hover.png").toExternalForm(), 16, 16, false, false));
		this.repeatImage = new ImageBuilder(new Image(Main.class.getResource(ResourceIcon.AUDIO_BAR.getPath() + "repeat.png").toExternalForm(), 16, 16, false, false), new Image(Main.class.getResource(ResourceIcon.AUDIO_BAR.getPath() + "repeat-hover.png").toExternalForm(), 16, 16, false, false));
		this.shuffleImage = new ImageBuilder(new Image(Main.class.getResource(ResourceIcon.AUDIO_BAR.getPath() + "shuffle.png").toExternalForm(), 16, 16, false, false), new Image(Main.class.getResource(ResourceIcon.AUDIO_BAR.getPath() + "shuffle-hover.png").toExternalForm(), 16, 16, false, false));
		this.addToPlaylistImage = new ImageBuilder(new Image(Main.class.getResource(ResourceIcon.AUDIO_BAR.getPath() + "add_song_to_playlist_icon.png").toExternalForm(), 18, 18, true, false), new Image(Main.class.getResource(ResourceIcon.AUDIO_BAR.getPath() + "add_song_to_playlist_hover_icon.png").toExternalForm(), 18, 18, true, false));
		this.likeMusicImage = new ImageBuilder(new Image(Main.class.getResource(ResourceIcon.V2.getPath() + "like_music.png").toExternalForm(), 18, 18, false, false), new Image(Main.class.getResource(ResourceIcon.V2.getPath() + "like_music_hover.png").toExternalForm(), 18, 18, false, false));
		this.backToPlayingPlaylistImage = new ImageBuilder(new Image(Main.class.getResource(ResourceIcon.NAVIGATION.getPath() + "back_icon.png").toExternalForm(), 24, 24, false, false), new Image(Main.class.getResource(ResourceIcon.NAVIGATION.getPath() + "back_icon_hover.png").toExternalForm(), 24, 24, false, false));

		this.pause_play = new PausePlay();

		this.currentDurationText = new Text("0:00/0:00");
		this.currentTrackText = new Text();
		this.currentTrackArtistNameText = new Text();
		this.currentTrackFeaturingText = new Text();
		this.fullCurrentTrack = new Text();
	}

	public void configure() {
		this.right_hbox.setAlignment(Pos.CENTER_LEFT);

		this.center_vbox.layoutXProperty().bind(GUIManager.getInstance().audioBar.audioBarPane.widthProperty().divide(3));
		this.center_vbox.layoutYProperty().bind(GUIManager.getInstance().audioBar.audioBarPane.heightProperty().divide(4.5));
		this.center_vbox.setId("centerVBox");

		this.right_vbox.layoutXProperty().bind(GUIManager.getInstance().audioBar.audioBarPane.widthProperty().subtract(100));
		this.right_vbox.layoutYProperty().bind(GUIManager.getInstance().audioBar.audioBarPane.heightProperty().divide(1.6));
		this.right_vbox.setManaged(false);
		this.right_vbox.setId("rightVBox");

		this.left_vbox.setLayoutX(20);
		this.left_vbox.setLayoutY(10);

		this.pausePlayHBox.setAlignment(Pos.CENTER);
		this.track_hbox.setAlignment(Pos.TOP_LEFT);
		this.right_vbox.setAlignment(Pos.CENTER_RIGHT);
		this.center_vbox.setAlignment(Pos.CENTER);
		this.sound_hbox.setAlignment(Pos.CENTER_LEFT);

		Tooltip.install(this.repeatImage.getHBox(), new CustomToolTip("Repeat"));
		Tooltip.install(this.shuffleImage.getHBox(), new CustomToolTip("Shuffle"));

		// Add to Playlist image styles
		this.addToPlaylistImage.getHBox().setAlignment(Pos.TOP_LEFT);
		this.addToPlaylistImage.getHBox().setPadding(new Insets(3, this.addToPlaylistImage.getHBox().getPadding().getRight(), this.addToPlaylistImage.getHBox().getPadding().getBottom(), this.addToPlaylistImage.getHBox().getPadding().getLeft()));
		this.addToPlaylistImage.getHBox().setVisible(false);
		CustomToolTip.install(this.addToPlaylistImage.getHBox(), new CustomToolTip("Add current playing song to playlist."));

		this.likeMusicImage.getHBox().setAlignment(Pos.TOP_LEFT);
		this.likeMusicImage.getHBox().setPadding(new Insets(3, this.addToPlaylistImage.getHBox().getPadding().getRight(), this.addToPlaylistImage.getHBox().getPadding().getBottom(), this.addToPlaylistImage.getHBox().getPadding().getLeft()));
		this.likeMusicImage.getHBox().setVisible(false);
		CustomToolTip.install(this.likeMusicImage.getHBox(), new CustomToolTip("Add current playing song to liked music."));

		// Current Playing Music Styles
		this.currentTrackText.setFont(Font.font(FontType.DEFAULT.getName(), 16));
		this.currentTrackText.setFill(Color.web(Settings.getInstance().getDefaultColor()));

		this.currentTrackArtistNameText.setFill(Color.web(CustomColor.GRAY.getColorHex()));
		this.currentTrackArtistNameText.setFont(Font.font(FontType.DEFAULT.getName(), 14));
		this.currentTrackArtistNameText.setCursor(Cursor.HAND);

		this.currentTrackFeaturingText.setFill(Color.web(CustomColor.GRAY.getColorHex()));
		this.currentTrackFeaturingText.setFont(Font.font(FontType.DEFAULT.getName(), 14));

		// Repeat and shuffle music styles.
		this.repeatImage.getHBox().setMinSize(30, 30);
		this.shuffleImage.getHBox().setMinSize(30, 30);

		if (Data.getInstance().shuffle)
			this.shuffleImage.swap();

		this.sound_hbox.setStyle("-fx-cursor: hand;");

		// Volume Slider Styles
		this.volumeProgress.getSlider().setMin(0.00);
		this.volumeProgress.getSlider().setMax(100);
		if (Settings.getInstance().getLastVolume() == -1)
			this.volumeProgress.getSlider().setValue(Settings.getInstance().getLastVolume() * 100);
		else
			this.volumeProgress.getSlider().setValue(Data.getInstance().volumeLevel * 100);
		this.volumeProgress.getSlider().setMinWidth(150);
		this.volumeProgress.getSlider().setMaxHeight(15);
		this.volumeProgress.getSlider().setStyle("-fx-background-radius: 10px; -fx-border-radius: 10px;");
		this.volumeProgress.getSlider().setCursor(Cursor.HAND);

		// Volume Progress Bar Styles
		this.volumeProgress.getProgressBar().setMaxWidth(700);
		this.volumeProgress.getProgressBar().setMaxHeight(10);
		this.volumeProgress.getProgressBar().setStyle("-fx-accent: " + Settings.getInstance().getDefaultColor() + ";");
		this.volumeProgress.getProgressBar().setProgress(Data.getInstance().volumeLevel);

		// Current Playing Duration Progress Slider Styles
		this.songDurationProgress.getSlider().setMaxWidth(700);
		this.songDurationProgress.getSlider().minWidthProperty().bind(GUIManager.getInstance().audioBar.audioBarPane.widthProperty().divide(2.8));
		this.songDurationProgress.getSlider().setStyle("-fx-background-radius: 10px; -fx-border-radius: 10px;");
		this.songDurationProgress.getSlider().setCursor(Cursor.HAND);

		// Currently Playing Song Progress Bar
		this.songDurationProgress.getProgressBar().setMaxWidth(700);
		this.songDurationProgress.getProgressBar().minWidthProperty().bind(GUIManager.getInstance().audioBar.audioBarPane.widthProperty().divide(2.8));
		this.songDurationProgress.getProgressBar().setMaxHeight(10);
		this.songDurationProgress.getProgressBar().setStyle("-fx-accent: " + Settings.getInstance().getDefaultColor() + ";");

		// Current Duration Text Styles
		this.currentDurationText.setFill(Color.web(CustomColor.GRAY.getColorHex()));
		this.currentDurationText.setFont(Font.font(FontType.ARIAL.getName(), 14));

		// Skip Song Styles
		Tooltip.install(this.skipImage.getHBox(), new CustomToolTip("Skip"));
		this.skipImage.getHBox().setMinSize(30, 30);
		this.skipImage.getHBox().setStyle("-fx-border-radius: 5px; -fx-background-color: " + CustomColor.AUDIO_BAR_BACKGROUND_COLOR.getColorHex() + "; -fx-cursor: hand;");

		// Previous Song Styles
		Tooltip.install(this.previousImage.getHBox(), new CustomToolTip("Previous"));
		this.previousImage.getHBox().setMinSize(30, 30);
		this.previousImage.getHBox().setStyle("-fx-border-radius: 5px; -fx-background-color: " + CustomColor.AUDIO_BAR_BACKGROUND_COLOR.getColorHex() + "; -fx-cursor: hand;");

		this.backToPlayingPlaylistImage.getHBox().setDisable(true);
		this.backToPlayingPlaylistImage.getHBox().setOpacity(0.7);
		this.backToPlayingPlaylistImage.getHBox().setCursor(Cursor.HAND);
		CustomToolTip.install(this.backToPlayingPlaylistImage.getHBox(), new CustomToolTip("Go to current playing playlist."));

		this.sound_hbox.getChildren().addAll(this.soundImage, this.volumeProgress);

		this.pausePlayHBox.getChildren().addAll(this.previousImage.getHBox(), this.pause_play.getHBox(), this.skipImage.getHBox());
		this.track_vbox.getChildren().addAll(this.currentTrackText, this.currentTrackArtistNameText, this.currentTrackFeaturingText);
		this.trackLeftVBox.getChildren().addAll(this.addToPlaylistImage.getHBox(), this.likeMusicImage.getHBox());
		this.track_hbox.getChildren().addAll(this.trackLeftVBox, this.track_vbox);

		this.sound_hbox.setPadding(new Insets(0, 0, 0, 20));

		this.center_vbox.getChildren().addAll(this.pausePlayHBox, this.songDurationProgress);

		this.right_hbox.getChildren().addAll(this.backToPlayingPlaylistImage.getHBox(), this.shuffleImage.getHBox(), this.repeatImage.getHBox(), this.sound_hbox);
		this.right_vbox.getChildren().addAll(this.currentDurationText, this.right_hbox);

		this.left_vbox.getChildren().add(this.track_hbox);

		GUIManager.getInstance().audioBar.audioBarPane.getChildren().addAll(this.left_vbox, this.center_vbox, this.right_vbox);

		listen();
	}

	private void listen() {
		this.addToPlaylistImage.getHBox().setOnMouseClicked(event -> {
			String currentSongTitle = YouTube.getTitle().replace("- YouTube", "").trim();
			JSONWrapper song_data = new JSONWrapper(SongTitle.getSongInformationFromTitle(currentSongTitle));

			if (!song_data.getJSONString("artist").isEmpty() && !song_data.getJSONString("artist").equals("null")) {
				Popup.addSongToPlaylist(currentSongTitle, BrowserManager.getInstance().getVideoURL());
			} else {
				String title = this.currentTrackArtistNameText.getText() + " - " + this.currentTrackText.getText();
				Popup.addSongToPlaylist(title, BrowserManager.getInstance().getVideoURL());
			}
		});
		this.backToPlayingPlaylistImage.getHBox().setOnMouseClicked(event -> {
			if (GUIManager.getInstance().currentTab.tab == TabType.OPEN_PLAYLIST_VIEW) {
				if (PlaylistLoader.getInstance().playlistView.playlist.getURL().equals(PlaylistManager.getInstance().current_playlist.getURL())) {
					return;
				}
				PlaylistLoader.getInstance().loadCurrentPlayingPlaylist();
			} else {
				PlaylistLoader.getInstance().loadCurrentPlayingPlaylist();
			}
		});
		this.likeMusicImage.getHBox().setOnMouseClicked(event -> {
			if (CooldownManager.getInstance().hasCooldown("LIKE_UNLIKE_COOLDOWN")) {
				Notification.getInstance().createNotification("Like / Unlike", "Please wait 1 second before trying this again!", AlertType.ERROR);
				return;
			}
			CooldownManager.getInstance().setCooldown("LIKE_UNLIKE_COOLDOWN", 1, "second");

			String currentSongTitle = YouTube.getTitle().replace("- YouTube", "").trim();
			JSONWrapper song_data = new JSONWrapper(SongTitle.getSongInformationFromTitle(currentSongTitle));

			if (!song_data.getJSONString("artist").isEmpty() && !song_data.getJSONString("artist").equals("null")) {
				LikeOrUnlikeSong.send(BrowserManager.getInstance().getVideoURL(), currentSongTitle, true, new UpdateSongLikeEvent() {

					@Override
					public void onUpdate(String newState) {

					}
				});
			} else {
				String title = this.currentTrackArtistNameText.getText() + " - " + this.currentTrackText.getText();
				LikeOrUnlikeSong.send(BrowserManager.getInstance().getVideoURL(), title, true, new UpdateSongLikeEvent() {

					@Override
					public void onUpdate(String newState) {

					}
				});
			}
		});
		this.sound_hbox.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				double volume = Data.getInstance().volumeLevel;

				if (this.volumeProgress.getSlider().getValue() == 0) {
					BrowserManager.getInstance().getCurrentBrowser().web_engine.audio().unmute();
					CustomToolTip.install(this.soundImage, new CustomToolTip("Mute"));
				} else {
					BrowserManager.getInstance().getCurrentBrowser().web_engine.audio().mute();
					CustomToolTip.install(this.soundImage, new CustomToolTip("Unmute"));
					volume = 0;
				}

				this.volumeProgress.getProgressBar().setProgress(volume);
				this.volumeProgress.getSlider().setValue(volume);
				setSoundWave((volume * 100));
			} else {
				Settings.getInstance().setLoudnessEqualization(Settings.getInstance().hasLoudnessEqualization() ? false : true, true);
				Notification.getInstance().createNotification("Loudness Equalization", "Loudness Equalization " + (Settings.getInstance().hasLoudnessEqualization() ? "enabled" : "disabled"), AlertType.SUCCESS);
			}
		});
		this.repeatImage.getHBox().setOnMouseClicked(event -> {
			if (Data.getInstance().repeat) {
				this.repeatImage.swap();
				Tooltip.install(this.repeatImage.getHBox(), new CustomToolTip("Repeat"));
				Data.getInstance().repeat = false;
			} else {
				this.repeatImage.swap();
				Tooltip.install(this.repeatImage.getHBox(), new CustomToolTip("Turn off Repeat"));
				Data.getInstance().repeat = true;
			}
		});

		this.shuffleImage.getHBox().setOnMouseClicked(event -> {
			if (Data.getInstance().shuffle) {
				this.shuffleImage.swap();
				this.shuffleImage.installToolTip("Turn on Shuffle");

				Data.getInstance().shuffle = false;
			} else {
				this.shuffleImage.swap();
				this.shuffleImage.installToolTip("Turn off Shuffle");
				Data.getInstance().shuffle = true;
			}
			if (GUIManager.getInstance().currentTab.tab == TabType.OPEN_PLAYLIST_VIEW) {
				PlaylistLoader.getInstance().reloadCurrentPlaylist();
			}
		});
		this.currentTrackArtistNameText.setOnMouseClicked(event -> {
			SongOrder.loadCurrentPlayingArtistPage();
		});
		// User interacts with pause/play button.
		this.pause_play.getHBox().setOnMouseClicked(event -> {
			if (BrowserManager.getInstance().getCurrentBrowser() != null && BrowserManager.getInstance().getVideoURL().contains("watch")) {
				if (this.pause_play.isPaused()) {
					// Play
					this.pause_play.setState(false);
					YouTube.setVideoPauseState(false);
					// if (Utilities.getInstance().mediaPlayer != null && PlaylistManager.getInstance().current_playlist.isLocalPlaylist())
					// Utilities.getInstance().getMediaPlayer().play();

				} else {
					// Set paused
					this.pause_play.setState(true);
					YouTube.setVideoPauseState(true);

					// if (Utilities.getInstance().mediaPlayer != null && PlaylistManager.getInstance().current_playlist.isLocalPlaylist())
					// Utilities.getInstance().getMediaPlayer().pause();
				}
			}
		});

		this.skipImage.getHBox().setOnMouseClicked(event -> {
			if (PlaylistManager.getInstance().current_playlist != null) {
				if (Data.getInstance().shuffle && Settings.getInstance().getShuffleType() == ShuffleType.SONG_RANDOM) {
					int random = new Random().nextInt(PlaylistManager.getInstance().current_playlist.getSongs().size());
					GUIManager.getInstance().audioBar.audioListener.selectSong(random, PlaylistManager.getInstance().current_playlist);
				} else {
					GUIManager.getInstance().audioBar.audioListener.selectSong(PlaylistManager.getInstance().current_playlist.getPosition() + 1, PlaylistManager.getInstance().current_playlist);
				}
			} else {
				GUIManager.getInstance().videoBrowser.getWebEngine().mainFrame().get().executeJavaScript("skip();");
			}
		});

		this.previousImage.getHBox().setOnMouseClicked(event -> {
			if (PlaylistManager.getInstance().current_playlist != null) {
				GUIManager.getInstance().audioBar.audioListener.selectSong(PlaylistManager.getInstance().current_playlist.getPosition() - 1, PlaylistManager.getInstance().current_playlist);
			} else {
				GUIManager.getInstance().videoBrowser.getWebEngine().mainFrame().get().executeJavaScript("previous();");
			}
		});

		MediaTooltip media_progress_slider_tooltip = new MediaTooltip(this.songDurationProgress.getSlider()), volume_slider_tooltip = new MediaTooltip(this.volumeProgress.getSlider());

		this.songDurationProgress.getSlider().setOnMouseMoved(event -> {
			if (Utilities.getInstance().mediaPlayer != null) {
				double max_x = this.songDurationProgress.getSlider().getBoundsInParent().getMaxX();
				double percentage = (event.getX() / max_x);
				double duration = Utilities.getInstance().mediaPlayer.getTotalDuration().toSeconds();
				double hover_time = (duration * percentage);

				String result = YouTube.getFormattedTime((int) hover_time);

				Bounds bounds = this.songDurationProgress.localToScene(this.songDurationProgress.getBoundsInLocal());

				media_progress_slider_tooltip.setPopupLocation(event.getSceneX() - 20, bounds.getMinY() - 40);
				media_progress_slider_tooltip.setText(result);
			} else {
				if (!BrowserManager.getInstance().getVideoURL().contains("watch"))
					return;
				double max_x = this.songDurationProgress.getSlider().getBoundsInParent().getMaxX();
				double percentage = (event.getX() / max_x);
				double duration = YouTube.getDuration();
				double hover_time = (duration * percentage);

				String result = YouTube.getFormattedTime((int) hover_time);

				Bounds bounds = this.songDurationProgress.localToScene(this.songDurationProgress.getBoundsInLocal());
				media_progress_slider_tooltip.setPopupLocation(event.getSceneX() - 20, bounds.getMinY() - 40);
				media_progress_slider_tooltip.setText(result);
			}
		});

		this.volumeProgress.getSlider().setOnMouseMoved(event -> {
			if (BrowserManager.getInstance().getCurrentBrowser().web_engine.audio().isMuted()) {
				BrowserManager.getInstance().getCurrentBrowser().web_engine.audio().unmute();
			}
			Bounds bounds = this.volumeProgress.localToScene(this.volumeProgress.getBoundsInLocal());

			double maxX = this.volumeProgress.getSlider().getBoundsInParent().getMaxX();
			int percentage = (int) (((event.getX() / maxX) * 100) + 2);

			String result = percentage + "%";
			if (percentage >= 100) {
				result = "100%";
			}

			volume_slider_tooltip.setPopupLocation(event.getSceneX() - 20, bounds.getMinY() - 40);
			volume_slider_tooltip.setText(result);
		});

		this.songDurationProgress.getSlider().valueProperty().addListener(observable -> {
			if (this.songDurationProgress.getSlider().isPressed()) {
				if (Utilities.getInstance().mediaPlayer != null && Utilities.getInstance().mediaPlayer.getStatus() == Status.PLAYING) {
					Utilities.getInstance().mediaPlayer.setStartTime(Utilities.getInstance().mediaPlayer.getMedia().getDuration().multiply(this.songDurationProgress.getSlider().getValue() / 100.0));
					if (!Data.getInstance().isPaused) {
						Utilities.getInstance().mediaPlayer.stop();
						Utilities.getInstance().mediaPlayer.play();
					}
					updateMediaPlayer();
					return;
				}
				if (BrowserManager.getInstance().getCurrentBrowser() != null && BrowserManager.getInstance().getVideoURL().contains("watch")) {
					double total_duration = YouTube.getDuration();
					double percent = (this.songDurationProgress.getSlider().getValue() / 100);

					double time = total_duration * percent;
					if (time >= (total_duration - 3)) {
						GUIManager.getInstance().audioBar.audioListener.endVideo();
					} else {
						YouTube.setCurrentTime(time);
					}

					this.currentDurationText.setText(YouTube.getFormattedTime((int) time) + "/" + YouTube.getFormattedTime((int) total_duration));
					this.songDurationProgress.getProgressBar().setProgress((time / total_duration));
				} else {
					this.songDurationProgress.getSlider().setValue(0);
				}
			}
		});
		this.volumeProgress.getSlider().valueProperty().addListener((observable, oldValue, newValue) -> {
			if (this.volumeProgress.getSlider().isPressed() && BrowserManager.getInstance().getCurrentBrowser().getWebEngine() != null) {
				setSoundWave(newValue.intValue());
				// sound_slider.setStyle("-fx-background-color: linear-gradient(to right, " + Settings.getInstance().getDefaultColor() + " 0%, " + Settings.getInstance().getDefaultColor() + " " + sound_slider.getValue() + "%, " + CustomColor.BACKGROUND.getColorHex() + " " + sound_slider.getValue() + "%, " + CustomColor.BACKGROUND.getColorHex() + " 100%); -fx-background-radius: 10px; -fx-border-radius: 10px; -fx-border-color: " + Settings.getInstance().getDefaultColor() + ";");

				double value = (newValue.doubleValue() / 100);
				if (value > 1 || value < 0.00)
					value = 0.4;

				Data.getInstance().volumeLevel = value;

				if (Utilities.getInstance().mediaPlayer != null)
					Utilities.getInstance().mediaPlayer.setVolume((newValue.doubleValue() / 100));

				if (BrowserManager.getInstance().getCurrentBrowser() != null) {
					if (value <= 0) {
						BrowserManager.getInstance().getCurrentBrowser().getWebEngine().audio().mute();
						BrowserManager.getInstance().getCurrentBrowser().getWebEngine().audio().unmute();
					}
					BrowserManager.getInstance().getCurrentBrowser().getWebEngine().mainFrame().get().executeJavaScript("setCustomVolume(" + value + ");");
				}
				this.volumeProgress.getProgressBar().setProgress(value);
			}
		});
		this.volumeProgress.getSlider().setOnMouseReleased(event -> {
			Settings.getInstance().updateVolume(true);
		});
	}

	public void updateMediaPlayer() {
		if (Utilities.getInstance().mediaPlayer != null && this.songDurationProgress.getSlider() != null) {
			double elapsed = (double) Math.floor(Utilities.getInstance().mediaPlayer.getCurrentTime().toSeconds());
			double end = (double) Math.floor(Utilities.getInstance().mediaPlayer.getStopTime().toSeconds());
			this.currentDurationText.setText(formatTime(Utilities.getInstance().mediaPlayer.getCurrentTime(), Utilities.getInstance().mediaPlayer.getMedia().getDuration()));
			// Utilities.getInstance().mediaPlayer.setStartTime(Utilities.getInstance().duration.multiply(media_progress_slider.getValue() / 100.0));
			if (!this.songDurationProgress.getSlider().isDisabled() && Utilities.getInstance().mediaPlayer.getMedia().getDuration().greaterThan(Duration.ZERO) && !this.songDurationProgress.getSlider().isValueChanging()) {
				this.songDurationProgress.getProgressBar().setProgress((elapsed / end));
				this.songDurationProgress.getSlider().setValue((elapsed / end) * 100);
			}
		}
	}

	public String formatTime(Duration elapsed, Duration duration) {
		int intElapsed = (int) Math.floor(elapsed.toSeconds());
		int elapsedHours = intElapsed / (60 * 60);
		if (elapsedHours > 0)
			intElapsed -= elapsedHours * 60 * 60;
		int elapsedMinutes = intElapsed / 60;
		int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60;

		if (duration.greaterThan(Duration.ZERO)) {
			int intDuration = (int) Math.floor(duration.toSeconds());
			int durationHours = intDuration / (60 * 60);
			if (durationHours > 0)
				intDuration -= durationHours * 60 * 60;
			int durationMinutes = intDuration / 60;
			int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;
			if (durationHours > 0)
				return String.format("%d:%02d:%02d/%d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds, durationHours, durationMinutes, durationSeconds);
			else
				return String.format("%02d:%02d/%02d:%02d", elapsedMinutes, elapsedSeconds, durationMinutes, durationSeconds);
		} else {
			if (elapsedHours > 0)
				return String.format("%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
			else
				return String.format("%02d:%02d", elapsedMinutes, elapsedSeconds);
		}
	}

	public String getElapsed(Duration elapsed, Duration duration) {
		int intElapsed = (int) Math.floor(elapsed.toSeconds());
		int elapsedHours = intElapsed / (60 * 60);
		if (elapsedHours > 0)
			intElapsed -= elapsedHours * 60 * 60;
		int elapsedMinutes = intElapsed / 60;
		int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60;

		if (duration.greaterThan(Duration.ZERO)) {
			int intDuration = (int) Math.floor(duration.toSeconds());
			int durationHours = intDuration / (60 * 60);
			if (durationHours > 0)
				intDuration -= durationHours * 60 * 60;
			int durationMinutes = intDuration / 60;
			int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;
			if (durationHours > 0)
				return String.format("%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
			else
				return String.format("%02d:%02d", elapsedMinutes, elapsedSeconds, durationMinutes, durationSeconds);
		} else {
			if (elapsedHours > 0)
				return String.format("%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
			else
				return String.format("%02d:%02d", elapsedMinutes, elapsedSeconds);
		}
	}

	public void setSoundWave(double value) {
		if (value >= 80)
			this.soundImage.setImage(new Image(Main.class.getResource("/resources/icons/audioBar/volume_3.png").toExternalForm(), 20, 20, false, false));
		else if (value >= 40 && value < 80)
			this.soundImage.setImage(new Image(Main.class.getResource("/resources/icons/audioBar/volume_2.png").toExternalForm(), 20, 20, false, false));
		else if (value >= 1 && value < 40)
			this.soundImage.setImage(new Image(Main.class.getResource("/resources/icons/audioBar/volume_1.png").toExternalForm(), 20, 20, false, false));
		else
			this.soundImage.setImage(new Image(Main.class.getResource("/resources/icons/audioBar/volume_mute.png").toExternalForm(), 20, 20, false, false));
	}

	// Update loading text and color to show that a song is loading in the audiobar.
	public void setLoading(boolean isLoading) {
		if (isLoading) {
			this.currentTrackText.setText("Loading Song...");
			this.currentTrackArtistNameText.setText("");
			this.currentTrackFeaturingText.setText("");
			this.currentTrackText.setFill(Color.web(CustomColor.ORANGE.getColorHex()));
		} else {
			this.currentTrackText.setFill(Color.web(CustomColor.RHYTHM.getColorHex()));
		}
	}
}