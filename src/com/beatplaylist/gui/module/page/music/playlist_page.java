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
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONObject;

import com.beatplaylist.Main;
import com.beatplaylist.enums.FontType;
import com.beatplaylist.enums.PaddingSide;
import com.beatplaylist.enums.ResourceIcon;
import com.beatplaylist.enums.SpecialPlaylistType;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.module.layout.sidebar.TabType;
import com.beatplaylist.gui.module.page.profile.ProfileLoader;
import com.beatplaylist.gui.utilities.FXUtilities;
import com.beatplaylist.gui.utilities.playlist.PlaylistVBox;
import com.beatplaylist.gui.utilities.playlist.SongTitle;
import com.beatplaylist.settings.Settings;
import com.beatplaylist.utilities.CustomColor;
import com.beatplaylist.utilities.CustomToolTip;
import com.beatplaylist.utilities.control.BorderTransition;
import com.beatplaylist.utilities.control.ContextItem;
import com.beatplaylist.utilities.control.EvenSpacedHBox;
import com.beatplaylist.utilities.control.HeaderTextWithButton;
import com.beatplaylist.utilities.data.Data;
import com.beatplaylist.utilities.events.CompleteEvent;
import com.beatplaylist.utilities.events.SocketReceiveEvent;
import com.beatplaylist.utilities.image.ImageBuilder;
import com.beatplaylist.utilities.image.ImageManager;
import com.beatplaylist.utilities.network.get.GetLikedMusicCount;
import com.beatplaylist.utilities.network.netty.NETTYClient;
import com.beatplaylist.utilities.network.post.FollowPlaylist;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.network.serialized.PacketType;
import com.beatplaylist.utilities.network.serialized.Post;
import com.beatplaylist.utilities.playlist.Playlist;
import com.beatplaylist.utilities.playlist.PlaylistManager;
import com.beatplaylist.utilities.popup.Popup;
import com.beatplaylist.utilities.user.UserManager;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class playlist_page {

	// Page layout
	// New Playlist | Merge Playlist - hbox
	// Playlist Items - scrollpane

	// Test variables that will change the environment of the playlist page. These settings allow for ease of testing.

	private boolean testingNoCreatedPlaylists = false, testingNoFollowedPlaylists = false, testingNoFeaturedPlaylists = false;

	// Non test variables/objects below:

	private ScrollPane playlistScrollPane;

	private FlowPane yourPlaylistFlowPane, followedPlaylistFlowPane, newReleasePlaylistFlowPane, yearEndTopPlaylistFlowPane;
	private HBox newReleaseTextHBox, topButtons;
	private VBox containerVBox, yourPlaylistVBox, yourPlaylistHeaderVBox, followedPlaylistVBox, newReleasePlaylistVBox, yearEndTopPlaylistVBox;
	private Text newReleasePlaylistText, newReleaseViewMoreText, yearEndTopPlaylistText;
	private HeaderTextWithButton yourPlaylistText, followedPlaylistText;
	private List<String> loadedColorWheel;
	private Button createPlaylistButton, mergePlaylistButton;

	public playlist_page() {
		this.playlistScrollPane = new ScrollPane();

		this.containerVBox = new VBox(60);

		this.yourPlaylistVBox = new VBox(5);
		this.yourPlaylistHeaderVBox = new VBox(25);
		this.followedPlaylistVBox = new VBox(5);
		this.newReleasePlaylistVBox = new VBox(5);
		this.yearEndTopPlaylistVBox = new VBox(5);

		this.yourPlaylistFlowPane = new FlowPane();
		this.followedPlaylistFlowPane = new FlowPane();
		this.newReleasePlaylistFlowPane = new FlowPane();
		this.yearEndTopPlaylistFlowPane = new FlowPane();

		this.newReleaseTextHBox = new HBox(15);
		this.topButtons = new HBox(10);

		this.yourPlaylistText = new HeaderTextWithButton("YOUR PLAYLISTS", getPlaylistOrderButton(true));
		this.followedPlaylistText = new HeaderTextWithButton("FOLLOWED PLAYLISTS", getPlaylistOrderButton(false));
		this.newReleasePlaylistText = new Text("FEATURED NEW RELEASES");
		this.newReleaseViewMoreText = new Text("BROWSE ALL");
		this.yearEndTopPlaylistText = new Text("TOP 50 OF THE YEAR");

		this.loadedColorWheel = new ArrayList<>();

		this.createPlaylistButton = new Button("Create Playlist");
		this.mergePlaylistButton = new Button("Merge Playlist");

		// Clear current cache of playlists.
		PlaylistManager.getInstance().getPlaylists().clear();

		// Configure page styles.
		configure();
		listen();

	}

	private void configure() {
		FXUtilities.setNodePadding(this.containerVBox, GUIManager.getInstance().padding);

		this.playlistScrollPane.setContent(this.containerVBox);
		this.playlistScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		this.playlistScrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		this.playlistScrollPane.setStyle("-fx-background-color: " + CustomColor.TRANSPARENT.getColorHex() + ";");
		this.playlistScrollPane.minWidthProperty().bind(GUIManager.getInstance().contentManager.contentPane.widthProperty().subtract(100));
		this.playlistScrollPane.maxWidthProperty().bind(GUIManager.getInstance().contentManager.contentPane.widthProperty().subtract(100));
		this.playlistScrollPane.minHeightProperty().bind(GUIManager.getInstance().contentManager.contentPane.heightProperty().subtract(10));
		this.playlistScrollPane.maxHeightProperty().bind(GUIManager.getInstance().contentManager.contentPane.heightProperty().subtract(10));

		this.yourPlaylistFlowPane.setHgap(20);
		this.yourPlaylistFlowPane.setVgap(20);
		this.yourPlaylistFlowPane.maxWidthProperty().bind(this.playlistScrollPane.widthProperty().subtract(100));
		FXUtilities.setNodePadding(this.yourPlaylistFlowPane, 60, PaddingSide.BOTTOM, "-fx-border-color: #4E4E58; -fx-border-width: 0 0 2 0;");

		this.followedPlaylistFlowPane.setHgap(20);
		this.followedPlaylistFlowPane.setVgap(20);
		this.followedPlaylistFlowPane.maxWidthProperty().bind(this.playlistScrollPane.widthProperty().subtract(100));
		FXUtilities.setNodePadding(this.followedPlaylistFlowPane, 60, PaddingSide.BOTTOM, "-fx-border-color: #4E4E58; -fx-border-width: 0 0 2 0;");

		this.newReleasePlaylistFlowPane.setHgap(20);
		this.newReleasePlaylistFlowPane.setVgap(20);
		this.newReleasePlaylistFlowPane.maxWidthProperty().bind(this.playlistScrollPane.widthProperty().subtract(100));
		FXUtilities.setNodePadding(this.newReleasePlaylistFlowPane, 60, PaddingSide.BOTTOM, "-fx-border-color: #4E4E58; -fx-border-width: 0 0 2 0;");

		this.yearEndTopPlaylistFlowPane.setHgap(20);
		this.yearEndTopPlaylistFlowPane.setVgap(20);
		this.yearEndTopPlaylistFlowPane.maxWidthProperty().bind(this.playlistScrollPane.widthProperty().subtract(100));

		this.yourPlaylistText.getText().setFont(Font.font(FontType.VERDANA.getName(), FontWeight.BOLD, 20));
		this.yourPlaylistText.getText().setFill(Color.web(CustomColor.WHITE.getColorHex()));

		this.followedPlaylistText.getText().setFont(Font.font(FontType.VERDANA.getName(), FontWeight.BOLD, 20));
		this.followedPlaylistText.getText().setFill(Color.web(CustomColor.WHITE.getColorHex()));

		this.newReleasePlaylistText.setFont(Font.font(FontType.VERDANA.getName(), FontWeight.BOLD, 20));
		this.newReleasePlaylistText.setFill(Color.web(CustomColor.WHITE.getColorHex()));

		this.yearEndTopPlaylistText.setFont(Font.font(FontType.VERDANA.getName(), FontWeight.BOLD, 20));
		this.yearEndTopPlaylistText.setFill(Color.web(CustomColor.WHITE.getColorHex()));

		// Load user created and followed playlists.
		loadPlaylists(false);

		// Load featured playlists.
		loadPlaylists(true);

		this.createPlaylistButton.setStyle("-fx-background-color: transparent; -fx-border-color: " + CustomColor.DROPDOWN_MENU_COLOR.getColorHex() + "; -fx-border-width: 2px; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-padding: 5px 20px 5px 5px;");
		this.createPlaylistButton.setFont(Font.font(FontType.VERDANA.getName(), 16));
		this.createPlaylistButton.setMinSize(150, 35);
		this.createPlaylistButton.setGraphic(new ImageView(new Image(Main.class.getResource(ResourceIcon.AUDIO_BAR.getPath() + "add_song_to_playlist_small_icon.png").toExternalForm(), 25, 25, false, false)));
		this.createPlaylistButton.setCursor(Cursor.HAND);
		this.createPlaylistButton.setTextFill(Color.web(CustomColor.GRAY.getColorHex()));

		this.mergePlaylistButton.setStyle("-fx-background-color: transparent; -fx-border-color: " + CustomColor.DROPDOWN_MENU_COLOR.getColorHex() + "; -fx-border-width: 2px; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-padding: 5px 20px 5px 5px;");
		this.mergePlaylistButton.setFont(Font.font(FontType.VERDANA.getName(), 16));
		this.mergePlaylistButton.setMinSize(150, 35);
		this.mergePlaylistButton.setGraphic(new ImageView(new Image(Main.class.getResource(ResourceIcon.V2.getPath() + "merge.png").toExternalForm(), 25, 25, false, false)));
		this.mergePlaylistButton.setCursor(Cursor.HAND);
		this.mergePlaylistButton.setTextFill(Color.web(CustomColor.GRAY.getColorHex()));

		this.newReleaseTextHBox.setAlignment(Pos.CENTER_LEFT);
		this.newReleaseTextHBox.setMaxWidth(150);

		FXUtilities.setTextDefault(this.newReleaseViewMoreText, Font.font(FontType.VERDANA.getName(), 14), CustomColor.RHYTHM, Cursor.HAND);

		this.topButtons.getChildren().addAll(this.createPlaylistButton, this.mergePlaylistButton);

		this.newReleaseTextHBox.getChildren().addAll(this.newReleasePlaylistText, this.newReleaseViewMoreText);

		this.yourPlaylistHeaderVBox.getChildren().addAll(this.topButtons, this.yourPlaylistText.getHBox());
		this.yourPlaylistVBox.getChildren().addAll(this.yourPlaylistHeaderVBox, this.yourPlaylistFlowPane);
		this.followedPlaylistVBox.getChildren().addAll(this.followedPlaylistText.getHBox(), this.followedPlaylistFlowPane);
		this.newReleasePlaylistVBox.getChildren().addAll(this.newReleaseTextHBox, this.newReleasePlaylistFlowPane);
		this.yearEndTopPlaylistVBox.getChildren().addAll(this.yearEndTopPlaylistText, this.yearEndTopPlaylistFlowPane);

		this.containerVBox.minWidthProperty().bind(this.playlistScrollPane.widthProperty());
		this.containerVBox.maxWidthProperty().bind(this.playlistScrollPane.widthProperty());
		this.containerVBox.getChildren().addAll(this.yourPlaylistVBox, this.newReleasePlaylistVBox, this.yearEndTopPlaylistVBox);

		GUIManager.getInstance().contentManager.contentPane.getChildren().add(this.playlistScrollPane);

		FadeTransition fadeTransition = new FadeTransition(Duration.millis(150), this.playlistScrollPane);
		fadeTransition.setFromValue(0.0);
		fadeTransition.setToValue(1.0);
		fadeTransition.play();

		addSpecialPlaylists();
		addMyLikesPlaylist();
	}

	private void listen() {
		this.containerVBox.setOnScroll(event -> {

			double deltaY = event.getDeltaY() * 2; // *6 to make the scrolling a bit faster
			double height = this.playlistScrollPane.getContent().getBoundsInLocal().getHeight();
			double vvalue = this.playlistScrollPane.getVvalue();
			// Only apply changes to small scrollpanes.
			this.playlistScrollPane.setVvalue(vvalue + -deltaY / height); // deltaY/width to make the scrolling equally fast regardless of the actual height of the component
		});
		BorderTransition transition = new BorderTransition(this.createPlaylistButton, Color.web(CustomColor.DROPDOWN_MENU_COLOR.getColorHex()), Color.web(Settings.getInstance().getDefaultColor()), 2, new CompleteEvent() {

			@Override
			public void onSuccess() {
				createPlaylistButton.setGraphic(new ImageView(new Image(Main.class.getResource(ResourceIcon.AUDIO_BAR.getPath() + "add_song_to_playlist_hover_small_icon.png").toExternalForm(), 25, 25, false, false)));
				createPlaylistButton.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
			}

			@Override
			public void onFail(String error) {
			}
		});
		BorderTransition transition2 = new BorderTransition(this.mergePlaylistButton, Color.web(CustomColor.DROPDOWN_MENU_COLOR.getColorHex()), Color.web(Settings.getInstance().getDefaultColor()), 2, new CompleteEvent() {

			@Override
			public void onSuccess() {
				mergePlaylistButton.setGraphic(new ImageView(new Image(Main.class.getResource(ResourceIcon.V2.getPath() + "merge_hover.png").toExternalForm(), 25, 25, false, false)));
				mergePlaylistButton.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
			}

			@Override
			public void onFail(String error) {
			}
		});
		this.createPlaylistButton.setOnMouseExited(event -> {
			transition.fillTransition.stop();
			this.createPlaylistButton.setStyle("-fx-background-color: transparent; -fx-border-color: " + CustomColor.DROPDOWN_MENU_COLOR.getColorHex() + "; -fx-border-width: 2px; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-padding: 5px 20px 5px 5px;");
			this.createPlaylistButton.setGraphic(new ImageView(new Image(Main.class.getResource(ResourceIcon.AUDIO_BAR.getPath() + "add_song_to_playlist_small_icon.png").toExternalForm(), 25, 25, false, false)));
			this.createPlaylistButton.setTextFill(Color.web(CustomColor.GRAY.getColorHex()));
		});
		this.mergePlaylistButton.setOnMouseExited(event -> {
			transition2.fillTransition.stop();
			this.mergePlaylistButton.setStyle("-fx-background-color: transparent; -fx-border-color: " + CustomColor.DROPDOWN_MENU_COLOR.getColorHex() + "; -fx-border-width: 2px; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-padding: 5px 20px 5px 5px;");
			this.mergePlaylistButton.setGraphic(new ImageView(new Image(Main.class.getResource(ResourceIcon.V2.getPath() + "merge.png").toExternalForm(), 25, 25, false, false)));
			this.mergePlaylistButton.setTextFill(Color.web(CustomColor.GRAY.getColorHex()));
		});
		this.mergePlaylistButton.setOnMouseClicked(event -> {
			Popup.mergePlaylists(null);
		});
		this.createPlaylistButton.setOnMouseClicked(event -> {
			Popup.createPlaylist(new CompleteEvent() {

				@Override
				public void onSuccess() {
					// Empty created and followed playlists.
					yourPlaylistFlowPane.getChildren().clear();
					followedPlaylistFlowPane.getChildren().clear();
					addSpecialPlaylists();
					addMyLikesPlaylist();
					loadPlaylists(false);
				}

				@Override
				public void onFail(String error) {

				}
			});
		});
		this.newReleaseViewMoreText.setOnMouseEntered(event -> {
			this.newReleaseViewMoreText.setFill(Color.web(CustomColor.DARK_GREEN.getColorHex()));
		});
		this.newReleaseViewMoreText.setOnMouseExited(event -> {
			this.newReleaseViewMoreText.setFill(Color.web(CustomColor.RHYTHM.getColorHex()));
		});
		this.newReleaseViewMoreText.setOnMouseClicked(event -> {
			GUIManager.getInstance().sideBar.sideBarTab.changeTab(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.ALL_FEATURED_PLAYLIST_VIEW));
		});
	}

	// Boolean loadedFeaturedPlaylist set to true, will request the featured playlists from the server. Set to false will request the users created and followed playlists.
	private void loadPlaylists(boolean loadFeaturedPlaylists) {
		Post post = new Post();
		post.setDetails();
		if (loadFeaturedPlaylists)
			post.setPacketType(PacketType.GET_MOST_RECENT_FEATURED_PLAYLISTS);
		else
			post.setPacketType(PacketType.LOAD_PLAYLISTS);

		JSONObject object = new JSONObject();
		object.put("createdPlaylistOrder", Settings.getInstance().getCreatedPlaylistOrder().name());
		object.put("followedPlaylistOrder", Settings.getInstance().getFollowedPlaylistOrder().name());

		post.setJSONArray(object);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {
			@Override
			public void onSuccess(Post post) {
				JSONWrapper response = new JSONWrapper(post.getJSONMessage());
				Iterator<JSONObject> playlists = response.getJSONArray("playlists");

				while (playlists.hasNext()) {
					JSONWrapper wrapper = new JSONWrapper(playlists.next());

					String username = wrapper.getJSONString("username");
					String playlist_name = wrapper.getJSONString("playlist_name");
					String playlist_url = wrapper.getJSONString("playlist_url");
					int song_count = wrapper.getJSONInteger("song_count");

					if (!loadFeaturedPlaylists && PlaylistManager.getInstance().current_playlist != null && PlaylistManager.getInstance().current_playlist.getURL().equals(playlist_url)) {
						Platform.runLater(() -> {
							addPlaylistToDisplay(PlaylistManager.getInstance().current_playlist);
						});
					} else {

						Playlist playlist = new Playlist();

						playlist.setName(StringEscapeUtils.unescapeJava(playlist_name));
						playlist.setURL(playlist_url);
						playlist.setCreatorUsername(username);
						playlist.setSongCount(song_count);
						playlist.setFollowerCount(wrapper.getJSONInteger("follower_count"));
						playlist.setFollowing(wrapper.getJSONBoolean("isFollowing"));

						if (loadFeaturedPlaylists || username.toLowerCase().equals("beatplaylist")) {
							playlist.setFeatureType(wrapper.getJSONString("playlist_feature_type"));
						}
						// If playlist creator is user, add playlist to cached playlist.
						if (playlist.getCreatorUsername().equals(UserManager.getInstance().getUser().username)) {
							PlaylistManager.getInstance().addPlaylist(playlist);
						}

						Platform.runLater(() -> {
							addPlaylistToDisplay(playlist);
						});
					}
				}
			}

			@Override
			public void onError(String error) {
			}
		});
	}

	private void addPlaylistToDisplay(Playlist playlist) {
		PlaylistVBox playlistVBox = new PlaylistVBox(), contentVBox = new PlaylistVBox(), playlistImageContainerVBox = new PlaylistVBox(10), playlistTextVBox = new PlaylistVBox();

		EvenSpacedHBox footerHBox = new EvenSpacedHBox(), topHBox = new EvenSpacedHBox();
		HBox bottomBorderHBox = new HBox();

		playlistVBox.setMinSize(232, 232);
		playlistVBox.setMaxSize(232, 232);

		String randomColor = getRandomColor();
		if (Settings.getInstance().playlistHasCustomColor(playlist.getURL())) {
			String color = Settings.getInstance().getPlaylistColor(playlist.getURL());
			if (!color.equals("RANDOM")) {
				randomColor = color;
				System.out.println("Playlist Color for '" + playlist.getName() + "' is " + randomColor);
			}
		}

		Pane colorPane = new Pane(), bottomBorderPane = new Pane();
		colorPane.setStyle("-fx-background-color: " + randomColor + "; -fx-background-radius: 10px;");
		colorPane.setMinSize(80, 80);
		colorPane.setMaxSize(80, 80);

		bottomBorderHBox.getChildren().add(bottomBorderPane);
		bottomBorderHBox.setAlignment(Pos.CENTER);

		bottomBorderPane.setStyle("-fx-background-color: " + randomColor + "; -fx-background-radius: 10px; -fx-padding: 15px 15px 0px 15px;");
		bottomBorderPane.setMinSize(116, 5);
		bottomBorderPane.setMaxSize(116, 5);
		bottomBorderPane.layoutYProperty().bindBidirectional(playlistVBox.minHeightProperty());

		playlistVBox.setStyle("-fx-padding: 15px 15px 0px 15px; -fx-background-color: #17171D; -fx-cursor: hand; -fx-background-radius: 15px;");

		Text playlistNameText = new Text(playlist.getName()), creatorUsernameText = new Text(playlist.getCreatorUsername().isEmpty() || playlist.isSpecialPlaylist() ? "Official Playlist" : "@" + playlist.getCreatorUsername());
		Text followerCountText = new Text(" " + playlist.getFollowerCount() + (playlist.getFollowerCount() == 1 ? " follower" : " followers"));
		Text songCountText = new Text();
		Button playlistDropdownButton = new Button("...");

		playlistDropdownButton.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-background-radius: 50%;");
		playlistDropdownButton.setVisible(false);
		playlistDropdownButton.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
		playlistDropdownButton.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 18));

		ContextMenu playlistContextMenu = new ContextMenu();
		if (playlist.getCreatorUsername().equals(UserManager.getInstance().getUser().username) && !playlist.isSpecialPlaylist()) {
			ContextItem editPlaylist = new ContextItem("Edit Playlist"), deletePlaylist = new ContextItem("Delete Playlist");

			editPlaylist.setOnAction(event -> {
				if (!playlist.getCreatorUsername().equals(UserManager.getInstance().getUser().username))
					return;
				PlaylistManager.getInstance().editPlaylist(playlist, new CompleteEvent() {

					@Override
					public void onSuccess() {
						playlistNameText.setText(playlist.getName());
					}

					@Override
					public void onFail(String error) {

					}
				});
			});

			deletePlaylist.setOnAction(event -> {
				if (!playlist.getCreatorUsername().equals(UserManager.getInstance().getUser().username))
					return;
				Popup.confirmDeletePlaylist(playlist);
				if (this.yourPlaylistFlowPane.getChildren().contains(playlistVBox))
					this.yourPlaylistFlowPane.getChildren().remove(playlistVBox);
				if (this.followedPlaylistFlowPane.getChildren().contains(playlistVBox))
					this.followedPlaylistFlowPane.getChildren().remove(playlistVBox);
				if (this.yearEndTopPlaylistFlowPane.getChildren().contains(playlistVBox))
					this.yearEndTopPlaylistFlowPane.getChildren().add(playlistVBox);
				if (this.newReleasePlaylistFlowPane.getChildren().contains(playlistVBox))
					this.newReleasePlaylistFlowPane.getChildren().add(playlistVBox);
			});
			playlistContextMenu.getItems().addAll(editPlaylist, deletePlaylist);
		} else {
			if (!playlist.isSpecialPlaylist()) {
				ContextItem unfollowPlaylist = new ContextItem("Unfollow");

				unfollowPlaylist.setOnAction(event -> {
					FollowPlaylist.send(playlist, null);
					if (this.yourPlaylistFlowPane.getChildren().contains(playlistVBox))
						this.yourPlaylistFlowPane.getChildren().remove(playlistVBox);
					if (this.followedPlaylistFlowPane.getChildren().contains(playlistVBox))
						this.followedPlaylistFlowPane.getChildren().remove(playlistVBox);
					if (this.yearEndTopPlaylistFlowPane.getChildren().contains(playlistVBox))
						this.yearEndTopPlaylistFlowPane.getChildren().add(playlistVBox);
					if (this.newReleasePlaylistFlowPane.getChildren().contains(playlistVBox))
						this.newReleasePlaylistFlowPane.getChildren().add(playlistVBox);
				});

				playlistContextMenu.getItems().addAll(unfollowPlaylist);
			}
		}
		playlistNameText.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		playlistNameText.setFont(Font.font(FontType.VERDANA.getName(), FontWeight.BOLD, 14));
		playlistNameText.setWrappingWidth(165);

		creatorUsernameText.setFill(Color.web(CustomColor.GRAY.getColorHex()));
		creatorUsernameText.setFont(Font.font(FontType.VERDANA.getName(), 14));
		creatorUsernameText.setWrappingWidth(165);

		followerCountText.setFill(Color.web(CustomColor.GRAY.getColorHex()));
		followerCountText.setFont(Font.font(FontType.VERDANA.getName(), 14));

		songCountText.setFill(Color.web(CustomColor.GRAY.getColorHex()));
		songCountText.setFont(Font.font(FontType.VERDANA.getName(), 14));
		songCountText.textProperty().bind(Bindings.concat(" ", playlist.songCount.asString(), (playlist.getSongCount() == 1 ? " song" : " songs")));

		if (playlist.getFeatureType().equals("NONE")) {
			// footerHBox.setManaged(false);
			// footerHBox.layoutYProperty().bind(playlistVBox.minHeightProperty().subtract(50));
		}

		footerHBox.getLeft().getChildren().add(songCountText);
		footerHBox.getRight().getChildren().add(followerCountText);

		contentVBox.setMinHeight(170);
		contentVBox.setStyle("-fx-border-color: #4E4E58; -fx-border-width: 0 0 2px 0;");
		footerHBox.getContainer().setStyle("-fx-padding: 10px 0 15px 0;");

		topHBox.getLeft().getChildren().add(colorPane);
		topHBox.getRight().getChildren().add(playlistDropdownButton);
		topHBox.getRight().setAlignment(Pos.TOP_RIGHT);

		playlistTextVBox.getChildren().addAll(playlistNameText, creatorUsernameText);
		playlistImageContainerVBox.getChildren().addAll(topHBox.getContainer(), playlistTextVBox);
		contentVBox.getChildren().addAll(playlistImageContainerVBox, footerHBox.getContainer());
		playlistVBox.getChildren().addAll(contentVBox, footerHBox.getContainer(), bottomBorderHBox);

		playlistVBox.setOnMouseEntered(event -> {
			playlistNameText.setFill(Color.web(CustomColor.RHYTHM.getColorHex()));
			playlistVBox.setStyle("-fx-padding: 15px 15px 0px 15px; -fx-background-color: #22222E; -fx-cursor: hand; -fx-background-radius: 15px;");

			if (playlist.getCreatorUsername().equals(UserManager.getInstance().user.username) && !playlist.isSpecialPlaylist())
				playlistDropdownButton.setVisible(true);
		});
		playlistVBox.setOnMouseExited(event -> {
			playlistNameText.setFill(Color.web(CustomColor.WHITE.getColorHex()));
			playlistVBox.setStyle("-fx-padding: 15px 15px 0px 15px; -fx-background-color: #17171D; -fx-cursor: hand; -fx-background-radius: 15px;");

			if (playlist.getCreatorUsername().equals(UserManager.getInstance().user.username) && !playlist.isSpecialPlaylist())
				playlistDropdownButton.setVisible(false);
		});

		playlistDropdownButton.setOnMouseEntered(event -> {
			playlistDropdownButton.setTextFill(Color.web(CustomColor.RHYTHM.getColorHex()));
		});
		playlistDropdownButton.setOnMouseExited(event -> {
			playlistDropdownButton.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
		});
		creatorUsernameText.setOnMouseEntered(event -> {
			if (!playlist.isSpecialPlaylist()) {
				playlistNameText.setFill(Color.WHITE);
				creatorUsernameText.setFill(Color.web(CustomColor.RHYTHM.getColorHex()));
			}
		});
		creatorUsernameText.setOnMouseExited(event -> {
			if (!playlist.isSpecialPlaylist()) {
				playlistNameText.setFill(Color.WHITE);
				creatorUsernameText.setFill(Color.web(CustomColor.GRAY.getColorHex()));
			}
		});
		creatorUsernameText.setOnMouseClicked(event -> {
			if (!playlist.isSpecialPlaylist()) {
				ProfileLoader.loadProfile(playlist.getCreatorUsername());
			}
		});
		playlistVBox.setOnMouseClicked(event -> {
			if (event.getTarget() == creatorUsernameText)
				return;
			if (event.getButton() == MouseButton.PRIMARY)
				GUIManager.getInstance().sideBar.sideBarTab.changeTab(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.OPEN_PLAYLIST_VIEW), playlist.getURL());
		});
		playlistDropdownButton.setOnMouseClicked(event -> {
			if (playlistContextMenu != null && playlistContextMenu.isShowing())
				playlistContextMenu.hide();

			playlistContextMenu.show(playlistVBox, event.getScreenX(), event.getScreenY());
		});
		// Was playlist created by user?
		if (playlist.getCreatorUsername().equals(UserManager.getInstance().getUser().username)) {
			// If testing the display of owned playlists, return.
			if (this.testingNoCreatedPlaylists)
				return;
			this.yourPlaylistFlowPane.getChildren().add(playlistVBox);
		} else {
			// If playlist is featured.
			if (!playlist.getFeatureType().equals("NONE")) {

				playlistImageContainerVBox.getChildren().remove(topHBox.getContainer());

				ImageView albumImageView = new ImageView();
				albumImageView.setCache(true);
				albumImageView.setCacheHint(CacheHint.SPEED);

				ImageManager.getAlbumImage(albumImageView, playlist.getURL() + "-cover", 80, 80);
				playlistImageContainerVBox.getChildren().add(0, albumImageView);

				playlistVBox.setOnMouseEntered(event -> {
					albumImageView.setEffect(new InnerShadow(BlurType.GAUSSIAN, Color.GRAY, 20, 0, 0, 0));
					playlistNameText.setFill(Color.web(Settings.getInstance().getDefaultColor()));
				});
				playlistVBox.setOnMouseExited(event -> {
					albumImageView.setEffect(null);
					playlistNameText.setFill(Color.web(CustomColor.WHITE.getColorHex()));
				});

				if (playlist.getName().contains("-")) {
					JSONWrapper wrapper = new JSONWrapper(SongTitle.getSongInformationFromTitle(playlist.getName()));
					Text artist = new Text(wrapper.getJSONString("artist"));
					artist.setFill(Color.web(CustomColor.GRAY.getColorHex()));
					artist.setFont(Font.font(FontType.VERDANA.getName(), 14));
					artist.setWrappingWidth(170);

					playlistNameText.setText(wrapper.getJSONString("song_name"));
					creatorUsernameText.setText(wrapper.getJSONString("artist"));
					// playlistVBox.getChildren().add(2, artist);
				}
				if (playlist.isFollowing()) {
					// If testing the display of no followed playlists, return.
					if (this.testingNoFollowedPlaylists)
						return;
					this.followedPlaylistFlowPane.getChildren().add(playlistVBox);
				} else if (playlist.getFeatureType().equals("YEAR_TOP_50")) {
					// If testing the display of no featured playlists, return.
					if (this.testingNoFeaturedPlaylists)
						return;
					this.yearEndTopPlaylistFlowPane.getChildren().add(playlistVBox);
				} else {
					// If testing the display of no featured playlists, return.
					if (this.testingNoFeaturedPlaylists)
						return;
					this.newReleasePlaylistFlowPane.getChildren().add(playlistVBox);
				}
			} else {
				// If testing the display of no followed playlists, return.
				if (this.testingNoFollowedPlaylists)
					return;
				this.followedPlaylistFlowPane.getChildren().add(playlistVBox);
			}
		}
		// Check if the user has any followed playlists. If a user doesn't have any followed playlists we won't display the section.
		if (this.followedPlaylistFlowPane.getChildren().size() > 0 && !this.containerVBox.getChildren().contains(this.followedPlaylistVBox)) {
			this.containerVBox.getChildren().add(1, this.followedPlaylistVBox);
		}
	}

	private String getRandomColor() {
		if (this.loadedColorWheel.isEmpty()) {
			loadColors();
		}
		int random = new Random().nextInt(this.loadedColorWheel.size());
		String color = this.loadedColorWheel.get(random);
		this.loadedColorWheel.remove(random);
		return color;
	}

	private void loadColors() {
		this.loadedColorWheel.add("#654ea3 0%, #eaafc8 100%");
		this.loadedColorWheel.add("#ED213A 0%, #93291E 100%");
		this.loadedColorWheel.add("#FDC830 0%, #F37335 100%");
		this.loadedColorWheel.add("#00B4DB 0%, #0083B0 100%");
		this.loadedColorWheel.add("#DA4453 0%, #89216B 100%");
		this.loadedColorWheel.add("#ad5389 0%, #3c1053 100%");
		this.loadedColorWheel.add("#4e54c8 0%, #8f94fb 100%");
		this.loadedColorWheel.add("#c94b4b 0%, #4b134f 100%");
		this.loadedColorWheel.add("#23074d 0%, #cc5333 100%");
		this.loadedColorWheel.add("#7F00FF 0%, #E100FF 100%");
		this.loadedColorWheel.add("#f85032 0%, #e73827 100%");
		this.loadedColorWheel.add("#76b852 0%, #8DC26F 100%");

		// NEW
		this.loadedColorWheel.add("#56CCF2 0%, #2F80ED 100%");
		this.loadedColorWheel.add("#2980b9 0%, #2c3e50 100%");
		this.loadedColorWheel.add("#005C97 0%, #363795 100%");
		this.loadedColorWheel.add("#304352 0%, #d7d2cc 100%");
		this.loadedColorWheel.add("#2193b0 0%, #6dd5ed 100%");
		this.loadedColorWheel.add("#2193b0 0%, #6dd5ed 100%");
		this.loadedColorWheel.add("#7474BF 0%, #348AC7 100%");
		this.loadedColorWheel.add("#000428 0%, #004e92 100%");
		this.loadedColorWheel.add("#FF416C 0%, #FF4B2B 100%");
		this.loadedColorWheel.add("#F2994A 0%, #F2C94C 100%");
		this.loadedColorWheel.add("#E44D26 0%, #F16529 100%");
		this.loadedColorWheel.add("#cb2d3e 0%, #ef473a 100%");
	}

	private void configureScrollPane(ScrollPane scrollPane, HBox scrollPaneHBoxContent, Text scrollPaneHeaderText) {
		scrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
		scrollPane.setContent(scrollPaneHBoxContent);
		scrollPane.setStyle("-fx-background-color: transparent;");
		scrollPane.setPadding(new Insets(0, scrollPane.getPadding().getRight() + 15, 0, 0));
		scrollPane.minWidthProperty().bind(GUIManager.getInstance().contentManager.contentPane.widthProperty().subtract(230));
		scrollPane.maxWidthProperty().bind(GUIManager.getInstance().contentManager.contentPane.widthProperty().subtract(230));
		scrollPane.setMinHeight(200);

		scrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> {
			scrollPane.setVvalue(0);
		});
		scrollPaneHBoxContent.setStyle("-fx-background-color: transparent;");
		scrollPaneHBoxContent.minWidthProperty().bind(scrollPane.widthProperty());
		scrollPaneHBoxContent.setMinHeight(189);

		scrollPaneHeaderText.setFont(Font.font(FontType.VERDANA.getName(), FontWeight.BOLD, 20));
		scrollPaneHeaderText.setFill(Color.web(CustomColor.WHITE.getColorHex()));

		scrollPaneHBoxContent.setOnScroll(event -> {
			double deltaY = event.getDeltaY() * 4; // *6 to make the scrolling a bit faster
			double width = scrollPane.getContent().getBoundsInLocal().getWidth();
			double vvalue = scrollPane.getHvalue();
			scrollPane.setHvalue(vvalue + -deltaY / width); // deltaY/width to make the scrolling equally fast regardless of the actual height of the component
		});
	}

	private void addSpecialPlaylists() {
		for (SpecialPlaylistType specialPlaylist : SpecialPlaylistType.values()) {
			if (specialPlaylist == SpecialPlaylistType.QUEUE && PlaylistManager.getInstance().queue_list.isEmpty())
				continue;
			Playlist playlist = new Playlist();
			playlist.setName(specialPlaylist.name);
			playlist.setURL(specialPlaylist.url);
			playlist.setFollowerCount(1);

			if (specialPlaylist == SpecialPlaylistType.QUEUE) {
				playlist.setIsQueue(true);
				playlist.setCreatorUsername(UserManager.getInstance().user.username);
			} else if (specialPlaylist == SpecialPlaylistType.TRENDING_MUSIC) {
				playlist.setSongCount(35);
			}
			Platform.runLater(() -> {
				addPlaylistToDisplay(playlist);
			});
		}
	}

	private void addMyLikesPlaylist() {
		Playlist myLikes = new Playlist();
		myLikes.setName("My Likes");
		myLikes.setURL("my-likes");
		myLikes.setFollowerCount(1);
		myLikes.setCreatorUsername(UserManager.getInstance().user.username);
		myLikes.setSpecialPlaylist(true);

		Playlist recentAdded = new Playlist();
		recentAdded.setName("Recently Added");
		recentAdded.setURL("recent-music");
		recentAdded.setDescription("Your recent 100 added songs.");
		recentAdded.setFollowerCount(1);
		recentAdded.setCreatorUsername(UserManager.getInstance().user.username);
		recentAdded.setSpecialPlaylist(true);

		if (Data.getInstance().cachedLikedMusicCount == -1) {
			GetLikedMusicCount.send(myLikes, recentAdded, new CompleteEvent() {

				@Override
				public void onSuccess() {
					Data.getInstance().cachedLikedMusicCount = myLikes.getSongCount();
					Data.getInstance().cachedRecentlyAddedMusicCount = recentAdded.getSongCount();
					if (myLikes.getSongCount() > 0) {
						Platform.runLater(() -> {
							addPlaylistToDisplay(myLikes);
						});
					}
					if (recentAdded.getSongCount() > 0) {
						Platform.runLater(() -> {
							addPlaylistToDisplay(recentAdded);
						});
					}
				}

				@Override
				public void onFail(String error) {

				}
			});
		} else {
			myLikes.setSongCount(Data.getInstance().cachedLikedMusicCount);
			recentAdded.setSongCount(Data.getInstance().cachedRecentlyAddedMusicCount);
			if (myLikes.getSongCount() > 0) {
				Platform.runLater(() -> {
					addPlaylistToDisplay(myLikes);
				});
			}
			if (recentAdded.getSongCount() > 0) {
				Platform.runLater(() -> {
					addPlaylistToDisplay(recentAdded);
				});
			}
		}
	}

	private ImageBuilder getPlaylistOrderButton(boolean isCreatedPlaylist) {
		ImageBuilder imageBuilder = new ImageBuilder(new Image(Main.class.getResource("/resources/icons/v2/orderPlaylistIcon.png").toExternalForm()), new Image(Main.class.getResource("/resources/icons/v2/orderPlaylistIconHover.png").toExternalForm()));

		CustomToolTip.install(imageBuilder.getHBox(), new CustomToolTip(isCreatedPlaylist ? "Change created playlist order" : "Change followed playlist order"));

		imageBuilder.getHBox().setOnMouseClicked(event -> {
			Popup.changePlaylistOrder(new CompleteEvent() {
				@Override
				public void onSuccess() {
					yourPlaylistFlowPane.getChildren().clear();
					followedPlaylistFlowPane.getChildren().clear();
					addSpecialPlaylists();
					addMyLikesPlaylist();
					loadPlaylists(false);
				}

				@Override
				public void onFail(String error) {

				}
			}, isCreatedPlaylist);
		});

		return imageBuilder;
	}
}