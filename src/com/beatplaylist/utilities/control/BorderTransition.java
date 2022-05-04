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

import com.beatplaylist.utilities.events.CompleteEvent;

import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class BorderTransition {

	public FillTransition fillTransition = null;
	public Rectangle rectangle = null;

	public BorderTransition(Region node, Color startColor, Color endColor, int borderWidth, CompleteEvent completeEvent) {
		this.fillTransition = new FillTransition();
		this.rectangle = new Rectangle();
		this.rectangle.setFill(startColor);
		node.setOnMouseEntered(event -> {
			completeEvent.onSuccess();

			this.fillTransition.setShape(this.rectangle);
			this.fillTransition.setDuration(Duration.millis(300));
			this.fillTransition.setFromValue(startColor);
			this.fillTransition.setToValue(endColor);

			this.fillTransition.setInterpolator(new Interpolator() {
				@Override
				protected double curve(double transition) {
					node.setBorder(new Border(new BorderStroke(rectangle.getFill(), BorderStrokeStyle.SOLID, new CornerRadii(20), new BorderWidths(borderWidth))));
					return transition;
				}
			});

			this.fillTransition.play();
		});

	}
}