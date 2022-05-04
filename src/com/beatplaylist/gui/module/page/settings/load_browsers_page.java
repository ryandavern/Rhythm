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

package com.beatplaylist.gui.module.page.settings;

import com.beatplaylist.enums.FontType;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.utilities.CustomColor;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class load_browsers_page {

	public VBox vbox;
	public Text text;

	public load_browsers_page(boolean firstLoad) {
		this.vbox = new VBox();
		if (firstLoad)
			this.text = new Text("We are initializing the Chromium browsers for the first time. This process should take less than 30 seconds! Please wait...");
		else
			this.text = new Text("Loading Music Library...");

		this.vbox.setLayoutX(20);
		this.vbox.setLayoutY(20);
		this.vbox.setStyle("-fx-background-color: " + CustomColor.RHYTHM.getColorHex() + "; -fx-padding: 10px; -fx-background-radius: 15px;");

		this.text.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 24));
		this.text.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		this.text.setWrappingWidth(700);

		this.vbox.getChildren().add(this.text);
		GUIManager.getInstance().contentManager.contentPane.getChildren().add(this.vbox);

		GUIManager.getInstance().sideBar.sideBarTab.disableTabs();
		GUIManager.getInstance().sideBar.profileMenu.disableProfileMenu();

		GUIManager.getInstance().initEngine(true);
	}
}