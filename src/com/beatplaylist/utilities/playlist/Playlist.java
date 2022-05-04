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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.beatplaylist.gui.utilities.playlist.SongOrder;

import javafx.beans.property.SimpleIntegerProperty;

public class Playlist {

	// Playlist Details
	private String playlistName = "", playlistDescription = "", playlistCreationDate = "", playlistVisibility = "", playlistURL = "", playlistFeatureType = "NONE";

	// Playlist Creator Details
	private String playlistCreatorUsername = "", playlistCreatorDisplayName = "";

	private List<Song> playlistSongs = new ArrayList<>();
	private List<String> genres = new ArrayList<>(), tags = new ArrayList<>();
	private boolean following = false, syncToYouTube = false, isQueuePlaylist = false, isSpecialPlaylist = false;
	private int position = 0, loaded_songs = 0, follower_count = 0;

	// songCount is displayed on the playlist_page class.
	public SimpleIntegerProperty songCount;

	// Stores user role in playlist.
	private RoleType role = RoleType.LISTEN;

	public Playlist() {
		this.songCount = new SimpleIntegerProperty();
	}

	public void resetTags(boolean isGenre) {
		if (isGenre)
			this.genres.clear();
		else
			this.tags.clear();
	}

	public void addTag(String tag, boolean isGenre) {
		if (isGenre) {
			this.genres.add(tag.toUpperCase());
		} else {
			this.tags.add(tag.toUpperCase());
		}
	}

	public List<String> getGenres() {
		return this.genres;
	}

	public List<String> getTags() {
		return this.tags;
	}

	// Called in the PlaylistLoader class.
	public boolean containsSongWithURL(String url) {
		for (Song song : this.getSongs()) {
			if (song.getURL().equals(url))
				return true;
		}
		return false;
	}

	public void setFeatureType(String value) {
		this.playlistFeatureType = value.toUpperCase();
	}

	public void setSpecialPlaylist(boolean value) {
		this.isSpecialPlaylist = value;
	}

	public boolean isSpecialPlaylist() {
		return this.isSpecialPlaylist;
	}

	public String getFeatureType() {
		return this.playlistFeatureType;
	}

	public void setIsQueue(boolean value) {
		this.isQueuePlaylist = value;
	}

	public boolean isQueuePlaylist() {
		return this.isQueuePlaylist;
	}

	public void setSyncToYouTube(boolean value) {
		this.syncToYouTube = value;
	}

	public boolean isSyncedToYouTube() {
		return this.syncToYouTube;
	}

	public void setFollowerCount(int value) {
		this.follower_count = value;
	}

	public int getFollowerCount() {
		if (this.follower_count <= 0)
			return 1;
		return this.follower_count;
	}

	public void setLoadedSongs(int value) {
		this.loaded_songs = value;
	}

	public int getLoadedSongs() {
		return this.loaded_songs;
	}

	public void setFollowing(boolean value) {
		this.following = value;
	}

	public boolean isFollowing() {
		return this.following;
	}

	public void setRole(RoleType role) {
		this.role = role;
	}

	public RoleType getRole() {
		if (this.role == null)
			return RoleType.LISTEN;
		return this.role;
	}

	public void setVisibility(String value) {
		this.playlistVisibility = value;
	}

	public String getVisibility() {
		return this.playlistVisibility;
	}

	public void setURL(String value) {
		this.playlistURL = value;
	}

	public String getURL() {
		return this.playlistURL;
	}

	public void setCreatorUsername(String value) {
		this.playlistCreatorUsername = value;
	}

	public String getCreatorUsername() {
		return this.playlistCreatorUsername;
	}

	public void setCreatorDisplayName(String value) {
		this.playlistCreatorDisplayName = value;
	}

	public String getCreatorDisplayName() {
		return this.playlistCreatorDisplayName;
	}

	public void setName(String value) {
		this.playlistName = value.replace("&#44;", ",").replace("\\u2019s", "'").replace("\\u2022", ".");
	}

	public String getName() {
		return this.playlistName;
	}

	public void setDescription(String value) {
		this.playlistDescription = value;
	}

	public String getDescription() {
		return this.playlistDescription;
	}

	public void setCreationDate(String value) {
		this.playlistCreationDate = value;
	}

	public String getCreationDate() {
		return this.playlistCreationDate;
	}

	public Song getPreviousSong() {
		return this.getSongs().get(this.getPosition() - 1);
	}

	public Song getNextSong() {
		return this.getSongs().get(this.getPosition() + 1);
	}

	public int getSongCount() {
		if (this.isQueuePlaylist())
			return PlaylistManager.getInstance().queue_list.size();
		if (this.songCount.get() > 0)
			return this.songCount.get();
		else
			return this.playlistSongs.size();
	}

	public void setSongCount(int value) {
		this.songCount.set(value);
	}

	public List<Song> getSongs() {
		if (this.isQueuePlaylist())
			return PlaylistManager.getInstance().queue_list;
		return this.playlistSongs;
	}

	public void setPosition(int value) {
		this.position = value;
	}

	public void incrementPosition() {
		this.position++;
	}

	public void resetPosition() {
		this.position = 0;
	}

	public void decrementPosition() {
		this.position--;
	}

	public int getPosition() {
		return this.position;
	}

	public int getSongPositionInPlaylist(Song song) {
		for (int i = 0; i < this.getSongs().size(); i++) {
			if (this.getSongs().get(i).getFullSongTitle().equals(song.getFullSongTitle()))
				return i;
		}
		return 0;
	}

	public void addSong(Song song, boolean sort) {
		this.playlistSongs.add(song);
		this.songCount.set(this.songCount.get() + 1);
		if (sort)
			attemptSort();
	}

	public void removeSong(Song song, boolean sort) {
		this.playlistSongs.remove(song);
		this.songCount.set(this.songCount.get() - 1);
		if (sort)
			attemptSort();
	}

	public void attemptSort() {
		if (!getFeatureType().equals("NONE"))
			return;
		if (PlaylistManager.getInstance().getPlaylistOrder(this.getURL()) == PlaylistSongOrderType.SONG_NAME_AZ)
			SongOrder.sort(this);
		if (PlaylistManager.getInstance().getPlaylistOrder(this.getURL()) == PlaylistSongOrderType.SONG_NAME_ZA)
			SongOrder.sortByTitle(this, true);
		if (PlaylistManager.getInstance().getPlaylistOrder(this.getURL()) == PlaylistSongOrderType.SONG_INDEX_REVERSE)
			Collections.reverse(this.getSongs());
		if (PlaylistManager.getInstance().getPlaylistOrder(this.getURL()) == PlaylistSongOrderType.ARTIST_AZ)
			SongOrder.sortByArtist(this, false);
		if (PlaylistManager.getInstance().getPlaylistOrder(this.getURL()) == PlaylistSongOrderType.ARTIST_ZA)
			SongOrder.sortByArtist(this, true);
	}

	public void shuffle() {
		Collections.shuffle(this.playlistSongs);
	}

	public void removeSongByURL(Song song, boolean sort) {
		Song song_to_remove = null;
		for (Song songs : this.getSongs()) {
			if (songs.getURL().equals(song.getURL()))
				song_to_remove = songs;
		}
		this.playlistSongs.remove(song_to_remove);
		this.songCount.set(this.songCount.get() - 1);
		if (sort)
			attemptSort();
	}

	public void clearSongs() {
		this.playlistSongs.clear();
		this.songCount.set(0);
	}

	public List<Song> getRangedSongs(int min, int max, boolean ordered) {
		List<Song> temp = new ArrayList<>();
		int index = 0;
		for (Song song : this.playlistSongs) {
			if (index >= min && index <= max) {
				for (int i = 0; i < 50; i++) {
					temp.add(song);
				}
			}
			for (int i = 0; i < 50; i++)
				index++;
		}
		if (ordered)
			SongOrder.sortByTitleAZ(temp);
		else
			SongOrder.sortByTitleZA(temp);

		return temp;
	}
}