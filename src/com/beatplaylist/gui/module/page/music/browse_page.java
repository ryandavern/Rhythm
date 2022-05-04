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

import com.beatplaylist.Main;
import com.beatplaylist.Options;
import com.beatplaylist.chromium.BrowserManager;
import com.beatplaylist.chromium.Inspector;
import com.beatplaylist.chromium.SearchBrowser;
import com.beatplaylist.enums.FontType;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.module.layout.sidebar.TabType;
import com.beatplaylist.gui.module.page.profile.ProfileLoader;
import com.beatplaylist.gui.utilities.playlist.SongTitle;
import com.beatplaylist.utilities.CustomColor;
import com.beatplaylist.utilities.CustomToolTip;
import com.beatplaylist.utilities.control.TextField;
import com.beatplaylist.utilities.data.StoredURL;
import com.beatplaylist.utilities.image.ImageBuilder;

import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class browse_page {

	public VBox containerVBox;
	public HBox textHBox, buttonHBox; // Stores searchTextTitle and extraInformation icon.
	public Text searchTextTitle;
	public ImageView extraInformationImageView;
	public Button reload;
	private ImageBuilder back_image, forward_image, reloadImage;

	public browse_page(String[] args) {
		if (GUIManager.getInstance().searchBrowser == null) {
			GUIManager.getInstance().searchBrowser = new SearchBrowser();
		}
		this.containerVBox = new VBox(10);
		this.textHBox = new HBox(10);
		this.buttonHBox = new HBox(10);
		this.searchTextTitle = new Text("Find your favorite songs, artists, albums, podcasts & videos");
		this.extraInformationImageView = new ImageView(new Image(Main.class.getResource("/resources/icons/v2/extra_information_icon.png").toExternalForm()));

		this.back_image = new ImageBuilder(new Image(Main.class.getResource("/resources/icons/navigation/back_icon.png").toExternalForm()), new Image(Main.class.getResource("/resources/icons/navigation/back_icon_hover.png").toExternalForm()));
		this.back_image.installToolTip("Go back a page.");

		this.forward_image = new ImageBuilder(new Image(Main.class.getResource("/resources/icons/navigation/forward_icon.png").toExternalForm()), new Image(Main.class.getResource("/resources/icons/navigation/forward_icon_hover.png").toExternalForm()));
		this.forward_image.installToolTip("Go forward a page.");

		this.reloadImage = new ImageBuilder(new Image(Main.class.getResource("/resources/icons/navigation/reload.png").toExternalForm()), new Image(Main.class.getResource("/resources/icons/navigation/reload-hover.png").toExternalForm()));
		this.reloadImage.installToolTip("Reload your current page.");

		CustomToolTip tooltip = new CustomToolTip("Search for a Playlist: /playlist/<playlist url> or enter a YouTube Playlist URL\nSearch for a User: /user/<username>\nSearch for a Song or enter a YouTube Song URL");
		tooltip.setStyle("-fx-font-size: 15;");
		CustomToolTip.install(this.extraInformationImageView, tooltip);

		configure();
		listen();

		GUIManager.getInstance().topBar.adjustToBrowsePageHeight();

		if (!GUIManager.getInstance().topBar.titleBar.searchTextfield.getText().isEmpty()) {
			if (GUIManager.getInstance().topBar.titleBar.searchTextfield.getText().startsWith("/") || GUIManager.getInstance().topBar.titleBar.searchTextfield.getText().startsWith("@"))
				GUIManager.getInstance().topBar.titleBar.searchTextfield.clear();
			else
				searchYouTube();
		}

		// If args.length > 0 we will display the browser on page load.
		if (args.length > 0) {
			if (!this.containerVBox.getChildren().contains(GUIManager.getInstance().searchBrowser.getWebView()))
				this.containerVBox.getChildren().add(GUIManager.getInstance().searchBrowser.getWebView());
		} else {
			if (BrowserManager.getInstance().getSearchURL().contains("/user/") || BrowserManager.getInstance().getSearchURL().contains("/results") || BrowserManager.getInstance().getSearchURL().contains("/channel/")) {
				if (!this.containerVBox.getChildren().contains(GUIManager.getInstance().searchBrowser.getWebView()))
					this.containerVBox.getChildren().add(GUIManager.getInstance().searchBrowser.getWebView());
			}
			if (!GUIManager.getInstance().searchBrowser.web_engine.url().contains("https://www.youtube.com/")) {
				if (GUIManager.getInstance().searchBrowser.getWebEngine().url().contains("?v="))
					GUIManager.getInstance().searchBrowser.web_engine.navigation().loadUrl(StoredURL.getInstance().lastBrowseURL);
				else
					GUIManager.getInstance().searchBrowser.web_engine.navigation().loadUrl("https://www.youtube.com/feed/trending?bp=4gINGgt5dG1hX2NoYXJ0cw%3D%3D");
			}
		}
	}

	private void configure() {
		GUIManager.getInstance().searchBrowser.web_view.minWidthProperty().bind(GUIManager.getInstance().contentManager.contentPane.widthProperty().subtract(300));
		GUIManager.getInstance().searchBrowser.web_view.maxWidthProperty().bind(GUIManager.getInstance().contentManager.contentPane.widthProperty().divide(300));
		GUIManager.getInstance().searchBrowser.web_view.minHeightProperty().bind(GUIManager.getInstance().contentManager.contentPane.heightProperty().subtract(85));
		GUIManager.getInstance().searchBrowser.web_view.maxHeightProperty().bind(GUIManager.getInstance().contentManager.contentPane.heightProperty().subtract(85));

		this.containerVBox.setStyle("-fx-padding: 7px " + GUIManager.getInstance().padding + "px;");

		GUIManager.getInstance().topBar.titleBar.searchTextfield.setMinSize(600, 45);
		GUIManager.getInstance().topBar.titleBar.searchTextfield.setMaxSize(600, 45);
		GUIManager.getInstance().topBar.titleBar.searchTextfield.setFont(Font.font(FontType.VERDANA.getName(), 16));
		GUIManager.getInstance().topBar.titleBar.searchTextfield.setPromptText("SEARCH");
		GUIManager.getInstance().topBar.titleBar.searchTextfield.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-background-radius: 20px; -fx-text-fill: white; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");

		this.searchTextTitle.setFont(Font.font(FontType.VERDANA.getName(), 14));
		this.searchTextTitle.setFill(Color.web(CustomColor.GRAY.getColorHex()));

		this.extraInformationImageView.setCursor(Cursor.HAND);

		this.buttonHBox.getChildren().addAll(this.back_image.getHBox(), this.forward_image.getHBox(), this.reloadImage.getHBox());
		this.textHBox.getChildren().addAll(this.searchTextTitle, this.extraInformationImageView);
		this.containerVBox.getChildren().add(0, this.textHBox);
		this.containerVBox.getChildren().add(1, this.buttonHBox);

		GUIManager.getInstance().contentManager.contentPane.getChildren().add(this.containerVBox);

		if (Options.test_mode) {
			TextField scriptTextField = new TextField();
			scriptTextField.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
			scriptTextField.setPromptText("Enter script");
			scriptTextField.setMinSize(400, 35);
			scriptTextField.setMaxWidth(400);

			scriptTextField.setOnKeyPressed(event -> {
				if (event.getCode() == KeyCode.ENTER) {
					GUIManager.getInstance().searchBrowser.getWebEngine().mainFrame().get().executeJavaScript(scriptTextField.getText());
					scriptTextField.clear();
				}
			});
			// this.textHBox.getChildren().add(scriptTextField);
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
				if (Inspector.getInstance().stage == null)
					Inspector.getInstance().inspectInitialize();
				Inspector.getInstance().inspect(false);
			});

			// this.textHBox.getChildren().add(button);
		}

		this.containerVBox.getChildren().add(GUIManager.getInstance().searchBrowser.getWebView());
	}

	private void listen() {
		this.back_image.onHBoxClick(event -> {
			GUIManager.getInstance().searchBrowser.getWebEngine().navigation().goBack();
		});
		this.forward_image.onHBoxClick(event -> {
			GUIManager.getInstance().searchBrowser.getWebEngine().navigation().goForward();
		});
		this.reloadImage.onHBoxClick(event -> {
			StoredURL.getInstance().currentSearchURL = ""; // Required for the SearchBrowser JS injections.
			GUIManager.getInstance().searchBrowser.getWebEngine().navigation().reload();
		});
		GUIManager.getInstance().topBar.titleBar.searchTextfield.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				String searchText = GUIManager.getInstance().topBar.titleBar.searchTextfield.getText();
				if (searchText.startsWith("/playlist/") || GUIManager.getInstance().topBar.titleBar.isYouTubePlaylist(searchText)) {
					GUIManager.getInstance().sideBar.sideBarTab.changeTab(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.OPEN_PLAYLIST_VIEW), searchText.replace("/playlist/", ""));
				} else if (searchText.startsWith("/user/") || searchText.startsWith("@")) {
					ProfileLoader.loadProfile(searchText.replace("/user/", "").replace("@", ""));
				} else if (searchText.contains("youtube.com/watch?v=")) {
					BrowserManager.getInstance().getCurrentBrowser().getWebEngine().navigation().loadUrl(SongTitle.getCorrectYouTubeURL(searchText));
				} else {
					searchYouTube();
				}
			}
		});

	}

	private void searchYouTube() {
		if (!this.containerVBox.getChildren().contains(GUIManager.getInstance().searchBrowser.getWebView()))
			this.containerVBox.getChildren().add(GUIManager.getInstance().searchBrowser.getWebView());

		if (this.searchTextTitle.isVisible()) {
			this.containerVBox.getChildren().remove(this.searchTextTitle);
		}

		String searchParam = GUIManager.getInstance().topBar.titleBar.searchTextfield.getText().replace("https://music.youtube.com", "https://www.youtube.com");

		// If entered URL is a song from either YouTube Music or YouTube, play the song.
		if (searchParam.contains("youtube.com/watch?v=")) {
			BrowserManager.getInstance().getCurrentBrowser().getWebEngine().navigation().loadUrl(searchParam);
			GUIManager.getInstance().audioBar.audioBar.setLoading(true);
			return;
		}

		StringBuilder string_builder = new StringBuilder();
		String[] words = searchParam.split(" ");
		for (String word : words)
			string_builder.append(word + "+");

		String queries = string_builder.toString();
		if (queries.endsWith("+"))
			queries = queries.substring(0, queries.length() - 1);

		if (!Options.actAsDefaultBrowser)
			GUIManager.getInstance().searchBrowser.getWebEngine().navigation().loadUrl("https://www.youtube.com/results?search_query=" + queries);
		else {
			if (searchParam.equals("stop")) {
				// GUIManager.getInstance().searchBrowser.web_engine.dispose(true);
				// BrowserCore.shutdown();
				// new Thread(() -> {
				// BrowserCore.initialize();
				// }).start();
			} else {
				GUIManager.getInstance().searchBrowser.getWebEngine().navigation().loadUrl(searchParam);
			}
		}
	}
}