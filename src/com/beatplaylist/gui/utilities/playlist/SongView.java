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

package com.beatplaylist.gui.utilities.playlist;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.beatplaylist.chromium.BrowserManager;
import com.beatplaylist.enums.FontType;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.settings.Settings;
import com.beatplaylist.utilities.CustomColor;
import com.beatplaylist.utilities.Log;
import com.beatplaylist.utilities.control.ContextItem;
import com.beatplaylist.utilities.cooldown.CooldownManager;
import com.beatplaylist.utilities.events.CompleteEvent;
import com.beatplaylist.utilities.events.UpdateSongLikeEvent;
import com.beatplaylist.utilities.network.post.LikeOrUnlikeSong;
import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;
import com.beatplaylist.utilities.playlist.Playlist;
import com.beatplaylist.utilities.playlist.PlaylistManager;
import com.beatplaylist.utilities.playlist.PlaylistWorker;
import com.beatplaylist.utilities.playlist.RoleType;
import com.beatplaylist.utilities.playlist.Song;
import com.beatplaylist.utilities.popup.Popup;
import com.beatplaylist.utilities.user.UserManager;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

// Class handles songs displayed in playlist.
public class SongView {

	// Song data.
	public Song song;

	// Playlist song is in.
	public Playlist playlist;

	// Row in the playlist grid
	public int gridpaneRow = 0;

	public HBox songHBox, songTitleHBox, songArtistHBox, songFeaturingHBox, dateAddedHBox;
	public Text songArtistText, songFeaturingText, dateAddedText, durationText, songIsRemixText;
	public Label songTitleText;

	public Button extraOptionButton;
	public ContextMenu playlistContextMenu;

	public String filter;

	public SongView(Playlist playlist, Song song, int row, String filter) {
		this.gridpaneRow = row;
		this.playlist = playlist;
		this.song = song;
		this.filter = filter;

		this.songHBox = new HBox();
		this.songTitleHBox = new HBox();
		this.songArtistHBox = new HBox();
		this.songFeaturingHBox = new HBox();
		this.dateAddedHBox = new HBox(25);

		this.songTitleText = new Label();
		this.songArtistText = new Text();
		this.songFeaturingText = new Text();

		this.extraOptionButton = new Button("...") {
			public void requestFocus() {

			}
		};
		this.dateAddedText = new Text();
		try {
			if (!song.getDateAdded().equals("null")) {
				Date date = new SimpleDateFormat("yyyy-MM-dd h:mm:ss").parse(song.getDateAdded().replace(".0", ""));

				this.dateAddedText.setText(new SimpleDateFormat("dd/MM/yyyy").format(date));
			}

		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		this.songIsRemixText = new Text(" (Remix)");
		this.playlistContextMenu = getContextMenu();

		configure();
		listen();

		// Add SongView instance to storedSongView
		PlaylistManager.getInstance().storedSongView.add(this);
	}

	private void configure() {
		this.songIsRemixText.setFill(Color.web(CustomColor.RED.getColorHex()));
		this.songIsRemixText.setFont(Font.font(FontType.ARIAL.getName(), 14));

		// Song title styles
		this.songTitleText.setFont(Font.font(FontType.VERDANA.getName(), 13));
		this.songTitleText.setStyle("-fx-padding: 1px; -fx-background-insets: 0;");
		this.songTitleText.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
		this.songTitleText.maxWidthProperty().bind(this.songTitleHBox.widthProperty().subtract(20));
		if (!this.filter.isEmpty() && this.song.getSongName().get().toLowerCase().contains(this.filter.toLowerCase())) {
			this.songTitleText.setTextFill(Color.web(CustomColor.RHYTHM.getColorHex()));
		}

		// Song artist styles
		this.songArtistText.setFill(Color.web(CustomColor.GRAY.getColorHex()));
		this.songArtistText.setFont(Font.font(FontType.VERDANA.getName(), 13));
		// this.songArtistText.setText((this.song.getArtist().get().length() > 30) ? this.song.getArtist().get().substring(0, 30) + "..." : this.song.getArtist().get());
		this.songArtistText.textProperty().bind(this.song.getArtist());
		if (this.songArtistText.getText().equals("null")) {
			this.songArtistText.textProperty().unbind();
			this.songArtistText.setText("");
		}
		if (!this.filter.isEmpty() && this.song.getArtist().get().toLowerCase().contains(this.filter.toLowerCase())) {
			this.songArtistText.setFill(Color.web(CustomColor.RHYTHM.getColorHex()));
		}

		// Song featuring styles
		this.songFeaturingText.setFill(Color.web(CustomColor.GRAY.getColorHex()));
		this.songFeaturingText.setFont(Font.font(FontType.VERDANA.getName(), 13));
		// this.songFeaturingText.setText((featuring_name.length() > 30) ? featuring_name.substring(0, 30) + "..." : featuring_name);
		this.songFeaturingText.textProperty().bind(this.song.getFeaturing());

		if (this.songFeaturingText.getText().equals("null")) {
			this.songFeaturingText.textProperty().unbind();
			this.songFeaturingText.setText("");
		}
		if (!this.filter.isEmpty() && this.song.getFeaturing().get().toLowerCase().contains(this.filter.toLowerCase())) {
			this.songFeaturingText.setFill(Color.web(CustomColor.RHYTHM.getColorHex()));
		}

		// Song Added to playlist date styles
		this.dateAddedText.setFill(Color.web(CustomColor.GRAY.getColorHex()));
		this.dateAddedText.setFont(Font.font(FontType.VERDANA.getName(), 13));

		// Set song title hbox bind variables and add text flow to hbox.
		this.songTitleHBox.minWidthProperty().bind(PlaylistLoader.getInstance().playlistView.songListVBox.widthProperty().divide(4));
		this.songTitleHBox.maxWidthProperty().bind(PlaylistLoader.getInstance().playlistView.songListVBox.widthProperty().divide(4));
		this.songTitleHBox.setAlignment(Pos.CENTER_LEFT);
		this.songTitleHBox.setStyle("-fx-padding: 0 0 0 15;");

		this.songTitleHBox.getChildren().add(this.songTitleText); // If song is remix, add to textflow.
		this.songTitleText.textProperty().bind(this.song.getSongName());
		if (this.song.isRemix) {
			this.songTitleHBox.getChildren().add(this.songIsRemixText);
		}
		// Set song artist hbox bind variables and add artist text to hbox.
		this.songArtistHBox.minWidthProperty().bind(PlaylistLoader.getInstance().playlistView.songListVBox.widthProperty().divide(4));
		this.songArtistHBox.maxWidthProperty().bind(PlaylistLoader.getInstance().playlistView.songListVBox.widthProperty().divide(4));
		this.songArtistHBox.setAlignment(Pos.CENTER_LEFT);
		this.songArtistHBox.getChildren().add(this.songArtistText);
		this.songArtistHBox.setStyle("-fx-padding: 0 0 0 15;");

		// Set song featuring hbox bind variables and add featuring text to hbox.
		this.songFeaturingHBox.minWidthProperty().bind(PlaylistLoader.getInstance().playlistView.songListVBox.widthProperty().divide(4));
		this.songFeaturingHBox.maxWidthProperty().bind(PlaylistLoader.getInstance().playlistView.songListVBox.widthProperty().divide(4));
		this.songFeaturingHBox.setAlignment(Pos.CENTER_LEFT);
		this.songFeaturingHBox.getChildren().add(this.songFeaturingText);

		this.dateAddedHBox.minWidthProperty().bind(PlaylistLoader.getInstance().playlistView.songListVBox.widthProperty().divide(4));
		this.dateAddedHBox.maxWidthProperty().bind(PlaylistLoader.getInstance().playlistView.songListVBox.widthProperty().divide(4));
		this.dateAddedHBox.setAlignment(Pos.CENTER_LEFT);
		this.dateAddedHBox.getChildren().addAll(this.dateAddedText, this.extraOptionButton);

		this.extraOptionButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #fff;");
		this.extraOptionButton.setFont(Font.font(FontType.VERDANA.getName(), FontWeight.BOLD, 16));
		this.extraOptionButton.setAlignment(Pos.CENTER);
		this.extraOptionButton.minHeightProperty().bind(this.dateAddedHBox.heightProperty());
		this.extraOptionButton.maxHeightProperty().bind(this.dateAddedHBox.heightProperty());
		this.extraOptionButton.setVisible(false);
		// CustomToolTip.install(this.extraOptionButton, new CustomToolTip("More Options"));

		this.songHBox.setAlignment(Pos.CENTER_LEFT);
		this.songHBox.setMinHeight(40);

		updateBackgroundColor();
		this.songHBox.setCursor(Cursor.HAND);

		this.songHBox.getChildren().addAll(this.songTitleHBox, this.songArtistHBox, this.songFeaturingHBox, this.dateAddedHBox);
		PlaylistLoader.getInstance().playlistView.songListVBox.getChildren().add(this.songHBox);

		// If current playing song, select the song in the playlist view.
		if (BrowserManager.getInstance().getVideoURL().equals(this.song.getURL())) {
			if (PlaylistManager.getInstance().currentPlayingSongView != null) {
				PlaylistManager.getInstance().currentPlayingSongView.unselectSong();
			}
			PlaylistManager.getInstance().currentPlayingSongView = this;
			selectSong();

		}
	}

	// Sets the background color of the song hbox.
	public void updateBackgroundColor() {
		if ((this.gridpaneRow & 1) == 0) {
			this.songHBox.setStyle("-fx-background-color: #353535;");
		} else {
			this.songHBox.setStyle("-fx-background-color: #2d2d2d;");
		}
	}

	private void listen() {
		this.songHBox.setOnMouseEntered(event -> {
			if (!this.extraOptionButton.isVisible()) {
				this.extraOptionButton.setVisible(true);
			}

			selectSong();
		});
		this.songHBox.setOnMouseExited(event -> {
			if (this.extraOptionButton.isVisible() && (this.playlistContextMenu == null || !this.playlistContextMenu.isShowing())) {
				this.extraOptionButton.setVisible(false);
			}
			if (GUIManager.getInstance().audioBar.audioBar.currentTrackText.getText().equals(this.songTitleText.getText()) || (PlaylistManager.getInstance().currentPlayingSongView != null && PlaylistManager.getInstance().currentPlayingSongView == this)) {
				selectSong();
				return;
			}
			unselectSong();
		});
		this.songHBox.setOnMouseClicked(event -> {
			if (event.getTarget() instanceof Button) {
				return;
			}
			if (event.getButton() == MouseButton.PRIMARY) {
				// If current playlist is not set to the playlist the clicked song is in. Set the current playlist to the new playlist.
				GUIManager.getInstance().audioBar.audioBar.setLoading(true);
				if (PlaylistManager.getInstance().current_playlist == null || PlaylistManager.getInstance().current_playlist != null && !PlaylistManager.getInstance().current_playlist.getURL().equals(this.playlist.getURL()))
					PlaylistManager.getInstance().current_playlist = this.playlist;

				if (PlaylistManager.getInstance().currentPlayingSongView != null) {
					PlaylistManager.getInstance().currentPlayingSongView.unselectSong();
				}
				PlaylistManager.getInstance().currentPlayingSongView = this;
				selectSong();

				GUIManager.getInstance().audioBar.audioListener.audioBar.pause_play.setState(false);

				if (GUIManager.getInstance().audioBar.audioBar.backToPlayingPlaylistImage.getHBox().isDisabled()) {
					GUIManager.getInstance().audioBar.audioBar.backToPlayingPlaylistImage.getHBox().setDisable(false);
					GUIManager.getInstance().audioBar.audioBar.backToPlayingPlaylistImage.getHBox().setOpacity(1);
				}

				new Thread(() -> {
					BrowserManager.getInstance().loadURL(BrowserManager.getInstance().getCurrentBrowser().web_engine, this.song.getURL());
				}).start();
				this.playlist.setPosition(this.playlist.getSongPositionInPlaylist(this.song));
			} else {
				// Display song context menu
				if (this.playlistContextMenu != null && this.playlistContextMenu.isShowing())
					this.playlistContextMenu.hide();

				this.playlistContextMenu.show(this.songHBox, event.getScreenX(), event.getScreenY());
			}
		});
		this.extraOptionButton.setOnMouseEntered(event -> {
			this.extraOptionButton.setStyle("-fx-background-color: transparent; -fx-text-fill: " + Settings.getInstance().getDefaultColor() + ";");
			if (GUIManager.getInstance().audioBar.audioBar.currentTrackText.getText().equals(this.songTitleText.getText()) || (PlaylistManager.getInstance().currentPlayingSongView != null && PlaylistManager.getInstance().currentPlayingSongView == this)) {
				return;
			}
			unselectSong();
		});
		this.extraOptionButton.setOnMouseExited(event -> {
			this.extraOptionButton.setStyle("-fx-background-color: transparent; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + ";");
		});
		this.extraOptionButton.setOnMouseClicked(event -> {
			if (this.playlistContextMenu != null && this.playlistContextMenu.isShowing()) {
				this.playlistContextMenu.hide();
			} else {
				Bounds boundsInScreen = this.extraOptionButton.localToScreen(this.extraOptionButton.getBoundsInLocal());
				this.playlistContextMenu.show(this.extraOptionButton, boundsInScreen.getMaxX() - 15, boundsInScreen.getMaxY() - 15);
			}
		});
		// Hide extra option button if the context menu is closed.
		this.playlistContextMenu.setOnAutoHide(event -> {
			if (this.extraOptionButton.isVisible() && (this.playlistContextMenu == null || !this.playlistContextMenu.isShowing())) {
				this.extraOptionButton.setVisible(false);
			}
		});
	}

	public void unselectSong() {
		if (!this.filter.isEmpty())
			return;
		this.songTitleText.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
		this.songArtistText.setFill(Color.web(CustomColor.GRAY.getColorHex()));
		this.songFeaturingText.setFill(Color.web(CustomColor.GRAY.getColorHex()));
		this.dateAddedText.setFill(Color.web(CustomColor.GRAY.getColorHex()));
	}

	public void selectSong() {
		if (!this.filter.isEmpty())
			return;
		this.songTitleText.setTextFill(Color.web(Settings.getInstance().getDefaultColor()));
		this.songArtistText.setFill(Color.web(Settings.getInstance().getDefaultColor()));
		this.songFeaturingText.setFill(Color.web(Settings.getInstance().getDefaultColor()));
		this.dateAddedText.setFill(Color.web(Settings.getInstance().getDefaultColor()));
	}

	private ContextMenu getContextMenu() {
		ContextMenu contextMenu = new ContextMenu();
		contextMenu.setStyle("-fx-cursor: hand; -fx-background-color: " + CustomColor.DROPDOWN_MENU_COLOR.getColorHex() + ";");
		// If playlist is not a YouTube playlist, do the following.
		if (this.playlist.getName().isEmpty()) {
			Menu add_to_playlist = new Menu();
			ContextItem open_folder = new ContextItem("Open Containing Folder");
			Label label = new Label("Add to Playlist");
			label.setAlignment(Pos.CENTER_LEFT);
			label.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 14));
			label.setStyle("-fx-text-fill: " + CustomColor.WHITE.getColorHex() + ";");
			label.setPadding(new Insets(label.getPadding().getTop(), label.getPadding().getRight(), label.getPadding().getBottom(), label.getPadding().getLeft() + 25));

			add_to_playlist.setStyle("-fx-cursor: hand; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + ";");
			add_to_playlist.setGraphic(label);
			add_to_playlist.getStyleClass().add("sub-menu");

			ContextItem select_playlist = new ContextItem("Select a Playlist");
			select_playlist.getStyleClass().add("header");
			add_to_playlist.getItems().add(select_playlist);

			for (Playlist playlists : PlaylistManager.getInstance().getPlaylists()) {
				ContextItem menu_item = new ContextItem(playlists.getName());
				menu_item.setOnAction((event) -> {
					PlaylistWorker.updateSongInPlaylist(playlists, this, false, new CompleteEvent() {

						@Override
						public void onSuccess() {
							Notification.getInstance().createNotification("Song", "Song added to the playlist '" + playlists.getName() + "'!", AlertType.SUCCESS);
						}

						@Override
						public void onFail(String error) {
							if (error.equals("SONG_ALREADY_IN_PLAYLIST"))
								Notification.getInstance().createNotification("Playlist", "This song is already in the playlist you selected.", AlertType.ERROR);
							else
								Notification.getInstance().createNotification("Playlist", "An error occured while adding this song to your playlist.", AlertType.ERROR);
						}
					});
				});
				add_to_playlist.getItems().add(menu_item);
			}

			open_folder.setOnAction((event) -> {
				try {
					Desktop.getDesktop().open(new File(Settings.getInstance().getMusicDirectory()));
				} catch (IOException e) {
					System.out.println("Could not open folder");
					Log.getInstance().write(e.getStackTrace().toString());
				}
			});
			contextMenu.getItems().addAll(add_to_playlist, open_folder);
		} else { // If the playlist is a YouTube playlist do the following.
			Menu add_to_playlist = new Menu();
			add_to_playlist.getStyleClass().add("sub-menu");
			Label label = new Label("Add to Playlist");
			label.setAlignment(Pos.CENTER_LEFT);
			label.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 14));
			label.setTextFill(Color.web(CustomColor.POPUP_SUB_HEADER.getColorHex()));
			label.setPadding(new Insets(label.getPadding().getTop(), label.getPadding().getRight(), label.getPadding().getBottom(), label.getPadding().getLeft() + 25));
			label.setMinSize(100, 30);

			add_to_playlist.setStyle("-fx-cursor: hand; -fx-text-fill: " + CustomColor.POPUP_SUB_HEADER.getColorHex() + ";");
			add_to_playlist.setGraphic(label);

			ContextItem selectPlaylist = new ContextItem("Select a Playlist"), delete = new ContextItem("Remove from Playlist"), saveToLikes = new ContextItem("Save to your Liked Songs");
			ContextItem editMetadata = new ContextItem("Edit Metadata"), open_url = new ContextItem("Open in Browser"), queue_item = new ContextItem("Add to Queue"), copy_url = new ContextItem("Copy URL");

			selectPlaylist.getStyleClass().add("header");
			editMetadata.getStyleClass().add("header");
			delete.getStyleClass().add("header");

			add_to_playlist.getItems().add(selectPlaylist);
			if (!this.playlist.getName().equals("Top Trending"))
				contextMenu.getItems().add(editMetadata);

			if (this.song.hasLiked) {
				saveToLikes.label.setText("Remove from your Liked Songs");
			}

			for (Playlist playlists : PlaylistManager.getInstance().getPlaylists()) {
				ContextItem menu_item = new ContextItem(playlists.getName());
				menu_item.setOnAction(event -> {
					PlaylistWorker.updateSongInPlaylist(playlists, this, false, new CompleteEvent() {

						@Override
						public void onSuccess() {
							Notification.getInstance().createNotification("Song", "Song added to the playlist '" + playlists.getName() + "'!", AlertType.SUCCESS);
						}

						@Override
						public void onFail(String error) {
							if (error.equals("SONG_ALREADY_IN_PLAYLIST"))
								Notification.getInstance().createNotification("Playlist", "This song is already in the playlist you selected.", AlertType.ERROR);
							else
								Notification.getInstance().createNotification("Playlist", "An error occured while adding this song to your playlist.", AlertType.ERROR);
						}
					});
				});
				add_to_playlist.getItems().add(menu_item);
			}

			saveToLikes.setOnAction(event -> {
				if (CooldownManager.getInstance().hasCooldown("LIKE_UNLIKE_COOLDOWN")) {
					Notification.getInstance().createNotification("Like / Unlike", "Please wait 1 second before trying this again!", AlertType.ERROR);
					return;
				}
				CooldownManager.getInstance().setCooldown("LIKE_UNLIKE_COOLDOWN", 1, "second");
				if (this.playlist.getURL().equals("my-likes")) {
					LikeOrUnlikeSong.sendFromPlaylistView(this, new UpdateSongLikeEvent() {
						@Override
						public void onUpdate(String newState) {
							if (newState.equals("REMOVED")) {
								Platform.runLater(() -> {
									PlaylistLoader.getInstance().playlistView.songListVBox.getChildren().remove(songHBox);
								});
								song.hasLiked = false;
								saveToLikes.label.setText("Save to your Liked Songs");
							} else {
								song.hasLiked = true;
								saveToLikes.label.setText("Remove from your Liked Songs");
							}
						}
					});
				} else {
					LikeOrUnlikeSong.send(this.song.url, this.song.fullSongTitle, false, new UpdateSongLikeEvent() {
						@Override
						public void onUpdate(String newState) {
							if (newState.equals("REMOVED")) {
								song.hasLiked = false;
								saveToLikes.label.setText("Save to your Liked Songs");
							} else {
								song.hasLiked = true;
								saveToLikes.label.setText("Remove from your Liked Songs");
							}
						}
					});
				}
			});
			delete.setOnAction(event -> {
				unselectSong();
				Popup.confirmDeleteSongFromPlaylist(this.playlist, this);
				if (this.extraOptionButton.isVisible()) {
					this.extraOptionButton.setVisible(false);
				}
			});

			open_url.setOnAction(event -> {
				if (Desktop.isDesktopSupported()) {
					try {
						Desktop.getDesktop().browse(new URI(this.song.getURL()));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			queue_item.setOnAction(event -> {
				if (queue_item.getLabelText().startsWith("Add")) {
					PlaylistManager.getInstance().addSongToQueue(this.song);
					queue_item.setLabelText("Remove from Queue");
					Notification.getInstance().createNotification("Song", "The song '" + SongTitle.formatSongTitle(this.song.getFullSongTitle()) + "' was added to your queue!", AlertType.SUCCESS);
				} else {
					PlaylistManager.getInstance().removeSongFromQueue(this.song);
					queue_item.setLabelText("Add to Queue");
					Notification.getInstance().createNotification("Song", "The song '" + SongTitle.formatSongTitle(this.song.getFullSongTitle()) + "' was removed from your queue!", AlertType.SUCCESS);
				}
				if (this.playlist.isQueuePlaylist()) {
					PlaylistLoader.getInstance().loadPlaylist("queued-music");
				}
			});
			copy_url.setOnAction(event -> {
				Clipboard clipboard = Clipboard.getSystemClipboard();
				ClipboardContent content = new ClipboardContent();
				content.putString(this.song.getURL().replaceAll("]", ""));
				clipboard.setContent(content);
			});
			editMetadata.setOnAction(event -> {
				unselectSong();
				if (this.extraOptionButton.isVisible()) {
					this.extraOptionButton.setVisible(false);
				}
				Popup.editSongMetadata(this.playlist, this.song, new CompleteEvent() {
					public void onSuccess() {

					}

					public void onFail(String error) {

					}
				});
			});

			if (PlaylistManager.getInstance().getQueue().contains(song))
				queue_item.setLabelText("Remove from Queue");

			contextMenu.getItems().addAll(add_to_playlist, saveToLikes, queue_item);
			if (this.playlist.getRole() == RoleType.EDIT || this.playlist.getCreatorUsername().equals(UserManager.getInstance().getUser().username)) {
				if (!this.playlist.isQueuePlaylist() && !this.playlist.getURL().equals("recent-music") && !this.playlist.getURL().equals("my-likes")) {
					contextMenu.getItems().addAll(delete);
				} else {
					queue_item.getStyleClass().add("header");
				}
			}
			contextMenu.getItems().addAll(open_url, copy_url);
		}
		return contextMenu;
	}

}