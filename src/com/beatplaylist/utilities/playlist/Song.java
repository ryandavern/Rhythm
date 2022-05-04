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

import com.beatplaylist.gui.utilities.playlist.SongTitle;
import com.beatplaylist.settings.Settings;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.validation.ValidateManager;

import javafx.beans.property.SimpleStringProperty;

public class Song {

	public String fullSongTitle = "", url = "", date_added = "";
	public boolean isRemix = false, hasLiked = false;
	public SimpleStringProperty songName, songArtist, songFeaturing;
	public int startTime = 0, endTime = 0;

	public Song() {
		this.songName = new SimpleStringProperty();
		this.songArtist = new SimpleStringProperty();
		this.songFeaturing = new SimpleStringProperty();
	}

	public void setSong(String songTitle) {
		this.fullSongTitle = songTitle;
		JSONWrapper songInformation = new JSONWrapper(SongTitle.getSongInformationFromTitle(songTitle));
		String songName = songInformation.getJSONString("song_name");

		if (songInformation.getJSONBoolean("isRemix"))
			this.songName.set((songName.length() > 30) ? songName.substring(0, 30) : songName);
		else
			this.songName.set((songName.length() > 30) ? songName.substring(0, 30) + "..." : songName);

		if (songInformation.keyExists("artist")) {
			String artistName = songInformation.getJSONString("artist");
			this.songArtist.set((artistName.length() > 30) ? artistName.substring(0, 30) + "..." : artistName);
		} else
			this.songArtist.set("");

		if (songInformation.keyExists("featuring")) {
			String featuring = songInformation.getJSONString("featuring");
			this.songFeaturing.set((featuring.length() > 30) ? featuring.substring(0, 30) + "..." : featuring);
		} else
			this.songFeaturing.set("");

		this.isRemix = songInformation.getJSONBoolean("isRemix");
	}

	public void setLiked(boolean value) {
		this.hasLiked = value;
	}

	public boolean hasLiked() {
		return this.hasLiked;
	}

	public void setFullSongTitle(String value) {
		setSong(value);
	}

	public void setURL(String value) {
		this.url = value;
	}

	public SimpleStringProperty getSongName() {
		return this.songName;
	}

	public SimpleStringProperty getArtist() {
		return this.songArtist;
	}

	public SimpleStringProperty getFeaturing() {
		return this.songFeaturing;
	}

	public void setStartTime(String value) {
		if (!ValidateManager.isNumeric(value) || value == "null" || value == null)
			this.startTime = 0;
		else
			this.startTime = Integer.valueOf(value);
	}

	public void setEndTime(String value) {
		if (!ValidateManager.isNumeric(value) || value == "null" || value == null)
			this.endTime = 0;
		else
			this.endTime = Integer.valueOf(value);
	}

	public void setDateAdded(String value) {
		this.date_added = value;
	}

	public String getFullSongTitle() {
		return this.fullSongTitle;
	}

	public String getURL() {
		return this.url;
	}

	public int getStartTime() {
		return this.startTime;
	}

	public int getEndTime() {
		return this.endTime;
	}

	public String getDateAdded() {
		return this.date_added;
	}

	// Minimum end time is 20 seconds
	public boolean hasReachedSongEndTime(double currentTimeInVideo, double currentVideoTotalDuration) {
		// If there is no end time set return false.
		if (this.endTime <= 0) {
			return false;
		}

		int endTime = Integer.valueOf(this.getEndTime());

		// If user has cross fade enabled.
		if (Settings.getInstance().crossFadeSongEnabled()) {
			endTime -= Settings.getInstance().getCrossFadeLength();
		}
		if (endTime >= 20 && currentTimeInVideo >= endTime && currentVideoTotalDuration > 45) {
			return true;
		}
		return false;
	}
}