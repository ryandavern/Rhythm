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

package com.beatplaylist.utilities.popup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.json.simple.JSONArray;

import com.beatplaylist.Main;
import com.beatplaylist.chromium.BrowserManager;
import com.beatplaylist.chromium.youtube.PageManager;
import com.beatplaylist.controller.ButtonSelector;
import com.beatplaylist.controller.PlaylistSelector;
import com.beatplaylist.enums.FontType;
import com.beatplaylist.enums.PaddingSide;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.module.layout.sidebar.TabType;
import com.beatplaylist.gui.utilities.FXUtilities;
import com.beatplaylist.gui.utilities.playlist.PlaylistLoader;
import com.beatplaylist.gui.utilities.playlist.SongTitle;
import com.beatplaylist.gui.utilities.playlist.SongView;
import com.beatplaylist.settings.Settings;
import com.beatplaylist.settings.SocialType;
import com.beatplaylist.utilities.CustomColor;
import com.beatplaylist.utilities.control.ControlledTextArea;
import com.beatplaylist.utilities.control.ControlledTextField;
import com.beatplaylist.utilities.control.PasswordField;
import com.beatplaylist.utilities.control.TextField;
import com.beatplaylist.utilities.control.TextLink;
import com.beatplaylist.utilities.control.ToggleSwitch;
import com.beatplaylist.utilities.events.CompleteEvent;
import com.beatplaylist.utilities.image.ImageManager;
import com.beatplaylist.utilities.network.post.AddOrRemoveSongFromPlaylist;
import com.beatplaylist.utilities.network.post.CancelSubscription;
import com.beatplaylist.utilities.network.post.Confirm2FACode;
import com.beatplaylist.utilities.network.post.EditSongMetadata;
import com.beatplaylist.utilities.network.post.FiatBuy;
import com.beatplaylist.utilities.network.post.LinkSocialNetworkingAccount;
import com.beatplaylist.utilities.network.post.MergePlaylist;
import com.beatplaylist.utilities.network.post.SubmitAFeaturedPlaylist;
import com.beatplaylist.utilities.network.post.SubmitFeedback;
import com.beatplaylist.utilities.network.post.UpdatePassword;
import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;
import com.beatplaylist.utilities.playlist.LyricManager;
import com.beatplaylist.utilities.playlist.Playlist;
import com.beatplaylist.utilities.playlist.PlaylistManager;
import com.beatplaylist.utilities.playlist.PlaylistOrderType;
import com.beatplaylist.utilities.playlist.PlaylistSongOrderType;
import com.beatplaylist.utilities.playlist.PlaylistWorker;
import com.beatplaylist.utilities.playlist.RoleType;
import com.beatplaylist.utilities.playlist.Song;
import com.beatplaylist.utilities.popup.control.PopupHBox;
import com.beatplaylist.utilities.popup.control.PopupVBox;
import com.beatplaylist.utilities.update.StartupData;
import com.beatplaylist.utilities.update.UpdateCategory;
import com.beatplaylist.utilities.update.UpdateManager;
import com.beatplaylist.utilities.user.UserManager;
import com.beatplaylist.utilities.validation.ValidateManager;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.WindowEvent;

public class Popup {

	// Use PopupVBox instead of VBox

	public static void fiatBuy() {
		PopupBuilder popup = new PopupBuilder();
		popup.getContentVBox().setSpacing(25);

		popup.setHeaderText("Rhythm - Fiat Buy");
		popup.setConfirmButtonText("Buy $RHYTHM");

		ControlledTextField cardNumber = new ControlledTextField(16, true), cvvNumber = new ControlledTextField(4, true);

		cardNumber.getTextField().setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
		cardNumber.getTextField().setMinWidth(300);
		cardNumber.getTextField().setMinHeight(35);
		cardNumber.getTextField().setPromptText("Card number");

		cvvNumber.getTextField().setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
		cvvNumber.getTextField().setMinWidth(75);
		cvvNumber.getTextField().setMinHeight(35);
		cvvNumber.getTextField().setPromptText("CVV");

		ComboBox<String> expiryMonth = new ComboBox<>(), expiryYear = new ComboBox<>();

		expiryMonth.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + ";");
		expiryMonth.setValue("Expiry Month");
		expiryMonth.setMinSize(200, 35);
		expiryMonth.setCursor(Cursor.HAND);
		expiryMonth.getItems().addAll("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");

		expiryYear.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + ";");
		expiryYear.setValue("Expiry Year");
		expiryYear.setMinSize(200, 35);
		expiryYear.setCursor(Cursor.HAND);

		for (int i = 2022; i < 2030; i++) {
			expiryYear.getItems().addAll(String.valueOf(i));
		}

		PopupHBox hbox = new PopupHBox(10), cardHBox = new PopupHBox(10), cardExpiryHBox = new PopupHBox(10);
		StringProperty chosenOption = new SimpleStringProperty();
		List<Button> chosen = new ArrayList<>();

		for (int i = 1; i < 6; i++) {
			final int number = i;
			Button buy = new Button("Buy $" + (50 * i));

			if (i == 1) {
				chosen.add(buy);
				buy.setOpacity(0.4);
			}

			buy.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + ";");
			buy.setMinSize(75, 35);
			buy.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));
			buy.setTextFill(Color.WHITE);

			buy.setOnMouseEntered(event -> {
				if (buy.getOpacity() < 0.8)
					return;

				buy.setCursor(Cursor.HAND);
				buy.setTextFill(Color.web(CustomColor.RHYTHM.getColorHex()));
			});
			buy.setOnMouseExited(event -> {
				buy.setTextFill(Color.WHITE);
			});
			buy.setOnAction(event -> {
				chosenOption.set(String.valueOf(50 * number));

				buy.setOpacity(0.4);

				if (chosen.get(0) != null) {
					chosen.get(0).setOpacity(1);
				}

				chosen.clear();
				chosen.add(buy);
			});

			hbox.getChildren().add(buy);
		}

		popup.onConfirm(event -> {

			FiatBuy.send(cardNumber.getTextField().getText(), cvvNumber.getTextField().getText(), expiryMonth.getValue(), expiryYear.getValue(), chosenOption.get());

			popup.close();
		});

		cardExpiryHBox.getChildren().addAll(expiryMonth, expiryYear);
		cardHBox.getChildren().addAll(cardNumber, cvvNumber);
		popup.getContentVBox().getChildren().addAll(hbox, cardHBox, cardExpiryHBox);

		popup.open();
	}

	public static void addCardToAccount() {
		PopupBuilder popup = new PopupBuilder();
		popup.getContentVBox().setSpacing(25);

		popup.setHeaderText("Rhythm - Add Card");
		popup.setConfirmButtonText("Add Card");

		ControlledTextField cardNumber = new ControlledTextField(16, true), cvvNumber = new ControlledTextField(4, true);

		cardNumber.getTextField().setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
		cardNumber.getTextField().setMinWidth(300);
		cardNumber.getTextField().setMinHeight(35);
		cardNumber.getTextField().setPromptText("Card number");

		cvvNumber.getTextField().setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
		cvvNumber.getTextField().setMinWidth(75);
		cvvNumber.getTextField().setMinHeight(35);
		cvvNumber.getTextField().setPromptText("CVV");

		ComboBox<String> expiryMonth = new ComboBox<>(), expiryYear = new ComboBox<>();

		expiryMonth.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + ";");
		expiryMonth.setValue("Expiry Month");
		expiryMonth.setMinSize(200, 35);
		expiryMonth.setCursor(Cursor.HAND);
		expiryMonth.getItems().addAll("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");

		expiryYear.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + ";");
		expiryYear.setValue("Expiry Year");
		expiryYear.setMinSize(200, 35);
		expiryYear.setCursor(Cursor.HAND);

		for (int i = 2022; i < 2030; i++) {
			expiryYear.getItems().addAll(String.valueOf(i));
		}

		PopupHBox cardHBox = new PopupHBox(10), cardExpiryHBox = new PopupHBox(10);

		popup.onConfirm(event -> {

			//FiatBuy.send(cardNumber.getTextField().getText(), cvvNumber.getTextField().getText(), expiryMonth.getValue(), expiryYear.getValue());

			popup.close();
		});

		cardExpiryHBox.getChildren().addAll(expiryMonth, expiryYear);
		cardHBox.getChildren().addAll(cardNumber, cvvNumber);
		popup.getContentVBox().getChildren().addAll(cardHBox, cardExpiryHBox);

		popup.open();
	}

	public static void createPlaylist(CompleteEvent completeEvent) {
		PopupBuilder popup = new PopupBuilder();
		popup.setScrollPaneEnabled(true, 600, 180, 600, 500);
		popup.getContentVBox().setSpacing(25);
		popup.setHeaderText("Create a new Playlist");

		PopupVBox playlistNameVBox = new PopupVBox(5), playlistVisibilityVBox = new PopupVBox(5);
		PopupHBox syncHBox = new PopupHBox(10);

		Text playlistNameText = new Text("Name"), playlistViewText = new Text("Who can view your playlist?"), syncText = new Text("Sync To YouTube?");
		ComboBox<String> playlistVisibilityComboBox = new ComboBox<>();
		ControlledTextField playlistNameTextField = new ControlledTextField(100);
		ToggleSwitch syncPlaylistToYouTubeToggle = new ToggleSwitch();
		syncPlaylistToYouTubeToggle.setEnabled(false);

		playlistNameText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));
		playlistNameText.setFill(Color.web(CustomColor.POPUP_SUB_HEADER.getColorHex()));

		playlistViewText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));
		playlistViewText.setFill(Color.web(CustomColor.POPUP_SUB_HEADER.getColorHex()));

		playlistNameTextField.getTextField().setPromptText("Enter a name for your playlist");
		playlistNameTextField.getTextField().setMinSize(400, 35);
		playlistNameTextField.getTextField().setMaxWidth(400);
		playlistNameTextField.getTextField().setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
		playlistNameTextField.getTextField().setFont(Font.font(FontType.VERDANA.getName(), 14));

		playlistVisibilityComboBox.getItems().addAll("Anyone", "Only Me", "Link Required");
		playlistVisibilityComboBox.setStyle("-fx-font: 14px \"" + FontType.DEFAULT.getName() + "\"; -fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-border-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-cursor: hand;");
		playlistVisibilityComboBox.setValue("Anyone");
		playlistVisibilityComboBox.setMinWidth(200);
		playlistVisibilityComboBox.setMinHeight(35);

		List<String> colors = PlaylistManager.getInstance().getColors();

		ScrollPane scrollpane = new ScrollPane();
		scrollpane.setVbarPolicy(ScrollBarPolicy.NEVER);
		scrollpane.setMaxWidth(550);
		FXUtilities.setNodePadding(scrollpane, 5, PaddingSide.BOTTOM, "-fx-background-color: transparent;");

		HBox scrollHBox = new HBox(10);

		scrollpane.setContent(scrollHBox);

		StringProperty chosenColor = new SimpleStringProperty("RANDOM");
		List<Button> chosen = new ArrayList<>();

		Button random = new Button("Random");
		random.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-background-radius: 10px;");
		random.setMinSize(100, 45);
		random.setCursor(Cursor.HAND);
		random.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
		random.setOpacity(0.8);
		random.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 14));
		chosen.add(random);

		random.setOnMouseEntered(event -> {
			if (random.getOpacity() > 0.9)
				random.setTextFill(Color.web(CustomColor.RHYTHM.getColorHex()));
		});
		random.setOnMouseExited(event -> {
			if (random.getOpacity() > 0.9)
				random.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
		});

		random.setOnMouseClicked(event -> {
			chosenColor.set("RANDOM");
			random.setOpacity(0.4);

			if (chosen.get(0) != null) {
				chosen.get(0).setOpacity(1);
			}

			chosen.clear();
			chosen.add(random);
		});

		scrollHBox.getChildren().add(random);

		for (String color : colors) {
			Button button = new Button();
			button.setStyle("-fx-background-color: linear-gradient(to bottom, " + color + "); -fx-background-radius: 10px;");
			button.setMinSize(100, 45);
			button.setCursor(Cursor.HAND);

			button.setOnMouseEntered(event -> {
				if (button.getOpacity() > 0.9)
					button.setStyle("-fx-background-color: linear-gradient(to top, " + color + "); -fx-background-radius: 10px;");
			});
			button.setOnMouseExited(event -> {
				if (button.getOpacity() > 0.9)
					button.setStyle("-fx-background-color: linear-gradient(to bottom, " + color + "); -fx-background-radius: 10px;");
			});

			button.setOnMouseClicked(event -> {
				chosenColor.set(color);
				button.setOpacity(0.4);

				if (chosen.get(0) != null) {
					chosen.get(0).setOpacity(1);
				}

				chosen.clear();
				chosen.add(button);
			});

			scrollHBox.getChildren().add(button);
		}

		syncText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));
		syncText.setFill(Color.web(CustomColor.POPUP_SUB_HEADER.getColorHex()));

		popup.onConfirm(event -> {
			if (playlistNameTextField.getText().isEmpty() || playlistNameTextField.getText().isBlank()) {
				Notification.getInstance().createNotification("Playlist", "Please enter a name for your playlist.", AlertType.ERROR);
				return;
			}
			Playlist playlist = new Playlist();

			PlaylistWorker.createPlaylist(playlist, playlistNameTextField.getText().trim(), "", playlistVisibilityComboBox.getValue(), PlaylistManager.getInstance().syncedToYouTube, new CompleteEvent() {

				@Override
				public void onSuccess() {
					Settings.getInstance().addPlaylistColor(playlist.getURL(), chosenColor.get());
					Notification.getInstance().createNotification("Playlist", "Your playlist was successfully created!", AlertType.SUCCESS);
					completeEvent.onSuccess();
				}

				@Override
				public void onFail(String error) {
					completeEvent.onFail(error);
				}
			});

			popup.close();
		});
		syncPlaylistToYouTubeToggle.setOnMouseClicked(event -> {
			if (PlaylistManager.getInstance().syncedToYouTube) {
				PlaylistManager.getInstance().syncedToYouTube = false;
			} else {
				PlaylistManager.getInstance().syncedToYouTube = true;
			}
			syncPlaylistToYouTubeToggle.setEnabled(PlaylistManager.getInstance().syncedToYouTube);
		});

		playlistNameVBox.getChildren().addAll(playlistNameText, playlistNameTextField);
		playlistVisibilityVBox.getChildren().addAll(playlistViewText, playlistVisibilityComboBox);
		syncHBox.getChildren().addAll(syncText, syncPlaylistToYouTubeToggle);
		if (UserManager.getInstance().user.getAccount(SocialType.YOUTUBE) == null)
			popup.getContentVBox().getChildren().addAll(playlistNameVBox, playlistVisibilityVBox, scrollpane);
		else
			popup.getContentVBox().getChildren().addAll(playlistNameVBox, playlistVisibilityVBox, syncHBox, scrollpane);

		popup.open();
	}

	public static void mergePlaylists(PlaylistSelector mainPlaylist) {
		PopupBuilder popup = new PopupBuilder();
		popup.getContentVBox().setSpacing(5);
		popup.setWrapperMinimumSize(400, 150);
		popup.setHeaderText("Merge Playlists");

		if (mainPlaylist == null)
			popup.setConfirmButtonText("Next");
		else {
			popup.setConfirmButtonText("Merge");
			popup.setCancelButtonText("Back");
		}
		ScrollPane scrollPane = new ScrollPane();
		VBox vbox = new VBox(5);

		PlaylistSelector mainPlaylistSelector = new PlaylistSelector();
		ButtonSelector playlistButtonSelector = new ButtonSelector();

		popup.setCustomScrollPaneDefaults(scrollPane, vbox, 400, 250, 400, 250);

		Text text = new Text("Select the playlist you want to merge songs into");
		text.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));
		text.setFill(Color.web(CustomColor.GRAY.getColorHex()));

		if (mainPlaylist != null) {
			text.setText("Select the playlist you want to merge songs from");
		}
		if (mainPlaylist == null) {
			popup.getContentVBox().getChildren().addAll(text, scrollPane);
		} else {
			Text playlistName = new Text("You are merging songs into the playlist: " + mainPlaylist.getSelectedPlaylist().getName());
			playlistName.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));
			playlistName.setFill(Color.web(CustomColor.GRAY.getColorHex()));
			playlistName.setWrappingWidth(420);

			popup.getContentVBox().getChildren().addAll(text, playlistName, scrollPane);
		}
		for (Playlist playlist : PlaylistManager.getInstance().getPlaylists()) {
			if ((mainPlaylist == null && playlist.getCreatorUsername().equals(UserManager.getInstance().user.username)) || mainPlaylist != null) {
				if (mainPlaylist != null && mainPlaylist.getSelectedPlaylist().getURL().equals(playlist.getURL()))
					continue;
				Button button = new Button(playlist.getName());

				if (playlist.getName().length() > 50) {
					button.setText(playlist.getName().substring(0, 50));
				}

				button.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + ";");
				button.setMinSize(400, 35);
				button.setMaxWidth(400);
				button.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));
				button.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
				button.setCursor(Cursor.HAND);

				button.setOnMouseEntered(event -> {
					if (playlistButtonSelector.hasButtonSelected() && playlistButtonSelector.getSelectedButton() == button)
						return;
					button.setTextFill(Color.web(CustomColor.RHYTHM.getColorHex()));
				});

				button.setOnMouseExited(event -> {
					if (playlistButtonSelector.hasButtonSelected() && playlistButtonSelector.getSelectedButton() == button)
						return;
					button.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
				});

				button.setOnAction(event -> {
					if (playlistButtonSelector.hasButtonSelected()) {
						playlistButtonSelector.getSelectedButton().setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
					}

					mainPlaylistSelector.selectPlaylist(playlist);
					playlistButtonSelector.selectButton(button);

					button.setTextFill(Color.web(CustomColor.RHYTHM.getColorHex()));
				});

				vbox.getChildren().add(button);
			}
		}
		popup.onCancel(event -> {
			if (popup.getCancelText().equals("Back")) {
				popup.quickClose();
				mergePlaylists(null);
			} else {
				popup.close();
			}
		});
		popup.onConfirm(event -> {
			if (!mainPlaylistSelector.hasPlaylistSelected()) {
				Notification.getInstance().createNotification("Playlist", "Please select a playlist before continuing", AlertType.ERROR);
				return;
			}
			popup.quickClose();
			if (mainPlaylist == null)
				mergePlaylists(mainPlaylistSelector);
			else {
				JSONArray playlists = new JSONArray();

				playlists.add(mainPlaylistSelector.getSelectedPlaylist().getURL());

				MergePlaylist.send(mainPlaylist.getSelectedPlaylist(), playlists, new CompleteEvent() {

					@Override
					public void onSuccess() {

					}

					@Override
					public void onFail(String error) {

					}
				});
			}
		});

		popup.open();
	}

	// Delete a playlist popup - Called from the playlist_view_page
	public static void confirmDeletePlaylist(Playlist playlist) {
		PopupBuilder popup = new PopupBuilder();
		popup.getContentVBox().setSpacing(30);
		popup.setWrapperMinimumSize(500, 150);
		popup.setHeaderText("Do you really want to delete this playlist?");

		Text playlist_name_text = new Text(playlist.getName());

		playlist_name_text.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 18));
		playlist_name_text.setFill(Color.web(CustomColor.POPUP_SUB_HEADER.getColorHex()));
		playlist_name_text.setTextAlignment(TextAlignment.CENTER);

		popup.onConfirm(event -> {
			popup.quickClose();
			PlaylistWorker.deletePlaylist(playlist.getURL(), new CompleteEvent() {

				@Override
				public void onSuccess() {
					// If the displaying page is the playlist that the user deleted, send the user back to the playlists page.
					if ((GUIManager.getInstance().currentTab != null && GUIManager.getInstance().currentTab.tab == TabType.OPEN_PLAYLIST_VIEW) && PlaylistLoader.getInstance().playlistView.playlist.getURL().equals(playlist.getURL())) {
						GUIManager.getInstance().sideBar.sideBarTab.changeTab(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.PLAYLISTS));
						Notification.getInstance().createNotification("Playlist", "You have deleted the playlist \"" + playlist.getName() + "\"", AlertType.SUCCESS);
					}
				}

				@Override
				public void onFail(String error) {
					Notification.getInstance().createNotification("Playlist", "An error occurred while deleting your playlist.", AlertType.ERROR);
				}
			});
		});

		popup.getContentVBox().getChildren().addAll(playlist_name_text);
		popup.open();
	}

	public static void errorAlert(String title, String error, CustomColor color) {
		PopupBuilder popup = new PopupBuilder();
		popup.getContentVBox().setSpacing(30);
		popup.setWrapperMinimumSize(500, 150);
		popup.hideCancelButton();
		popup.setConfirmButtonText("Close");
		popup.setHeaderText(title);

		Text playlist_name_text = new Text(error);

		playlist_name_text.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 18));
		playlist_name_text.setFill(Color.web(color.getColorHex()));
		playlist_name_text.setTextAlignment(TextAlignment.CENTER);

		popup.onConfirm(event -> {
			popup.close();
		});

		popup.getContentVBox().getChildren().addAll(playlist_name_text);
		popup.open();
	}

	// Edit song title, artist name.
	public static void editSongMetadata(Playlist playlist, Song song, CompleteEvent completeEvent) {
		PopupBuilder popup = new PopupBuilder();
		popup.getContentVBox().setSpacing(25);
		popup.setWrapperMinimumSize(500, 150);
		popup.setHeaderText("Edit song metadata");
		popup.setConfirmButtonText("Save");

		PopupVBox songTitleVBox = new PopupVBox(5), startTimeVBox = new PopupVBox(5), endTimeVBox = new PopupVBox(5);

		Text songTitle = new Text("Song Name"), startTimeText = new Text("Song start time in seconds"), endTimeText = new Text("Song end time in seconds");
		ControlledTextField songTitleTextField = new ControlledTextField(100), songStartTextField = new ControlledTextField(4, true), songEndTextField = new ControlledTextField(4, true);

		songTitle.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));
		songTitle.setFill(Color.web(CustomColor.POPUP_SUB_HEADER.getColorHex()));

		startTimeText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));
		startTimeText.setFill(Color.web(CustomColor.POPUP_SUB_HEADER.getColorHex()));

		endTimeText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));
		endTimeText.setFill(Color.web(CustomColor.POPUP_SUB_HEADER.getColorHex()));

		songTitleTextField.getTextField().setPromptText("Enter song title");
		songTitleTextField.getTextField().setMinSize(400, 35);
		songTitleTextField.getTextField().setMaxWidth(400);
		songTitleTextField.getTextField().setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
		songTitleTextField.getTextField().setFont(Font.font(FontType.VERDANA.getName(), 14));
		songTitleTextField.getTextField().setText(SongTitle.getWithoutBracket(SongTitle.formatSongTitle(song.getFullSongTitle())));

		songStartTextField.getTextField().setPromptText("Start Time e.g. 130");
		songStartTextField.getTextField().setMinSize(400, 35);
		songStartTextField.getTextField().setMaxWidth(400);
		songStartTextField.getTextField().setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
		songStartTextField.getTextField().setFont(Font.font(FontType.VERDANA.getName(), 14));
		if (song.getStartTime() != 0) {
			songStartTextField.setText(String.valueOf(song.getStartTime()));
		}

		songEndTextField.getTextField().setPromptText("End Time e.g. 130");
		songEndTextField.getTextField().setMinSize(400, 35);
		songEndTextField.getTextField().setMaxWidth(400);
		songEndTextField.getTextField().setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
		songEndTextField.getTextField().setFont(Font.font(FontType.VERDANA.getName(), 14));
		if (song.getEndTime() != 0) {
			songEndTextField.setText(String.valueOf(song.getEndTime()));
		}

		popup.onConfirm(event -> {
			String startTime = songStartTextField.getText(), endTime = songEndTextField.getText();
			boolean changeMade = false;

			// If the entered time by the user is not numeric or is empty, set the time to 0.
			if (startTime.isEmpty() || !ValidateManager.isNumeric(songStartTextField.getText()))
				startTime = "0";

			if (endTime.isEmpty() || !ValidateManager.isNumeric(songEndTextField.getText()))
				endTime = "0";

			if (!endTime.isEmpty() && !endTime.equals("0") && Double.valueOf(endTime) < 20) {
				Notification.getInstance().createNotification("Song", "Please set an end time of at least 20 seconds.", AlertType.ERROR);
				return;
			}
			if (!startTime.isEmpty() && !endTime.isEmpty() && Double.valueOf(startTime) == Double.valueOf(endTime)) {
				Notification.getInstance().createNotification("Song", "Please set a different start and end time.", AlertType.ERROR);
				return;
			}
			if (!startTime.isEmpty() && !endTime.isEmpty() && Double.valueOf(endTime) > 0 && Double.valueOf(startTime) > Double.valueOf(endTime)) {
				Notification.getInstance().createNotification("Song", "Start time cannot be greater than the end time.", AlertType.ERROR);
				return;
			}
			if (song.getEndTime() > 0 && endTime.isEmpty() || !endTime.isEmpty() && song.getEndTime() != Double.valueOf(endTime)) {
				changeMade = true;
			}
			if (song.getStartTime() > 0 && startTime.isEmpty() || !startTime.isEmpty() && song.getStartTime() != Double.valueOf(startTime)) {
				changeMade = true;
			}
			if (!song.getFullSongTitle().equals(songTitleTextField.getText())) {
				changeMade = true;
			}
			if (!changeMade) {
				Notification.getInstance().createNotification("Song", "You have not made any changes to the song metadata.", AlertType.ERROR);
				return;
			}

			song.setEndTime(endTime);
			song.setStartTime(startTime);
			song.setFullSongTitle(songTitleTextField.getText());
			EditSongMetadata.send(playlist, song.getURL(), song.getFullSongTitle(), String.valueOf(song.getStartTime()), String.valueOf(song.getEndTime()));
			completeEvent.onSuccess();
			popup.close();
		});

		songTitleVBox.getChildren().addAll(songTitle, songTitleTextField);
		startTimeVBox.getChildren().addAll(startTimeText, songStartTextField);
		endTimeVBox.getChildren().addAll(endTimeText, songEndTextField);

		popup.getContentVBox().getChildren().addAll(songTitleVBox, startTimeVBox, endTimeVBox);
		popup.open();
	}

	public static void submitFeedback() {
		PopupBuilder popup = new PopupBuilder();
		popup.getContentVBox().setSpacing(25);
		popup.setWrapperMinimumSize(450, 150);
		popup.setHeaderText("Submit Feedback");
		popup.setConfirmButtonText("Submit Feedback");

		ControlledTextArea feedbackTextfield = new ControlledTextArea(600);

		feedbackTextfield.getTextArea().setPromptText("Enter feedback - We have assigned 10 million Rhythm for the first round of bug-bounty. Submit bugs / suggestions and win some Rhythm!");
		feedbackTextfield.getTextArea().setMinSize(400, 35);
		feedbackTextfield.getTextArea().setMaxWidth(400);
		feedbackTextfield.getTextArea().setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
		feedbackTextfield.getTextArea().setFont(Font.font(FontType.VERDANA.getName(), 14));

		popup.onConfirm(event -> {
			if (feedbackTextfield.getText().isEmpty() || feedbackTextfield.getText().isBlank()) {
				Notification.getInstance().createNotification("Feedback", "Please enter some feedback!", AlertType.ERROR);
				return;
			}
			SubmitFeedback.send(feedbackTextfield.getText());
			popup.close();
		});

		popup.getContentVBox().getChildren().addAll(feedbackTextfield);
		popup.open();
	}

	// Called when a user tries to delete a song from their playlist.
	public static void confirmDeleteSongFromPlaylist(Playlist playlist, SongView songView) {
		Song song = songView.song;

		PopupBuilder popup = new PopupBuilder();
		popup.getContentVBox().setSpacing(7);
		popup.setWrapperMinimumSize(500, 100);
		popup.setHeaderText("Are you sure you want to remove the song:");

		if (playlist.getURL().equals("my-likes")) {
			popup.setHeaderText("Are you sure you want to unlike the song:");
		}

		Text song_name_text = new Text(SongTitle.formatSongTitle(song.getFullSongTitle()));

		song_name_text.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 14));
		song_name_text.setFill(Color.web(Settings.getInstance().getDefaultColor()));

		popup.onConfirm(event -> {
			AddOrRemoveSongFromPlaylist.send(playlist, song, true, new CompleteEvent() {
				@Override
				public void onSuccess() {
					// If current displayed tab is the playlist view showing the song list, remove the songView from the song list vbox.
					if (PlaylistLoader.getInstance().playlistView != null && GUIManager.getInstance().currentTab != null && GUIManager.getInstance().currentTab.tab == TabType.OPEN_PLAYLIST_VIEW) {
						if (PlaylistLoader.getInstance().playlistView.songListVBox.getChildren().contains(songView.songHBox)) {
							PlaylistLoader.getInstance().playlistView.songListVBox.getChildren().remove(songView.songHBox);
						}
						int row = songView.gridpaneRow;
						for (SongView songView : PlaylistManager.getInstance().storedSongView) {
							if (songView.gridpaneRow > row) {
								songView.gridpaneRow -= 1;
								songView.updateBackgroundColor();
							}
						}
					}
				}

				@Override
				public void onFail(String error) {

				}
			});
			if (playlist.getURL().equals("my-likes"))
				Notification.getInstance().createNotification("Song", "Song has been removed from your likes!", AlertType.SUCCESS);
			else
				Notification.getInstance().createNotification("Song", "Song removed from the playlist '" + playlist.getName() + "'!", AlertType.SUCCESS);
			popup.close();
		});

		popup.getContentVBox().getChildren().addAll(song_name_text);
		popup.open();
	}

	// Called when a user tries to quit the program.
	public static void confirmCloseProgram() {
		PopupBuilder popup = new PopupBuilder();
		popup.setWrapperSize(400, 0, 400, 500);
		popup.setHeaderText("Are you sure you want to exit Rhythm?");
		popup.setConfirmButtonText("Exit Rhythm");

		popup.onConfirm(event -> {
			GUIManager.getInstance().stopApplication();
			GUIManager.getInstance().stage.fireEvent(new WindowEvent(GUIManager.getInstance().stage, WindowEvent.WINDOW_CLOSE_REQUEST));
			Platform.exit();
		});

		popup.open();
	}

	public static void confirmRemoveSocialMediaAccount(String socialMediaType, CompleteEvent completeEvent) {
		PopupBuilder popup = new PopupBuilder();
		popup.setWrapperSize(400, 0, 400, 500);
		popup.setHeaderText("Are you sure you want to remove your linked " + socialMediaType + " account?");

		popup.onConfirm(event -> {
			completeEvent.onSuccess();
			popup.close();
		});

		popup.open();
	}

	// Called when a user tries to logout of their account.
	public static void confirmLogout() {
		PopupBuilder popup = new PopupBuilder();
		popup.setWrapperSize(400, 0, 400, 500);
		popup.setHeaderText("Are you sure you want to logout?");
		popup.setConfirmButtonText("Logout");

		popup.onConfirm(event -> {
			UserManager.getInstance().logout();
		});

		popup.open();
	}

	// Add custom lyrics to song popup.
	public static void addCustomGeniusLyricsToSong(CompleteEvent completeEvent) {

		String currentURL = BrowserManager.getInstance().getVideoURL().replace("https://www.youtube.com/watch?v=", "").replace("https://music.youtube.com/watch?v=", "");

		PopupBuilder popup = new PopupBuilder();
		popup.getContentVBox().setSpacing(15);
		popup.setHeaderText("Add Custom Lyrics");
		popup.setWrapperSize(400, 0, 400, 500);
		popup.setConfirmButtonText("Add Lyrics");

		TextField textField = new TextField();
		textField.setStyle("-fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-background-radius: 5px; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
		textField.setPromptText("Enter a url from https://genius.com/");
		textField.setMinWidth(350);
		textField.setMaxWidth(350);
		if (LyricManager.getInstance().cached_lyrics.containsKey(currentURL.replace("https://www.youtube.com/watch?v=", ""))) {
			textField.setText(LyricManager.getInstance().cached_lyrics.get(currentURL.replace("https://www.youtube.com/watch?v=", "")));
		}

		popup.onConfirm(event -> {

			if (!textField.getText().isEmpty() && !textField.getText().startsWith("https://genius.com/") && !textField.getText().startsWith("https://www.genius.com/")) {
				Notification.getInstance().createNotification("Genius", "Please enter a url from https://genius.com/", AlertType.ERROR);
				return;
			}
			if (textField.getText().isEmpty() || textField.getText().isBlank())
				LyricManager.getInstance().removeLyric(currentURL);
			else
				LyricManager.getInstance().addLyric(currentURL, textField.getText());
			completeEvent.onSuccess();
			popup.close();
		});

		popup.getContentVBox().getChildren().addAll(textField);
		popup.open();
	}

	public static void addSongToPlaylist(String songURL) {
		Thread thread = new Thread(() -> {
			String title = PageManager.getInstance().getPageTitle(songURL);
			Platform.runLater(() -> {
				addSongToPlaylist(title, songURL);
			});
		});
		thread.start();
	}

	public static void addSongToPlaylist(String title, String songURL) {
		if (songURL.isEmpty())
			return;

		if (GUIManager.getInstance().currentTab.tab == TabType.NOW_PLAYING) {
			BrowserManager.getInstance().getCurrentBrowser().web_view.setVisible(false);
		}
		if (GUIManager.getInstance().currentTab.tab == TabType.BROWSE) {
			GUIManager.getInstance().searchBrowser.web_view.setVisible(false);
		}

		PlaylistManager.getInstance().printPlaylists();

		PopupBuilder popup = new PopupBuilder();
		popup.setScrollPaneEnabled(true, 500, 0, 500, 500);
		popup.setHeaderText("Add song to playlist");
		popup.centerHeaderText();

		popup.hideButtonBar();
		popup.getContentVBox().setAlignment(Pos.CENTER);

		Button new_playlist = new Button("New Playlist");
		new_playlist.setMinSize(250, 30);
		new_playlist.setMaxSize(250, 30);
		new_playlist.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 15));
		new_playlist.setStyle("-fx-background-color: " + CustomColor.ORANGE.getColorHex() + "; -fx-cursor: hand; -fx-background-radius: 5px; -fx-border-radius: 5px;");
		new_playlist.setOnAction(event -> {
			createNewPlaylistPopup(title, songURL);
		});

		new_playlist.setOnMouseEntered(event -> {
			new_playlist.setStyle("-fx-background-color: #d35400; -fx-cursor: hand; -fx-background-radius: 5px; -fx-border-radius: 5px;");
		});
		new_playlist.setOnMouseExited(event -> {
			new_playlist.setStyle("-fx-background-color: " + CustomColor.ORANGE.getColorHex() + "; -fx-cursor: hand; -fx-background-radius: 5px; -fx-border-radius: 5px;");
		});

		popup.getContentVBox().getChildren().addAll(new_playlist);
		for (Playlist playlist : PlaylistManager.getInstance().getPlaylists()) {
			if (playlist.getRole() == RoleType.EDIT || playlist.getCreatorUsername().toLowerCase().equals(UserManager.getInstance().getUser().username.toLowerCase())) {
				Button button = new Button(playlist.getName()) {
					public void requestFocus() {

					}
				};
				button.setTextAlignment(TextAlignment.LEFT);
				if (playlist.getName().length() > 20)
					button.setText(playlist.getName().substring(0, 20));

				button.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-cursor: hand; -fx-background-radius: 5px; -fx-border-radius: 5px;");
				button.setMinSize(250, 30);
				button.setMaxSize(250, 30);
				button.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 15));

				button.setOnAction(event -> {
					popup.close();
					if (songURL.contains("watch")) {
						Song song = new Song();
						song.setFullSongTitle(title.replace("&amp;", "&").replace("- YouTube", "").trim());
						song.setURL(songURL);
						PlaylistWorker.updateSongInPlaylist(playlist, song, false, new CompleteEvent() {

							@Override
							public void onSuccess() {
								Notification.getInstance().createNotification("Song", "Song added to the playlist '" + playlist.getName() + "'!", AlertType.SUCCESS);
							}

							@Override
							public void onFail(String error) {
								if (error.equals("SONG_ALREADY_IN_PLAYLIST"))
									Notification.getInstance().createNotification("Playlist", "This song is already in the playlist you selected.", AlertType.ERROR);
								else
									Notification.getInstance().createNotification("Playlist", "An error occured while adding this song to your playlist.", AlertType.ERROR);
							}
						});
					}
				});
				button.setOnMouseEntered(event -> {
					button.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-text-fill: " + Settings.getInstance().getDefaultColor() + "; -fx-cursor: hand; -fx-background-radius: 5px; -fx-border-radius: 5px;");
				});
				button.setOnMouseExited(event -> {
					button.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-cursor: hand; -fx-background-radius: 5px; -fx-border-radius: 5px;");
				});
				popup.getContentVBox().getChildren().add(button);
			} else {
				System.out.println(playlist.getRole().name());
				System.out.println(playlist.getCreatorDisplayName() + " : " + UserManager.getInstance().getUser().username.toLowerCase());
			}
		}
		popup.open();
	}

	public static void createNewPlaylistPopup(String songTitle, String songURL) {
		PopupBuilder popup = new PopupBuilder();
		popup.setScrollPaneEnabled(true, 600, 180, 600, 500);
		popup.getContentVBox().setSpacing(25);
		popup.setHeaderText("Create a new Playlist");

		PopupVBox playlistNameVBox = new PopupVBox(5), playlistVisibilityVBox = new PopupVBox(5);
		PopupHBox syncHBox = new PopupHBox(10);

		Text playlistNameText = new Text("Name"), playlistViewText = new Text("Who can view your playlist?"), syncText = new Text("Sync To YouTube?");
		ComboBox<String> playlistVisibilityComboBox = new ComboBox<>();
		ControlledTextField playlistNameTextField = new ControlledTextField(100);
		ToggleSwitch syncPlaylistToYouTubeToggle = new ToggleSwitch();
		syncPlaylistToYouTubeToggle.setEnabled(false);

		playlistNameText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));
		playlistNameText.setFill(Color.web(CustomColor.POPUP_SUB_HEADER.getColorHex()));

		playlistViewText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));
		playlistViewText.setFill(Color.web(CustomColor.POPUP_SUB_HEADER.getColorHex()));

		playlistNameTextField.getTextField().setPromptText("Enter a name for your playlist");
		playlistNameTextField.getTextField().setMinSize(400, 35);
		playlistNameTextField.getTextField().setMaxWidth(400);
		playlistNameTextField.getTextField().setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
		playlistNameTextField.getTextField().setFont(Font.font(FontType.VERDANA.getName(), 14));

		playlistVisibilityComboBox.getItems().addAll("Anyone", "Only Me", "Link Required");
		playlistVisibilityComboBox.setStyle("-fx-font: 14px \"" + FontType.DEFAULT.getName() + "\"; -fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-border-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-cursor: hand;");
		playlistVisibilityComboBox.setValue("Anyone");
		playlistVisibilityComboBox.setMinWidth(200);
		playlistVisibilityComboBox.setMinHeight(35);

		List<String> colors = PlaylistManager.getInstance().getColors();

		ScrollPane scrollpane = new ScrollPane();
		scrollpane.setVbarPolicy(ScrollBarPolicy.NEVER);
		scrollpane.setMaxWidth(550);
		FXUtilities.setNodePadding(scrollpane, 5, PaddingSide.BOTTOM, "-fx-background-color: transparent;");

		HBox scrollHBox = new HBox(10);

		scrollpane.setContent(scrollHBox);

		StringProperty chosenColor = new SimpleStringProperty("RANDOM");
		List<Button> chosen = new ArrayList<>();

		Button random = new Button("Random");
		random.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-background-radius: 10px;");
		random.setMinSize(100, 45);
		random.setCursor(Cursor.HAND);
		random.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
		random.setOpacity(0.8);
		random.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 14));
		chosen.add(random);

		random.setOnMouseEntered(event -> {
			if (random.getOpacity() > 0.9)
				random.setTextFill(Color.web(CustomColor.RHYTHM.getColorHex()));
		});
		random.setOnMouseExited(event -> {
			if (random.getOpacity() > 0.9)
				random.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
		});

		random.setOnMouseClicked(event -> {
			chosenColor.set("RANDOM");
			random.setOpacity(0.4);

			if (chosen.get(0) != null) {
				chosen.get(0).setOpacity(1);
			}

			chosen.clear();
			chosen.add(random);
		});

		scrollHBox.getChildren().add(random);

		for (String color : colors) {
			Button button = new Button();
			button.setStyle("-fx-background-color: linear-gradient(to bottom, " + color + "); -fx-background-radius: 10px;");
			button.setMinSize(100, 45);
			button.setCursor(Cursor.HAND);

			button.setOnMouseEntered(event -> {
				if (button.getOpacity() > 0.9)
					button.setStyle("-fx-background-color: linear-gradient(to top, " + color + "); -fx-background-radius: 10px;");
			});
			button.setOnMouseExited(event -> {
				if (button.getOpacity() > 0.9)
					button.setStyle("-fx-background-color: linear-gradient(to bottom, " + color + "); -fx-background-radius: 10px;");
			});

			button.setOnMouseClicked(event -> {
				chosenColor.set(color);
				button.setOpacity(0.4);

				if (chosen.get(0) != null) {
					chosen.get(0).setOpacity(1);
				}

				chosen.clear();
				chosen.add(button);
			});

			scrollHBox.getChildren().add(button);
		}

		syncText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));
		syncText.setFill(Color.web(CustomColor.POPUP_SUB_HEADER.getColorHex()));

		popup.onConfirm(event -> {
			if (playlistNameTextField.getText().isEmpty() || playlistNameTextField.getText().isBlank()) {
				Notification.getInstance().createNotification("Playlist", "Please enter a name for your playlist.", AlertType.ERROR);
				return;
			}
			Playlist playlist = new Playlist();

			PlaylistWorker.createPlaylist(playlist, playlistNameTextField.getText().trim(), "", playlistVisibilityComboBox.getValue(), PlaylistManager.getInstance().syncedToYouTube, new CompleteEvent() {

				@Override
				public void onSuccess() {
					Settings.getInstance().addPlaylistColor(playlist.getURL(), chosenColor.get());
					Notification.getInstance().createNotification("Song", "A new playlist was created and the song was added to your new playlist.", AlertType.SUCCESS);

					Song song = new Song();
					song.fullSongTitle = songTitle;
					song.url = songURL;

					PlaylistWorker.updateSongInPlaylist(playlist, song, false, new CompleteEvent() {

						@Override
						public void onSuccess() {
							popup.close();
						}

						@Override
						public void onFail(String error) {

						}
					});
				}

				@Override
				public void onFail(String error) {
				}
			});

			popup.close();
		});
		syncPlaylistToYouTubeToggle.setOnMouseClicked(event -> {
			if (PlaylistManager.getInstance().syncedToYouTube) {
				PlaylistManager.getInstance().syncedToYouTube = false;
			} else {
				PlaylistManager.getInstance().syncedToYouTube = true;
			}
			syncPlaylistToYouTubeToggle.setEnabled(PlaylistManager.getInstance().syncedToYouTube);
		});

		playlistNameVBox.getChildren().addAll(playlistNameText, playlistNameTextField);
		playlistVisibilityVBox.getChildren().addAll(playlistViewText, playlistVisibilityComboBox);
		syncHBox.getChildren().addAll(syncText, syncPlaylistToYouTubeToggle);
		if (UserManager.getInstance().user.getAccount(SocialType.YOUTUBE) == null)
			popup.getContentVBox().getChildren().addAll(playlistNameVBox, playlistVisibilityVBox, scrollpane);
		else
			popup.getContentVBox().getChildren().addAll(playlistNameVBox, playlistVisibilityVBox, syncHBox, scrollpane);

		popup.open();
	}

	// Remove two factor authentication from user account confirmation popup - Called from general_settings_page.
	public static void remove2FA(CompleteEvent completeEvent) {
		PopupBuilder popup = new PopupBuilder();
		popup.setHeaderText("Are you sure you want to remove 2FA from your account?");
		popup.setConfirmButtonText("Remove 2FA");

		popup.onConfirm(event -> {
			popup.close();
			completeEvent.onSuccess();
		});

		popup.onCancel(event -> {
			completeEvent.onFail("");
			popup.close();
		});
		popup.open();
	}

	// Displays two factor authentication QR code.
	// @param imageURL is the QR code image url hosted on Google.
	// @param twoFactorButton is the button in the general settings page.
	public static void display2FAPopup(String imageURL, Button twoFactorButton) {
		PopupBuilder popup = new PopupBuilder();
		popup.getContentVBox().setSpacing(10);
		popup.setHeaderText("Scan the image with your 2FA app");

		ImageView imageView = new ImageView(new Image(Main.class.getResource("/resources/default_profile.png").toExternalForm()));
		TextField codeTextField = new TextField();
		codeTextField.setPromptText("Enter 2FA code from authentication app");
		codeTextField.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
		codeTextField.setMinSize(450, 35);
		codeTextField.setMaxSize(450, 35);
		codeTextField.setFont(Font.font(FontType.DEFAULT.getName(), 18));
		codeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (codeTextField.getText().length() > 6)
				codeTextField.setText(oldValue);
		});
		popup.onConfirm(event -> {
			popup.setButtonsDisabled(true);
			Confirm2FACode.send(codeTextField.getText(), popup, twoFactorButton);
		});

		popup.getContentVBox().getChildren().addAll(imageView, codeTextField);
		popup.open();

		new Thread(() -> {
			Image image = new Image(imageURL);
			Platform.runLater(() -> {
				imageView.setImage(image);
			});
		}).start();
	}

	// Displays announcements from info.json file hosted on website.
	public static void displayServerAnnouncement() {
		PopupBuilder popup = new PopupBuilder();
		popup.setHeaderText("New Announcement");
		popup.setConfirmButtonText("Close");
		popup.hideCancelButton();

		Text text = new Text(StartupData.getInstance().announcement);
		text.setFont(Font.font(FontType.DEFAULT.getName(), 14));
		text.setFill(Color.web(CustomColor.WHITE.getColorHex()));

		popup.getContentVBox().getChildren().add(text);

		popup.open();

		Settings.getInstance().setLastAnnouncement(StartupData.getInstance().announcement, true);
	}

	public static void showPatchNotes() {
		if (Settings.getInstance().hasCheckedUpdate()) {
			if (Settings.getInstance().getLastAnnouncement().equals(StartupData.getInstance().announcement) || StartupData.getInstance().announcement.isEmpty())
				return;
			displayServerAnnouncement();
			return;
		}
		Settings.getInstance().setHasCheckedUpdate(true, true);

		PopupBuilder popup = new PopupBuilder();
		popup.setScrollPaneEnabled(true, 500, 0, 500, 500);
		popup.setHeaderText("WHAT'S NEW");
		popup.setConfirmButtonText("Close");
		popup.hideCancelButton();
		popup.getContentVBox().setSpacing(10);

		popup.onConfirm(event -> {
			popup.close();
		});

		Text update_text = new Text(UpdateManager.getDate());
		update_text.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 14));
		update_text.setFill(Color.web(CustomColor.GRAY.getColorHex()));

		int spacing = 5;
		PopupVBox version_vbox = new PopupVBox(spacing), coming_soon_vbox = new PopupVBox(spacing), bug_vbox = new PopupVBox(spacing), new_feature_vbox = new PopupVBox(spacing), improvementVBox = new PopupVBox(spacing);

		Iterator<Entry<String, UpdateCategory>> iterator = UpdateManager.updates.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<String, UpdateCategory> update = iterator.next();

			String description = update.getKey();
			UpdateCategory category = update.getValue();

			if (category == UpdateCategory.VERSION)
				makeItem(version_vbox, description);
			else if (category == UpdateCategory.NEW_FEATURE)
				makeItem(new_feature_vbox, description);
			else if (category == UpdateCategory.IMPROVEMENT)
				makeItem(improvementVBox, description);
			else if (category == UpdateCategory.BUG_FIX)
				makeItem(bug_vbox, description);
			else if (category == UpdateCategory.COMING_SOON)
				makeItem(coming_soon_vbox, description);
		}

		if (!version_vbox.getChildren().isEmpty()) {
			addCategory(popup.getContentVBox(), UpdateCategory.VERSION);
			popup.getContentVBox().getChildren().addAll(version_vbox);
		}

		if (!new_feature_vbox.getChildren().isEmpty()) {
			addCategory(popup.getContentVBox(), UpdateCategory.NEW_FEATURE);
			popup.getContentVBox().getChildren().addAll(new_feature_vbox);
		}
		if (!improvementVBox.getChildren().isEmpty()) {
			addCategory(popup.getContentVBox(), UpdateCategory.IMPROVEMENT);
			popup.getContentVBox().getChildren().addAll(improvementVBox);
		}

		if (!bug_vbox.getChildren().isEmpty()) {
			addCategory(popup.getContentVBox(), UpdateCategory.BUG_FIX);
			popup.getContentVBox().getChildren().addAll(bug_vbox);
		}

		if (!coming_soon_vbox.getChildren().isEmpty()) {
			addCategory(popup.getContentVBox(), UpdateCategory.COMING_SOON);
			popup.getContentVBox().getChildren().addAll(coming_soon_vbox);
		}

		TextLink previous_update_log_text = new TextLink("View previous update logs");
		previous_update_log_text.setURLOnClick("https://rhythm.cc/patch-notes");
		popup.getContentVBox().getChildren().add(previous_update_log_text);

		popup.open();
		popup.getWrappedVBox().getChildren().add(1, update_text);
	}

	private static void makeItem(PopupVBox vbox, String description_text) {
		PopupVBox item_vbox = new PopupVBox();
		Text description = new Text(description_text);

		description.setFont(Font.font(FontType.DEFAULT.getName(), 14));
		description.setFill(Color.web(CustomColor.GRAY.getColorHex()));
		description.setWrappingWidth(390);

		item_vbox.getChildren().addAll(description);
		vbox.getChildren().add(item_vbox);
	}

	private static void addCategory(VBox vbox, UpdateCategory category) {
		Text category_text = new Text(category.getName());

		category_text.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));
		category_text.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		vbox.getChildren().add(category_text);
	}

	// Called from featured_playlist_page class
	public static void submitFeaturedAlbum() {
		PopupBuilder popup = new PopupBuilder();
		popup.getContentVBox().setSpacing(15);
		popup.setHeaderText("Submit Featured Album");
		popup.setWrapperSize(400, 0, 400, 500);
		popup.setConfirmButtonText("Submit Album");

		TextField textField = new TextField();
		textField.setStyle("-fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-background-radius: 5px; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
		textField.setPromptText("Enter a YouTube album/playlist url from a popular artist");
		textField.setMinWidth(350);
		textField.setMaxWidth(350);

		popup.onConfirm(event -> {
			if (textField.getText().isEmpty() || !textField.getText().contains("playlist")) {
				Notification.getInstance().createNotification("Featured Album", "Please enter a valid YouTube playlist URL.", AlertType.ERROR);
				return;
			}
			SubmitAFeaturedPlaylist.send(textField.getText());
			popup.close();
		});

		popup.getContentVBox().getChildren().addAll(textField);
		popup.open();
	}

	// Called from LinkTwitter class
	public static void twitterAuthenticationConfirmationCode() {

		PopupBuilder popup = new PopupBuilder();
		popup.getContentVBox().setSpacing(25);
		popup.setHeaderText("Enter Twitter Auth Code");
		popup.setConfirmButtonText("Save");

		TextField codeTextField = new TextField();
		codeTextField.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
		codeTextField.setPromptText("Enter generated Twitter authorization code");
		codeTextField.setMinSize(400, 35);
		codeTextField.setMaxWidth(400);

		codeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (codeTextField.getText().length() > 10 || !ValidateManager.isValidCode(newValue))
				codeTextField.setText(oldValue.toUpperCase());
			else
				codeTextField.setText(newValue.toUpperCase());
		});

		popup.onConfirm(event -> {
			if (codeTextField.getText().isEmpty()) {
				Notification.getInstance().createNotification("Twitter", "Please enter your authentication code.", AlertType.ERROR);
				return;
			}
			LinkSocialNetworkingAccount.send(codeTextField.getText(), "TWITTER");
			popup.close();
		});
		popup.onCancel(event -> {
			popup.close();
		});

		popup.getContentVBox().getChildren().addAll(codeTextField);
		popup.open();
	}

	public static void displayCancelPremiumSubscription(CompleteEvent completeEvent) {
		PopupBuilder popup = new PopupBuilder();
		popup.getContentVBox().setSpacing(10);
		popup.setHeaderText("Are you sure you want to cancel your premium subscription?");

		popup.onCancel(event -> {
			popup.close();
		});
		popup.onConfirm(event -> {
			CancelSubscription.send();
			completeEvent.onSuccess();
			popup.close();
		});

		popup.open();
	}

	public static void changePassword() {

		PopupBuilder popup = new PopupBuilder();
		popup.getContentVBox().setSpacing(15);
		popup.setHeaderText("Change Password");
		popup.setConfirmButtonText("Update Password");

		PasswordField currentPasswordTextField = new PasswordField(), newPasswordTextField = new PasswordField();
		currentPasswordTextField.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
		currentPasswordTextField.setPromptText("Current password");
		currentPasswordTextField.setMinSize(400, 35);
		currentPasswordTextField.setMaxWidth(400);

		newPasswordTextField.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
		newPasswordTextField.setPromptText("New password");
		newPasswordTextField.setMinSize(400, 35);
		newPasswordTextField.setMaxWidth(400);

		popup.onConfirm(event -> {
			if (currentPasswordTextField.getText().isEmpty() || currentPasswordTextField.getText().isBlank()) {
				Notification.getInstance().createNotification("Password", "Please enter your current password", AlertType.ERROR);
				return;
			}
			if (newPasswordTextField.getText().isEmpty() || newPasswordTextField.getText().isBlank()) {
				Notification.getInstance().createNotification("Password", "Please enter a new password", AlertType.ERROR);
				return;
			}
			if (newPasswordTextField.getText().length() < 8) {
				Notification.getInstance().createNotification("Password", "Your password must be longer than 7 characters.", AlertType.ERROR);
				return;
			}
			UpdatePassword.send(currentPasswordTextField.getText(), newPasswordTextField.getText(), new CompleteEvent() {

				@Override
				public void onSuccess() {
					popup.close();
					Notification.getInstance().createNotification("Password", "Your password has been successfully updated", AlertType.SUCCESS);
				}

				@Override
				public void onFail(String error) {
					if (error.equals("INCORRECT_PASSWORD")) {
						Notification.getInstance().createNotification("Password", "The current password you entered is incorrect. Please try again.", AlertType.SUCCESS);
					}
				}
			});
		});
		popup.onCancel(event -> {
			popup.close();
		});

		popup.getContentVBox().getChildren().addAll(currentPasswordTextField, newPasswordTextField);
		popup.open();
	}

	// Change song order in playlist.
	public static void changeSongOrder(Playlist playlist, CompleteEvent completeEvent) {
		PopupBuilder popup = new PopupBuilder();
		popup.getContentVBox().setSpacing(10);
		popup.setWrapperMinimumSize(450, 150);
		popup.setHeaderText("Change Song Order");

		// Center Popup items
		popup.getHeaderText().setTextAlignment(TextAlignment.CENTER);
		popup.getContentVBox().setAlignment(Pos.CENTER);
		popup.getButtonHBox().setAlignment(Pos.CENTER);

		StringProperty selectedOrderType = new SimpleStringProperty();
		List<Button> clickedButton = new ArrayList<>();

		for (PlaylistSongOrderType orderType : PlaylistSongOrderType.values()) {
			Button button = new Button(orderType.getName()) {
				public void requestFocus() {

				}
			};
			button.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-background-radius: 10px;");
			button.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
			button.setMinSize(300, 35);
			button.setAlignment(Pos.CENTER);
			button.setCursor(Cursor.HAND);
			button.setFont(Font.font(FontType.DEFAULT.getName(), 16));

			if (PlaylistManager.getInstance().getPlaylistOrder(playlist.getURL()) == orderType) {
				button.setTextFill(Color.web(CustomColor.RHYTHM.getColorHex()));
				clickedButton.add(button);
			}

			button.setOnMouseEntered(event -> {
				if (!clickedButton.isEmpty() && clickedButton.get(0) == button)
					return;
				button.setTextFill(Color.web(CustomColor.RHYTHM.getColorHex()));
			});
			button.setOnMouseExited(event -> {
				if (!clickedButton.isEmpty() && clickedButton.get(0) == button)
					return;
				button.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
			});
			button.setOnMouseClicked(event -> {
				if (!clickedButton.isEmpty())
					clickedButton.get(0).setTextFill(Color.web(CustomColor.WHITE.getColorHex()));

				selectedOrderType.set(orderType.name());
				clickedButton.clear();
				clickedButton.add(button);
				button.setTextFill(Color.web(CustomColor.RHYTHM.getColorHex()));
			});
			popup.getContentVBox().getChildren().add(button);
		}
		popup.onConfirm(event -> {
			clickedButton.clear();
			if (selectedOrderType.get() != null && !selectedOrderType.get().isEmpty()) {
				PlaylistSongOrderType orderType = PlaylistSongOrderType.getOrderByName(selectedOrderType.get());
				if (orderType != null) {
					PlaylistManager.getInstance().addPlaylistToOrderMap(playlist.getURL(), orderType);
					completeEvent.onSuccess();
				}
			}
			popup.close();
		});
		popup.open();
	}

	public static void changePlaylistOrder(CompleteEvent completeEvent, boolean isCreated) {
		PopupBuilder popup = new PopupBuilder();
		popup.getContentVBox().setSpacing(10);
		popup.setWrapperMinimumSize(450, 150);
		popup.setHeaderText("Change Created Playlists Order");
		if (!isCreated)
			popup.setHeaderText("Change Followed Playlists Order");

		// Center Popup items
		popup.getHeaderText().setTextAlignment(TextAlignment.CENTER);
		popup.getContentVBox().setAlignment(Pos.CENTER);
		popup.getButtonHBox().setAlignment(Pos.CENTER);

		StringProperty selectedOrderType = new SimpleStringProperty();
		List<Button> clickedButton = new ArrayList<>();

		for (PlaylistOrderType orderType : PlaylistOrderType.values()) {
			Button button = new Button(orderType.getName()) {
				public void requestFocus() {

				}
			};
			button.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-background-radius: 10px;");
			button.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
			button.setMinSize(300, 35);
			button.setAlignment(Pos.CENTER);
			button.setCursor(Cursor.HAND);
			button.setFont(Font.font(FontType.DEFAULT.getName(), 16));

			if ((isCreated && Settings.getInstance().getCreatedPlaylistOrder() == orderType) || (!isCreated && Settings.getInstance().getFollowedPlaylistOrder() == orderType)) {
				button.setTextFill(Color.web(CustomColor.RHYTHM.getColorHex()));
				clickedButton.add(button);
			}

			button.setOnMouseEntered(event -> {
				if (!clickedButton.isEmpty() && clickedButton.get(0) == button)
					return;
				button.setTextFill(Color.web(CustomColor.RHYTHM.getColorHex()));
			});
			button.setOnMouseExited(event -> {
				if (!clickedButton.isEmpty() && clickedButton.get(0) == button)
					return;
				button.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
			});
			button.setOnMouseClicked(event -> {
				if (!clickedButton.isEmpty())
					clickedButton.get(0).setTextFill(Color.web(CustomColor.WHITE.getColorHex()));

				selectedOrderType.set(orderType.name());
				clickedButton.clear();
				clickedButton.add(button);
				button.setTextFill(Color.web(CustomColor.RHYTHM.getColorHex()));
			});
			popup.getContentVBox().getChildren().add(button);
		}
		popup.onConfirm(event -> {
			clickedButton.clear();
			if (selectedOrderType.get() != null && !selectedOrderType.get().isEmpty()) {
				PlaylistOrderType orderType = PlaylistOrderType.getOrderByName(selectedOrderType.get());
				if (orderType != null) {
					if (isCreated)
						Settings.getInstance().setCreatedPlaylistOrder(orderType, true);
					else
						Settings.getInstance().setFollowedPlaylistOrder(orderType, true);
					completeEvent.onSuccess();
				}
			}
			popup.close();
		});
		popup.open();
	}

	// Display Full Sized Image
	public static void openLargeImage(String imageURL) {
		if (imageURL.isEmpty()) {
			return;
		}
		PopupBuilder popup = new PopupBuilder();
		popup.getContentVBox().setSpacing(10);
		popup.hideCancelButton();
		popup.setConfirmButtonText("Close");

		// Center Popup items
		popup.getHeaderText().setTextAlignment(TextAlignment.CENTER);
		popup.getContentVBox().setAlignment(Pos.CENTER);
		popup.getButtonHBox().setAlignment(Pos.CENTER);

		ImageView imageView = new ImageView();

		ImageManager.getImage(imageView, imageURL);

		imageView.fitHeightProperty().bind(GUIManager.getInstance().contentManager.contentPane.heightProperty().subtract(50));
		imageView.fitWidthProperty().bind(GUIManager.getInstance().getPane().widthProperty().subtract(200));
		imageView.setPreserveRatio(true);

		popup.onConfirm(event -> {
			popup.close();
		});

		popup.getContentVBox().getChildren().add(imageView);
		popup.open();
	}
}