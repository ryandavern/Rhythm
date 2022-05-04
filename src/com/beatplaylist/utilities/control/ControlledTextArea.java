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
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ControlledTextArea extends StackPane {

	public TextArea textArea;
	public Text charactersRemainingText;
	private int maxCharacters = 0;

	public ControlledTextArea(int maxCharacters) {
		this.maxCharacters = maxCharacters;
		this.textArea = new TextArea();
		this.charactersRemainingText = new Text(String.valueOf(maxCharacters));

		this.charactersRemainingText.setFont(Font.font(FontType.DEFAULT.getName(), 14));
		this.charactersRemainingText.setFill(Color.web(CustomColor.RHYTHM.getColorHex()));
		this.textArea.setPadding(new Insets(0, 30, 0, 0));

		super.getChildren().addAll(this.textArea, this.charactersRemainingText);
		super.maxWidthProperty().bind(this.textArea.widthProperty());

		StackPane.setAlignment(this.charactersRemainingText, Pos.TOP_RIGHT);
		StackPane.setMargin(this.charactersRemainingText, new Insets(2, 5, 0, 0));

		Color red = Color.web(CustomColor.RED.getColorHex()), green = Color.web(CustomColor.RHYTHM.getColorHex());

		this.textArea.textProperty().addListener((observable, oldValue, newValue) -> {
			if (this.textArea.getText().length() >= maxCharacters) {
				String value = newValue.substring(0, maxCharacters);
				setText(value);
			}
			// If character count is 85% or more of total max characters, change color to red.
			if (this.textArea.getText().length() > (maxCharacters * 0.85)) {
				this.charactersRemainingText.setFill(red);
			} else {
				this.charactersRemainingText.setFill(green);
			}
			this.charactersRemainingText.setText(String.valueOf(this.maxCharacters - this.textArea.getText().length()));
		});
	}

	public void setText(String text) {
		this.textArea.setText(text);
		this.charactersRemainingText.setText(String.valueOf(this.maxCharacters - text.length()));
	}

	public String getText() {
		return this.textArea.getText();
	}

	public TextArea getTextArea() {
		return this.textArea;
	}

	public int getMaxCharacters() {
		return this.maxCharacters;
	}
}