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

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.beatplaylist.Main;
import com.beatplaylist.Options;
import com.beatplaylist.chromium.BrowserManager;
import com.beatplaylist.chromium.EngineBrowser;
import com.beatplaylist.chromium.Inspector;
import com.beatplaylist.chromium.YouTube;
import com.beatplaylist.enums.FontType;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.module.layout.sidebar.TabType;
import com.beatplaylist.gui.utilities.FXUtilities;
import com.beatplaylist.gui.utilities.playlist.PlaylistLoader;
import com.beatplaylist.gui.utilities.playlist.SongOrder;
import com.beatplaylist.gui.utilities.playlist.SongTitle;
import com.beatplaylist.settings.Settings;
import com.beatplaylist.utilities.CustomColor;
import com.beatplaylist.utilities.CustomToolTip;
import com.beatplaylist.utilities.animation.BounceOutLeft;
import com.beatplaylist.utilities.events.CompleteEvent;
import com.beatplaylist.utilities.image.ImageBuilder;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;
import com.beatplaylist.utilities.playlist.LyricManager;
import com.beatplaylist.utilities.playlist.Playlist;
import com.beatplaylist.utilities.playlist.RoleType;
import com.beatplaylist.utilities.playlist.Song;
import com.beatplaylist.utilities.popup.Popup;
import com.teamdev.jxbrowser.cookie.CookieStore;
import com.teamdev.jxbrowser.js.JsObject;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class now_playing_page {

	public ScrollPane lyricScrollPane;
	public VBox containerVBox, lyricContainerVBox;
	public HBox containerHBox, videoTitleHBox, videoFooterHBox, youtubeSignInHBox, geniusHeaderHBox;
	public Button addToPlaylistButton, addCustomGeniusLyricsButton, signInToYouTubeButton, openMixButton;
	public ImageView artistImageView, extraInformationImageView;
	public ImageBuilder geniusLinkImage;

	// Video Title
	public Text videoTitleText, geniusHeaderText, geniusLyricText;

	public now_playing_page() {
		this.containerVBox = new VBox(20);
		this.lyricContainerVBox = new VBox();
		this.containerHBox = new HBox(10);
		this.videoTitleHBox = new HBox(10);
		this.videoFooterHBox = new HBox(10);
		this.youtubeSignInHBox = new HBox(5);
		this.geniusHeaderHBox = new HBox(10);

		this.addToPlaylistButton = new Button("Add to Playlist") {
			public void requestFocus() {

			}
		};
		this.addCustomGeniusLyricsButton = new Button("Add Custom Lyrics") {
			public void requestFocus() {

			}
		};
		this.signInToYouTubeButton = new Button("Sign in to YouTube") {
			public void requestFocus() {

			}
		};
		this.openMixButton = new Button("Open Mix");
		this.extraInformationImageView = new ImageView(new Image(Main.class.getResource("/resources/icons/v2/extra_information_icon.png").toExternalForm()));

		this.lyricScrollPane = new ScrollPane() {
			public void requestFocus() {

			}
		};

		this.geniusHeaderText = new Text("BEHIND THE LYRICS");
		this.videoTitleText = new Text();
		this.geniusLyricText = new Text();

		this.artistImageView = new ImageView();
		this.geniusLinkImage = new ImageBuilder(new Image(Main.class.getResource("/resources/icons/v2/open_genius_link.png").toExternalForm()), new Image(Main.class.getResource("/resources/icons/v2/open_genius_link_hover.png").toExternalForm()));

		CustomToolTip tooltip = new CustomToolTip("Sign-in to your YouTube account to access age restricted music.");
		tooltip.setStyle("-fx-font-size: 12;");
		CustomToolTip.install(this.extraInformationImageView, tooltip);

		configure();
		listen();
	}

	private void configure() {
		BrowserManager.getInstance().getCurrentBrowser().web_view.minWidthProperty().bind(GUIManager.getInstance().contentManager.contentPane.widthProperty().divide(1.8));
		BrowserManager.getInstance().getCurrentBrowser().web_view.maxWidthProperty().bind(GUIManager.getInstance().contentManager.contentPane.widthProperty().divide(1.8));
		BrowserManager.getInstance().getCurrentBrowser().web_view.minHeightProperty().bind(GUIManager.getInstance().contentManager.contentPane.heightProperty().divide(1.7));
		BrowserManager.getInstance().getCurrentBrowser().web_view.maxHeightProperty().bind(GUIManager.getInstance().contentManager.contentPane.heightProperty().divide(1.7));

		GUIManager.getInstance().contentManager.contentPane.getChildren().clear();

		this.containerHBox.minHeightProperty().bind(GUIManager.getInstance().contentManager.contentPane.heightProperty().subtract(25));
		this.containerHBox.maxHeightProperty().bind(GUIManager.getInstance().contentManager.contentPane.heightProperty().subtract(25));

		FXUtilities.setNodePadding(this.containerVBox, GUIManager.getInstance().padding);

		this.videoTitleHBox.setAlignment(Pos.CENTER_LEFT);
		this.youtubeSignInHBox.setAlignment(Pos.CENTER_LEFT);
		this.geniusHeaderHBox.setAlignment(Pos.CENTER_LEFT);

		this.videoTitleText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 24));
		this.videoTitleText.setFill(Color.web(Settings.getInstance().getDefaultColor()));
		this.videoTitleText.textProperty().bind(GUIManager.getInstance().audioBar.audioBar.fullCurrentTrack.textProperty());

		this.artistImageView.setCursor(Cursor.HAND);

		this.lyricScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		this.lyricScrollPane.setStyle("-fx-background-color: transparent;");
		this.lyricScrollPane.setContent(this.geniusLyricText);
		this.lyricScrollPane.minHeightProperty().bind(this.containerHBox.heightProperty().subtract(50));
		this.lyricScrollPane.maxHeightProperty().bind(this.containerHBox.heightProperty().subtract(50));
		this.lyricScrollPane.setMaxWidth(380);
		this.lyricScrollPane.setMinWidth(380);

		this.geniusLyricText.setFill(Color.web(CustomColor.GRAY.getColorHex()));
		this.geniusLyricText.setFont(Font.font(FontType.VERDANA.getName(), 14));
		this.geniusLyricText.setWrappingWidth(330);
		this.geniusLyricText.textProperty().bind(LyricManager.getInstance().currentStoredLyrics);

		this.geniusHeaderText.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		this.geniusHeaderText.setFont(Font.font(FontType.VERDANA.getName(), FontWeight.BOLD, 16));

		this.addToPlaylistButton.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-background-radius: 20px; -fx-border-radius: 20px; -fx-text-fill: " + CustomColor.POPUP_SUB_HEADER.getColorHex() + ";");
		this.addToPlaylistButton.setMinSize(150, 35);
		this.addToPlaylistButton.setGraphic(new ImageView(new Image(Main.class.getResource("/resources/icons/audioBar/add_song_to_playlist_small_icon.png").toExternalForm(), 15, 15, false, false)));
		this.addToPlaylistButton.setCursor(Cursor.HAND);
		this.addToPlaylistButton.setFont(Font.font(FontType.VERDANA.getName(), FontWeight.BOLD, 12));

		this.addCustomGeniusLyricsButton.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-background-radius: 20px; -fx-border-radius: 20px; -fx-text-fill: " + CustomColor.POPUP_SUB_HEADER.getColorHex() + ";");
		this.addCustomGeniusLyricsButton.setMinSize(150, 35);
		this.addCustomGeniusLyricsButton.setCursor(Cursor.HAND);
		this.addCustomGeniusLyricsButton.setFont(Font.font(FontType.VERDANA.getName(), FontWeight.BOLD, 12));

		this.signInToYouTubeButton.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-background-radius: 20px; -fx-border-radius: 20px; -fx-text-fill: " + CustomColor.POPUP_SUB_HEADER.getColorHex() + ";");
		this.signInToYouTubeButton.setMinSize(150, 35);
		this.signInToYouTubeButton.setCursor(Cursor.HAND);
		this.signInToYouTubeButton.setFont(Font.font(FontType.VERDANA.getName(), FontWeight.BOLD, 12));

		this.openMixButton.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-background-radius: 20px; -fx-border-radius: 20px; -fx-text-fill: " + CustomColor.POPUP_SUB_HEADER.getColorHex() + ";");
		this.openMixButton.setMinSize(150, 35);
		this.openMixButton.setCursor(Cursor.HAND);
		this.openMixButton.setFont(Font.font(FontType.VERDANA.getName(), FontWeight.BOLD, 12));

		this.extraInformationImageView.setCursor(Cursor.HAND);

		this.videoTitleHBox.getChildren().addAll(this.artistImageView, this.videoTitleText);
		this.youtubeSignInHBox.getChildren().addAll(this.signInToYouTubeButton, this.extraInformationImageView);

		if (GUIManager.getInstance().videoBrowser.getWebEngine().url().contains("&list"))
			this.videoFooterHBox.getChildren().addAll(this.addToPlaylistButton, this.addCustomGeniusLyricsButton, this.openMixButton, this.youtubeSignInHBox);
		else
			this.videoFooterHBox.getChildren().addAll(this.addToPlaylistButton, this.addCustomGeniusLyricsButton, this.youtubeSignInHBox);

		this.geniusHeaderHBox.getChildren().addAll(this.geniusHeaderText, this.geniusLinkImage.getHBox());
		this.lyricContainerVBox.getChildren().addAll(this.geniusHeaderHBox, this.lyricScrollPane);
		this.containerVBox.getChildren().addAll(this.videoTitleHBox, BrowserManager.getInstance().getCurrentBrowser().web_view, this.videoFooterHBox);
		this.containerHBox.getChildren().addAll(this.containerVBox, this.lyricContainerVBox);

		GUIManager.getInstance().contentManager.contentPane.getChildren().add(this.containerHBox);

		FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), this.containerHBox);
		fadeTransition.setFromValue(0.0);
		fadeTransition.setToValue(1.0);
		fadeTransition.play();

		if (GUIManager.getInstance().backgroundPane.getChildren().contains(GUIManager.getInstance().audioBar.nowPlayingButton)) {
			// FadeTransition fadeOutTransition = new FadeTransition(Duration.millis(300), GUIManager.getInstance().audioBar.nowPlayingButton);
			// fadeOutTransition.setFromValue(1.0);
			// fadeOutTransition.setToValue(0.0);
			// fadeOutTransition.play();
			// fadeOutTransition.setOnFinished(event -> {
			// GUIManager.getInstance().backgroundPane.getChildren().remove(GUIManager.getInstance().audioBar.nowPlayingButton);
			// });

			BounceOutLeft transition = new BounceOutLeft(GUIManager.getInstance().audioBar.nowPlayingButton);
			transition.setOnFinished(event -> {
				if (GUIManager.getInstance().currentTab.tab == TabType.NOW_PLAYING)
					GUIManager.getInstance().backgroundPane.getChildren().remove(GUIManager.getInstance().audioBar.nowPlayingButton);
				transition.resetNode();
			});
			transition.play();
		}

		// Get Genius Lyrics
		String title = (String) YouTube.getTitle();
		LyricManager.getInstance().getLyrics("https://genius.com/" + SongTitle.getReplacedTitle(title.replace(" - YouTube", ""), true) + "-lyrics");

		if (GUIManager.getInstance().audioBar.audioListener.currentArtistProfileImageView == null) {
			GUIManager.getInstance().audioBar.audioListener.currentArtistProfileImageView = new ImageView();
		}
		Rectangle clip = new Rectangle(60, 60);
		clip.setArcWidth(Math.min(60, 60));
		clip.setArcHeight(Math.min(60, 60));
		this.artistImageView.setClip(clip);
		this.artistImageView.imageProperty().bind(GUIManager.getInstance().audioBar.audioListener.currentArtistProfileImageView.imageProperty());

		detectIfUserHasSignedIn();

		if (Options.test_mode) {
			Button button = new Button("Inspect") {
				public void requestFocus() {

				}
			};
			button.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-background-radius: 20px; -fx-border-radius: 20px; -fx-text-fill: " + CustomColor.POPUP_SUB_HEADER.getColorHex() + ";");
			button.setMinSize(150, 35);
			button.setCursor(Cursor.HAND);
			button.setFont(Font.font(FontType.VERDANA.getName(), FontWeight.BOLD, 12));
			button.setOnMouseEntered(event -> {
				button.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-background-radius: 20px; -fx-border-radius: 20px; -fx-text-fill: " + CustomColor.RHYTHM.getColorHex() + ";");
			});
			button.setOnMouseExited(event -> {
				button.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-background-radius: 20px; -fx-border-radius: 20px; -fx-text-fill: " + CustomColor.POPUP_SUB_HEADER.getColorHex() + ";");
			});
			button.setOnMouseClicked(event -> {
				// GUIManager.getInstance().videoBrowser.web_engine.loadURL("https://www.youtube.com/");
				if (Inspector.getInstance().stage == null)
					Inspector.getInstance().inspectInitialize();
				Inspector.getInstance().inspect(true);
			});

			// this.videoFooterHBox.getChildren().add(button);
		}
	}

	// document.getElementById("avatar").getElementsByTagName('img').img.src;

	private void listen() {
		this.openMixButton.setOnMouseEntered(event -> {
			this.openMixButton.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-background-radius: 20px; -fx-border-radius: 20px; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + ";");
		});
		this.openMixButton.setOnMouseExited(event -> {
			this.openMixButton.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-background-radius: 20px; -fx-border-radius: 20px; -fx-text-fill: " + CustomColor.POPUP_SUB_HEADER.getColorHex() + ";");
		});
		this.openMixButton.setOnAction(event -> {
			if (!GUIManager.getInstance().videoBrowser.getWebEngine().url().contains("&list")) {
				Notification.getInstance().createNotification("Mix", "You are not playing a mix.", AlertType.ERROR);
				return;
			}
			Playlist playlist = new Playlist();
			playlist.setName("My Mix");
			playlist.setDescription("Imported Mix from YouTube - This is an Alpha feature and will be improved over time.");
			playlist.setURL("my-mix");
			playlist.setSpecialPlaylist(true);
			playlist.setRole(RoleType.EDIT);
			playlist.setFollowing(true);

			JsObject obj = GUIManager.getInstance().videoBrowser.getWebEngine().mainFrame().get().executeJavaScript("getMixLinks();");

			String jsonLong = GUIManager.getInstance().videoBrowser.getWebEngine().mainFrame().get().json().stringify(obj).toString();

			JSONParser parser = new JSONParser();
			try {
				JSONArray array = (JSONArray) parser.parse(jsonLong);

				if (array.size() <= 0) {
					Notification.getInstance().createNotification("Mix", "You are not playing a mix.", AlertType.ERROR);
					return;
				}

				Iterator<String> iterator = array.iterator();

				while (iterator.hasNext()) {
					JSONObject songObj = (JSONObject) parser.parse(iterator.next());

					String title = (String) songObj.get("title");
					String url = (String) songObj.get("url");

					title = title.replace("NOW PLAYING", "").replace("▶", "").replaceAll("\\d", "").replaceAll("\n", " ").replace(":", "");

					Song song = new Song();
					song.setFullSongTitle(title);
					song.setURL(url);
					song.setDateAdded(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));

					System.out.println(title);

					playlist.addSong(song, false);
				}
				GUIManager.getInstance().sideBar.sideBarTab.loadMix(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.OPEN_PLAYLIST_VIEW), playlist);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			// List<String> urls = new ArrayList<>(Arrays.asList(GUIManager.getInstance().videoBrowser.getWebEngine().mainFrame().get().json().stringify(obj).split(",")));
			//
			// for (String url : urls) {
			// System.out.println(url);
			// System.out.println("NEW LINE");
			// }
		});
		this.artistImageView.setOnMouseClicked(event -> {
			SongOrder.loadCurrentPlayingArtistPage();
		});
		this.addToPlaylistButton.setOnMouseEntered(event -> {
			this.addToPlaylistButton.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-background-radius: 20px; -fx-border-radius: 20px; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + ";");
			this.addToPlaylistButton.setGraphic(new ImageView(new Image(Main.class.getResource("/resources/icons/audioBar/add_song_to_playlist_hover_small_icon.png").toExternalForm(), 15, 15, false, false)));
		});
		this.addToPlaylistButton.setOnMouseExited(event -> {
			this.addToPlaylistButton.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-background-radius: 20px; -fx-border-radius: 20px; -fx-text-fill: " + CustomColor.POPUP_SUB_HEADER.getColorHex() + ";");
			this.addToPlaylistButton.setGraphic(new ImageView(new Image(Main.class.getResource("/resources/icons/audioBar/add_song_to_playlist_small_icon.png").toExternalForm(), 15, 15, false, false)));
		});
		this.addToPlaylistButton.setOnMouseClicked(event -> {
			String currentSongTitle = YouTube.getTitle().replace("- YouTube", "").trim();
			JSONWrapper song_data = new JSONWrapper(SongTitle.getSongInformationFromTitle(currentSongTitle));

			if (!song_data.getJSONString("artist").isEmpty() && !song_data.getJSONString("artist").equals("null")) {
				Popup.addSongToPlaylist(currentSongTitle, BrowserManager.getInstance().getVideoURL());
			} else {
				String title = GUIManager.getInstance().audioBar.audioBar.currentTrackArtistNameText.getText() + " - " + GUIManager.getInstance().audioBar.audioBar.currentTrackText.getText();
				Popup.addSongToPlaylist(title, BrowserManager.getInstance().getVideoURL());
			}
		});
		this.addCustomGeniusLyricsButton.setOnMouseEntered(event -> {
			this.addCustomGeniusLyricsButton.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-background-radius: 20px; -fx-border-radius: 20px; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + ";");
		});
		this.addCustomGeniusLyricsButton.setOnMouseExited(event -> {
			this.addCustomGeniusLyricsButton.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-background-radius: 20px; -fx-border-radius: 20px; -fx-text-fill: " + CustomColor.POPUP_SUB_HEADER.getColorHex() + ";");
		});
		this.addCustomGeniusLyricsButton.setOnMouseClicked(event -> {
			Popup.addCustomGeniusLyricsToSong(new CompleteEvent() {

				@Override
				public void onSuccess() {
					LyricManager.getInstance().getLyrics(BrowserManager.getInstance().getVideoURL());
				}

				@Override
				public void onFail(String error) {
				}
			});
		});
		this.signInToYouTubeButton.setOnMouseEntered(event -> {
			this.signInToYouTubeButton.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-background-radius: 20px; -fx-border-radius: 20px; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + ";");
		});
		this.signInToYouTubeButton.setOnMouseExited(event -> {
			this.signInToYouTubeButton.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-background-radius: 20px; -fx-border-radius: 20px; -fx-text-fill: " + CustomColor.POPUP_SUB_HEADER.getColorHex() + ";");
		});
		this.signInToYouTubeButton.setOnMouseClicked(event -> {
			if (this.signInToYouTubeButton.getText().equals("Sign out of YouTube account")) {
				// Clear Video Browser Cookies

				CookieStore videoCookieStorage = EngineBrowser.getInstance().engine.profiles().defaultProfile().cookieStore();

				videoCookieStorage.deleteAll();
				videoCookieStorage.persist();

				this.signInToYouTubeButton.setText("Sign in to YouTube");
			} else {
				GUIManager.getInstance().searchBrowser.getWebEngine().navigation().loadUrl("https://beatplaylist.com/api/v1/google/connect");
				GUIManager.getInstance().sideBar.sideBarTab.changeTab(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.BROWSE), "DISPLAY");
			}
		});
		this.geniusLinkImage.getHBox().setOnMouseClicked(event -> {
			try {
				if (this.geniusLyricText.getText().startsWith("No lyrics could be found. Click the 'Add Custom Lyrics' button to add your own lyrics."))
					return;
				Desktop.getDesktop().browse(new URI(LyricManager.getInstance().lastQueriedGeniusURL));
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		});
	}

	// Updates sign in to youtube account text to sign out if a user is already signed in.
	private void detectIfUserHasSignedIn() {
		new Thread(() -> {
			boolean isSignedIn = BrowserManager.getInstance().getCurrentBrowser().getWebEngine().mainFrame().get().executeJavaScript("yt.config_.LOGGED_IN;"); // ytcfg.data_.LOGGED_IN
			if (isSignedIn) {
				Platform.runLater(() -> {
					this.signInToYouTubeButton.setText("Sign out of YouTube account");
				});
			}
		}).start();
	}
}