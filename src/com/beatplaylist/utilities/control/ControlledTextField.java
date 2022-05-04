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
import com.beatplaylist.utilities.validation.ValidateManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ControlledTextField extends StackPane {

	public TextField textField;
	public Text charactersRemainingText;
	private int maxCharacters = 0;

	public ControlledTextField(int maxCharacters) {
		init(maxCharacters, false);
	}

	public ControlledTextField(int maxCharacters, boolean isNumeric) {
		init(maxCharacters, isNumeric);
	}

	private void init(int maxCharacters, boolean isNumeric) {
		this.maxCharacters = maxCharacters;
		this.textField = new TextField();
		this.charactersRemainingText = new Text(String.valueOf(maxCharacters));

		this.charactersRemainingText.setFont(Font.font(FontType.DEFAULT.getName(), 14));
		this.charactersRemainingText.setFill(Color.web(CustomColor.RHYTHM.getColorHex()));
		this.textField.setPadding(new Insets(5, 30, 5, 5));

		super.getChildren().addAll(this.textField, this.charactersRemainingText);
		super.maxWidthProperty().bind(this.textField.widthProperty());

		StackPane.setAlignment(this.charactersRemainingText, Pos.CENTER_RIGHT);
		StackPane.setMargin(this.charactersRemainingText, new Insets(2, 5, 0, 0));

		Color red = Color.web(CustomColor.RED.getColorHex()), green = Color.web(CustomColor.RHYTHM.getColorHex());

		this.textField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (this.textField.getText().length() >= maxCharacters) {
				String value = newValue.substring(0, maxCharacters);
				setText(value);
			}
			// If character count is 85% or more of total max characters, change color to red.
			if (this.textField.getText().length() > (maxCharacters * 0.85)) {
				this.charactersRemainingText.setFill(red);
			} else {
				this.charactersRemainingText.setFill(green);
			}
			this.charactersRemainingText.setText(String.valueOf(this.maxCharacters - this.textField.getText().length()));
		});
		if (isNumeric) {
			this.textField.textProperty().addListener((observable, oldValue, newValue) -> {
				if (!ValidateManager.isNumeric(this.textField.getText()) || this.textField.getText().length() > 5) {
					this.textField.setText(oldValue);
					return;
				}
			});
		}
	}

	public void setText(String text) {
		this.textField.setText(text);
		this.charactersRemainingText.setText(String.valueOf(this.maxCharacters - text.length()));
	}

	public String getText() {
		return this.textField.getText();
	}

	public TextField getTextField() {
		return this.textField;
	}

	public int getMaxCharacters() {
		return this.maxCharacters;
	}
}