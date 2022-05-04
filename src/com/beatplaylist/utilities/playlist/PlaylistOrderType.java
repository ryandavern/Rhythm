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

package com.beatplaylist.utilities.playlist;

public enum PlaylistOrderType {

	PLAYLIST_NAME_AZ("Playlist Name (A-Z)"), // Order playlist by song name (A-Z).
	PLAYLIST_NAME_ZA("Playlist Name (Z-A)"), // Order playlist by song name (Z-A).
	PLAYLIST_INDEX("Playlist Add Order (Old to New)"), // Order playlist by the song add order
	PLAYLIST_INDEX_REVERSE("Playlist Add Order (New to Old)"); //

	private String name;

	PlaylistOrderType(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public static PlaylistOrderType getOrderByName(String value) {
		PlaylistOrderType type = PlaylistOrderType.valueOf(value.toUpperCase());
		if (type == null)
			return PLAYLIST_INDEX_REVERSE;
		return type;
	}
}