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
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;

public class ControlledProgressBar extends StackPane {

	public ProgressBar progressBar;
	public Slider slider;

	public ControlledProgressBar() {
		this.progressBar = new ProgressBar(0);
		this.slider = new Slider(0, 100, 0);

		StackPane.setAlignment(this.slider, Pos.CENTER_LEFT);
		StackPane.setAlignment(this.progressBar, Pos.CENTER_LEFT);

		super.getChildren().addAll(this.progressBar, this.slider);
	}

	public void setSliderHeight(double value) {
		super.setMinHeight(value);
		super.setMaxHeight(value);

		this.slider.setMinHeight(value);
		this.slider.setMaxHeight(value);
	}

	public ProgressBar getProgressBar() {
		return this.progressBar;
	}

	public Slider getSlider() {
		return this.slider;
	}
}