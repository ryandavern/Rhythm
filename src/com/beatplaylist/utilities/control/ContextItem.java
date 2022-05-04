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

import com.beatplaylist.enums.FontType;
import com.beatplaylist.utilities.CustomColor;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ContextItem extends MenuItem {

	public Label label;
	public HBox hbox;

	public ContextItem(String text) {
		this.hbox = new HBox(25);

		this.setStyle("-fx-cursor: hand; -fx-text-fill: " + CustomColor.POPUP_SUB_HEADER.getColorHex() + ";");

		this.label = new Label(text);
		this.label.setWrapText(true);
		this.label.setTextFill(Color.web(CustomColor.POPUP_SUB_HEADER.getColorHex()));
		this.label.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 14));
		this.label.setMinSize(100, 30);
		this.label.setPadding(new Insets(this.label.getPadding().getTop(), this.label.getPadding().getRight(), this.label.getPadding().getBottom(), this.label.getPadding().getLeft() + 25));

		this.hbox.getChildren().add(this.label);
		this.hbox.setAlignment(Pos.CENTER_LEFT);
		this.hbox.setCursor(Cursor.HAND);

		this.setGraphic(this.hbox);
	}

	public String getLabelText() {
		return this.label.getText();
	}

	public void setLabelText(String text) {
		this.label.setText(text);
	}

	public void setIcon(ImageView image_view) {
		if (this.hbox == null)
			return;
		this.hbox.getChildren().add(0, image_view);
	}

	public void setHBoxSize(double width, double height) {
		if (this.hbox == null)
			return;
		this.hbox.setMinSize(width, height);
	}

	public void setLabelSize(double width, double height) {
		if (this.label == null)
			return;
		this.label.setMinSize(width, height);
	}

	public double getDefaultHeight() {
		return 25;
	}
}