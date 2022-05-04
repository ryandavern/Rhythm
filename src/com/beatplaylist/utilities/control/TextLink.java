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

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.beatplaylist.settings.Settings;
import com.beatplaylist.utilities.CustomColor;

import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class TextLink extends Text {

	public TextLink() {
		setDefaults();
	}

	public TextLink(String text) {
		this.setText(text);
		setDefaults();
	}

	private void setDefaults() {
		this.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		this.setStyle("-fx-cursor: hand;");
		this.setOnMouseEntered(event -> {
			this.setFill(Color.web(Settings.getInstance().getDefaultColor()));
			this.setStyle("-fx-cursor: hand;");
		});
		this.setOnMouseExited(event -> {
			this.setFill(Color.web(CustomColor.WHITE.getColorHex()));
			this.setStyle("-fx-cursor: hand;");
		});
	}

	public void setURLOnClick(String url) {
		this.setOnMouseClicked(event -> {
			try {
				Desktop.getDesktop().browse(new URI(url));
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		});
	}
}