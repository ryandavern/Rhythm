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

import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class BackgroundTransition {

	public FillTransition fillTransition = null;
	public Rectangle rectangle = null;

	public BackgroundTransition(Region node, Color startColor, Color endColor) {
		this.fillTransition = new FillTransition();
		this.rectangle = new Rectangle();
		this.rectangle.setFill(startColor);

		this.fillTransition.setShape(this.rectangle);
		this.fillTransition.setDuration(Duration.millis(150));
		this.fillTransition.setFromValue(startColor);
		this.fillTransition.setToValue(endColor);

		this.fillTransition.setInterpolator(new Interpolator() {
			@Override
			protected double curve(double transition) {
				node.setBackground(new Background(new BackgroundFill(rectangle.getFill(), new CornerRadii(5), Insets.EMPTY)));
				return transition;
			}
		});

		this.fillTransition.play();

	}
}