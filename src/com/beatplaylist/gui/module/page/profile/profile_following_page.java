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

import java.util.Iterator;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.beatplaylist.Main;
import com.beatplaylist.enums.FontType;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.settings.Settings;
import com.beatplaylist.utilities.CustomColor;
import com.beatplaylist.utilities.data.StoredID;
import com.beatplaylist.utilities.events.CompleteEvent;
import com.beatplaylist.utilities.events.SocketReceiveEvent;
import com.beatplaylist.utilities.image.ImageManager;
import com.beatplaylist.utilities.network.netty.NETTYClient;
import com.beatplaylist.utilities.network.post.UpdateFollowState;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.network.serialized.PacketType;
import com.beatplaylist.utilities.network.serialized.Post;
import com.beatplaylist.utilities.user.BaseUser;
import com.beatplaylist.utilities.user.UserManager;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

// Displays users playlists
public class profile_following_page {

	private VBox contentVBox;
	private ScrollPane scrollPane;
	private FlowPane flowPane;
	private Button backButton;

	private BaseUser searchUser;
	private boolean searchForFollowersOnly;

	public profile_following_page(BaseUser searchUser, boolean searchForFollowersOnly) {
		this.searchUser = searchUser;
		this.searchForFollowersOnly = searchForFollowersOnly;

		this.contentVBox = new VBox(15); // Just a wrapper for the scrollpane to apply padding.
		this.scrollPane = new ScrollPane() {
			public void requestFocus() {

			}
		};
		this.flowPane = new FlowPane();

		this.backButton = new Button("Back to @" + searchUser.username + "'s profile") {
			public void requestFocus() {

			}
		};

		configure();
		listen();

		loadFollowing();
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
		this.backButton.setTextFill(Color.web(CustomColor.GRAY.getColorHex()));
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
			ProfileLoader.loadProfile(this.searchUser.username);
		});
	}

	private void loadFollowing() {
		Post post = new Post();
		post.setDetails();
		post.setPacketType(PacketType.GET_FOLLOWING);

		StoredID.getInstance().followingPageIndex = Integer.MAX_VALUE;

		JSONObject object = new JSONObject();
		object.put("search_user", JSONValue.escape(this.searchUser.username));
		object.put("lastID", StoredID.getInstance().followingPageIndex);
		object.put("isFollowing", this.searchForFollowersOnly);

		post.setJSONArray(object);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {
			@Override
			public void onSuccess(Post post) {
				JSONWrapper response = new JSONWrapper(post.getJSONMessage());
				if (response.isJSONInvalid())
					return;

				StoredID.getInstance().followingPageIndex = response.getJSONInteger("lastID");

				Iterator<JSONObject> users = response.getJSONArray("users");

				while (users.hasNext()) {
					JSONWrapper user = new JSONWrapper(users.next());

					Platform.runLater(() -> {
						addUser(user);
					});
				}
			}

			@Override
			public void onError(String error) {
			}
		});
	}

	private void addUser(JSONWrapper wrapper) {
		String username = wrapper.getJSONString("username");
		String display_name = wrapper.getJSONString("display_name");
		String profile_image = wrapper.getJSONString("profile_image");
		boolean isFollowing = wrapper.getJSONBoolean("followingUser");
		boolean isFollowingYou = wrapper.getJSONBoolean("followsYou");
		boolean verified = wrapper.getJSONBoolean("isVerified");

		createFollowing(username, display_name, isFollowing, isFollowingYou, verified, profile_image, this.flowPane);
	}

	private void createFollowing(String username, String display_name, boolean following, boolean follows_you, boolean verified, String picture, FlowPane flow_pane) {
		if (UserManager.getInstance().user.username.equals(username))
			return;
		VBox vbox = new VBox(5), name_username_vbox = new VBox(2);
		HBox name_hbox = new HBox(5), username_hbox = new HBox(5);
		Text username_text = new Text("@" + username), display_name_text = new Text(display_name);

		name_username_vbox.setMinHeight(50);
		vbox.setStyle("-fx-cursor: hand; -fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-padding: 15px;");
		vbox.setMinWidth(200);

		ImageView image_view = new ImageView();
		ImageManager.getProfileImage(image_view, picture, 100, 100);
		image_view.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0); -fx-border-color: " + CustomColor.WHITE.getColorHex() + "; -fx-border-insets: 3px; -fx-border-radius: 7px; -fx-border-width: 1.0");

		image_view.setOnMouseEntered(event -> {
			image_view.setStyle("-fx-cursor: hand; -fx-border-color: white;");
		});
		image_view.setOnMouseExited(event -> {
			image_view.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0); -fx-border-color: " + CustomColor.WHITE.getColorHex() + "; -fx-border-insets: 3px; -fx-border-radius: 7px; -fx-border-width: 1.0");
		});
		vbox.setOnMouseEntered(event -> {
			username_text.setFill(Color.web(CustomColor.RHYTHM.getColorHex()));
			display_name_text.setFill(Color.web(CustomColor.RHYTHM.getColorHex()));
		});
		vbox.setOnMouseExited(event -> {
			username_text.setFill(Color.web(CustomColor.GRAY.getColorHex()));
			display_name_text.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		});
		vbox.setOnMouseClicked(event -> {
			if (event.getTarget() instanceof Button)
				return;
			ProfileLoader.loadProfile(username);
		});

		username_text.setFill(Color.web(CustomColor.GRAY.getColorHex()));
		username_text.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.THIN, 13));

		display_name_text.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		display_name_text.setFont(Font.font(FontType.DEFAULT.getName(), 14));

		Button follow_unfollow_button = new Button("Follow"), follows_you_text = new Button("Follows you");

		if (following) {
			follow_unfollow_button.setText("Following");
			follow_unfollow_button.setStyle("-fx-cursor: hand; -fx-background-color: " + Settings.getInstance().getDefaultColor() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-border-color: " + Settings.getInstance().getDefaultColor() + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
		} else
			follow_unfollow_button.setStyle("-fx-cursor: hand; -fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-border-color: #000; -fx-border-radius: 10px; -fx-background-radius: 10px;");

		follow_unfollow_button.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 12));
		follow_unfollow_button.setMaxHeight(30);
		follow_unfollow_button.setMinWidth(100);
		follow_unfollow_button.setOnAction(event -> {
			if (follow_unfollow_button.getText().equals("Unfollow")) {
				follow_unfollow_button.setText("Follow");
				follow_unfollow_button.setStyle("-fx-cursor: hand; -fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-border-color: #000; -fx-border-radius: 10px; -fx-background-radius: 10px;");
			} else {
				follow_unfollow_button.setText("Following");
				follow_unfollow_button.setStyle("-fx-cursor: hand; -fx-background-color: " + Settings.getInstance().getDefaultColor() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-border-color: " + Settings.getInstance().getDefaultColor() + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
			}
			UpdateFollowState.send(username, new CompleteEvent() {

				@Override
				public void onSuccess() {

				}

				@Override
				public void onFail(String error) {
				}
			});
		});
		follow_unfollow_button.setOnMouseEntered(event -> {
			if (follow_unfollow_button.getText().equals("Following")) {
				follow_unfollow_button.setText("Unfollow");
				follow_unfollow_button.setStyle("-fx-cursor: hand; -fx-background-color: " + CustomColor.RED.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-border-color: " + CustomColor.RED.getColorHex() + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
			} else {
				follow_unfollow_button.setText("Follow");
				follow_unfollow_button.setStyle("-fx-cursor: hand; -fx-background-color: " + Settings.getInstance().getDefaultColor() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-border-color: " + Settings.getInstance().getDefaultColor() + "; -fx-border-width: 1px; -fx-border-radius: 10px; -fx-background-radius: 10px;");
			}
		});
		follow_unfollow_button.setOnMouseExited(event -> {
			if (follow_unfollow_button.getText().equals("Follow")) {
				follow_unfollow_button.setText("Follow");
				follow_unfollow_button.setStyle("-fx-cursor: hand; -fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-border-color: #000; -fx-border-radius: 10px; -fx-background-radius: 10px;");
			} else {
				follow_unfollow_button.setStyle("-fx-cursor: hand; -fx-background-color: " + Settings.getInstance().getDefaultColor() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-border-color: " + Settings.getInstance().getDefaultColor() + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
				follow_unfollow_button.setText("Following");
			}
		});

		follows_you_text.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-text-fill: " + CustomColor.GRAY.getColorHex() + ";");
		follows_you_text.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.THIN, 9));
		follows_you_text.setMaxHeight(25);

		name_hbox.setAlignment(Pos.CENTER_LEFT);
		name_hbox.getChildren().addAll(display_name_text);
		if (verified) {
			ImageView verified_image_view = new ImageView();
			Image verified_image = new Image(Main.class.getResource("/resources/pins/verified_extra_small.png").toExternalForm());
			verified_image_view.setImage(verified_image);
			verified_image_view.setFitHeight(19);
			verified_image_view.setFitWidth(19);
			verified_image_view.setStyle("-fx-cursor: hand;");
			Tooltip.install(verified_image_view, new Tooltip("Verified account."));
			name_hbox.getChildren().add(verified_image_view);
		}
		username_hbox.getChildren().addAll(username_text);
		if (follows_you)
			username_hbox.getChildren().addAll(follows_you_text);

		name_username_vbox.getChildren().addAll(name_hbox, username_hbox);
		vbox.getChildren().addAll(image_view, name_username_vbox, follow_unfollow_button);
		flow_pane.getChildren().add(vbox);
	}
}