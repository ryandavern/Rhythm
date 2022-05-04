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

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.beatplaylist.Main;
import com.beatplaylist.enums.FontType;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.module.layout.sidebar.TabType;
import com.beatplaylist.gui.utilities.FXUtilities;
import com.beatplaylist.settings.Settings;
import com.beatplaylist.utilities.CustomColor;
import com.beatplaylist.utilities.CustomToolTip;
import com.beatplaylist.utilities.events.CompleteEvent;
import com.beatplaylist.utilities.events.ImageResponseEvent;
import com.beatplaylist.utilities.events.SocketResponseEvent;
import com.beatplaylist.utilities.format.NumberFormat;
import com.beatplaylist.utilities.image.ImageManager;
import com.beatplaylist.utilities.network.post.UpdateFollowState;
import com.beatplaylist.utilities.network.serialized.Post;
import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;
import com.beatplaylist.utilities.popup.Popup;
import com.beatplaylist.utilities.user.BaseUser;
import com.beatplaylist.utilities.user.UserManager;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class profile_page {

	private BaseUser profileUser;

	// rightSideVBox is the far right display that holds the user details, follower, following and playlist count.
	// profileHeaderVBox stores the profileImageView, usernameText and displayNameText
	// statVBox stores follower, following and playlist count.
	private VBox rightSideVBox, profileHeaderVBox, statVBox, profileDetailVBox;

	private HBox followMessageHBox;

	private Text usernameText, displayNameText, bioText;

	// Profile Image Objects
	private StackPane profileImageStackPane;
	private ImageView profileImageView;

	// Follow Button is the default button that controls, following, unfollowing and editing a profile.
	private Button followButton, messageButton;

	public profile_page() {
		this.profileUser = UserManager.getInstance().profileUser;

		this.rightSideVBox = new VBox();
		this.profileHeaderVBox = new VBox();
		this.statVBox = new VBox(5);
		this.profileDetailVBox = new VBox(7);
		this.followMessageHBox = new HBox(20);

		this.profileImageStackPane = new StackPane();

		this.usernameText = new Text("@" + this.profileUser.username);
		this.displayNameText = new Text(this.profileUser.displayName);
		this.bioText = new Text(this.profileUser.bio);

		this.profileImageView = new ImageView();

		this.followButton = new Button("FOLLOW") {
			public void requestFocus() {

			}
		};

		this.messageButton = new Button("MESSAGE") {
			public void requestFocus() {

			}
		};

		configure();
		listen();

		GUIManager.getInstance().topBar.titleBar.setSearchTextFieldTextAndUpdateCaret("@" + this.profileUser.username);
	}

	private void configure() {
		FXUtilities.setNodePadding(this.rightSideVBox, GUIManager.getInstance().padding);

		this.profileHeaderVBox.setAlignment(Pos.TOP_CENTER);
		this.profileHeaderVBox.setStyle("-fx-background-color: #4c4c4c; -fx-padding: 15px 25px 5px 25px;");
		this.profileHeaderVBox.setMinWidth(300);
		this.profileHeaderVBox.setMaxWidth(300);

		this.statVBox.setMinWidth(300);
		this.statVBox.setMaxWidth(300);
		this.statVBox.setStyle("-fx-background-color: #3d3d3d; -fx-padding: 15px 25px 15px 25px;");

		this.profileDetailVBox.setStyle("-fx-background-color: #4c4c4c; -fx-padding: 15px 25px 15px 25px;");
		this.profileDetailVBox.setMinWidth(300);
		this.profileDetailVBox.setMaxWidth(300);

		this.followMessageHBox.setAlignment(Pos.TOP_CENTER);
		this.followMessageHBox.setStyle("-fx-padding: 10px;");

		// Load user profile image.
		ImageManager.getProfileImage(this.profileImageView, this.profileUser.profileImageURL, 75, 75);

		String borderColor = "#fff";

		if (UserManager.getInstance().profileUser.isVerified()) {
			borderColor = "#2ecc71;";
		} else if (UserManager.getInstance().profileUser.isPremium()) {
			borderColor = "#3498db";
		}

		this.profileImageStackPane.setStyle("-fx-cursor: hand; -fx-padding: 2px; -fx-border-color: " + borderColor + "; -fx-border-width: 2px; -fx-border-insets: 3px; -fx-border-radius: 100%;");
		this.profileImageStackPane.setMaxWidth(75);

		// Username and Display Name Text Styles
		this.displayNameText.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		this.displayNameText.setFont(Font.font(FontType.VERDANA.getName(), 18));

		this.usernameText.setFill(Color.web("#898989"));
		this.usernameText.setFont(Font.font(FontType.VERDANA.getName(), 14));

		// Bio Text Styles
		this.bioText.setFill(Color.web("#c3c3c3"));
		this.bioText.setFont(Font.font(FontType.VERDANA.getName(), 14));
		this.bioText.setWrappingWidth(250);

		// Handle Location and Date Of Birth Styles and text value
		loadExtraProfileDetails();

		// Follow Button Styles
		if (this.profileUser.username.equals(UserManager.getInstance().user.username)) {
			this.followButton.setStyle("-fx-background-color: #3d3d3d; -fx-background-radius: 15px;");
			this.followButton.setText("EDIT");
		} else if (this.profileUser.isFollowing) {
			this.followButton.setStyle("-fx-background-color: " + CustomColor.RED.getColorHex() + "; -fx-background-radius: 15px;");
			this.followButton.setText("UNFOLLOW");
		} else {
			this.followButton.setStyle("-fx-background-color: " + CustomColor.RHYTHM.getColorHex() + "; -fx-background-radius: 15px;");
		}
		this.followButton.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
		this.followButton.setMinWidth(115);
		this.followButton.setFont(Font.font(FontType.DEFAULT.getName(), 14));
		this.followButton.setCursor(Cursor.HAND);

		// Message Button Styles
		this.messageButton.setStyle("-fx-background-color: #3d3d3d; -fx-background-radius: 15px;");
		this.messageButton.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
		this.messageButton.setMinWidth(115);
		this.messageButton.setFont(Font.font(FontType.DEFAULT.getName(), 14));
		// this.messageButton.setCursor(Cursor.HAND);
		this.messageButton.setOpacity(0.5);
		CustomToolTip.install(this.messageButton, new CustomToolTip("This feature is currently disabled and will be enabled in a future update"));

		this.followMessageHBox.getChildren().addAll(this.followButton, this.messageButton);
		this.profileImageStackPane.getChildren().add(this.profileImageView);
		this.profileHeaderVBox.getChildren().addAll(this.profileImageStackPane, this.displayNameText, this.usernameText, this.followMessageHBox);

		if (!this.bioText.getText().isEmpty())
			this.profileDetailVBox.getChildren().add(0, this.bioText);

		this.rightSideVBox.getChildren().addAll(this.profileHeaderVBox, this.statVBox, this.profileDetailVBox);

		GUIManager.getInstance().contentManager.contentPane.getChildren().add(this.rightSideVBox);

		getStat("Followers:", this.profileUser.followerCount);
		getStat("Following:", this.profileUser.followingCount);
		getStat("Public Playlists:", this.profileUser.playlistCount);

		FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), this.rightSideVBox);
		fadeTransition.setFromValue(0.0);
		fadeTransition.setToValue(1.0);
		fadeTransition.play();
	}

	private void listen() {
		this.profileImageStackPane.setOnMouseClicked(event -> {
			Popup.openLargeImage(this.profileUser.profileImageURL);
		});
		this.followButton.setOnMouseEntered(event -> {
			if (this.profileUser.username.equals(UserManager.getInstance().user.username))
				this.followButton.setTextFill(Color.web(Settings.getInstance().getDefaultColor()));
			else if (this.profileUser.isFollowing)
				this.followButton.setStyle("-fx-background-color: " + CustomColor.DARK_RED.getColorHex() + "; -fx-background-radius: 15px;");
			else
				this.followButton.setStyle("-fx-background-color: " + CustomColor.DARK_GREEN.getColorHex() + "; -fx-background-radius: 15px;");
		});
		this.followButton.setOnMouseExited(event -> {
			if (this.profileUser.username.equals(UserManager.getInstance().user.username))
				this.followButton.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
			else if (this.profileUser.isFollowing)
				this.followButton.setStyle("-fx-background-color: " + CustomColor.RED.getColorHex() + "; -fx-background-radius: 15px;");
			else
				this.followButton.setStyle("-fx-background-color: " + CustomColor.RHYTHM.getColorHex() + "; -fx-background-radius: 15px;");
		});
		this.followButton.setOnMouseClicked(event -> {
			if (this.profileUser.username.equals(UserManager.getInstance().user.username)) {
				ProfilePopup.editProfile(new ImageResponseEvent() {

					@Override
					public void onSuccess(String imageURL) {
						ImageManager.getProfileImage(profileImageView, imageURL, 75, 75);
					}

					@Override
					public void onFail(String error) {

					}
				}, new SocketResponseEvent() {

					@Override
					public void onSuccess(Post post) {
						int followerCount = profileUser.followerCount, followingCount = profileUser.followingCount, playlistCount = profileUser.playlistCount;

						profileUser = UserManager.getInstance().user;
						profileUser.followerCount = followerCount;
						profileUser.followingCount = followingCount;
						profileUser.playlistCount = playlistCount;

						usernameText.setText("@" + profileUser.username);
						displayNameText.setText(profileUser.displayName);
						bioText.setText(profileUser.bio);
						profileDetailVBox.getChildren().clear();
						statVBox.getChildren().clear();

						loadExtraProfileDetails();
						getStat("Followers:", profileUser.followerCount);
						getStat("Following:", profileUser.followingCount);
						getStat("Public Playlists:", profileUser.playlistCount);

						if (!bioText.getText().isEmpty())
							profileDetailVBox.getChildren().add(0, bioText);
					}

					@Override
					public void onFail(String error) {
						if (error.equals("EMAIL IN USE"))
							Notification.getInstance().createNotification("Email in use", "This email address is already in use by another account! Please try another.", AlertType.ERROR);
						else if (error.equals("USERNAME CLAIMED"))
							Notification.getInstance().createNotification("Email in use", "This username is already in use by another account! Please try another.", AlertType.ERROR);
					}
				});
			} else {
				// Follow / Unfollow User
				UpdateFollowState.send(this.profileUser.username, new CompleteEvent() {

					@Override
					public void onSuccess() {
						Platform.runLater(()->{
							if (profileUser.isFollowing) {
								followButton.setStyle("-fx-background-color: " + CustomColor.RHYTHM.getColorHex() + "; -fx-background-radius: 15px;");
								followButton.setText("FOLLOW");
								profileUser.isFollowing = false;
							} else {
								followButton.setStyle("-fx-background-color: " + CustomColor.RED.getColorHex() + "; -fx-background-radius: 15px;");
								followButton.setText("UNFOLLOW");
								profileUser.isFollowing = true;
							}
						});
					}

					@Override
					public void onFail(String error) {

					}
				});
			}
		});
		// this.messageButton.setOnMouseEntered(event -> {
		// this.messageButton.setTextFill(Color.web(CustomColor.GREEN.getColorHex()));
		// });
		// this.messageButton.setOnMouseExited(event -> {
		// this.messageButton.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
		// });
	}

	// This is re-called when a user edits their profile, so created this function to remove duplicate code.
	private void loadExtraProfileDetails() {
		String location = this.profileUser.location;

		if (!location.isEmpty()) {
			getProfileDetail("/resources/icons/my_location_icon.png", location);
		}

		if (this.profileUser.birthMonth > 0) {
			String month_name = new DateFormatSymbols().getMonths()[this.profileUser.birthMonth - 1];
			getProfileDetail("/resources/icons/birthday_cake_icon.png", "Born on " + month_name + " " + this.profileUser.birthDay + ", " + this.profileUser.birthYear);
		}

		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

			Date date = format.parse(this.profileUser.registrationDate);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);

			String month_name = new DateFormatSymbols().getMonths()[calendar.get(Calendar.MONTH)];

			getProfileDetail("/resources/icons/join_date_icon.png", "Joined " + month_name + " " + calendar.get(Calendar.DAY_OF_MONTH) + ", " + calendar.get(Calendar.YEAR));

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private void getStat(String statName, int statCount) {
		HBox statWrapperHBox = new HBox();
		Text statNameText = new Text(statName), statCountText = new Text(NumberFormat.getFormattedNumber(statCount));

		statWrapperHBox.setCursor(Cursor.HAND);

		Region regionFiller = new Region();
		regionFiller.setPrefWidth(225);

		statWrapperHBox.setAlignment(Pos.CENTER_RIGHT);
		statWrapperHBox.setMinWidth(250);

		Color color = Color.web("#c3c3c3"), hoverColor = Color.web(Settings.getInstance().getDefaultColor());
		Font font = Font.font(FontType.DEFAULT.getName(), 18);

		statNameText.setFill(color);
		statNameText.setFont(font);
		statNameText.setTextAlignment(TextAlignment.LEFT);

		statCountText.setFill(color);
		statCountText.setFont(font);
		statCountText.setTextAlignment(TextAlignment.RIGHT);

		statWrapperHBox.getChildren().addAll(statNameText, regionFiller, statCountText);

		statWrapperHBox.setOnMouseClicked(event -> {
			if (statName.toUpperCase().equals("PUBLIC PLAYLISTS:")) {
				GUIManager.getInstance().sideBar.sideBarTab.changeTab(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.PROFILE_PLAYLIST_VIEW), this.profileUser.username);
			} else if (statName.toUpperCase().contains("FOLLOWING")) {
				GUIManager.getInstance().sideBar.sideBarTab.changeTab(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.PROFILE_FOLLOWING_VIEW), this.profileUser);
			} else if (statName.toUpperCase().contains("FOLLOWERS")) {
				GUIManager.getInstance().sideBar.sideBarTab.changeTab(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.PROFILE_FOLLOWER_VIEW), this.profileUser);
			}
		});

		statWrapperHBox.setOnMouseEntered(event -> {
			statNameText.setFill(hoverColor);
			statCountText.setFill(hoverColor);
		});

		statWrapperHBox.setOnMouseExited(event -> {
			statNameText.setFill(color);
			statCountText.setFill(color);
		});

		if (statCount <= 0) {
			statWrapperHBox.setDisable(true);
		}

		CustomToolTip.install(statCountText, new CustomToolTip(String.valueOf(java.text.NumberFormat.getInstance().format(statCount))));

		this.statVBox.getChildren().add(statWrapperHBox);
	}

	private void getProfileDetail(String iconURL, String detailText) {
		HBox hbox = new HBox(7);
		ImageView imageView = new ImageView(new Image(Main.class.getResource(iconURL).toExternalForm(), 18, 18, false, false));
		Text text = new Text(detailText);

		text.setFill(Color.web("#c3c3c3"));
		text.setFont(Font.font(FontType.VERDANA.getName(), 14));
		text.setWrappingWidth(250);

		hbox.setAlignment(Pos.CENTER_LEFT);
		hbox.getChildren().addAll(imageView, text);

		this.profileDetailVBox.getChildren().add(hbox);
	}
}