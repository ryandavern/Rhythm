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

package com.beatplaylist.utilities.data;

public class StoredURL {

	private static StoredURL instance = new StoredURL();

	public static StoredURL getInstance() {
		return instance;
	}

	// Stores url of page to stop duplicate javascript injections
	public String lastInjectedURL = "", lastSearchInjectedURL = "";
	
	// Stores url of last loaded video page to determine if artist profile picture has already loaded
	public String lastArtistImageURL = "";

	// Stores current loaded artist profile picture
	public String currentArtistProfileImageURL = "";
	
	// Stores current YouTube channel url
	public String uniqueChannelID = "";
	
	// Stores current loaded search page url
	public String currentSearchURL = "";
	
	// Stores last url that isn't a ?v= url
	public String lastBrowseURL = "";
}