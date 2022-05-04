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

package com.beatplaylist.gui;

import com.beatplaylist.utilities.CustomColor;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CenterBox {

	// HBox starts at 0,0 and is set to height of background pane and width of background pane.
	public HBox wrapperHBox;

	// Wrapper VBox wraps the content vbox and is centered. Content vbox is centered.
	public VBox wrapperVBox, contentVBox;

	public CenterBox() {
		this.wrapperHBox = new HBox();
		this.wrapperVBox = new VBox(30);
		this.contentVBox = new VBox(10);

		this.wrapperHBox.minWidthProperty().bind(GUIManager.getInstance().getPane().widthProperty());
		this.wrapperHBox.minHeightProperty().bind(GUIManager.getInstance().getPane().heightProperty());
		this.wrapperHBox.setAlignment(Pos.CENTER);

		this.wrapperVBox.setAlignment(Pos.CENTER);
		this.contentVBox.setAlignment(Pos.CENTER);

		this.wrapperHBox.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + ";");
		this.wrapperVBox.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + ";");
		this.contentVBox.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + ";");

		this.wrapperVBox.getChildren().add(this.contentVBox);
		this.wrapperHBox.getChildren().add(this.wrapperVBox);
	}

	public HBox getWrapperHBox() {
		return this.wrapperHBox;
	}

	public VBox getContentVBox() {
		return this.contentVBox;
	}

	public VBox getWrapperVBox() {
		return this.wrapperVBox;
	}
}
