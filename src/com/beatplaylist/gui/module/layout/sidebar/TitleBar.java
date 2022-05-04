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

package com.beatplaylist.gui.module.layout.sidebar;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import com.beatplaylist.Main;
import com.beatplaylist.chromium.BrowserManager;
import com.beatplaylist.enums.FontType;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.UIResize;
import com.beatplaylist.gui.module.page.profile.ProfileLoader;
import com.beatplaylist.gui.utilities.playlist.SongTitle;
import com.beatplaylist.utilities.CustomColor;
import com.beatplaylist.utilities.data.StoredURL;
import com.beatplaylist.utilities.image.ImageBuilder;
import com.beatplaylist.utilities.popup.Popup;

import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class TitleBar {

	public BorderPane titleBarBorderPane;
	public HBox titleBarHbox;

	// Maximise UI settings.
	public boolean lastWasMaximise = false;
	public double heightBeforeMaximise = 0, widthBeforeMaximise = 0, positionXBeforeMaximise = 0, positionYBeforeMaximise = 0;

	public TextField searchTextfield;
	public Screen lastMaximisedScreen = null;

	// Drag UI top of screen styling
	private double offsetY, offsetX, screenY;
	private boolean isDim = false;
	private Stage dimStage;
	private AnchorPane anchorPane;

	public TitleBar() {
		this.titleBarBorderPane = new BorderPane();
		this.titleBarHbox = new HBox();
		this.searchTextfield = new TextField();
		this.anchorPane = new AnchorPane();

		Rectangle rec = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

		this.anchorPane.setPrefHeight(rec.height - 10);
		this.anchorPane.setPrefWidth(rec.width - 10);
		this.anchorPane.setStyle("-fx-background-color: rgba(189, 195, 199,0.25);");

		this.dimStage = new Stage();
		this.dimStage.setScene(new Scene(this.anchorPane));
		this.dimStage.initStyle(StageStyle.TRANSPARENT);
		this.dimStage.getScene().setFill(Color.TRANSPARENT);
		this.dimStage.setAlwaysOnTop(false);

		this.titleBarBorderPane.layoutXProperty().bind(GUIManager.getInstance().topBar.topPane.widthProperty().subtract(180));
		this.titleBarBorderPane.setLayoutY(7);

		this.titleBarBorderPane.setMinWidth(50);
		this.titleBarBorderPane.maxWidthProperty().bind(GUIManager.getInstance().topBar.topPane.widthProperty().subtract(180));
		this.titleBarBorderPane.setMinHeight(22);

		BorderPane.setAlignment(this.titleBarBorderPane, Pos.TOP_RIGHT);
		BorderPane.setAlignment(this.searchTextfield, Pos.TOP_LEFT);

		this.searchTextfield.setLayoutY(7);
		this.searchTextfield.setLayoutX(20);
		this.searchTextfield.setPromptText("SEARCH");
		this.searchTextfield.setMinWidth(350);
		this.searchTextfield.setMinHeight(30);
		this.searchTextfield.setFont(Font.font(FontType.VERDANA.getName(), 12));
		this.searchTextfield.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-background-radius: 20px; -fx-text-fill: white; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");

		ImageBuilder close_builder = new ImageBuilder(new Image(Main.class.getResource("/resources/title_bar_icon/exit.png").toExternalForm()), new Image(Main.class.getResource("/resources/title_bar_icon/exit_hover.png").toExternalForm()));
		ImageBuilder minimise_builder = new ImageBuilder(new Image(Main.class.getResource("/resources/title_bar_icon/minimise.png").toExternalForm()), new Image(Main.class.getResource("/resources/title_bar_icon/minimise_hover.png").toExternalForm()));
		ImageBuilder maximise_builder = new ImageBuilder(new Image(Main.class.getResource("/resources/title_bar_icon/maximise.png").toExternalForm()), new Image(Main.class.getResource("/resources/title_bar_icon/maximise_hover.png").toExternalForm()));

		close_builder.getHBox().setMinWidth(60);
		close_builder.getHBox().setAlignment(Pos.CENTER);
		close_builder.getHBox().setStyle("-fx-cursor: hand;");

		minimise_builder.getHBox().setMinWidth(60);
		minimise_builder.getHBox().setAlignment(Pos.CENTER);
		minimise_builder.getHBox().setStyle("-fx-cursor: hand;");

		maximise_builder.getHBox().setMinWidth(60);
		maximise_builder.getHBox().setAlignment(Pos.CENTER);
		maximise_builder.getHBox().setStyle("-fx-cursor: hand;");

		minimise_builder.getHBox().setOnMouseClicked(event -> {
			GUIManager.getInstance().stage.setIconified(true);
		});
		close_builder.getHBox().setOnMouseClicked(event -> {
			Popup.confirmCloseProgram();
		});
		maximise_builder.getHBox().setOnMouseClicked(event -> {
			fullscreen();
		});

		this.titleBarHbox.getChildren().addAll(minimise_builder.getHBox(), maximise_builder.getHBox(), close_builder.getHBox());
		this.titleBarBorderPane.setRight(this.titleBarHbox);

		GUIManager.getInstance().topBar.topPane.getChildren().addAll(this.searchTextfield, this.titleBarBorderPane);

		GUIManager.getInstance().topBar.topPane.setOnMousePressed(event -> {
			this.offsetX = event.getSceneX();
			this.offsetY = event.getSceneY();
		});

		GUIManager.getInstance().topBar.topPane.setOnMouseDragged(event -> {
			this.screenY = event.getScreenY() - this.offsetY;

			if (this.screenY < 0 && !this.isDim) {
				this.isDim = true;
				this.dimStage.show();
				GUIManager.getInstance().stage.toFront();
			} else if (this.screenY > 0) {
				this.dimStage.hide();
				this.isDim = false;
			}

			(GUIManager.getInstance().topBar.topPane.getScene().getWindow()).setX(event.getScreenX() - this.offsetX);
			(GUIManager.getInstance().topBar.topPane.getScene().getWindow()).setY(this.screenY);

		});

		GUIManager.getInstance().topBar.topPane.setOnMouseReleased(event -> {
			if (this.screenY < 0) {
				this.dimStage.hide();
				this.isDim = false;
			}
		});

		updateSearchTextFieldKeyPressListener();
	}

	private Screen getRhythmScreen() {
		for (Screen screen : Screen.getScreensForRectangle(GUIManager.getInstance().stage.getX(), GUIManager.getInstance().stage.getY(), 1., 1.)) {
			return screen;
		}
		return Screen.getPrimary();
	}

	public void updateSearchTextFieldKeyPressListener() {
		this.searchTextfield.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				StoredURL.getInstance().currentSearchURL = "";
				String searchText = this.searchTextfield.getText();

				if (searchText.startsWith("/playlist/") || isYouTubePlaylist(searchText)) {
					System.out.println(searchText.replace("/playlist/", ""));
					GUIManager.getInstance().sideBar.sideBarTab.changeTab(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.OPEN_PLAYLIST_VIEW), searchText.replace("/playlist/", ""));
				} else if (searchText.startsWith("/user/") || searchText.startsWith("@")) {
					ProfileLoader.loadProfile(searchText.replace("/user/", "").replace("@", ""));
				} else if (searchText.contains("youtube.com/watch?v=")) {
					BrowserManager.getInstance().getCurrentBrowser().getWebEngine().navigation().loadUrl(SongTitle.getCorrectYouTubeURL(searchText));
				} else {
					GUIManager.getInstance().sideBar.sideBarTab.changeTab(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.BROWSE), this.searchTextfield.getText());
				}
			}
		});
	}

	public void fullscreen() {

		Screen screen = getRhythmScreen();

		if (this.lastWasMaximise && this.lastMaximisedScreen == screen) {
			GUIManager.getInstance().stage.setWidth(this.widthBeforeMaximise);
			GUIManager.getInstance().stage.setHeight(this.heightBeforeMaximise);
			GUIManager.getInstance().stage.setX(this.positionXBeforeMaximise);
			GUIManager.getInstance().stage.setY(this.positionYBeforeMaximise);
			this.lastWasMaximise = false;
		} else {
			this.heightBeforeMaximise = GUIManager.getInstance().stage.getHeight();
			this.widthBeforeMaximise = GUIManager.getInstance().stage.getWidth();
			this.positionXBeforeMaximise = GUIManager.getInstance().stage.getX();
			this.positionYBeforeMaximise = GUIManager.getInstance().stage.getY();

			Rectangle2D bounds = screen.getVisualBounds();

			GUIManager.getInstance().stage.setX(bounds.getMinX());
			GUIManager.getInstance().stage.setY(bounds.getMinY());
			GUIManager.getInstance().stage.setWidth(bounds.getWidth());
			GUIManager.getInstance().stage.setHeight(bounds.getHeight());
			this.lastWasMaximise = true;
		}
		this.lastMaximisedScreen = screen;
		UIResize.handleHeightResize(GUIManager.getInstance().stage.getHeight());
	}

	public void unfullscreen() {
		GUIManager.getInstance().stage.setWidth(this.widthBeforeMaximise);
		GUIManager.getInstance().stage.setHeight(this.heightBeforeMaximise);
		GUIManager.getInstance().stage.setX(this.positionXBeforeMaximise);
		GUIManager.getInstance().stage.setY(this.positionYBeforeMaximise);
		this.lastWasMaximise = false;
		UIResize.handleHeightResize(GUIManager.getInstance().stage.getHeight());
	}

	public boolean isYouTubePlaylist(String playlistURL) {
		if (playlistURL.contains("youtube.com") && (playlistURL.contains("/playlist?list=") || playlistURL.contains("&list="))) {
			return true;
		}
		return false;
	}

	// Sets the search textfield text value and positions the caret at the end of the text
	public void setSearchTextFieldTextAndUpdateCaret(String text) {
		this.searchTextfield.setText(text);
		this.searchTextfield.positionCaret(this.searchTextfield.getText().length() + 1);
	}
}