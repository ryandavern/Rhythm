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

package com.beatplaylist.utilities.control;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

public class EvenSpacedHBox {

	public HBox containerHBox, leftHBox, rightHBox;

	public EvenSpacedHBox() {
		this.containerHBox = new HBox();
		this.leftHBox = new HBox();
		this.rightHBox = new HBox();

		this.leftHBox.minWidthProperty().bind(this.containerHBox.widthProperty().divide(2));
		this.rightHBox.minWidthProperty().bind(this.containerHBox.widthProperty().divide(2));

		this.leftHBox.setAlignment(Pos.CENTER_LEFT);
		this.rightHBox.setAlignment(Pos.CENTER_RIGHT);

		this.containerHBox.getChildren().addAll(this.leftHBox, this.rightHBox);
	}
	
	public HBox getLeft() {
		return this.leftHBox;
	}
	
	public HBox getRight() {
		return this.rightHBox;
	}
	
	public HBox getContainer() {
		return this.containerHBox;
	}
}