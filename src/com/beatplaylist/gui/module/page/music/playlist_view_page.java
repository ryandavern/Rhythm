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

package com.beatplaylist.gui.module.page.music;

import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.lang3.StringEscapeUtils;

import com.beatplaylist.Main;
import com.beatplaylist.chromium.BrowserManager;
import com.beatplaylist.enums.FontType;
import com.beatplaylist.enums.PaddingSide;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.module.layout.sidebar.TabType;
import com.beatplaylist.gui.module.page.profile.ProfileLoader;
import com.beatplaylist.gui.utilities.FXUtilities;
import com.beatplaylist.gui.utilities.playlist.SongView;
import com.beatplaylist.settings.Settings;
import com.beatplaylist.utilities.CustomColor;
import com.beatplaylist.utilities.control.ContextItem;
import com.beatplaylist.utilities.data.Data;
import com.beatplaylist.utilities.events.CompleteEvent;
import com.beatplaylist.utilities.format.NumberFormat;
import com.beatplaylist.utilities.image.ImageManager;
import com.beatplaylist.utilities.network.post.FollowPlaylist;
import com.beatplaylist.utilities.network.post.SavePlaylist;
import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;
import com.beatplaylist.utilities.playlist.Playlist;
import com.beatplaylist.utilities.playlist.PlaylistManager;
import com.beatplaylist.utilities.playlist.Song;
import com.beatplaylist.utilities.popup.Popup;
import com.beatplaylist.utilities.user.UserManager;

import javafx.animation.FadeTransition;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

// This class displays a playlist and the playlist songs.
public class playlist_view_page {

	// Playlist being displayed
	public Playlist playlist;

	// Playlist Icon ImageView
	private ImageView iconImageView;

	// Playlist Name Text
	private Text playlistNameText, playlistDescriptionText;

	// Stores playlist icon, name text and description text.
	private HBox headerHBox, headerButtonHBox;

	// Stores playlist details such as name, description text, date created, etc.
	// songContainerVBox holds the songListScrollPane and songHBox.
	public VBox headerVBox, playlistDetailVBox, songContainerVBox, songListVBox;

	// Playlist created by textflow
	private TextFlow playlistCreateByTextFlow;
	private Text createdByStarterText, creatorUsernameText, songCountText, followerCountText;

	// Playlist playlist from start button and follow/unfollow/edit playlist button.
	private Button playPlaylistButton, updatePlaylistStateButton, extraPlaylistOptionButton;
	private TextField songFilterTextField;

	private ScrollPane songListScrollPane, playlistDescriptionScrollPane;

	// Playlist Song Grid Headers
	public HBox songHBox, songTitleHBox, songArtistHBox, songFeaturingHBox, dateAddedHBox;
	// Grid Header Text shown above songs in playlist.
	public Text titleText, artistText, featuringText, dateAddedText;

	public ContextMenu playlistContextMenu;

	public playlist_view_page() {
		this.headerHBox = new HBox(7);
		this.headerButtonHBox = new HBox(10);
		this.headerVBox = new VBox(25);
		this.playlistDetailVBox = new VBox(10);
		this.songListVBox = new VBox(0);
		this.songContainerVBox = new VBox();

		this.iconImageView = new ImageView();
		this.playlistNameText = new Text();
		this.playlistDescriptionText = new Text();

		this.createdByStarterText = new Text("Created by ");
		this.creatorUsernameText = new Text();
		this.songCountText = new Text();
		this.followerCountText = new Text();

		this.playlistCreateByTextFlow = new TextFlow(this.createdByStarterText, this.creatorUsernameText, this.songCountText, this.followerCountText);

		this.playPlaylistButton = new Button("PLAY") {
			public void requestFocus() {

			}
		};
		this.updatePlaylistStateButton = new Button("FOLLOW") {
			public void requestFocus() {

			}
		};
		this.extraPlaylistOptionButton = new Button("...") {
			public void requestFocus() {

			}
		};
		this.songFilterTextField = new TextField();

		this.songListScrollPane = new ScrollPane() {
			public void requestFocus() {

			}
		};
		this.playlistDescriptionScrollPane = new ScrollPane() {
			public void requestFocus() {

			}
		};

		this.songHBox = new HBox();
		this.songTitleHBox = new HBox();
		this.songArtistHBox = new HBox();
		this.songFeaturingHBox = new HBox();
		this.dateAddedHBox = new HBox();

		this.titleText = new Text("TITLE");
		this.artistText = new Text("ARTIST");
		this.featuringText = new Text("FEATURES");
		this.dateAddedText = new Text("DATE ADDED");

		configure();
		listen();
	}

	public void changePlaylist(Playlist playlist) {
		if (PlaylistManager.getInstance().storedSongView == null) {
			PlaylistManager.getInstance().storedSongView = new ArrayList<>();
		}
		// Clear storedSongView list.
		PlaylistManager.getInstance().storedSongView.clear();
		// Set current playing songview to null.
		PlaylistManager.getInstance().currentPlayingSongView = null;

		this.playlist = playlist;
		this.songListVBox.getChildren().clear();

		// this.iconImageView.setImage(new Image(Main.class.getResource("").toExternalForm()));

		this.playlistNameText.setText(StringEscapeUtils.unescapeJava(playlist.getName()));
		this.playlistDescriptionText.setText(StringEscapeUtils.unescapeJava(playlist.getDescription()));
		this.creatorUsernameText.setText(playlist.getCreatorDisplayName());
		this.songCountText.setText(" | " + (playlist.getSongCount()) + " songs");
		this.followerCountText.setText(" | " + NumberFormat.getFormattedNumber(playlist.getFollowerCount()) + " followers");

		if (playlist.getSongCount() == 1)
			this.songCountText.setText(" | " + playlist.getSongCount() + " song");

		if (playlist.getFollowerCount() == 1 || playlist.getFollowerCount() == 0)
			this.followerCountText.setText(" | 1 follower");

		if (playlist.getFeatureType().equals("YOUTUBE")) {
			this.createdByStarterText.setText("YouTube Playlist");
		} else {
			this.createdByStarterText.setText("Created by ");
		}

		// Unbind scrollpane height binds.
		this.songListScrollPane.minHeightProperty().unbind();
		this.songListScrollPane.maxHeightProperty().unbind();

		if (playlist.getDescription().isEmpty()) {
			// Remove playlist description from detail vbox as the playlist contains no description.
			if (this.playlistDetailVBox.getChildren().contains(this.playlistDescriptionText))
				this.playlistDetailVBox.getChildren().remove(this.playlistDescriptionText);

			// Update scrollpane height binds.
			this.songListScrollPane.minHeightProperty().bind(GUIManager.getInstance().contentManager.contentPane.heightProperty().subtract(195));
			this.songListScrollPane.maxHeightProperty().bind(GUIManager.getInstance().contentManager.contentPane.heightProperty().subtract(195));
		} else {
			// Add playlist description to detail vbox.
			if (!this.playlistDetailVBox.getChildren().contains(this.playlistDescriptionText))
				this.playlistDetailVBox.getChildren().add(2, this.playlistDescriptionText);

			double bindHeightValue = 225;
			// Update scrollpane height binds.
			if (playlist.getDescription().length() > 250) {
				bindHeightValue = 260;
			} else if (playlist.getDescription().length() > 155) {
				bindHeightValue = 240;
			}
			this.songListScrollPane.minHeightProperty().bind(GUIManager.getInstance().contentManager.contentPane.heightProperty().subtract(bindHeightValue));
			this.songListScrollPane.maxHeightProperty().bind(GUIManager.getInstance().contentManager.contentPane.heightProperty().subtract(bindHeightValue));
		}

		// Update playlist follow/unfollow button if user is following playlist.
		if (playlist.isFollowing()) {
			this.updatePlaylistStateButton.setStyle("-fx-background-color: " + CustomColor.RED.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-background-radius: 25px;");
			this.updatePlaylistStateButton.setText("UNFOLLOW");
		} else if (playlist.getCreatorUsername().equals(UserManager.getInstance().getUser().username)) {
			this.updatePlaylistStateButton.setStyle("-fx-background-color: " + CustomColor.RHYTHM.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-background-radius: 25px;");
			this.updatePlaylistStateButton.setText("EDIT");
		} else {
			this.updatePlaylistStateButton.setStyle("-fx-background-color: " + CustomColor.RHYTHM.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-background-radius: 25px;");
			this.updatePlaylistStateButton.setText("FOLLOW");
		}

		// If playlist was imported from YouTube, user can save the playlist but cannot follow.
		// Saving the playlist will create a new playlist.
		if (playlist.getFeatureType().equals("YOUTUBE")) {
			this.updatePlaylistStateButton.setStyle("-fx-background-color: " + CustomColor.RHYTHM.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-background-radius: 25px;");
			this.updatePlaylistStateButton.setText("SAVE");
		}

		this.playlistContextMenu = getPlaylistDropdownMenu(playlist);
		this.extraPlaylistOptionButton.setOnMouseClicked(event -> {
			if (this.playlistContextMenu != null && this.playlistContextMenu.isShowing()) {
				this.playlistContextMenu.hide();
			} else {
				Bounds boundsInScreen = this.extraPlaylistOptionButton.localToScreen(this.extraPlaylistOptionButton.getBoundsInLocal());
				this.playlistContextMenu.show(this.extraPlaylistOptionButton, boundsInScreen.getMaxX() - 5, boundsInScreen.getMaxY() - 5);
			}
		});

		displaySongs("");

		// Add header vbox to contentPane.
		if (!GUIManager.getInstance().contentManager.contentPane.getChildren().contains(this.headerVBox))
			GUIManager.getInstance().contentManager.contentPane.getChildren().add(this.headerVBox);

		FadeTransition fadeTransition = new FadeTransition(Duration.millis(150), this.headerVBox);
		fadeTransition.setFromValue(0.0);
		fadeTransition.setToValue(1.0);
		fadeTransition.play();

		// Add album cover to playlist if the playlist is a featured album or contains image art.
		if (!playlist.getFeatureType().equals("NONE") && !playlist.getFeatureType().equals("YOUTUBE") && !playlist.isSpecialPlaylist()) {
			ImageManager.getAlbumImage(this.iconImageView, playlist.getURL() + "-cover", 150, 150);
			// Add playlist icon image if playlist is featured and icon isn't already added.
			if (!this.headerHBox.getChildren().contains(this.iconImageView))
				this.headerHBox.getChildren().add(0, this.iconImageView);
			// If the previous loaded playlist was an album or contained a custom icon, we need to update the position of the header button hbox.
			if (this.headerVBox.getChildren().contains(this.headerButtonHBox))
				this.headerVBox.getChildren().remove(this.headerButtonHBox);
			if (!this.playlistDetailVBox.getChildren().contains(this.headerButtonHBox))
				this.playlistDetailVBox.getChildren().add(this.headerButtonHBox);
		} else {
			// Remove playlist image if playlist isn't featured.
			if (this.headerHBox.getChildren().contains(this.iconImageView))
				this.headerHBox.getChildren().remove(this.iconImageView);

			// If the previous loaded playlist was not an album or didn't contained a custom icon, we need to update the position of the header button hbox and move it to the right so the song list doesn't go off the screen.
			if (this.playlistDetailVBox.getChildren().contains(this.headerButtonHBox))
				this.playlistDetailVBox.getChildren().remove(this.headerButtonHBox);

			if (!this.headerVBox.getChildren().contains(this.headerButtonHBox))
				this.headerVBox.getChildren().add(1, this.headerButtonHBox);
		}

		if (this.playlist.isQueuePlaylist() || this.playlist.getURL().equals("youtube-top-trending") || this.playlist.isSpecialPlaylist()) {
			this.headerButtonHBox.getChildren().remove(this.updatePlaylistStateButton);
			this.playlistCreateByTextFlow.getChildren().remove(this.followerCountText);
		} else {
			if (!this.headerButtonHBox.getChildren().contains(this.updatePlaylistStateButton))
				this.headerButtonHBox.getChildren().add(1, this.updatePlaylistStateButton);
			if (!this.playlistCreateByTextFlow.getChildren().contains(this.followerCountText))
				this.playlistCreateByTextFlow.getChildren().add(this.followerCountText);
		}

		if (!this.playlist.getURL().startsWith("https://"))
			GUIManager.getInstance().topBar.titleBar.setSearchTextFieldTextAndUpdateCaret("/playlist/" + this.playlist.getURL());
		else
			GUIManager.getInstance().topBar.titleBar.setSearchTextFieldTextAndUpdateCaret(this.playlist.getURL());

		this.songListScrollPane.setVvalue(0);
	}

	private void configure() {
		FXUtilities.setNodePadding(this.headerVBox, GUIManager.getInstance().padding);

		// Playlist Name Text Styles
		FXUtilities.setTextDefault(this.playlistNameText, Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 32), CustomColor.WHITE);

		// Playlist Description Styles
		FXUtilities.setTextDefault(this.playlistDescriptionText, Font.font(FontType.VERDANA.getName(), 14), CustomColor.GRAY);
		this.playlistDescriptionText.setWrappingWidth(800);

		this.playlistDescriptionScrollPane.setContent(this.playlistDescriptionText);
		this.playlistDescriptionScrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		this.playlistDescriptionScrollPane.setMaxHeight(35);
		this.playlistDescriptionScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		this.playlistDescriptionScrollPane.maxWidthProperty().bind(GUIManager.getInstance().contentManager.contentPane.widthProperty().subtract(300));
		// this.playlistDescriptionText.wrappingWidthProperty().bind(this.playlistDescriptionScrollPane.widthProperty());
		FXUtilities.setNodePadding(this.playlistDescriptionScrollPane, 20, PaddingSide.RIGHT, "-fx-background-color: transparent;");

		// Playlist created by styles
		FXUtilities.setTextDefault(this.createdByStarterText, Font.font(FontType.VERDANA.getName(), 14), CustomColor.GRAY);

		// Playlist creator username styles
		FXUtilities.setTextDefault(this.creatorUsernameText, Font.font(FontType.VERDANA.getName(), 14), CustomColor.WHITE, Cursor.HAND);

		// Song count text styles
		FXUtilities.setTextDefault(this.songCountText, Font.font(FontType.VERDANA.getName(), 14), CustomColor.GRAY);

		// Follower count text styles
		FXUtilities.setTextDefault(this.followerCountText, Font.font(FontType.VERDANA.getName(), 14), CustomColor.GRAY);

		this.playPlaylistButton.setStyle("-fx-background-color: " + CustomColor.RHYTHM.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-background-radius: 25px;");
		this.playPlaylistButton.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 14));
		this.playPlaylistButton.setMinSize(110, 25);

		this.updatePlaylistStateButton.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 14));
		this.updatePlaylistStateButton.setMinSize(110, 25);

		this.extraPlaylistOptionButton.setStyle("-fx-background-color: transparent; -fx-border-color: " + CustomColor.DROPDOWN_MENU_COLOR.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-border-radius: 25px; -fx-background-radius: 25px;");
		this.extraPlaylistOptionButton.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 14));

		// Song ScrollPane styles
		this.songListScrollPane.setContent(this.songListVBox);
		this.songListScrollPane.setFitToHeight(true);
		this.songListScrollPane.setFitToWidth(true);
		this.songListScrollPane.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + ";");
		this.songListScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		this.songListScrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		this.songListScrollPane.minWidthProperty().bind(GUIManager.getInstance().contentManager.contentPane.widthProperty().subtract(100));
		this.songListScrollPane.maxWidthProperty().bind(GUIManager.getInstance().contentManager.contentPane.widthProperty().subtract(100));

		this.songListVBox.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + ";");
		this.songListVBox.minWidthProperty().bind(this.songListScrollPane.widthProperty());
		this.songListVBox.prefWidthProperty().bind(this.songListScrollPane.widthProperty());
		this.songListVBox.prefHeightProperty().bind(this.songListScrollPane.heightProperty());

		this.playlistNameText.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		this.playlistNameText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 32));

		// Header Styles
		this.songTitleHBox.minWidthProperty().bind(this.songListVBox.widthProperty().divide(4));
		this.songTitleHBox.maxWidthProperty().bind(this.songListVBox.widthProperty().divide(4));
		FXUtilities.setNodePadding(this.songTitleHBox, 15, PaddingSide.LEFT);

		this.titleText.setFont(Font.font(FontType.VERDANA.getName(), 15));
		this.titleText.setFill(Color.web(CustomColor.WHITE.getColorHex()));

		this.songArtistHBox.minWidthProperty().bind(this.songListVBox.widthProperty().divide(4));
		this.songArtistHBox.maxWidthProperty().bind(this.songListVBox.widthProperty().divide(4));
		FXUtilities.setNodePadding(this.songArtistHBox, 15, PaddingSide.LEFT);

		FXUtilities.setTextDefault(this.artistText, Font.font(FontType.VERDANA.getName(), 15), Color.web("#6e6e6e"));

		this.songFeaturingHBox.minWidthProperty().bind(this.songListVBox.widthProperty().divide(4));
		this.songFeaturingHBox.maxWidthProperty().bind(this.songListVBox.widthProperty().divide(4));

		FXUtilities.setTextDefault(this.featuringText, Font.font(FontType.VERDANA.getName(), 15), Color.web("#6e6e6e"));

		this.dateAddedHBox.minWidthProperty().bind(this.songListVBox.widthProperty().divide(4));
		this.dateAddedHBox.maxWidthProperty().bind(this.songListVBox.widthProperty().divide(4));

		FXUtilities.setTextDefault(this.dateAddedText, Font.font(FontType.VERDANA.getName(), 15), Color.web("#6e6e6e"));

		this.songFilterTextField.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-text-fill: #fff; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%); -fx-background-image:url('" + Main.class.getResource("/resources/icons/v2/song_filter_search_icon.png") + "'); -fx-background-repeat: no-repeat; -fx-background-position: 15px 50%;");
		this.songFilterTextField.minWidthProperty().bind(this.songListVBox.widthProperty());
		this.songFilterTextField.maxWidthProperty().bind(this.songListVBox.widthProperty());
		this.songFilterTextField.setMinHeight(35);
		this.songFilterTextField.setFont(Font.font(FontType.DEFAULT.getName(), 15));
		this.songFilterTextField.setPromptText("Filter for a specific song...");
		FXUtilities.setNodePadding(this.songFilterTextField, 40, PaddingSide.LEFT, true);

		// Add header
		this.songTitleHBox.getChildren().add(this.titleText);
		this.songArtistHBox.getChildren().add(this.artistText);
		this.songFeaturingHBox.getChildren().add(this.featuringText);
		this.dateAddedHBox.getChildren().add(this.dateAddedText);

		this.songHBox.getChildren().addAll(this.songTitleHBox, this.songArtistHBox, this.songFeaturingHBox, this.dateAddedHBox);

		// Add playlist name text and create by text to detail vbox.
		this.playlistDetailVBox.getChildren().addAll(this.playlistNameText, this.playlistCreateByTextFlow);

		// Add playlist icon and detail vbox to header hbox
		this.headerHBox.getChildren().addAll(this.iconImageView, this.playlistDetailVBox);
		// Add song title bar hbox and playlist song list to song container
		this.songContainerVBox.getChildren().addAll(this.songHBox, this.songListScrollPane);

		this.headerButtonHBox.getChildren().addAll(this.playPlaylistButton, this.updatePlaylistStateButton, this.extraPlaylistOptionButton);
		// Add header hbox, playlist play button and song container vbox to header vbox.
		this.headerVBox.getChildren().addAll(this.headerHBox, this.headerButtonHBox, this.songContainerVBox);

	}

	private void listen() {
		this.songFilterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (this.playlist.getSongCount() == 0)
				return;
			String latest = this.songFilterTextField.getText();

			this.songListVBox.getChildren().clear();
			this.playlist.setLoadedSongs(0);

			displaySongs(latest);
		});
		this.creatorUsernameText.setOnMouseClicked(event -> {
			ProfileLoader.loadProfile(this.playlist.getCreatorUsername());
		});
		this.creatorUsernameText.setOnMouseEntered(event -> {
			this.creatorUsernameText.setFill(Color.web(Settings.getInstance().getDefaultColor()));
		});
		this.creatorUsernameText.setOnMouseExited(event -> {
			this.creatorUsernameText.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		});
		this.playPlaylistButton.setOnMouseClicked(event -> {
			playFirstSongInPlaylist();
		});
		this.playPlaylistButton.setOnMouseEntered(event -> {
			this.playPlaylistButton.setStyle("-fx-background-color: " + CustomColor.DARK_GREEN.getColorHex() + "; -fx-cursor: hand; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-background-radius: 25px;");
		});
		this.playPlaylistButton.setOnMouseExited(event -> {
			this.playPlaylistButton.setStyle("-fx-background-color: " + CustomColor.RHYTHM.getColorHex() + "; -fx-cursor: hand; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-background-radius: 25px;");
		});
		this.updatePlaylistStateButton.setOnMouseEntered(event -> {
			if (this.playlist.isFollowing())
				this.updatePlaylistStateButton.setStyle("-fx-background-color: " + CustomColor.DARK_RED.getColorHex() + "; -fx-cursor: hand; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-background-radius: 25px;");
			else
				this.updatePlaylistStateButton.setStyle("-fx-background-color: " + CustomColor.DARK_GREEN.getColorHex() + "; -fx-cursor: hand; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-background-radius: 25px;");

		});
		this.updatePlaylistStateButton.setOnMouseExited(event -> {
			if (this.playlist.isFollowing())
				this.updatePlaylistStateButton.setStyle("-fx-background-color: " + CustomColor.RED.getColorHex() + "; -fx-cursor: hand; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-background-radius: 25px;");
			else
				this.updatePlaylistStateButton.setStyle("-fx-background-color: " + CustomColor.RHYTHM.getColorHex() + "; -fx-cursor: hand; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-background-radius: 25px;");
		});
		this.updatePlaylistStateButton.setOnMouseClicked(event -> {
			if (this.playlist.getFeatureType().equals("YOUTUBE")) {
				SavePlaylist.send(this.playlist.getURL());
			} else if (this.playlist.getCreatorUsername().equals(UserManager.getInstance().getUser().username)) {
				editPlaylist();
			} else {
				// Handle playlist following / unfollowing.
				FollowPlaylist.send(this.playlist, this.updatePlaylistStateButton);
			}
		});

		this.extraPlaylistOptionButton.setOnMouseEntered(event -> {
			this.extraPlaylistOptionButton.setStyle("-fx-cursor: hand; -fx-background-color: transparent; -fx-border-color: " + CustomColor.DROPDOWN_MENU_COLOR.getColorHex() + "; -fx-text-fill: " + CustomColor.RHYTHM.getColorHex() + "; -fx-border-radius: 25px; -fx-background-radius: 25px;");
		});
		this.extraPlaylistOptionButton.setOnMouseExited(event -> {
			this.extraPlaylistOptionButton.setStyle("-fx-background-color: transparent; -fx-border-color: " + CustomColor.DROPDOWN_MENU_COLOR.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-border-radius: 25px; -fx-background-radius: 25px;");
		});
		this.songListVBox.setOnScroll(event -> {
			double deltaY = event.getDeltaY() * 4; // *6 to make the scrolling a bit faster
			double height = this.songListScrollPane.getContent().getBoundsInLocal().getHeight();
			double vvalue = this.songListScrollPane.getVvalue();
			if (height < 1000)
				this.songListScrollPane.setVvalue(vvalue + -deltaY / height); // deltaY/width to make the scrolling equally fast regardless of the actual height of the component
		});

	}

	public void displaySongs(String filter) {
		int index = 0, loaded = 0;
		for (Song song : this.playlist.getSongs()) {
			if (index >= this.playlist.getLoadedSongs()) {
				// if (loaded < 100) {
				if (filter.isEmpty()) {
					new SongView(this.playlist, song, loaded, "");
					loaded++;
				} else {
					if (song.getFullSongTitle().replaceAll("\\s", "").toLowerCase().contains(filter.replaceAll("\\s", "").toLowerCase())) {
						new SongView(this.playlist, song, loaded, filter);
						loaded++;
					}
				}
				// }
			}
			index++;
		}
		this.playlist.setLoadedSongs(loaded);
		this.songListVBox.getChildren().add(0, this.songFilterTextField);
	}

	private ContextMenu getPlaylistDropdownMenu(Playlist playlist) {
		ContextMenu contextMenu = new ContextMenu();
		contextMenu.setStyle("-fx-cursor: hand; -fx-background-color: " + CustomColor.DROPDOWN_MENU_COLOR.getColorHex() + "; -fx-background-radius: 5px;");

		ContextItem changeSongOrder = new ContextItem("Change Song Order"), copyPlaylistURL = new ContextItem("Copy Playlist URL");
		changeSongOrder.label.setMinSize(200, 35);
		copyPlaylistURL.label.setMinSize(200, 35);

		contextMenu.getItems().add(copyPlaylistURL);

		if (playlist.getCreatorUsername().equals(UserManager.getInstance().getUser().username) && !playlist.isSpecialPlaylist()) {
			ContextItem deletePlaylist = new ContextItem("Delete Playlist"), editPlaylist = new ContextItem("Edit Playlist"), addGenre = new ContextItem("Edit Genres"), tags = new ContextItem("Edit Tags");

			deletePlaylist.label.setMinSize(200, 35);
			editPlaylist.label.setMinSize(200, 35);
			addGenre.label.setMinSize(200, 35);
			tags.label.setMinSize(200, 35);

			deletePlaylist.setOnAction(event -> {
				if (!playlist.getCreatorUsername().equals(UserManager.getInstance().getUser().username))
					return;
				Popup.confirmDeletePlaylist(playlist);
			});
			editPlaylist.setOnAction(event -> {
				if (!playlist.getCreatorUsername().equals(UserManager.getInstance().getUser().username))
					return;
				editPlaylist();
			});
			addGenre.setOnAction(event -> {
				if (!playlist.getCreatorUsername().equals(UserManager.getInstance().getUser().username))
					return;
				PlaylistManager.getInstance().changePlaylistGenres(playlist);
			});
			tags.setOnAction(event -> {
				if (!playlist.getCreatorUsername().equals(UserManager.getInstance().getUser().username))
					return;
				PlaylistManager.getInstance().updatePlaylistTags(playlist);
			});
			contextMenu.getItems().addAll(editPlaylist, addGenre, tags, changeSongOrder, deletePlaylist);
		} else {
			ContextItem viewTags = new ContextItem("View Genres & Tags");
			viewTags.label.setMinSize(200, 35);

			viewTags.setOnAction(event -> {
				PlaylistManager.getInstance().viewTagAndGenre(playlist);
			});

			contextMenu.getItems().addAll(changeSongOrder, viewTags);
		}
		changeSongOrder.setOnAction(event -> {
			Popup.changeSongOrder(this.playlist, new CompleteEvent() {

				@Override
				public void onSuccess() {
					GUIManager.getInstance().sideBar.sideBarTab.changeTab(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.OPEN_PLAYLIST_VIEW), playlist.getURL());
				}

				@Override
				public void onFail(String error) {
				}
			});
		});
		copyPlaylistURL.setOnAction(event -> {
			Clipboard clipboard = Clipboard.getSystemClipboard();
			ClipboardContent content = new ClipboardContent();
			if (playlist.getFeatureType().equals("YOUTUBE"))
				content.putString(playlist.getURL());
			else
				content.putString("/playlist/" + playlist.getURL());
			clipboard.setContent(content);
			Notification.getInstance().createNotification("Playlist", "Playlist URL copied to clipboard", AlertType.SUCCESS);
		});
		return contextMenu;
	}

	// Calls editPlaylist popup.
	private void editPlaylist() {
		PlaylistManager.getInstance().editPlaylist(this.playlist, new CompleteEvent() {

			@Override
			public void onSuccess() {
				playlistNameText.setText(playlist.getName());
				playlistDescriptionText.setText(playlist.getDescription());
				// Unbind scrollpane height binds.
				songListScrollPane.minHeightProperty().unbind();
				songListScrollPane.maxHeightProperty().unbind();

				if (playlist.getDescription().isEmpty()) {
					// Remove playlist description from detail vbox as the playlist contains no description.
					if (playlistDetailVBox.getChildren().contains(playlistDescriptionText))
						playlistDetailVBox.getChildren().remove(playlistDescriptionText);

					// Update scrollpane height binds.
					songListScrollPane.minHeightProperty().bind(GUIManager.getInstance().contentManager.contentPane.heightProperty().subtract(195));
					songListScrollPane.maxHeightProperty().bind(GUIManager.getInstance().contentManager.contentPane.heightProperty().subtract(195));
				} else {
					// Add playlist description to detail vbox.
					if (!playlistDetailVBox.getChildren().contains(playlistDescriptionText))
						playlistDetailVBox.getChildren().add(2, playlistDescriptionText);

					double bindHeightValue = 225;
					// Update scrollpane height binds.
					if (playlist.getDescription().length() > 155) {
						bindHeightValue = 240;
					}
					songListScrollPane.minHeightProperty().bind(GUIManager.getInstance().contentManager.contentPane.heightProperty().subtract(bindHeightValue));
					songListScrollPane.maxHeightProperty().bind(GUIManager.getInstance().contentManager.contentPane.heightProperty().subtract(bindHeightValue));
				}
			}

			@Override
			public void onFail(String error) {

			}
		});
	}

	// Plays first song in playlist.
	private void playFirstSongInPlaylist() {
		if (PlaylistManager.getInstance().storedSongView.isEmpty()) {
			return;
		}
		if (GUIManager.getInstance().audioBar.audioBar.backToPlayingPlaylistImage.getHBox().isDisabled()) {
			GUIManager.getInstance().audioBar.audioBar.backToPlayingPlaylistImage.getHBox().setDisable(false);
			GUIManager.getInstance().audioBar.audioBar.backToPlayingPlaylistImage.getHBox().setOpacity(1);
		}
		PlaylistManager.getInstance().current_playlist = this.playlist;

		// Update current playing song view styles and class.
		if (PlaylistManager.getInstance().currentPlayingSongView != null) {
			PlaylistManager.getInstance().currentPlayingSongView.unselectSong();
		}

		// If shuffle is enabled, first song will be random anyway.
		PlaylistManager.getInstance().currentPlayingSongView = PlaylistManager.getInstance().storedSongView.get(0);
		PlaylistManager.getInstance().currentPlayingSongView.selectSong();
		this.playlist.setPosition(0);

		// Set audiobar to loading
		GUIManager.getInstance().audioBar.audioBar.setLoading(true);

		BrowserManager.getInstance().getCurrentBrowser().getWebEngine().navigation().loadUrl(PlaylistManager.getInstance().currentPlayingSongView.song.getURL());
	}
}