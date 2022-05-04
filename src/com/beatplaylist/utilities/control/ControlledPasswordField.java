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
import javafx.scene.control.PasswordField;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ControlledPasswordField extends StackPane {

	public PasswordField textField;
	public Text charactersRemainingText;
	private int minCharacters = 0, maxCharacters = 0;

	public ControlledPasswordField(int minCharacters, int maxCharacters) {
		this.minCharacters = minCharacters;
		this.maxCharacters = maxCharacters;
		this.textField = new PasswordField();
		this.charactersRemainingText = new Text(String.valueOf("0/" + maxCharacters));

		this.charactersRemainingText.setFont(Font.font(FontType.DEFAULT.getName(), 14));
		this.charactersRemainingText.setFill(Color.web(CustomColor.RED.getColorHex()));

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
			if (this.textField.getText().length() >= minCharacters) {
				this.charactersRemainingText.setFill(green);
			} else {
				this.charactersRemainingText.setFill(red);
			}
			this.charactersRemainingText.setText(String.valueOf(this.textField.getText().length() + "/" + this.minCharacters));
		});
	}

	public void setText(String text) {
		this.textField.setText(text);
		this.charactersRemainingText.setText(String.valueOf(this.minCharacters - text.length()));
	}

	public String getText() {
		return this.textField.getText();
	}

	public PasswordField getPasswordField() {
		return this.textField;
	}

	public int getMinCharacters() {
		return this.minCharacters;
	}
	
	public int getMaxCharacters() {
		return this.maxCharacters;
	}
}