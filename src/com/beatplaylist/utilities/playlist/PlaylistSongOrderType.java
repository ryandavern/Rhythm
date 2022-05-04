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

public enum PlaylistSongOrderType {

	ARTIST_AZ("Artist (A-Z)"), // Order by artist.
	ARTIST_ZA("Artist (Z-A)"), //
	SONG_NAME_AZ("Song Name (A-Z)"), // Order playlist by song name (A-Z).
	SONG_NAME_ZA("Song Name (Z-A)"), // Order playlist by song name (Z-A).
	SONG_INDEX("Song Add Order (Old to New)"), // Order playlist by the song add order
	SONG_INDEX_REVERSE("Song Add Order (New to Old)"); //

	String name;

	PlaylistSongOrderType(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public static PlaylistSongOrderType getOrderByName(String value) {
		PlaylistSongOrderType type = PlaylistSongOrderType.valueOf(value.toUpperCase());
		return type;
	}
}