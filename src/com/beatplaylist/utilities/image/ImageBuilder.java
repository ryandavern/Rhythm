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

package com.beatplaylist.utilities.image;

import com.beatplaylist.utilities.CustomToolTip;
import com.beatplaylist.utilities.control.Tooltip;
import com.beatplaylist.utilities.popup.control.PopupHBox;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class ImageBuilder {

	private PopupHBox hbox;
	private ImageView image_view;
	private Image image, hover_image;

	public ImageBuilder(Image image) {
		this.image = image;
		this.hbox = new PopupHBox();
		this.image_view = new ImageView(image);
		this.hbox.getChildren().add(this.image_view);
		this.hbox.setAlignment(Pos.CENTER);
	}

	public ImageBuilder(Image image, Image hover_image) {
		this.image = image;
		this.hover_image = hover_image;

		this.hbox = new PopupHBox();
		this.image_view = new ImageView(image);
		this.hbox.getChildren().add(this.image_view);
		this.hbox.setStyle("-fx-cursor: hand;");
		this.hbox.setAlignment(Pos.CENTER);

		this.onHBoxMouseEnter(event -> {
			setImage(hover_image);
		});
		this.onHBoxMouseExit(event -> {
			setImage(image);
		});
	}

	public void changeImages(Image image, Image hover_image) {
		this.image = image;
		this.hover_image = hover_image;
		this.image_view.setImage(image);

		this.onHBoxMouseEnter(event -> {
			setImage(hover_image);

		});
		this.onHBoxMouseExit(event -> {
			setImage(image);
		});
	}

	public void swap() {
		Image old_image = this.image, old_hover = this.hover_image;

		this.image = old_hover;
		this.hover_image = old_image;

		this.image_view.setImage(this.image);

		this.onHBoxMouseEnter(event -> {
			setImage(this.hover_image);
		});
		this.onHBoxMouseExit(event -> {
			setImage(this.image);
		});
	}

	public void installToolTip(String text) {
		Tooltip.install(this.hbox, new CustomToolTip(text));
	}

	public HBox getHBox() {
		return this.hbox;
	}

	public ImageView getImageView() {
		return this.image_view;
	}

	public void onHBoxClick(EventHandler<MouseEvent> handler) {
		this.hbox.setOnMouseClicked(handler);
	}

	public void onHBoxMouseEnter(EventHandler<MouseEvent> handler) {
		this.hbox.setOnMouseEntered(handler);
	}

	public void onHBoxMouseExit(EventHandler<MouseEvent> handler) {
		this.hbox.setOnMouseExited(handler);
	}

	public void onImageViewClick(EventHandler<MouseEvent> handler) {
		this.image_view.setOnMouseClicked(handler);
	}

	public void onImageViewMouseEnter(EventHandler<MouseEvent> handler) {
		this.image_view.setOnMouseEntered(handler);
	}

	public void onImageViewMouseExit(EventHandler<MouseEvent> handler) {
		this.image_view.setOnMouseExited(handler);
	}

	public void setImage(Image image) {
		this.image_view.setImage(image);
	}

	public Image getImage() {
		return this.image_view.getImage();
	}
}