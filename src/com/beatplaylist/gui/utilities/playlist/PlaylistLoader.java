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

import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.module.layout.sidebar.TabType;
import com.beatplaylist.gui.module.page.music.playlist_view_page;
import com.beatplaylist.utilities.CustomColor;
import com.beatplaylist.utilities.data.Data;
import com.beatplaylist.utilities.events.SocketReceiveEvent;
import com.beatplaylist.utilities.network.get.GetSongSettings;
import com.beatplaylist.utilities.network.netty.NETTYClient;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.network.serialized.PacketType;
import com.beatplaylist.utilities.network.serialized.Post;
import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;
import com.beatplaylist.utilities.playlist.Playlist;
import com.beatplaylist.utilities.playlist.PlaylistManager;
import com.beatplaylist.utilities.playlist.RoleType;
import com.beatplaylist.utilities.playlist.Song;
import com.beatplaylist.utilities.popup.Popup;

import javafx.application.Platform;

// Stores open playlist page class and loading playlist functions.
public class PlaylistLoader {

	private static PlaylistLoader instance = new PlaylistLoader();

	public static PlaylistLoader getInstance() {
		return instance;
	}

	public playlist_view_page playlistView;

	public void loadMix(Playlist playlist) {
		if (playlist.getSongs().isEmpty()) {
			GUIManager.getInstance().sideBar.sideBarTab.changeTab(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.PLAYLISTS));
			Notification.getInstance().createNotification("Mixes", "An error occurred while importing your mix.", AlertType.ERROR);
			Popup.errorAlert("Mix", "An error occurred while importing your mix.", CustomColor.RED);
			return;
		}
		if (this.playlistView == null) {
			this.playlistView = new playlist_view_page();
			this.playlistView.changePlaylist(playlist);
		} else {
			this.playlistView.changePlaylist(playlist);
		}
	}

	public void reloadCurrentPlaylist() {
		if (this.playlistView == null)
			return;
		Playlist playlist = this.playlistView.playlist;
		System.out.println(playlist.getURL());

		if (Data.getInstance().shuffle) {
			System.out.println("Shuffling");
			playlist.shuffle();
		} else {
			playlist.attemptSort();
		}
		playlist.setLoadedSongs(0);

		this.playlistView.changePlaylist(playlist);
		// playlist.setPosition(playlist.getPosition());
		PlaylistManager.getInstance().current_playlist = playlist;
	}

	public void loadCurrentPlayingPlaylist() {
		if (this.playlistView == null)
			return;
		System.out.println("Load current playing playlist");
		PlaylistManager.getInstance().current_playlist.setLoadedSongs(0);
		GUIManager.getInstance().sideBar.sideBarTab.loadMix(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.OPEN_PLAYLIST_VIEW), PlaylistManager.getInstance().current_playlist);
	}

	public void loadPlaylist(String url) {
		if (url.equals("queued-music")) {
			Playlist playlist = new Playlist();
			playlist.setIsQueue(true);
			playlist.setName("Your Queued Music");
			playlist.setURL("queued-music");
			if (playlistView == null) {
				playlistView = new playlist_view_page();
				playlistView.changePlaylist(playlist);
			} else {
				playlistView.changePlaylist(playlist);
			}
			return;
		}
		Post post = new Post();
		post.setDetails();
		post.setPacketType(PacketType.GET_PLAYLIST);

		JSONObject object = new JSONObject();
		object.put("playlist_url", JSONValue.escape(url));

		post.setJSONArray(object);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {

			@Override
			public void onSuccess(Post post) {
				Platform.runLater(() -> {
					if (post.getFailMessage().equals("RESULT EMPTY")) {
						GUIManager.getInstance().sideBar.sideBarTab.changeTab(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.PLAYLISTS));
						Notification.getInstance().createNotification("Playlist", "Could not match a playlist with the entered url! Please try again.", AlertType.ERROR);
						return;
					}
					Playlist playlist = new Playlist();

					JSONWrapper wrapper = new JSONWrapper(post.getJSONMessage());

					playlist.setName(wrapper.getJSONString("playlist_name"));
					playlist.setDescription(wrapper.getJSONString("playlist_description"));
					playlist.setCreationDate(wrapper.getJSONString("playlist_publish_date"));
					playlist.setVisibility(wrapper.getJSONString("playlist_visibility"));
					playlist.setRole(RoleType.getName(wrapper.getJSONString("playlist_role")));
					playlist.setURL(url);
					playlist.setCreatorUsername(wrapper.getJSONString("playlist_creator_username"));
					playlist.setCreatorDisplayName(wrapper.getJSONString("playlist_creator_display_name"));
					playlist.setFollowing(wrapper.getJSONBoolean("isFollowing"));
					playlist.setFeatureType(wrapper.getJSONString("playlist_feature_type"));
					playlist.setSyncToYouTube(wrapper.getJSONBoolean("playlist_sync_to_youtube"));

					if (url.equals("my-likes") || url.equals("recent-music"))
						playlist.setSpecialPlaylist(true);

					Iterator<JSONObject> iterator = wrapper.getJSONArray("songs");
					while (iterator.hasNext()) {
						JSONObject obj = (JSONObject) iterator.next();
						if (obj == null) {
							continue;
						}
						JSONWrapper songObject = new JSONWrapper(obj);

						String song_url = "https://www.youtube.com/watch?v=" + songObject.getJSONString("url");

						if (playlist.containsSongWithURL(song_url))
							continue;

						Song song = new Song();
						song.setFullSongTitle(songObject.getJSONString("title"));
						song.setURL(song_url);
						song.setDateAdded(songObject.getJSONString("date_added"));
						song.setLiked(songObject.getJSONBoolean("hasLiked"));

						playlist.addSong(song, false);
					}

					JSONArray genres = (JSONArray) wrapper.getJSONObject().get("genres"), tags = (JSONArray) wrapper.getJSONObject().get("tags");

					Iterator<String> genreIterator = genres.iterator(), tagIterator = tags.iterator();

					while (genreIterator.hasNext()) {
						String genre = genreIterator.next();
						playlist.addTag(genre, true);
					}
					while (tagIterator.hasNext()) {
						String tag = tagIterator.next();
						playlist.addTag(tag, false);
					}

					if (Data.getInstance().shuffle) {
						playlist.shuffle();
					} else {
						if (!playlist.getFeatureType().equals("YOUTUBE"))
							playlist.attemptSort();
					}

					if (playlistView == null) {
						playlistView = new playlist_view_page();
						playlistView.changePlaylist(playlist);
					} else {
						playlistView.changePlaylist(playlist);
					}

					// Get custom set song start and end times for playlist.
					GetSongSettings.send(playlist);

					// If currently playing playlist has the same url as the loaded playlist. Set the current playing playlist to the new reloaded version of the playlist.
					if (PlaylistManager.getInstance().current_playlist != null && PlaylistManager.getInstance().current_playlist.getURL().equals(url)) {
						playlist.setPosition(PlaylistManager.getInstance().current_playlist.getPosition());
						PlaylistManager.getInstance().current_playlist = playlist;
					}
				});
			}

			@Override
			public void onError(String error) {
				Platform.runLater(() -> {
					GUIManager.getInstance().sideBar.sideBarTab.changeTab(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.PLAYLISTS));
					if (error.equals("PLAYLIST_PRIVATE")) {
						Notification.getInstance().createNotification("Playlist", "The playlist you searched for is private", AlertType.ERROR);
						Popup.errorAlert("Playlist", "The playlist you searched for is private", CustomColor.RED);
					} else {
						Notification.getInstance().createNotification("Playlist", "The playlist you searched for does not exist", AlertType.ERROR);
						Popup.errorAlert("Playlist", "The playlist you searched for does not exist", CustomColor.RED);
					}
				});

			}
		});
	}
}