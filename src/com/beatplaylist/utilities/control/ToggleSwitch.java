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

import com.beatplaylist.utilities.CustomColor;

import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class ToggleSwitch extends Parent {

	private BooleanProperty switchedOn = new SimpleBooleanProperty(false);

	private TranslateTransition translateAnimation = new TranslateTransition(Duration.seconds(0.25));
	private FillTransition fillAnimation = new FillTransition(Duration.seconds(0.25));
	private ParallelTransition animation = new ParallelTransition(this.translateAnimation, this.fillAnimation);

	public ToggleSwitch() {
		Rectangle background = new Rectangle(45, 25);
		background.setArcWidth(25);
		background.setArcHeight(25);
		background.setFill(Color.web(CustomColor.BACKGROUND.getColorHex()));
		background.setStroke(Color.web(CustomColor.DARK_BACKGROUND.getColorHex()));
		background.setCursor(Cursor.HAND);

		Circle circle = new Circle(12);
		circle.setCenterX(12);
		circle.setCenterY(12.5);
		circle.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		circle.setStroke(Color.web(CustomColor.DARK_BACKGROUND.getColorHex()));
		circle.setCursor(Cursor.HAND);

		this.translateAnimation.setNode(circle);
		this.fillAnimation.setShape(background);

		getChildren().addAll(background, circle);

		this.switchedOn.addListener((obs, oldState, newState) -> {
			boolean isOn = newState.booleanValue();
			this.translateAnimation.setToX(isOn ? 45 - 25 : 0);
			this.fillAnimation.setFromValue(isOn ? Color.web(CustomColor.BACKGROUND.getColorHex()) : Color.web(CustomColor.RHYTHM.getColorHex()));
			this.fillAnimation.setToValue(isOn ? Color.web(CustomColor.RHYTHM.getColorHex()) : Color.web(CustomColor.BACKGROUND.getColorHex()));

			background.setStroke(isOn ? Color.web(CustomColor.RHYTHM.getColorHex()) : Color.web(CustomColor.DARK_BACKGROUND.getColorHex()));
			circle.setStroke(isOn ? Color.web(CustomColor.RHYTHM.getColorHex()) : Color.web(CustomColor.DARK_BACKGROUND.getColorHex()));

			this.animation.play();
		});

		setOnMouseClicked(event -> {
			setEnabled(!this.switchedOn.get());
		});
	}

	public void setEnabled(boolean value) {
		this.switchedOn.set(value);
	}

	public BooleanProperty switchedOnProperty() {
		return this.switchedOn;
	}
}