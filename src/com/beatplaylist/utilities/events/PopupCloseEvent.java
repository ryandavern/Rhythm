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

package com.beatplaylist.utilities.events;

import com.beatplaylist.chromium.BrowserManager;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.module.layout.sidebar.TabType;
import com.beatplaylist.utilities.popup.PopupManager;

import javafx.scene.Node;

public class PopupCloseEvent {

	public PopupCloseEvent() {
		for (Node child : GUIManager.getInstance().backgroundPane.getChildren()) {
			child.setOpacity(1);
			child.setEffect(null);
		}

		if (PopupManager.getInstance().activePopup.size() > 0 && PopupManager.getInstance().activePopup.get(0) != null)
			PopupManager.getInstance().activePopup.get(0).add();

		if (GUIManager.getInstance().audioBar.nowPlayingButton.isDisabled()) {
			GUIManager.getInstance().audioBar.nowPlayingButton.setOpacity(0.5);
		}
		if (GUIManager.getInstance().currentTab.tab == TabType.NOW_PLAYING) {
			BrowserManager.getInstance().getCurrentBrowser().web_view.setVisible(true);
		}
		if (GUIManager.getInstance().currentTab.tab == TabType.BROWSE) {
			GUIManager.getInstance().searchBrowser.web_view.setVisible(true);
		}
		GUIManager.getInstance().topBar.titleBar.searchTextfield.setDisable(false);
	}
}