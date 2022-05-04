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

import com.beatplaylist.utilities.image.ImageBuilder;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class HeaderTextWithButton {

	public HBox hbox;
	public Text text;
	public ImageBuilder imageBuilder;

	public HeaderTextWithButton(String text, ImageBuilder imageBuilder) {
		this.hbox = new HBox(10);
		this.text = new Text(text);
		this.imageBuilder = imageBuilder;
	
		this.hbox.getChildren().addAll(this.text, this.imageBuilder.getHBox());
		this.hbox.setAlignment(Pos.CENTER_LEFT);
	}
	
	public HBox getHBox() {
		return this.hbox;
	}
	
	public Text getText() {
		return this.text;
	}
	
	public ImageBuilder getImageBuilder() {
		return this.imageBuilder;
	}

}
