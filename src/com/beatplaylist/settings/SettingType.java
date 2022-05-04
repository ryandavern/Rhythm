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

public enum SettingType {

	DEFAULT_PROFILE_COLOR("default_profile_color", "#2ECC71"), //
	MUSIC_DIRECTORY("music_directory", "C:\\Users\\" + System.getProperty("user.name") + "\\Desktop"), //
	DISPLAY_MUSIC("display_music", "true"), //
	SHOW_VIDEO_ADVERTS("show_video_adverts", "false"), //
	CLEAN_SONG_NAMES("clean_song_names", "true"), //
	CHECKED_UPDATE_VERSION("checked_update_version", "none"), //
	LAST_ACCOUNT_KEY("last_account_key", ""), //
	REMOVE_BRACKET("remove_bracket", "true"), //
	NOTIFICATION("notification", "false"), //
	UNREAD_MESSAGE("unread_message", "false"), //
	OVERLAY_ENABLED("overlay_enabled", "true"), //
	ACCESS_KEY("accessKey", ""), //
	LAST_ANNOUNCEMENT("lastAnnouncement", ""), //
	ADVANCED_SONG_FORMAT("advancedSongFormat", "true"), //
	PLAYLIST_ORDER("playlist_order", "AZ"), //
	LOUDNESS_EQUALIZATION("loudness_equalization", "true"), //
	LAUNCH_MINIMIZED("launchMinimized", "true"), //
	CROSSFADE_SONGS("crossFadeSongs", "false"), //
	CROSSFADE_LENGTH("crossFadeLength", "5"), //
	SHUFFLE_TYPE("shuffleType", "PLAYLIST_SORT"), //
	HARDWARE_ACCELERATION("hardwareAcceleration", "false"), //
	MEDIA_KEYS("mediaKeysEnabled", "false"), //
	LAST_VOLUME("lastVolume", "0.4"), //
	HIDPI("hidpi", "true");

	private String setting_name, default_value;

	SettingType(String setting_name, String default_value) {
		this.setting_name = setting_name;
		this.default_value = default_value;
	}

	public String getSettingName() {
		return this.setting_name;
	}

	public String getDefaultValue() {
		return this.default_value;
	}
}