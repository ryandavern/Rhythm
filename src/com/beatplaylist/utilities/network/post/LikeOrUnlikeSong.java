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

package com.beatplaylist.utilities.network.post;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.beatplaylist.Main;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.utilities.playlist.SongTitle;
import com.beatplaylist.gui.utilities.playlist.SongView;
import com.beatplaylist.utilities.CustomToolTip;
import com.beatplaylist.utilities.data.Data;
import com.beatplaylist.utilities.events.SocketReceiveEvent;
import com.beatplaylist.utilities.events.UpdateSongLikeEvent;
import com.beatplaylist.utilities.network.netty.NETTYClient;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.network.serialized.PacketType;
import com.beatplaylist.utilities.network.serialized.Post;
import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;

import javafx.application.Platform;
import javafx.scene.image.Image;

public class LikeOrUnlikeSong {

	public static void send(String songURL, String songTitle, boolean fromAudioBar, UpdateSongLikeEvent event) {
		Post post = new Post();
		post.setDetails();
		post.setPacketType(PacketType.LIKE_OR_UNLIKE_SONG);

		JSONObject object = new JSONObject();
		object.put("song_url", JSONValue.escape(SongTitle.getVideoIDFromURL(songURL)));
		object.put("songTitle", JSONValue.escape(songTitle));

		post.setJSONArray(object);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {

			@Override
			public void onSuccess(Post post) {
				JSONWrapper response = new JSONWrapper(post.getJSONMessage());

				if (response.getJSONBoolean("hasLiked")) {
					event.onUpdate("ADDED");
					Data.getInstance().cachedLikedMusicCount++;
					if (fromAudioBar) {
						GUIManager.getInstance().audioBar.audioBar.likeMusicImage.changeImages(new Image(Main.class.getResource("/resources/icons/v2/like_music_fill.png").toExternalForm(), 18, 18, false, false), new Image(Main.class.getResource("/resources/icons/v2/like_music.png").toExternalForm(), 18, 18, false, false));
						CustomToolTip.install(GUIManager.getInstance().audioBar.audioBar.likeMusicImage.getHBox(), new CustomToolTip("Remove current playing song from liked music."));
						Data.getInstance().cachedLikedMusicCount++;
					} else {
						Notification.getInstance().createNotification("Liked Music", "\"" + songTitle + "\" has been added to your liked music", AlertType.SUCCESS);
					}
				} else {
					event.onUpdate("REMOVED");
					Data.getInstance().cachedLikedMusicCount--;
					if (fromAudioBar) {
						GUIManager.getInstance().audioBar.audioBar.likeMusicImage.changeImages(new Image(Main.class.getResource("/resources/icons/v2/like_music.png").toExternalForm(), 18, 18, false, false), new Image(Main.class.getResource("/resources/icons/v2/like_music_hover.png").toExternalForm(), 18, 18, false, false));
						CustomToolTip.install(GUIManager.getInstance().audioBar.audioBar.likeMusicImage.getHBox(), new CustomToolTip("Add current playing song to liked music."));
						Data.getInstance().cachedLikedMusicCount--;
					} else {
						Notification.getInstance().createNotification("Liked Music", "\"" + songTitle + "\" has been removed from your liked music", AlertType.SUCCESS);
					}

				}
			}

			@Override
			public void onError(String error) {

			}
		});
	}

	public static void sendFromPlaylistView(SongView song, UpdateSongLikeEvent event) {
		Post post = new Post();
		post.setDetails();
		post.setPacketType(PacketType.LIKE_OR_UNLIKE_SONG);

		JSONObject object = new JSONObject();
		object.put("song_url", JSONValue.escape(SongTitle.getVideoIDFromURL(song.song.url)));
		object.put("songTitle", JSONValue.escape(song.song.fullSongTitle));

		post.setJSONArray(object);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {

			@Override
			public void onSuccess(Post post) {
				JSONWrapper response = new JSONWrapper(post.getJSONMessage());

				if (response.getJSONBoolean("hasLiked")) {
					event.onUpdate("ADDED");
					Platform.runLater(() -> {
						Data.getInstance().cachedLikedMusicCount++;
						//PlaylistLoader.getInstance().loadPlaylist("my-likes");
						Notification.getInstance().createNotification("Liked Music", "\"" + song.song.fullSongTitle + "\" has been added to your liked music", AlertType.SUCCESS);
					});
				} else {
					event.onUpdate("REMOVED");
					Platform.runLater(() -> {
						Data.getInstance().cachedLikedMusicCount--;
						//PlaylistLoader.getInstance().loadPlaylist("my-likes");
						Notification.getInstance().createNotification("Liked Music", "\"" + song.song.fullSongTitle + "\" has been removed from your liked music", AlertType.SUCCESS);
					});
				}
			}

			@Override
			public void onError(String error) {

			}
		});
	}
}