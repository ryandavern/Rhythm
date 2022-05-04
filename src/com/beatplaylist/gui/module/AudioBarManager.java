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

package com.beatplaylist.gui.module;

import com.beatplaylist.Main;
import com.beatplaylist.Options;
import com.beatplaylist.enums.FontType;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.module.layout.audiobar.AudioBar;
import com.beatplaylist.gui.module.layout.audiobar.AudioListener;
import com.beatplaylist.gui.module.layout.sidebar.TabType;
import com.beatplaylist.utilities.CustomColor;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class AudioBarManager {

	public Pane audioBarPane;
	public AudioBar audioBar;
	public AudioListener audioListener;
	public VBox nowPlayingButton;

	public void initializeAudioBar() {
		this.audioBarPane = new Pane();
		this.audioBar = new AudioBar();
		this.audioListener = new AudioListener();

		if (Options.showLayoutConstraint)
			this.audioBarPane.setStyle("-fx-background-color: blue;");
		else
			this.audioBarPane.setStyle("-fx-background-color: " + CustomColor.AUDIO_BAR_BACKGROUND_COLOR.getColorHex() + ";");
	}

	public void configure() {
		this.audioBarPane.setLayoutX(0);
		// this.audioBarPane.setLayoutY(GUIManager.getInstance().sideBar.getHeightYValue()); // Set the starting y value to the max-height of the sidebar.
		this.audioBarPane.layoutYProperty().bind(GUIManager.getInstance().sideBar.sidebarPane.maxHeightProperty());

		// Set min/max bind values to the same.
		this.audioBarPane.minHeightProperty().bind(GUIManager.getInstance().stage.heightProperty().multiply(0.14)); // Bind SideBar to 11% of backgroundPane height. (SideBar = 80% + AudioBar = 20% = 100%)
		this.audioBarPane.maxHeightProperty().bind(GUIManager.getInstance().stage.heightProperty().multiply(0.14)); // Bind SideBar to 11% of backgroundPane height. (SideBar = 80% + AudioBar = 20% = 100%)

		this.audioBarPane.minWidthProperty().bind(GUIManager.getInstance().stage.widthProperty());
		this.audioBarPane.maxWidthProperty().bind(GUIManager.getInstance().stage.widthProperty());

		this.audioBar.configure();
	}

	public VBox getNowPlayingButton() {
		if (this.nowPlayingButton != null)
			return this.nowPlayingButton;

		this.nowPlayingButton = new VBox();
		Text nowPlayingText = new Text("NOW PLAYING");
		ImageView upImageView = new ImageView(new Image(Main.class.getResource("/resources/icons/v2/up_icon.png").toExternalForm(), 10, 10, false, false));

		this.nowPlayingButton.setOnMouseClicked(event -> {
			if (this.nowPlayingButton.isDisabled())
				return;
			GUIManager.getInstance().sideBar.sideBarTab.changeTab(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.NOW_PLAYING));
		});

		nowPlayingText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 12));
		nowPlayingText.setFill(Color.web(CustomColor.WHITE.getColorHex()));

		this.nowPlayingButton.getChildren().addAll(upImageView, nowPlayingText);
		this.nowPlayingButton.setAlignment(Pos.TOP_CENTER);
		this.nowPlayingButton.setStyle("-fx-opacity: 0.5; -fx-background-color: " + CustomColor.RHYTHM.getColorHex() + "; -fx-cursor: hand; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-border-radius: 20 20 0 0; -fx-background-radius: 20 20 0 0;");
		this.nowPlayingButton.layoutYProperty().bind(this.audioBarPane.layoutYProperty().subtract(30));
		this.nowPlayingButton.layoutXProperty().bind(GUIManager.getInstance().sideBar.sidebarPane.widthProperty().divide(6));
		this.nowPlayingButton.setMinHeight(30);
		this.nowPlayingButton.setMaxHeight(30);
		this.nowPlayingButton.minWidthProperty().bind(GUIManager.getInstance().sideBar.sidebarPane.widthProperty().divide(1.5));
		this.nowPlayingButton.maxWidthProperty().bind(GUIManager.getInstance().sideBar.sidebarPane.widthProperty().divide(1.5));
		this.nowPlayingButton.setDisable(true);

		return this.nowPlayingButton;
	}

	public Pane getAudioBarPane() {
		return this.audioBarPane;
	}

}