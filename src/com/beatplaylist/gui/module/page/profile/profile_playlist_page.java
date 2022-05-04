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

package com.beatplaylist.gui.module.page.profile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.beatplaylist.enums.FontType;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.module.layout.sidebar.TabType;
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

import javafx.animation.Animation.Status;
import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.application.Platform;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

// Displays users playlists
public class profile_playlist_page {

	private List<String> loadedColorWheel;
	private VBox contentVBox;
	private ScrollPane scrollPane;
	private FlowPane flowPane;
	private Button backButton;

	private String username;

	public profile_playlist_page(String username) {
		this.username = username;
		this.contentVBox = new VBox(15); // Just a wrapper for the scrollpane to apply padding.
		this.scrollPane = new ScrollPane() {
			public void requestFocus() {

			}
		};
		this.flowPane = new FlowPane();
		this.loadedColorWheel = new ArrayList<>();

		this.backButton = new Button("Back to @" + username + "'s profile") {
			public void requestFocus() {

			}
		};

		configure();
		listen();

		loadPlaylists(username);
	}

	private void configure() {
		this.contentVBox.setStyle("-fx-padding: " + GUIManager.getInstance().padding + "px; -fx-background-color: transparent;");

		this.scrollPane.setContent(this.flowPane);
		this.scrollPane.setStyle("-fx-background-color: transparent;");
		this.scrollPane.minWidthProperty().bind(GUIManager.getInstance().contentManager.contentPane.widthProperty().subtract(200));
		this.scrollPane.maxWidthProperty().bind(GUIManager.getInstance().contentManager.contentPane.widthProperty().subtract(200));
		this.scrollPane.minHeightProperty().bind(GUIManager.getInstance().contentManager.contentPane.heightProperty().subtract(75));
		this.scrollPane.maxHeightProperty().bind(GUIManager.getInstance().contentManager.contentPane.heightProperty().subtract(75));
		this.scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		this.scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);

		this.flowPane.setStyle("-fx-background-color: transparent;");
		this.flowPane.setHgap(5);
		this.flowPane.setVgap(5);
		this.flowPane.minWidthProperty().bind(this.scrollPane.widthProperty());
		this.flowPane.maxWidthProperty().bind(this.scrollPane.widthProperty());

		this.backButton.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-background-radius: 15px;");
		this.backButton.setMinSize(150, 35);
		this.backButton.setFont(Font.font(FontType.DEFAULT.getName(), 14));
		this.backButton.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
		this.backButton.setCursor(Cursor.HAND);

		this.contentVBox.getChildren().addAll(this.backButton, this.scrollPane);
		GUIManager.getInstance().contentManager.contentPane.getChildren().add(this.contentVBox);
	}

	private void listen() {
		this.backButton.setOnMouseEntered(event -> {
			this.backButton.setTextFill(Color.web(Settings.getInstance().getDefaultColor()));
		});
		this.backButton.setOnMouseExited(event -> {
			this.backButton.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
		});
		this.backButton.setOnMouseClicked(event -> {
			ProfileLoader.loadProfile(this.username);
		});
	}

	private void loadPlaylists(String username) {
		Post post = new Post();
		post.setDetails();
		post.setPacketType(PacketType.GET_USER_PLAYLISTS);

		JSONObject object = new JSONObject();
		object.put("search", JSONValue.escape(username));

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

					Playlist playlist = new Playlist();
					playlist.setName(playlist_name);
					playlist.setURL(playlist_url);
					playlist.setCreatorUsername(username);
					playlist.setSongCount(song_count);
					playlist.setFollowerCount(wrapper.getJSONInteger("follower_count"));
					playlist.setFeatureType(wrapper.getJSONString("playlist_feature_type"));
					playlist.setFollowing(false);

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

		System.out.println(playlist.getFeatureType());
		if (playlist.getFeatureType().equals("NONE") || playlist.getFeatureType().equals("YEAR_TOP_50")) {
			playlistVBox.setMinSize(175, 175);
			playlistVBox.setMaxSize(175, 175);
		} else {
			playlistVBox.setMinSize(175, 220);
			playlistVBox.setMaxSize(175, 250);
		}

		// playlistVBox.setStyle("-fx-padding: 5px; -fx-background-color: #4c4c4c; -fx-cursor: hand;");
		// playlistVBox.setStyle("-fx-padding: 5px; -fx-background-color: linear-gradient(to bottom, " + getRandomColor() + "); -fx-cursor: hand; -fx-background-radius: 15px;");

		String randomColor = getRandomColor();

		if (playlist.getFeatureType().equals("NONE"))
			playlistVBox.setStyle("-fx-padding: 10px; -fx-background-color: linear-gradient(to bottom, " + randomColor + "); -fx-cursor: hand; -fx-background-radius: 15px;");
		else
			playlistVBox.setStyle("-fx-padding: 10px; -fx-cursor: hand; -fx-background-radius: 15px;");
		playlistVBox.setBorder(new Border(new BorderStroke(Color.web(CustomColor.DARK_BACKGROUND.getColorHex()), BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(3))));

		Text playlistNameText = new Text(playlist.getName());
		Text followerCountText = new Text(" " + playlist.getFollowerCount() + (playlist.getFollowerCount() == 1 ? " follower" : " followers"));
		Text songCountText = new Text(" " + playlist.getSongCount() + (playlist.getSongCount() == 1 ? " song" : " songs"));

		playlistNameText.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		playlistNameText.setFont(Font.font(FontType.VERDANA.getName(), FontWeight.BOLD, 14));
		playlistNameText.setWrappingWidth(170);

		followerCountText.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		followerCountText.setFont(Font.font(FontType.VERDANA.getName(), 14));

		songCountText.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		songCountText.setFont(Font.font(FontType.VERDANA.getName(), 14));

		if (playlist.getFeatureType().equals("NONE")) {
			detailVBox.setManaged(false);
			detailVBox.layoutYProperty().bind(playlistVBox.minHeightProperty().subtract(50));
		}

		if (playlist.getFeatureType().equals("NONE"))
			detailVBox.getChildren().addAll(songCountText, followerCountText);
		playlistVBox.getChildren().addAll(playlistNameText, detailVBox);

		// Animate border for user created playlists and followed playlists.
		if (playlist.getFeatureType().equals("NONE")) {
			FillTransition fillTransition = new FillTransition();
			Rectangle rectangle = new Rectangle();

			playlistVBox.setOnMouseEntered(event -> {
				if (fillTransition.getStatus() != Status.RUNNING) {
					rectangle.setFill(Color.web(CustomColor.DARK_BACKGROUND.getColorHex()));
					fillTransition.setShape(rectangle);
					fillTransition.setDuration(Duration.millis(250));
					fillTransition.setFromValue(Color.web(CustomColor.DARK_BACKGROUND.getColorHex()));
					fillTransition.setToValue(Color.web(CustomColor.WHITE.getColorHex()));
					fillTransition.setInterpolator(new Interpolator() {
						@Override
						protected double curve(double transition) {
							playlistVBox.setBorder(new Border(new BorderStroke(rectangle.getFill(), BorderStrokeStyle.SOLID, new CornerRadii(14), new BorderWidths(3))));
							return transition;
						}
					});
					fillTransition.play();
				}
			});

			playlistVBox.setOnMouseExited(event -> {
				fillTransition.stop();
				playlistVBox.setBorder(new Border(new BorderStroke(Color.web(CustomColor.DARK_BACKGROUND.getColorHex()), BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(3))));
			});
		}
		playlistVBox.setOnMouseClicked(event -> {
			GUIManager.getInstance().sideBar.sideBarTab.changeTab(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.OPEN_PLAYLIST_VIEW), playlist.getURL());
		});

		// If playlist is featured.
		if (!playlist.getFeatureType().equals("NONE")) {
			ImageView albumImageView = new ImageView();

			ImageManager.getAlbumImage(albumImageView, playlist.getURL() + "-cover", 150, 150);
			playlistVBox.getChildren().add(0, albumImageView);

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

				playlistNameText.setText(wrapper.getJSONString("song_name"));
				playlistVBox.getChildren().add(2, artist);
			}
		}
		// Check if the user has any followed playlists. If a user doesn't have any followed playlists we won't display the section.
		this.flowPane.getChildren().add(playlistVBox);
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
	}
}