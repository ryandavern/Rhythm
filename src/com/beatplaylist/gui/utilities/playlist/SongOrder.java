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

package com.beatplaylist.gui.utilities.playlist;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.module.layout.sidebar.TabType;
import com.beatplaylist.utilities.data.StoredURL;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.playlist.Playlist;
import com.beatplaylist.utilities.playlist.Song;

public class SongOrder {

	public static void loadCurrentPlayingArtistPage() {
		if (StoredURL.getInstance().uniqueChannelID.isEmpty())
			return;
		GUIManager.getInstance().searchBrowser.getWebEngine().navigation().loadUrl("https://www.youtube.com/channel/" + StoredURL.getInstance().uniqueChannelID);
		GUIManager.getInstance().sideBar.sideBarTab.changeTab(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.BROWSE), "CHANNEL");
	}

	public static void sort(Playlist playlist) {
		sortByTitle(playlist, false);
	}

	// Sort playlist songs by artist.
	// If reverse is false, it will sort by artist A-Z
	// If reverse is true, it will sort by artist Z-A
	public static void sortByArtist(Playlist playlist, boolean reverse) {
		if (reverse) {
			Collections.sort(playlist.getSongs(), new Comparator<Song>() {
				public int compare(Song o1, Song o2) {
					Song first = o1;
					Song second = o2;

					JSONWrapper firstSongObject = new JSONWrapper(SongTitle.getSongInformationFromTitle(first.getFullSongTitle()));
					JSONWrapper secondSongObject = new JSONWrapper(SongTitle.getSongInformationFromTitle(second.getFullSongTitle()));

					String firstSongArtist = firstSongObject.getJSONString("artist");
					String firstSongName = firstSongObject.getJSONString("song_name");

					String secondSongArtist = secondSongObject.getJSONString("artist");
					String secondSongName = secondSongObject.getJSONString("song_name");

					if (firstSongArtist.equals("null"))
						return 1;
					if (!firstSongArtist.equals("null") && secondSongArtist.equals("null"))
						return -1;
					int compareArtist = secondSongArtist.compareToIgnoreCase(firstSongArtist);
					if (compareArtist != 0)
						return compareArtist;

					int compareSongName = firstSongName.compareToIgnoreCase(secondSongName);

					return compareSongName;
				}
			});
		} else {
			Collections.sort(playlist.getSongs(), new Comparator<Song>() {
				public int compare(Song o1, Song o2) {
					Song first = o1;
					Song second = o2;

					JSONWrapper firstSongObject = new JSONWrapper(SongTitle.getSongInformationFromTitle(first.getFullSongTitle()));
					JSONWrapper secondSongObject = new JSONWrapper(SongTitle.getSongInformationFromTitle(second.getFullSongTitle()));

					String firstSongArtist = firstSongObject.getJSONString("artist");
					String firstSongName = firstSongObject.getJSONString("song_name");

					String secondSongArtist = secondSongObject.getJSONString("artist");
					String secondSongName = secondSongObject.getJSONString("song_name");

					if (firstSongArtist.equals("null"))
						return 1;
					if (!firstSongArtist.equals("null") && secondSongArtist.equals("null"))
						return -1;
					int compareArtist = firstSongArtist.compareToIgnoreCase(secondSongArtist);
					if (compareArtist != 0)
						return compareArtist;

					int compareSongName = firstSongName.compareToIgnoreCase(secondSongName);

					return compareSongName;
				}
			});
		}
	}

	// Sorts songs by song name
	// If reverse is set to true the songs will be ordered from Z-A.
	// If reverse is set to false the songs will be ordered from A-Z.
	public static void sortByTitle(Playlist playlist, boolean reverse) {
		if (reverse) {
			Collections.sort(playlist.getSongs(), new Comparator<Song>() {
				public int compare(Song first, Song second) {
					String first_song_name = String.valueOf(SongTitle.getSongInformationFromTitle(first.getFullSongTitle()).get("song_name"));
					String second_song_name = String.valueOf(SongTitle.getSongInformationFromTitle(second.getFullSongTitle()).get("song_name"));
					return second_song_name.compareToIgnoreCase(first_song_name);
				}
			});
		} else {
			Collections.sort(playlist.getSongs(), new Comparator<Song>() {
				public int compare(Song first, Song second) {
					String first_song_name = String.valueOf(SongTitle.getSongInformationFromTitle(first.getFullSongTitle()).get("song_name"));
					String second_song_name = String.valueOf(SongTitle.getSongInformationFromTitle(second.getFullSongTitle()).get("song_name"));
					return first_song_name.compareToIgnoreCase(second_song_name);
				}
			});
		}
	}

	// Sort songs in list by A-Z
	public static void sortByTitleAZ(List<Song> list) {
		Collections.sort(list, new Comparator<Song>() {
			public int compare(Song first, Song second) {
				String first_song_name = String.valueOf(SongTitle.getSongInformationFromTitle(first.getFullSongTitle()).get("song_name")).toLowerCase();
				String second_song_name = String.valueOf(SongTitle.getSongInformationFromTitle(second.getFullSongTitle()).get("song_name")).toLowerCase();
				return first_song_name.compareTo(second_song_name);
			}
		});
	}

	// Sort songs in list by Z-A
	public static void sortByTitleZA(List<Song> list) {
		Collections.sort(list, new Comparator<Song>() {
			public int compare(Song first, Song second) {
				String first_song_name = String.valueOf(SongTitle.getSongInformationFromTitle(first.getFullSongTitle()).get("song_name")).toLowerCase();
				String second_song_name = String.valueOf(SongTitle.getSongInformationFromTitle(second.getFullSongTitle()).get("song_name")).toLowerCase();
				return second_song_name.compareTo(first_song_name);
			}
		});
	}
}