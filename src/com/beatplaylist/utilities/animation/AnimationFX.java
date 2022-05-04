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

import javafx.animation.Animation;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.util.Duration;

// Sourced from AnimateFX (https://github.com/Typhon0/AnimateFX)
public abstract class AnimationFX {

	/**
	 * Used to specify an animation that repeats indefinitely, until the {@code stop()} method is called.
	 */
	private Timeline timeline;
	private Node node;
	private boolean reset;

	/**
	 * Create a new animation
	 *
	 * @param node
	 *            the node to affect
	 */
	public AnimationFX(Node node) {
		super();
		setNode(node);

	}

	/**
	 * Default constructor
	 */
	public AnimationFX() {
		this.reset = false;
	}

	/**
	 * Handle when the animation is finished
	 *
	 * @return
	 */
	private AnimationFX onFinished() {
		if (this.reset) {
			resetNode();
		}
		return this;
	}

	/**
	 * Function to reset the node or not when the animation is finished
	 *
	 * @param reset
	 * @return
	 */
	public AnimationFX setResetOnFinished(boolean reset) {
		this.reset = reset;
		return this;
	}

	/**
	 * Play the animation
	 */
	public void play() {
		this.timeline.play();
	}

	/**
	 * Stop the animation
	 *
	 * @return
	 */
	public AnimationFX stop() {
		this.timeline.stop();
		return this;
	}

	/**
	 * Function the reset the node to original state
	 *
	 * @return
	 */
	abstract AnimationFX resetNode();

	/**
	 * Function to initialize the timeline
	 */
	abstract void initTimeline();

	public Timeline getTimeline() {
		return timeline;
	}

	public void setTimeline(Timeline timeline) {
		this.timeline = timeline;
	}

	public boolean isResetOnFinished() {
		return this.reset;
	}

	protected void setReset(boolean reset) {
		this.reset = reset;
	}

	public Node getNode() {
		return this.node;
	}

	public void setNode(Node node) {
		this.node = node;
		initTimeline();
		this.timeline.statusProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.equals(Animation.Status.STOPPED)) {
				onFinished();
			}
		});
	}

	/**
	 * Define the number of cycles in this animation
	 *
	 * @param value
	 * @return
	 */
	public AnimationFX setCycleCount(int value) {
		this.timeline.setCycleCount(value);
		return this;
	}

	/**
	 * Set the speed factor of the animation
	 *
	 * @param value
	 * @return
	 */
	public AnimationFX setSpeed(double value) {
		this.timeline.setRate(value);
		return this;
	}

	/**
	 * Delays the start of an animation
	 *
	 * @param value
	 * @return
	 */
	public AnimationFX setDelay(Duration value) {
		this.timeline.setDelay(value);
		return this;
	}

	/**
	 * Set event when the animation ended.
	 *
	 * @param value
	 */
	public final void setOnFinished(EventHandler<ActionEvent> value) {
		this.timeline.setOnFinished(value);
	}
}