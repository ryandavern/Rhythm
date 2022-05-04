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

package com.beatplaylist.gui.utilities;

import com.beatplaylist.enums.PaddingSide;
import com.beatplaylist.utilities.CustomColor;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class FXUtilities {

	public static void copyToClipboard(String text) {
		Clipboard clipboard = Clipboard.getSystemClipboard();
		ClipboardContent content = new ClipboardContent();
		content.putString(text);
		clipboard.setContent(content);
	}

	public static void setTextDefault(Text text, Font font, Color color) {
		text.setFont(font);
		text.setFill(color);
	}

	public static void setTextDefault(Text text, Font font, CustomColor color) {
		text.setFont(font);
		text.setFill(Color.web(color.getColorHex()));
	}

	public static void setTextDefault(Text text, Font font, CustomColor color, Cursor cursor) {
		text.setFont(font);
		text.setFill(Color.web(color.getColorHex()));
		text.setCursor(cursor);
	}

	public static void setNodePadding(Node node, double paddingAmount) {
		node.setStyle("-fx-padding: " + paddingAmount + "px;");
	}

	public static void setNodePadding(Node node, double paddingAmount, PaddingSide paddingSide) {
		double topPadding = 0, rightPadding = 0, bottomPadding = 0, leftPadding = 0;

		if (paddingSide == PaddingSide.TOP) {
			topPadding += paddingAmount;
		} else if (paddingSide == PaddingSide.RIGHT) {
			rightPadding += paddingAmount;
		} else if (paddingSide == PaddingSide.BOTTOM) {
			bottomPadding += paddingAmount;
		} else {
			leftPadding += paddingAmount;
		}
		node.setStyle("-fx-padding: " + topPadding + " " + rightPadding + " " + bottomPadding + " " + leftPadding + ";");
	}

	public static void setNodePadding(Node node, double paddingAmount, PaddingSide paddingSide, String extraStyle) {
		double topPadding = 0, rightPadding = 0, bottomPadding = 0, leftPadding = 0;

		if (paddingSide == PaddingSide.TOP) {
			topPadding += paddingAmount;
		} else if (paddingSide == PaddingSide.RIGHT) {
			rightPadding += paddingAmount;
		} else if (paddingSide == PaddingSide.BOTTOM) {
			bottomPadding += paddingAmount;
		} else {
			leftPadding += paddingAmount;
		}
		node.setStyle("-fx-padding: " + topPadding + " " + rightPadding + " " + bottomPadding + " " + leftPadding + "; " + extraStyle);
	}

	public static void setNodePadding(Control node, double paddingAmount, PaddingSide paddingSide, boolean addToPadding) {
		double topPadding = node.getPadding().getTop(), rightPadding = node.getPadding().getRight(), bottomPadding = node.getPadding().getBottom(), leftPadding = node.getPadding().getLeft();

		if (paddingSide == PaddingSide.TOP) {
			topPadding += paddingAmount;
		} else if (paddingSide == PaddingSide.RIGHT) {
			rightPadding += paddingAmount;
		} else if (paddingSide == PaddingSide.BOTTOM) {
			bottomPadding += paddingAmount;
		} else {
			leftPadding += paddingAmount;
		}

		node.setPadding(new Insets(topPadding, rightPadding, bottomPadding, leftPadding));

	}
}