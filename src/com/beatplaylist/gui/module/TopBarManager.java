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

import com.beatplaylist.Options;
import com.beatplaylist.enums.FontType;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.module.layout.sidebar.TitleBar;

import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

public class TopBarManager {

	// Stores top pane display
	public Pane topPane;
	public TitleBar titleBar;

	public void initializeTopBar() {
		this.topPane = new Pane();
		this.titleBar = new TitleBar();
		if (Options.showLayoutConstraint)
			this.topPane.setStyle("-fx-background-color: red;");
		else
			this.topPane.setStyle("-fx-background-color: #1e1e1e;");
	}

	public void configure() {
		this.topPane.minHeightProperty().bind(GUIManager.getInstance().contentManager.getContentWrapper().heightProperty().multiply(0.06)); // Bind topPane height to 6% of content wrapper height.
		this.topPane.maxHeightProperty().bind(GUIManager.getInstance().contentManager.getContentWrapper().heightProperty().multiply(0.06));

		this.topPane.minWidthProperty().bind(GUIManager.getInstance().contentManager.getContentWrapper().widthProperty());
		this.topPane.maxWidthProperty().bind(GUIManager.getInstance().contentManager.getContentWrapper().widthProperty());

		this.topPane.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				this.titleBar.fullscreen();
			}
		});
	}

	public Pane getTopPane() {
		return this.topPane;
	}

	public void adjustToBrowsePageHeight() {
		// Unbind current values.
		this.topPane.minHeightProperty().unbind();
		this.topPane.maxHeightProperty().unbind();

		this.topPane.minHeightProperty().bind(GUIManager.getInstance().contentManager.getContentWrapper().heightProperty().multiply(0.12)); // Bind topPane height to 6% of content wrapper height.
		this.topPane.maxHeightProperty().bind(GUIManager.getInstance().contentManager.getContentWrapper().heightProperty().multiply(0.12));

		this.titleBar.searchTextfield.layoutYProperty().unbind();

		if (GUIManager.getInstance().stage.getHeight() < 700) {
			this.titleBar.searchTextfield.setLayoutY(15);
		} else {
			this.titleBar.searchTextfield.layoutYProperty().bind(GUIManager.getInstance().topBar.topPane.heightProperty().divide(2.4));
		}

		GUIManager.getInstance().contentManager.adjustToBrowsePageHeight();
	}

	public void adjustToNormalPageHeight() {
		// Unbind current values.
		this.topPane.minHeightProperty().unbind();
		this.topPane.maxHeightProperty().unbind();

		this.topPane.minHeightProperty().bind(GUIManager.getInstance().contentManager.getContentWrapper().heightProperty().multiply(0.06)); // Bind topPane height to 6% of content wrapper height.
		this.topPane.maxHeightProperty().bind(GUIManager.getInstance().contentManager.getContentWrapper().heightProperty().multiply(0.06));

		this.titleBar.searchTextfield.setMinSize(350, 30);
		this.titleBar.searchTextfield.setMaxSize(350, 30);

		this.titleBar.searchTextfield.layoutYProperty().unbind();
		this.titleBar.searchTextfield.setLayoutY(7);
		this.titleBar.searchTextfield.setFont(Font.font(FontType.VERDANA.getName(), 12));
		if (GUIManager.getInstance().stage.getHeight() < 700) {
			GUIManager.getInstance().topBar.titleBar.searchTextfield.setMinHeight(25);
		}

		GUIManager.getInstance().contentManager.adjustToNormalPageHeight();
	}
}