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

package com.beatplaylist.utilities.playlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.beatplaylist.Main;
import com.beatplaylist.enums.FontType;
import com.beatplaylist.enums.GenreType;
import com.beatplaylist.enums.PaddingSide;
import com.beatplaylist.gui.utilities.FXUtilities;
import com.beatplaylist.gui.utilities.playlist.SongView;
import com.beatplaylist.settings.Settings;
import com.beatplaylist.settings.SocialType;
import com.beatplaylist.utilities.CustomColor;
import com.beatplaylist.utilities.CustomToolTip;
import com.beatplaylist.utilities.control.ControlledTextArea;
import com.beatplaylist.utilities.control.ControlledTextField;
import com.beatplaylist.utilities.control.ToggleSwitch;
import com.beatplaylist.utilities.events.CompleteEvent;
import com.beatplaylist.utilities.network.post.EditPlaylistInformation;
import com.beatplaylist.utilities.network.post.UpdatePlaylistTag;
import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;
import com.beatplaylist.utilities.popup.PopupBuilder;
import com.beatplaylist.utilities.popup.control.PopupHBox;
import com.beatplaylist.utilities.popup.control.PopupVBox;
import com.beatplaylist.utilities.user.UserManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class PlaylistManager {

	private static PlaylistManager instance = new PlaylistManager();

	public static PlaylistManager getInstance() {
		return instance;
	}

	public List<Playlist> playlists = new ArrayList<>();
	public List<Song> queue_list = new ArrayList<>();

	// Stores playlists that have custom song orders.
	public Map<String, PlaylistSongOrderType> playlist_order_map = new HashMap<>();

	// Stores the playlist currently playing music.
	public Playlist current_playlist;

	// Stores current songview playing - Could turn this into an int value in the future and just check it against the storedSongView list?
	public SongView currentPlayingSongView;

	// Stores all the song view classes displayed on a loaded playlist. This list is cleared when the playlist_view_page changePlaylist function is called.
	public List<SongView> storedSongView;

	public void addPlaylistToOrderMap(String url, PlaylistSongOrderType order) {
		this.playlist_order_map.put(url, order);
	}

	public PlaylistSongOrderType getPlaylistOrder(String url) {
		if (this.playlist_order_map.containsKey(url))
			return this.playlist_order_map.get(url);
		else
			return PlaylistSongOrderType.SONG_NAME_AZ;
	}

	public void addPlaylist(Playlist playlist) {
		for (Playlist playlists : this.getPlaylists()) {
			if (playlists.getURL().equals(playlist.getURL())) {
				return;
			}
		}
		this.playlists.add(playlist);
	}

	public void removePlaylist(Playlist playlist) {
		this.playlists.remove(playlist);
		playlist = null;
	}

	public Playlist getPlaylist(String name) {
		for (Playlist playlist : this.getPlaylists()) {
			if (playlist.getName().equals(name))
				return playlist;
		}
		return null;
	}

	public Playlist getPlaylistByURL(String url) {
		for (Playlist playlist : this.getPlaylists()) {
			if (playlist.getURL().equals(url)) {
				return playlist;
			}
		}
		return null;
	}

	public boolean songInPlaylist(Playlist playlist, Song song) {
		for (Song songs : playlist.getSongs()) {
			if (songs.getFullSongTitle().equals(song.getFullSongTitle()))
				return true;
		}
		return false;
	}

	public void printPlaylists() {
		for (Playlist playlist : getPlaylists()) {
			System.out.println(playlist.getURL() + ":" + playlist.getName());
		}
	}

	public List<Playlist> getPlaylists() {
		return this.playlists;
	}

	public void addSongToQueue(Song song) {
		this.queue_list.add(song);
	}

	public void removeSongFromQueue() {
		if (this.queue_list.size() >= 0)
			this.queue_list.remove(0);
	}

	public Song getCurrentQueueSong() {
		return this.queue_list.get(0);
	}

	public List<Song> getQueue() {
		return this.queue_list;
	}

	public void clearQueue() {
		this.queue_list.clear();
	}

	public void removeSongFromQueue(Song song) {
		this.queue_list.remove(song);
	}

	private String chosenVisibility = "";
	private Button chosenPrivacy = null;
	public boolean syncedToYouTube = false; // Called in the editPlaylist function and createPlaylist (Popup.class) function.

	public void editPlaylist(Playlist playlist, CompleteEvent event) {
		editPlaylist(playlist, null, event);
	}

	// Edit Playlist Popup
	public void editPlaylist(Playlist playlist, VBox playlistVBox, CompleteEvent completeEvent) {
		PopupBuilder popup = new PopupBuilder();
		popup.getContentVBox().setSpacing(20);
		popup.setHeaderText("Edit Playlist Details");
		popup.getHeaderText().setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.EXTRA_BOLD, 20));
		popup.setConfirmButtonText("Save");

		PopupVBox nameVBox = new PopupVBox(2), descriptionVBox = new PopupVBox(2), visibilityVBox = new PopupVBox(2);
		PopupHBox button_hbox = new PopupHBox(7), footer_hbox = new PopupHBox(10), syncHBox = new PopupHBox(10);
		Text nameText = styleText("Name"), descriptionText = styleText("Description"), visibilityText = styleText("Privacy"), syncText = styleText("Sync To YouTube?");

		ControlledTextField name_textfield = new ControlledTextField(100);
		ControlledTextArea descriptionTextArea = new ControlledTextArea(300);
		ToggleSwitch syncPlaylistToYouTubeToggle = new ToggleSwitch();
		List<String> visibilityType = Arrays.asList("PUBLIC", "LINK_ONLY", "PRIVATE");

		nameVBox.setStyle("-fx-padding: 25px 0 0 0x;");
		syncHBox.setAlignment(Pos.CENTER_LEFT);

		name_textfield.setText(playlist.getName());
		styleTextField(name_textfield);

		descriptionTextArea.getTextArea().setPromptText("Playlist Description");
		descriptionTextArea.getTextArea().setMinSize(350, 100);
		descriptionTextArea.getTextArea().setMaxSize(350, 100);
		descriptionTextArea.getTextArea().setWrapText(true);
		descriptionTextArea.getTextArea().setStyle("-fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-highlight-fill: " + CustomColor.RHYTHM.getColorHex() + "; -fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-background-radius: 5px; -fx-border-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
		if (!playlist.getDescription().equals("None")) {
			descriptionTextArea.getTextArea().setText(playlist.getDescription());
		}

		syncPlaylistToYouTubeToggle.setOpacity(0.5);
		syncPlaylistToYouTubeToggle.setEnabled(playlist.isSyncedToYouTube());
		CustomToolTip.install(syncPlaylistToYouTubeToggle, new CustomToolTip("This feature will be enabled in a future release."));
		this.syncedToYouTube = playlist.isSyncedToYouTube();

		for (String button_name : visibilityType) {
			Button button = new Button(getVisibility(button_name)) {
				public void requestFocus() {

				}
			};
			if (playlist.getVisibility().equals(button_name)) {
				button.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-cursor: hand; -fx-text-fill: " + Settings.getInstance().getDefaultColor() + ";");
				this.chosenPrivacy = button;
				this.chosenVisibility = button_name;
			} else
				button.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-cursor: hand; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + ";");
			button.setMinSize(100, 30);
			button.setFont(Font.font(FontType.VERDANA.getName(), 14));

			button.setOnMouseEntered(event -> {
				if (this.chosenPrivacy != null && button.getText().equals(this.chosenPrivacy.getText()))
					return;
				button.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-cursor: hand; -fx-text-fill: " + Settings.getInstance().getDefaultColor() + ";");
			});
			button.setOnMouseExited(event -> {
				if (this.chosenPrivacy != null && button.getText().equals(this.chosenPrivacy.getText()))
					return;
				button.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-cursor: hand; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + ";");
			});
			button.setOnAction(event -> {
				if (playlist.getVisibility().equals(button_name))
					return;
				if (this.chosenPrivacy != null)
					this.chosenPrivacy.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-cursor: hand; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + ";");
				button.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-cursor: hand; -fx-text-fill: " + Settings.getInstance().getDefaultColor() + ";");
				this.chosenVisibility = button.getText();
				this.chosenPrivacy = button;
			});
			button_hbox.getChildren().add(button);
		}

		syncPlaylistToYouTubeToggle.setOnMouseClicked(event -> {
			// if (PlaylistManager.getInstance().syncedToYouTube) {
			// PlaylistManager.getInstance().syncedToYouTube = false;
			// } else {
			// PlaylistManager.getInstance().syncedToYouTube = true;
			// }
			// syncPlaylistToYouTubeToggle.setEnabled(PlaylistManager.getInstance().syncedToYouTube);
		});
		List<String> colors = PlaylistManager.getInstance().getColors();
		String playlistColor = Settings.getInstance().getPlaylistColor(playlist.getURL());

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
		random.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 14));

		if (playlistColor == null || playlistColor != null && playlistColor.equals("RANDOM")) {
			random.setOpacity(0.8);
			chosen.add(random);
		}

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

			if (playlistColor != null && playlistColor.equals(color)) {
				button.setOpacity(0.4);
				chosen.add(button);
			}

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
		popup.onConfirm(event -> {
			if (name_textfield.getText().isEmpty()) {
				Notification.getInstance().createNotification("Playlist", "Your playlist name cannot be empty", AlertType.SUCCESS);
				return;
			}
			if (chosenColor.get().equals(playlistColor) && playlist.getName().equals(name_textfield.getText()) && playlist.getDescription().equals(descriptionTextArea.getText()) && playlist.getVisibility().equals(this.chosenVisibility) && playlist.isSyncedToYouTube() == this.syncedToYouTube) {
				Notification.getInstance().createNotification("Playlist", "You must make changes to one field, before updating your playlist", AlertType.ERROR);
				return;
			}
			// Update playlist details instantly, the update request will re-enforce the update and if any issues occur, will roll back changes.
			playlist.setName(name_textfield.getText());
			playlist.setDescription(descriptionTextArea.getText());
			playlist.setVisibility(this.chosenVisibility);
			playlist.setSyncToYouTube(this.syncedToYouTube);

			Settings.getInstance().addPlaylistColor(playlist.getURL(), chosenColor.get());

			EditPlaylistInformation.send(playlist, name_textfield.getText(), descriptionTextArea.getText(), this.chosenVisibility, this.syncedToYouTube, playlist.getURL());

			this.chosenPrivacy = null;
			this.chosenVisibility = "";

			if (playlistVBox != null) {
				String randomColor = "";
				if (Settings.getInstance().playlistHasCustomColor(playlist.getURL())) {
					String color = Settings.getInstance().getPlaylistColor(playlist.getURL());
					if (chosenColor.get().equals("RANDOM")) {
						randomColor = getRandomColor();
					} else {
						randomColor = color;
					}
					System.out.println(randomColor);

					playlistVBox.setStyle("-fx-padding: 10px; -fx-background-color: linear-gradient(to bottom, " + randomColor + "); -fx-cursor: hand; -fx-background-radius: 15px;");
				}
			}

			completeEvent.onSuccess();
			popup.close();
		});
		popup.onCancel(event -> {
			this.chosenPrivacy = null;
			this.chosenVisibility = "";
			popup.close();
		});

		nameVBox.getChildren().addAll(nameText, name_textfield);
		descriptionVBox.getChildren().addAll(descriptionText, descriptionTextArea);
		visibilityVBox.getChildren().addAll(visibilityText, button_hbox);
		syncHBox.getChildren().addAll(syncText, syncPlaylistToYouTubeToggle);

		footer_hbox.setPadding(new Insets(footer_hbox.getPadding().getTop() + 30, footer_hbox.getPadding().getLeft(), footer_hbox.getPadding().getBottom(), footer_hbox.getPadding().getRight()));

		if (UserManager.getInstance().user.getAccount(SocialType.YOUTUBE) == null)
			popup.getContentVBox().getChildren().addAll(nameVBox, descriptionVBox, visibilityVBox, scrollpane, footer_hbox);
		else
			popup.getContentVBox().getChildren().addAll(nameVBox, descriptionVBox, visibilityVBox, syncHBox, scrollpane, footer_hbox);

		popup.open();

	}

	public void changePlaylistGenres(Playlist playlist) {
		PopupBuilder popup = new PopupBuilder();
		popup.getContentVBox().setSpacing(20);
		popup.setWrapperSize(600, 500, 600, 500);
		popup.setHeaderText("Select Playlist Genres");
		popup.getHeaderText().setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.EXTRA_BOLD, 20));
		popup.setConfirmButtonText("Save");

		ScrollPane scrollpane = new ScrollPane();
		scrollpane.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrollpane.setMaxWidth(550);
		FXUtilities.setNodePadding(scrollpane, 5, PaddingSide.BOTTOM, "-fx-background-color: transparent;");

		FlowPane flowPane = new FlowPane();
		flowPane.setVgap(8);
		flowPane.setHgap(4);
		flowPane.setOnScroll(event -> {
			double deltaY = event.getDeltaY() * 20; // *6 to make the scrolling a bit faster
			double width = scrollpane.getContent().getBoundsInLocal().getWidth();
			double vvalue = scrollpane.getHvalue();
			scrollpane.setVvalue(vvalue + -deltaY / width); // deltaY/width to make the scrolling equally fast regardless of the actual height of the component
		});

		flowPane.maxWidthProperty().bind(popup.getContentVBox().widthProperty());
		flowPane.minWidthProperty().bind(popup.getContentVBox().widthProperty());
		scrollpane.setContent(flowPane);

		List<String> chosen = new ArrayList<>();

		for (String genre : playlist.getGenres()) {
			chosen.add(genre);
		}

		for (GenreType genre : GenreType.values()) {
			Button button = new Button(genre.getName());
			button.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-background-radius: 10px;");
			button.setMinSize(170, 45);
			button.setTextFill(Color.WHITE);
			button.setCursor(Cursor.HAND);
			button.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));

			if (chosen.contains(genre.getName().toUpperCase())) {
				button.setOpacity(0.4);
				button.setTextFill(Color.web(CustomColor.RHYTHM.getColorHex()));
			}

			button.setOnMouseEntered(event -> {
				if (button.getOpacity() > 0.9)
					button.setTextFill(Color.web(CustomColor.RHYTHM.getColorHex()));
			});
			button.setOnMouseExited(event -> {
				if (button.getOpacity() > 0.9)
					button.setTextFill(Color.WHITE);
			});

			button.setOnMouseClicked(event -> {
				if (chosen.contains(genre.getName().toUpperCase())) {
					chosen.remove(genre.getName());
					button.setOpacity(1);
					button.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
				} else {
					chosen.add(genre.getName());
					button.setOpacity(0.4);
					button.setTextFill(Color.web(CustomColor.RHYTHM.getColorHex()));
				}
			});

			flowPane.getChildren().add(button);
		}
		popup.onConfirm(event -> {
			playlist.getGenres().clear();
			UpdatePlaylistTag.send(chosen, true, playlist.getURL(), new CompleteEvent() {

				@Override
				public void onSuccess() {
					for (String genre : chosen) {
						playlist.addTag(genre, true);
					}
				}

				@Override
				public void onFail(String error) {
				}
			});
			popup.close();
		});
		popup.onCancel(event -> {
			popup.close();
		});

		popup.getContentVBox().getChildren().addAll(scrollpane);
		popup.open();
	}

	public void updatePlaylistTags(Playlist playlist) {
		PopupBuilder popup = new PopupBuilder();
		popup.getContentVBox().setSpacing(20);
		popup.setWrapperSize(450, 0, 600, 500);
		popup.setHeaderText("Change Playlist Tags");
		popup.getHeaderText().setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.EXTRA_BOLD, 20));
		popup.setConfirmButtonText("Save");
		popup.setButtonsDisabled(true);

		List<String> chosen = new ArrayList<>();

		HBox tagHBox = new HBox(5);
		tagHBox.setAlignment(Pos.CENTER_LEFT);

		ScrollPane scrollpane = new ScrollPane();
		scrollpane.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrollpane.setMaxWidth(550);
		FXUtilities.setNodePadding(scrollpane, 5, PaddingSide.BOTTOM, "-fx-background-color: transparent;");

		ControlledTextField textField = new ControlledTextField(11);
		Button addTag = new Button("Add Tag");
		FlowPane flowPane = new FlowPane();

		textField.getTextField().setPromptText("Enter a tag");
		textField.getTextField().setMinWidth(200);
		textField.getTextField().setMaxWidth(200);
		textField.getTextField().setMinHeight(45);
		textField.getTextField().setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));
		FXUtilities.setNodePadding(textField.getTextField(), 15, PaddingSide.LEFT, "-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-text-fill: white; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");

		addTag.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + ";");
		addTag.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
		addTag.setMinHeight(45);
		addTag.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));
		addTag.setMinWidth(150);
		addTag.setCursor(Cursor.HAND);

		addTag.setOnAction(event -> {
			if (textField.getTextField().getText().length() > 11) {
				Notification.getInstance().createNotification("Playlist Tag", "Your tag cannot be longer than 11 characters.", AlertType.ERROR);
				return;
			}
			if (chosen.contains(textField.getTextField().getText().toUpperCase())) {
				Notification.getInstance().createNotification("Playlist Tag", "You've already added this tag.", AlertType.ERROR);
				return;
			}
			chosen.add(textField.getTextField().getText().toUpperCase());
			addTag(textField.getTextField().getText(), flowPane, chosen);
			textField.getTextField().setText("");
			popup.setButtonsDisabled(false);
		});
		addTag.setOnMouseEntered(event -> {
			addTag.setTextFill(Color.web(CustomColor.RHYTHM.getColorHex()));
		});
		addTag.setOnMouseExited(event -> {
			addTag.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
		});

		tagHBox.getChildren().addAll(textField, addTag);

		flowPane.setVgap(8);
		flowPane.setHgap(4);
		flowPane.setOnScroll(event -> {
			double deltaY = event.getDeltaY() * 20; // *6 to make the scrolling a bit faster
			double width = scrollpane.getContent().getBoundsInLocal().getWidth();
			double vvalue = scrollpane.getHvalue();
			scrollpane.setVvalue(vvalue + -deltaY / width); // deltaY/width to make the scrolling equally fast regardless of the actual height of the component
		});

		flowPane.maxWidthProperty().bind(popup.getContentVBox().widthProperty());
		flowPane.minWidthProperty().bind(popup.getContentVBox().widthProperty());
		scrollpane.setContent(flowPane);

		for (String tag : playlist.getTags()) {
			if (tag.isEmpty())
				continue;
			chosen.add(tag);

			addTag(tag, flowPane, chosen);
			popup.setButtonsDisabled(false);
		}
		popup.onConfirm(event -> {
			playlist.getTags().clear();
			UpdatePlaylistTag.send(chosen, false, playlist.getURL(), new CompleteEvent() {

				@Override
				public void onSuccess() {
					for (String genre : chosen) {
						playlist.addTag(genre, false);
					}
				}

				@Override
				public void onFail(String error) {
				}
			});
			popup.close();
		});
		popup.onCancel(event -> {
			popup.close();
		});

		popup.getContentVBox().getChildren().addAll(scrollpane, tagHBox);
		popup.open();
	}

	// Build suggest tag feature
	public void viewTagAndGenre(Playlist playlist) {
		PopupBuilder popup = new PopupBuilder();
		popup.getContentVBox().setSpacing(20);
		popup.setWrapperSize(700, 0, 700, 500);
		popup.setHeaderText("Selected Genres and Tags");
		popup.getHeaderText().setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.EXTRA_BOLD, 20));
		popup.setButtonsDisabled(true);

		VBox vbox = new VBox(10), genreVBox = new VBox(3), tagVBox = new VBox(3);
		Text genreText = new Text("Selected Genres"), selectedTags = new Text("Selected Tags");
		FlowPane tagFlowPane = new FlowPane(), genreFlowPane = new FlowPane();
		ScrollPane scrollpane = new ScrollPane();

		scrollpane.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrollpane.setMaxWidth(700);
		scrollpane.setContent(vbox);
		FXUtilities.setNodePadding(scrollpane, 5, PaddingSide.BOTTOM, "-fx-background-color: transparent;");

		genreText.setFill(Color.web(CustomColor.GRAY.getColorHex()));
		genreText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));

		selectedTags.setFill(Color.web(CustomColor.GRAY.getColorHex()));
		selectedTags.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));

		tagFlowPane.setVgap(8);
		tagFlowPane.setHgap(4);

		tagFlowPane.maxWidthProperty().bind(popup.getContentVBox().widthProperty());
		tagFlowPane.minWidthProperty().bind(popup.getContentVBox().widthProperty());

		genreFlowPane.setVgap(8);
		genreFlowPane.setHgap(4);

		genreFlowPane.maxWidthProperty().bind(popup.getContentVBox().widthProperty());
		genreFlowPane.minWidthProperty().bind(popup.getContentVBox().widthProperty());

		int genres = 0, tags = 0;
		for (String tag : playlist.getTags()) {
			if (tag.isEmpty())
				continue;
			tags++;
			addTag(tag, tagFlowPane);
		}
		for (String genre : playlist.getGenres()) {
			if (genre.isEmpty())
				continue;
			genres++;
			addTag(genre, genreFlowPane);
		}
		if (tags <= 0) {
			addTag("None Selected", tagFlowPane);
		}
		if (genres <= 0) {
			addTag("None Selected", genreFlowPane);
		}

		vbox.setOnScroll(event -> {
			double deltaY = event.getDeltaY() * 20; // *6 to make the scrolling a bit faster
			double width = scrollpane.getContent().getBoundsInLocal().getWidth();
			double vvalue = scrollpane.getHvalue();
			scrollpane.setVvalue(vvalue + -deltaY / width); // deltaY/width to make the scrolling equally fast regardless of the actual height of the component
		});
		popup.onConfirm(event -> {
			popup.close();
		});
		popup.onCancel(event -> {
			popup.close();
		});

		genreVBox.getChildren().addAll(genreText, genreFlowPane);
		tagVBox.getChildren().addAll(selectedTags, tagFlowPane);
		vbox.getChildren().addAll(genreVBox, tagVBox);
		popup.getContentVBox().getChildren().addAll(scrollpane);
		popup.open();
	}

	private void addTag(String tag, FlowPane flowPane, List<String> list) {
		HBox hbox = new HBox();
		Button button = new Button(tag);

		button.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-background-radius: 10px 0px 0px 10px;");
		button.setMinSize(200, 45);
		button.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
		button.setCursor(Cursor.HAND);
		button.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));

		Button remove = new Button();
		ImageView removeImage = new ImageView(new Image(Main.class.getResource("/resources/title_bar_icon/exit.png").toExternalForm())), removeHoverImage = new ImageView(new Image(Main.class.getResource("/resources/title_bar_icon/exit_hover.png").toExternalForm()));

		remove.setGraphic(removeImage);
		remove.setMinSize(75, 45);
		remove.setTextFill(Color.web(CustomColor.GRAY.getColorHex()));
		remove.setCursor(Cursor.HAND);
		remove.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));
		remove.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-background-radius: 0px 10px 10px 0px;");
		remove.setOnAction(event -> {
			list.remove(tag.toUpperCase());
			flowPane.getChildren().remove(hbox);
		});
		remove.setOnMouseEntered(event -> {
			remove.setGraphic(removeHoverImage);
		});
		remove.setOnMouseExited(event -> {
			remove.setGraphic(removeImage);
		});

		hbox.getChildren().addAll(button, remove);
		flowPane.getChildren().add(hbox);
	}

	private void addTag(String tag, FlowPane flowPane) {
		Button button = new Button(tag);

		button.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-background-radius: 10px;");
		button.setMinSize(200, 45);
		button.setTextFill(Color.web(CustomColor.RHYTHM.getColorHex()));
		button.setCursor(Cursor.HAND);
		button.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));

		flowPane.getChildren().add(button);
	}

	private String getVisibility(String value) {
		if (value.equals("PRIVATE"))
			return "Private";
		else if (value.equals("PUBLIC"))
			return "Public";
		else if (value.equals("LINK_ONLY"))
			return "Link Only";
		else
			return "Private";
	}

	private Text styleText(String value) {
		Text text = new Text(value);
		text.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));
		text.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		return text;
	}

	private void styleTextField(ControlledTextField textField) {
		textField.getTextField().setPromptText("Playlist Name");
		textField.getTextField().setMinSize(350, 25);
		textField.getTextField().setMaxWidth(350);
		textField.getTextField().setFont(Font.font(FontType.VERDANA.getName(), 14));
		textField.getTextField().setAlignment(Pos.TOP_LEFT);
		textField.getTextField().setStyle("-fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-background-radius: 5px; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
	}

	public List<String> getColors() {
		List<String> colors = new ArrayList<>();
		colors.add("#654ea3 0%, #eaafc8 100%");
		colors.add("#ED213A 0%, #93291E 100%");
		colors.add("#FDC830 0%, #F37335 100%");
		colors.add("#00B4DB 0%, #0083B0 100%");
		colors.add("#DA4453 0%, #89216B 100%");
		colors.add("#ad5389 0%, #3c1053 100%");
		colors.add("#4e54c8 0%, #8f94fb 100%");
		colors.add("#c94b4b 0%, #4b134f 100%");
		colors.add("#23074d 0%, #cc5333 100%");
		colors.add("#7F00FF 0%, #E100FF 100%");
		colors.add("#f85032 0%, #e73827 100%");
		colors.add("#76b852 0%, #8DC26F 100%");
		colors.add("#56CCF2 0%, #2F80ED 100%");
		colors.add("#2980b9 0%, #2c3e50 100%");
		colors.add("#005C97 0%, #363795 100%");
		colors.add("#304352 0%, #d7d2cc 100%");
		colors.add("#2193b0 0%, #6dd5ed 100%");
		colors.add("#2193b0 0%, #6dd5ed 100%");
		colors.add("#7474BF 0%, #348AC7 100%");
		colors.add("#000428 0%, #004e92 100%");
		colors.add("#FF416C 0%, #FF4B2B 100%");
		colors.add("#F2994A 0%, #F2C94C 100%");
		colors.add("#E44D26 0%, #F16529 100%");
		colors.add("#cb2d3e 0%, #ef473a 100%");
		return colors;
	}

	public String getRandomColor() {
		List<String> colors = getColors();
		int random = new Random().nextInt(colors.size());
		String color = colors.get(random);
		return color;
	}
}