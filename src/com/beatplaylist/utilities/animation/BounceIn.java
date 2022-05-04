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

package com.beatplaylist.utilities.animation;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;

public class BounceIn extends AnimationFX {

	/**
	 * Create new BounceIn
	 *
	 * @param node
	 *            The node to affect
	 */
	public BounceIn(Node node) {
		super(node);

	}

	public BounceIn() {
		super();
	}

	@Override
	AnimationFX resetNode() {
		return this;
	}

	@Override
	void initTimeline() {
		setTimeline(new Timeline(
				new KeyFrame(Duration.millis(0), //
						new KeyValue(getNode().opacityProperty(), 0, Interpolator.SPLINE(0.215, 0.610, 0.355, 1.000)), //
						new KeyValue(getNode().scaleXProperty(), 0.3, Interpolator.SPLINE(0.215, 0.610, 0.355, 1.000)), //
						new KeyValue(getNode().scaleYProperty(), 0.3, Interpolator.SPLINE(0.215, 0.610, 0.355, 1.000))), //
				new KeyFrame(Duration.millis(200), //
						new KeyValue(getNode().opacityProperty(), 0.5, Interpolator.SPLINE(0.215, 0.610, 0.355, 1.000)), //
						new KeyValue(getNode().scaleXProperty(), 1.1, Interpolator.SPLINE(0.215, 0.610, 0.355, 1.000)), //
						new KeyValue(getNode().scaleYProperty(), 1.1, Interpolator.SPLINE(0.215, 0.610, 0.355, 1.000))), //
				new KeyFrame(Duration.millis(400), //
						new KeyValue(getNode().opacityProperty(), 1, Interpolator.SPLINE(0.215, 0.610, 0.355, 1.000)), //
						new KeyValue(getNode().scaleXProperty(), 1, Interpolator.SPLINE(0.215, 0.610, 0.355, 1.000)), //
						new KeyValue(getNode().scaleYProperty(), 1, Interpolator.SPLINE(0.215, 0.610, 0.355, 1.000)))));
				
				
	}
}