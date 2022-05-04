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

package com.beatplaylist.utilities.user;

import com.beatplaylist.settings.SocialType;

public class SocialAccount {

	private SocialType social_type;
	private String username;
	private boolean verified, is_visible_on_profile;

	public SocialAccount(String username, SocialType social_type, boolean verified, boolean is_visible_on_profile) {
		this.social_type = social_type;
		this.username = username;
		this.verified = verified;
		this.is_visible_on_profile = is_visible_on_profile;
	}

	public void setIsVisibleOnProfile(boolean value) {
		this.is_visible_on_profile = value;
	}

	public String getUsername() {
		return username;
	}

	public SocialType getSocialType() {
		return this.social_type;
	}

	public boolean isVerified() {
		return this.verified;
	}

	public boolean isVisibleOnProfile() {
		return this.is_visible_on_profile;
	}
}