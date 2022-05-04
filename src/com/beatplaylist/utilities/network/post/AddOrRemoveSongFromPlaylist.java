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

import com.beatplaylist.gui.utilities.playlist.SongTitle;
import com.beatplaylist.utilities.events.CompleteEvent;
import com.beatplaylist.utilities.events.SocketReceiveEvent;
import com.beatplaylist.utilities.network.netty.NETTYClient;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.network.serialized.PacketType;
import com.beatplaylist.utilities.network.serialized.Post;
import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;
import com.beatplaylist.utilities.playlist.Playlist;
import com.beatplaylist.utilities.playlist.Song;

import javafx.application.Platform;

// Called when a user tries to add or remove a song from their playlist.
public class AddOrRemoveSongFromPlaylist {

	public static void send(Playlist playlist, Song song, boolean remove, CompleteEvent event) {
		if (playlist == null || song == null)
			return;

		Post post = new Post();
		post.setDetails();
		post.setPacketType(PacketType.ADD_SONG_TO_PLAYLIST);

		JSONObject object = new JSONObject();
		object.put("playlist_url", JSONValue.escape(playlist.getURL()));
		object.put("song_name", JSONValue.escape(song.getFullSongTitle()));
		object.put("song_url", JSONValue.escape(SongTitle.getVideoIDFromURL(song.getURL())));
		object.put("remove", remove);

		post.setJSONArray(object);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {
			@Override
			public void onSuccess(Post post) {
				Platform.runLater(() -> {
					JSONWrapper response = new JSONWrapper(post.getJSONMessage());
					if (!response.getJSONString("response").equals("SONG_ADDED") && !response.getJSONString("response").equals("SONG_REMOVED")) {
						Notification.getInstance().createNotification("Playlist", "You do not have permission to modify this playlist!", AlertType.ERROR);
						event.onFail("NO_PERMISSION");
						return;
					}
					if (remove)
						playlist.removeSongByURL(song, true);
					else
						playlist.addSong(song, true);
					event.onSuccess();
				});
			}

			@Override
			public void onError(String error) {
				Platform.runLater(() -> {
					event.onFail(error);
				});
			}
		});
	}
}