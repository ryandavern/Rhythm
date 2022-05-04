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
import com.beatplaylist.gui.module.page.profile.ProfileLoader;
import com.beatplaylist.utilities.CustomColor;
import com.beatplaylist.utilities.events.SocketResponseEvent;
import com.beatplaylist.utilities.image.ImageManager;
import com.beatplaylist.utilities.network.get.GetSuggestedUsers;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.network.serialized.Post;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class hub_page {

	public VBox containerVBox, suggestedPlaylistVBox;

	public Text suggestedUserTitleText, suggestedPlaylistsText, comingSoon;
	public ScrollPane suggestedUserScrollPane;
	public HBox suggestedUserHBox;

	public hub_page() {
		this.suggestedUserScrollPane = new ScrollPane();
		this.suggestedUserHBox = new HBox(5);
		this.containerVBox = new VBox(25);
		this.suggestedPlaylistVBox = new VBox(5);
		
		this.suggestedUserTitleText = new Text("Suggested Users");
		this.suggestedPlaylistsText = new Text("Suggested Playlists");
		this.comingSoon = new Text("Coming Soon");
		
		configure();
		listen();

		GetSuggestedUsers.send(new SocketResponseEvent() {

			@Override
			public void onSuccess(Post post) {
				Iterator<JSONObject> users = new JSONWrapper(post.getJSONMessage()).getJSONArray("users");

				while (users.hasNext()) {
					JSONWrapper userWrapper = new JSONWrapper(users.next());

					String profileImageURL = userWrapper.getJSONString("profile_image");

					VBox vbox = new VBox(1);
					vbox.setAlignment(Pos.TOP_CENTER);
					vbox.setMinSize(100, 150);
					vbox.setMaxHeight(150);
					vbox.setStyle("-fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-padding: 5px; -fx-background-radius: 15px;");
					vbox.setCursor(Cursor.HAND);

					ImageView profileImage = new ImageView();
					Text displayName = new Text(userWrapper.getJSONString("display_name")), username = new Text("@" + userWrapper.getJSONString("username"));

					displayName.setFill(Color.web(CustomColor.WHITE.getColorHex()));
					displayName.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));

					username.setFill(Color.web(CustomColor.GRAY.getColorHex()));
					username.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 14));

					ImageManager.getProfileImage(profileImage, profileImageURL, 64, 64);

					vbox.setOnMouseEntered(event -> {
						displayName.setFill(Color.web(CustomColor.RHYTHM.getColorHex()));
					});
					vbox.setOnMouseExited(event -> {
						displayName.setFill(Color.web(CustomColor.WHITE.getColorHex()));
					});
					vbox.setOnMouseClicked(event -> {
						ProfileLoader.loadProfile(userWrapper.getJSONString("username").replace("@", ""));
					});

					vbox.getChildren().addAll(profileImage, displayName, username);

					suggestedUserHBox.getChildren().add(vbox);
				}
			}

			@Override
			public void onFail(String error) {

			}
		});
	}

	private void configure() {
		this.containerVBox.setStyle("-fx-padding: 35px " + GUIManager.getInstance().padding + "px;");

		this.suggestedUserScrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
		this.suggestedUserScrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		this.suggestedUserScrollPane.setStyle("-fx-background-color: transparent;");
		this.suggestedUserScrollPane.setPadding(new Insets(0, this.suggestedUserScrollPane.getPadding().getRight() + 15, 0, 0));
		this.suggestedUserScrollPane.minWidthProperty().bind(GUIManager.getInstance().contentManager.contentPane.widthProperty().subtract(230));
		this.suggestedUserScrollPane.maxWidthProperty().bind(GUIManager.getInstance().contentManager.contentPane.widthProperty().subtract(230));

		this.suggestedUserScrollPane.setMinHeight(150);
		this.suggestedUserScrollPane.setContent(this.suggestedUserHBox);

		this.suggestedUserTitleText.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		this.suggestedUserTitleText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 18));

		this.suggestedPlaylistsText.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		this.suggestedPlaylistsText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 18));
		
		this.comingSoon.setFill(Color.web(CustomColor.GRAY.getColorHex()));
		this.comingSoon.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 18));
		
		this.suggestedPlaylistVBox.getChildren().addAll(this.suggestedPlaylistsText, this.comingSoon);
		this.containerVBox.getChildren().addAll(this.suggestedUserTitleText, this.suggestedUserScrollPane, this.suggestedPlaylistVBox);

		GUIManager.getInstance().contentManager.contentPane.getChildren().add(this.containerVBox);
	}

	private void listen() {
	}
}