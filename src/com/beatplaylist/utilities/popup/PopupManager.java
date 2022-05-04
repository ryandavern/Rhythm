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

package com.beatplaylist.utilities.popup;

import java.util.ArrayList;
import java.util.List;

public class PopupManager {

	private static PopupManager instance = new PopupManager();

	public static PopupManager getInstance() {
		return instance;
	}

	public List<PopupBuilder> activePopup = new ArrayList<>();

	public void closeActivePopups() {
		for (PopupBuilder popup : activePopup)
			popup.quickCloseWithoutEvent();
		this.activePopup.clear();
	}

	public void closeActivePopupExcept(PopupBuilder dontClosePopup) {
		for (PopupBuilder popup : activePopup) {
			if (dontClosePopup != popup)
				popup.quickCloseWithoutEvent();
		}
		this.activePopup.clear();
		this.activePopup.add(dontClosePopup);
	}

	public String convertPlaylistVisibility(String value) {
		if (value.equals("Only Me"))
			return "Private";
		else if (value.equals("Anyone"))
			return "Public";
		else if (value.equals("Link Required"))
			return "Link Only";
		else
			return "Private";
	}
}
