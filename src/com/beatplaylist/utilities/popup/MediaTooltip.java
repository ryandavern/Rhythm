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

package com.beatplaylist.utilities.popup;

import com.beatplaylist.enums.FontType;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.utilities.CustomColor;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class MediaTooltip {

	public HBox hbox;
	public Text text;

	public MediaTooltip(Node node) {
		this.hbox = new HBox();
		this.text = new Text();

		this.hbox.setAlignment(Pos.CENTER);
		this.hbox.getChildren().add(this.text);
		this.text.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		this.text.setFont(Font.font(FontType.VERDANA.getName(), 12));
		this.hbox.setStyle("-fx-background-color: #000; -fx-padding: 10px; -fx-background-radius: 10px;");

		GUIManager.getInstance().backgroundPane.setOnMouseClicked(event -> {
			this.hbox.setVisible(false);
		});
		node.setOnMouseExited(event -> {
			this.hbox.setVisible(false);
		});
	}

	public void setPopupLocation(double x, double y) {
		this.hbox.setLayoutX(x);
		this.hbox.setLayoutY(y);
	}

	public void setText(String text) {
		if (!GUIManager.getInstance().backgroundPane.getChildren().contains(this.hbox))
			GUIManager.getInstance().backgroundPane.getChildren().add(this.hbox);
		this.text.setText(text);
		this.hbox.setVisible(true);
	}
}