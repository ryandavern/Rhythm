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

import com.beatplaylist.utilities.CustomColor;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.Clipboard;

public class TextField extends javafx.scene.control.TextField {

	Clipboard systemClipboard = Clipboard.getSystemClipboard();

	public TextField() {

		this.setContextMenu(getContext());

	}

	public TextField(String text) {
		this.setText(text);
	}

	public ContextMenu getContext() {
		ContextMenu context_menu = new ContextMenu();
		context_menu.setStyle("-fx-cursor: hand; -fx-background-color: " + CustomColor.DROPDOWN_MENU_COLOR.getColorHex() + ";");

		ContextItem copy_item = new ContextItem("Copy"), cut_item = new ContextItem("Cut"), paste_item = new ContextItem("Paste"), select_all_item = new ContextItem("Select All");

		copy_item.setVisible(false);
		copy_item.setDisable(true);
		cut_item.setVisible(false);
		cut_item.setDisable(true);

		this.selectedTextProperty().addListener((final ObservableValue<? extends String> ov, final String oldSelection, final String newSelection) -> {
			if (newSelection.isEmpty()) {
				copy_item.setVisible(false);
				copy_item.setDisable(true);
				cut_item.setVisible(false);
				cut_item.setDisable(true);
			} else {
				copy_item.setVisible(true);
				copy_item.setDisable(false);
				cut_item.setVisible(true);
				cut_item.setDisable(false);
			}
		});

		this.textProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (getSelectedText().isEmpty()) {
					copy_item.setVisible(false);
					copy_item.setDisable(true);
					cut_item.setVisible(false);
					cut_item.setDisable(true);
				} else {
					copy_item.setVisible(true);
					copy_item.setDisable(false);
					cut_item.setVisible(true);
					cut_item.setDisable(false);
				}
			}
		});

		copy_item.setOnAction(event -> {
			copy();
		});
		cut_item.setOnAction(event -> {
			cut();

		});
		paste_item.setOnAction(event -> {
			paste();
		});
		select_all_item.setOnAction(event -> {
			this.selectAll();
		});

		context_menu.getItems().addAll(copy_item, cut_item, paste_item, select_all_item);
		return context_menu;
	}

}
