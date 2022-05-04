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

package com.beatplaylist.utilities.image.album;

import javafx.scene.image.ImageView;

public class ImageCallback {

	private ImageView image_view;
	private double moreThanWidth, moreThanHeight, width, height;

	public ImageCallback(ImageView image_view, double moreThanWidth, double moreThanHeight, double width, double height) {
		this.image_view = image_view;
		this.moreThanWidth = moreThanWidth;
		this.moreThanHeight = moreThanHeight;
		this.width = width;
		this.height = height;
	}

	public void execute() {
		if (this.image_view.getImage() != null && this.image_view.getImage().getHeight() >= this.moreThanHeight)
			this.image_view.setFitHeight(this.height);
		if (this.image_view.getImage() != null && this.image_view.getImage().getWidth() >= this.moreThanWidth)
			this.image_view.setFitWidth(this.width);
	}
}