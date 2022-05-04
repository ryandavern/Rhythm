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

package com.beatplaylist.utilities.control.skin;

import com.beatplaylist.utilities.control.Tooltip;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;

public class TooltipSkin implements Skin<Tooltip> {

	/***************************************************************************
	 * * Private fields * *
	 **************************************************************************/

	private Label tipLabel;

	private Tooltip tooltip;

	/***************************************************************************
	 * * Constructors * *
	 **************************************************************************/

	/**
	 * Creates a new TooltipSkin instance for the given {@link Tooltip}.
	 * 
	 * @param t
	 *            the tooltip
	 */
	public TooltipSkin(Tooltip t) {
		this.tooltip = t;
		tipLabel = new Label();
		tipLabel.contentDisplayProperty().bind(t.contentDisplayProperty());
		tipLabel.fontProperty().bind(t.fontProperty());
		tipLabel.graphicProperty().bind(t.graphicProperty());
		tipLabel.graphicTextGapProperty().bind(t.graphicTextGapProperty());
		tipLabel.textAlignmentProperty().bind(t.textAlignmentProperty());
		tipLabel.textOverrunProperty().bind(t.textOverrunProperty());
		tipLabel.textProperty().bind(t.textProperty());
		tipLabel.wrapTextProperty().bind(t.wrapTextProperty());
		tipLabel.minWidthProperty().bind(t.minWidthProperty());
		tipLabel.prefWidthProperty().bind(t.prefWidthProperty());
		tipLabel.maxWidthProperty().bind(t.maxWidthProperty());
		tipLabel.minHeightProperty().bind(t.minHeightProperty());
		tipLabel.prefHeightProperty().bind(t.prefHeightProperty());
		tipLabel.maxHeightProperty().bind(t.maxHeightProperty());

		// RT-7512 - skin needs to have styleClass of the control
		// TODO - This needs to be bound together, not just set! Probably should
		// do the same for id and style as well.
		tipLabel.getStyleClass().setAll(t.getStyleClass());
		tipLabel.setStyle(t.getStyle());
		tipLabel.setId(t.getId());
	}

	/***************************************************************************
	 * * Public API * *
	 **************************************************************************/

	/** {@inheritDoc} */
	@Override
	public Tooltip getSkinnable() {
		return tooltip;
	}

	/** {@inheritDoc} */
	@Override
	public Node getNode() {
		return tipLabel;
	}

	/** {@inheritDoc} */
	@Override
	public void dispose() {
		tooltip = null;
		tipLabel = null;
	}
}