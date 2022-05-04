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

import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class SpacedText extends FlowPane {
	private Font font;
	private Color fill;

	public SpacedText(String s, double spacing) {
		setText(s);
		setHgap(spacing);
	}

	public void setText(String s) {
		getChildren().clear();
		for (int i = 0; i < s.length(); i++) {
			getChildren().add(new Text("" + s.charAt(i)));
		}
		setFont(this.font);
		setFill(this.fill);
	}

	public void setFont(Font font) {
		if (font != null) {
			this.font = font;
			for (Node t : getChildren()) {
				((Text) t).setFont(font);
			}
		}
	}

	public void setFill(Color fill) {
		if (fill != null) {
			this.fill = fill;
			for (Node t : getChildren()) {
				((Text) t).setFill(fill);
			}
		}
	}
}