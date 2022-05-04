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

import java.util.Iterator;

import org.json.simple.JSONObject;

import com.beatplaylist.enums.FontType;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.module.layout.sidebar.TabType;
import com.beatplaylist.gui.utilities.FXUtilities;
import com.beatplaylist.gui.utilities.playlist.PlaylistVBox;
import com.beatplaylist.gui.utilities.playlist.SongTitle;
import com.beatplaylist.settings.Settings;
import com.beatplaylist.utilities.CustomColor;
import com.beatplaylist.utilities.events.SocketReceiveEvent;
import com.beatplaylist.utilities.image.ImageManager;
import com.beatplaylist.utilities.network.netty.NETTYClient;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.network.serialized.PacketType;
import com.beatplaylist.utilities.network.serialized.Post;
import com.beatplaylist.utilities.playlist.Playlist;
import com.beatplaylist.utilities.playlist.PlaylistManager;
import com.beatplaylist.utilities.popup.Popup;
import com.beatplaylist.utilities.user.UserManager;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

// When a user clicks on the view all button on the playlist page next to featured new playlists this page is loaded.
public class featured_playlist_page {

	private VBox containerVBox;
	private ScrollPane containerScrollPane;

	private HBox headerHBox, innerHeaderTextHBox, innerButtonHBox;
	private Text flowPaneHeaderText;

	// Stores albums
	private FlowPane flowPane;

	private Button submitFeaturedAlbum;

	public featured_playlist_page() {
		this.containerVBox = new VBox(5);
		this.headerHBox = new HBox();
		this.innerHeaderTextHBox = new HBox();
		this.innerButtonHBox = new HBox();

		this.containerScrollPane = new ScrollPane() {
			public void requestFocus() {

			}
		};
		this.flowPane = new FlowPane();
		this.flowPaneHeaderText = new Text("Featured Albums");
		this.submitFeaturedAlbum = new Button("Submit Featured Album") {
			public void requestFocus() {

			}
		};

		configure();
		listen();
	}

	private void configure() {
		FXUtilities.setNodePadding(this.containerVBox, GUIManager.getInstance().padding);
		
		this.containerScrollPane.setContent(this.flowPane);
		this.containerScrollPane.setStyle("-fx-background-color: transparent;");
		this.containerScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		this.containerScrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		this.containerScrollPane.minWidthProperty().bind(GUIManager.getInstance().contentManager.contentPane.widthProperty().subtract(400));
		this.containerScrollPane.maxWidthProperty().bind(GUIManager.getInstance().contentManager.contentPane.widthProperty().subtract(400));
		this.containerScrollPane.minHeightProperty().bind(GUIManager.getInstance().contentManager.contentPane.heightProperty().subtract(60));
		this.containerScrollPane.maxHeightProperty().bind(GUIManager.getInstance().contentManager.contentPane.heightProperty().subtract(60));

		this.headerHBox.minWidthProperty().bind(this.containerVBox.widthProperty());
		this.headerHBox.maxWidthProperty().bind(this.containerVBox.widthProperty());

		this.innerHeaderTextHBox.minWidthProperty().bind(this.containerScrollPane.widthProperty().divide(2));
		this.innerHeaderTextHBox.maxWidthProperty().bind(this.containerScrollPane.widthProperty().divide(2));

		this.innerButtonHBox.minWidthProperty().bind(this.containerScrollPane.widthProperty().divide(2));
		this.innerButtonHBox.maxWidthProperty().bind(this.containerScrollPane.widthProperty().divide(2));
		this.innerButtonHBox.setAlignment(Pos.CENTER_RIGHT);

		this.flowPane.setHgap(5);
		this.flowPane.setVgap(5);
		this.flowPane.prefWidthProperty().bind(this.containerScrollPane.widthProperty());

		this.flowPaneHeaderText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 24));
		this.flowPaneHeaderText.setFill(Color.web(CustomColor.WHITE.getColorHex()));

		this.submitFeaturedAlbum.setStyle("-fx-background-color: transparent;");
		this.submitFeaturedAlbum.setCursor(Cursor.HAND);
		this.submitFeaturedAlbum.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));

		loadPlaylists();

		this.innerHeaderTextHBox.getChildren().add(this.flowPaneHeaderText);
		this.innerButtonHBox.getChildren().add(this.submitFeaturedAlbum);
		this.headerHBox.getChildren().addAll(this.innerHeaderTextHBox, this.innerButtonHBox);
		this.containerVBox.getChildren().addAll(this.headerHBox, this.containerScrollPane);
		GUIManager.getInstance().contentManager.contentPane.getChildren().add(this.containerVBox);

		FadeTransition fadeTransition = new FadeTransition(Duration.millis(250), this.containerVBox);
		fadeTransition.setFromValue(0.0);
		fadeTransition.setToValue(1.0);
		fadeTransition.play();
	}

	private void listen() {
		this.submitFeaturedAlbum.setOnMouseEntered(event -> {
			this.submitFeaturedAlbum.setTextFill(Color.web(Settings.getInstance().getDefaultColor()));
		});
		this.submitFeaturedAlbum.setOnMouseExited(event -> {
			this.submitFeaturedAlbum.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
		});
		this.submitFeaturedAlbum.setOnMouseClicked(event -> {
			Popup.submitFeaturedAlbum();
		});
	}

	private void loadPlaylists() {
		Post post = new Post();
		post.setDetails();
		post.setPacketType(PacketType.GET_ALL_FEATURED_PLAYLISTS);

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

					Playlist playlist = new Playlist();
					playlist.setName(playlist_name);
					playlist.setURL(playlist_url);
					playlist.setCreatorUsername(username);
					playlist.setSongCount(song_count);
					playlist.setFollowerCount(wrapper.getJSONInteger("follower_count"));
					playlist.setFollowing(wrapper.getJSONBoolean("isFollowing"));
					playlist.setFeatureType(wrapper.getJSONString("playlist_feature_type"));

					// If playlist creator is user, add playlist to cached playlist.
					if (playlist.getCreatorUsername().equals(UserManager.getInstance().getUser().username)) {
						PlaylistManager.getInstance().addPlaylist(playlist);
					}

					Platform.runLater(() -> {
						addPlaylistToDisplay(playlist);
					});
				}
			}

			@Override
			public void onError(String error) {
			}
		});
	}

	private void addPlaylistToDisplay(Playlist playlist) {
		PlaylistVBox playlistVBox = new PlaylistVBox(), detailVBox = new PlaylistVBox(3);

		playlistVBox.setMinSize(175, 220);
		playlistVBox.setMaxSize(175, 250);
		playlistVBox.setStyle("-fx-cursor: hand; -fx-background-radius: 15px;");
		playlistVBox.setBorder(new Border(new BorderStroke(Color.web(CustomColor.DARK_BACKGROUND.getColorHex()), BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(3))));

		Text playlistNameText = new Text(playlist.getName());
		Text followerCountText = new Text(" " + playlist.getFollowerCount() + (playlist.getFollowerCount() == 1 ? " follower" : " followers"));
		Text songCountText = new Text(" " + playlist.getSongCount() + (playlist.getSongCount() == 1 ? " song" : " songs"));

		ImageView albumImageView = new ImageView();
		ImageManager.getAlbumImage(albumImageView, playlist.getURL() + "-cover", 150, 150);

		playlistNameText.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		playlistNameText.setFont(Font.font(FontType.VERDANA.getName(), FontWeight.BOLD, 14));
		playlistNameText.setWrappingWidth(170);

		followerCountText.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		followerCountText.setFont(Font.font(FontType.VERDANA.getName(), 14));

		songCountText.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		songCountText.setFont(Font.font(FontType.VERDANA.getName(), 14));

		playlistVBox.getChildren().addAll(albumImageView, playlistNameText, detailVBox);

		playlistVBox.setOnMouseEntered(event -> {
			albumImageView.setEffect(new InnerShadow(BlurType.GAUSSIAN, Color.GRAY, 20, 0, 0, 0));
			playlistNameText.setFill(Color.web(Settings.getInstance().getDefaultColor()));
		});

		playlistVBox.setOnMouseExited(event -> {
			albumImageView.setEffect(null);
			playlistNameText.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		});

		playlistVBox.setOnMouseClicked(event -> {
			GUIManager.getInstance().sideBar.sideBarTab.changeTab(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.OPEN_PLAYLIST_VIEW), playlist.getURL());
		});

		if (playlist.getName().contains("-")) {
			JSONWrapper wrapper = new JSONWrapper(SongTitle.getSongInformationFromTitle(playlist.getName()));
			Text artist = new Text(wrapper.getJSONString("artist"));
			artist.setFill(Color.web(CustomColor.GRAY.getColorHex()));
			artist.setFont(Font.font(FontType.VERDANA.getName(), 14));
			artist.setWrappingWidth(170);

			playlistNameText.setText(wrapper.getJSONString("song_name"));
			playlistVBox.getChildren().add(2, artist);
		}
		this.flowPane.getChildren().add(playlistVBox);
	}
}