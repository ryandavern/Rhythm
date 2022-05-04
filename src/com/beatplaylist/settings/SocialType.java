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

package com.beatplaylist.settings;

public enum SocialType {

	TWITTER("Twitter", false), //
	YOUTUBE("YouTube", false), //
	INSTAGRAM("Instagram", true);

	private String name;
	private boolean disabled;

	SocialType(String name, boolean disabled) {
		this.name = name;
		this.disabled = disabled;
	}

	public String getName() {
		return this.name;
	}

	public boolean isDisabled() {
		return this.disabled;
	}

	public static SocialType getName(String value) {
		SocialType social_type = SocialType.valueOf(value.toUpperCase());
		return social_type;
	}
}