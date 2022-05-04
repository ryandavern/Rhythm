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
import com.beatplaylist.gui.GUIManager;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class ContentManager {

	// Wrap top bar and content pane inside pane.
	private VBox contentWrapper;

	// Content pane which loaded page data is displayed into.
	public Pane contentPane;

	public void initializeContentPane() {
		this.contentWrapper = new VBox();
		this.contentPane = new Pane();
		if (Options.showLayoutConstraint)
			this.contentPane.setStyle("-fx-background-color: yellow; -fx-padding: 0, 0, 0, 40px;");
		else {
			// this.contentPane.setStyle("-fx-background-color: #1e1e1e; -fx-padding: 0, 0, 0, 40px; -fx-background-image: url(\"" + Main.class.getResource("/resources/tab_icons/background.png") + "\"); -fx-background-repeat: no-repeat; -fx-background-position: center -75px;");
			this.contentPane.setStyle("-fx-background-color: #1e1e1e; -fx-padding: 0, 0, 0, 40px;");
		}
	}

	public void configure() {
		this.contentWrapper.layoutXProperty().bind(GUIManager.getInstance().sideBar.sidebarPane.maxWidthProperty()); // Bind x position to max-width of the sidebar.
		this.contentWrapper.setLayoutY(0);

		// Set min/max bind values to the same.
		// this.contentWrapper.minHeightProperty().bind(GUIManager.getInstance().stage.heightProperty().multiply(0.77)); // Bind content wrapper height to 80% of stage height. (content wrapper = 80% + AudioBar = 20% = 100%)
		// this.contentWrapper.maxHeightProperty().bind(GUIManager.getInstance().stage.heightProperty().multiply(0.74)); // Bind content wrapper height to 80% of stage height. (content wrapper = 80% + AudioBar = 20% = 100%)

		this.contentWrapper.minWidthProperty().bind(GUIManager.getInstance().stage.widthProperty().multiply(0.82));
		this.contentWrapper.maxWidthProperty().bind(GUIManager.getInstance().stage.widthProperty().multiply(0.82));
		this.contentWrapper.minHeightProperty().bind(GUIManager.getInstance().stage.heightProperty().multiply(0.87));
		this.contentWrapper.maxHeightProperty().bind(GUIManager.getInstance().stage.heightProperty().multiply(0.87));

		this.contentPane.minWidthProperty().bind(this.contentWrapper.widthProperty());
		this.contentPane.maxWidthProperty().bind(this.contentWrapper.widthProperty());
		this.contentPane.minHeightProperty().bind(this.contentWrapper.heightProperty().multiply(0.94)); // Bind content wrapper height to 79.7% of stage height. (content wrapper = 79.7% + AudioBar = 20.3% = 100%)
		this.contentPane.maxHeightProperty().bind(this.contentWrapper.heightProperty().multiply(0.94)); // Bind content wrapper height to 79.7% of stage height. (content wrapper = 79.7% + AudioBar = 20.3% = 100%)

		// Stack top pane above content pane.
		this.contentWrapper.getChildren().addAll(GUIManager.getInstance().topBar.getTopPane(), this.contentPane);
	}

	// Method only used in the GUIManager => handleBackgroundLayout function where it is added to the backgroundPane.
	// Any content updates to the center section are handled in the contentPane.
	// Any top panel updates are handled in the TopBar => topPane class.
	public Pane getContentWrapper() {
		return this.contentWrapper;
	}

	// Used to update content pane.
	public Pane getContentPane() {
		return this.contentPane;
	}

	public void adjustToBrowsePageHeight() {
		this.contentWrapper.minHeightProperty().bind(GUIManager.getInstance().stage.heightProperty().multiply(0.82));
		this.contentWrapper.maxHeightProperty().bind(GUIManager.getInstance().stage.heightProperty().multiply(0.82));
	}

	public void adjustToNormalPageHeight() {
		this.contentWrapper.minHeightProperty().bind(GUIManager.getInstance().stage.heightProperty().multiply(0.87));
		this.contentWrapper.maxHeightProperty().bind(GUIManager.getInstance().stage.heightProperty().multiply(0.87));
	}
}