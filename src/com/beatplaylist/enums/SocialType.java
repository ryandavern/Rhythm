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

package com.beatplaylist.enums;

public enum SocialType {

	TWITTER("Twitter", "twitter_icon", "https://www.twitter.com/", 20), //
	INSTAGRAM("Instagram", "instagram_icon", "https://www.instagram.com/", 30), //
	SNAPCHAT("Snapchat", "snapchat_icon", "https://www.snapchat.com/add/", 30), //
	YOUTUBE("YouTube", "youtube_icon", "https://www.youtube.com/user/", 50), //
	FACEBOOK("Facebook", "facebook_icon", "https://www.facebook.com/", 50), //
	DISCORD("Discord", "discord_icon", "", 20), //
	SOUNDCLOUD("Soundcloud", "soundcloud_icon", "https://www.soundcloud.com/", 35), //
	SPOTIFY("Spotify", "spotify_icon", "https://open.spotify.com/user/", 35), //
	LINKEDIN("LinkedIn", "linkedin_icon", "https://www.linkedin.com/in/", 35), //
	SKYPE("Skype", "skype_icon", "", 35);

	private String friendly_name, image_url, website_starter_url;
	private int character_limit;

	SocialType(String friendly_name, String image_url, String website_starter_url, int character_limit) {
		this.friendly_name = friendly_name;
		this.image_url = image_url;
		this.website_starter_url = website_starter_url;
		this.character_limit = character_limit;
	}

	public String getFriendlyName() {
		return this.friendly_name;
	}

	public String getImageURL() {
		return this.image_url;
	}

	public String getWebsiteStarterURL() {
		return this.website_starter_url;
	}

	public int getCharacterLimit() {
		return this.character_limit;
	}
}