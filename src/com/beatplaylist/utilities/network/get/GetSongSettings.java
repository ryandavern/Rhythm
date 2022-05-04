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

package com.beatplaylist.utilities.network.get;

import java.util.Iterator;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

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

public class GetSongSettings {

	public static void send(Playlist playlist) {
		Post post = new Post();
		post.setDetails();
		post.setPacketType(PacketType.GET_SONG_SETTINGS);

		JSONObject object = new JSONObject();
		object.put("playlist_url", JSONValue.escape(playlist.getURL()));

		post.setJSONArray(object);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {

			@Override
			public void onSuccess(Post post) {
				Platform.runLater(() -> {
					JSONWrapper response = new JSONWrapper(post.getJSONMessage());

					Iterator<JSONObject> iterator = response.getJSONArray("song_settings");

					while (iterator.hasNext()) {
						JSONWrapper song = new JSONWrapper(iterator.next());

						Song s = getSong(playlist, "https://www.youtube.com/watch?v=" + song.getJSONString("song_url"));
						if (s == null)
							return;

						String start_time = song.getJSONString("song_start_time");
						String end_time = song.getJSONString("song_end_time");
						String customTitle = song.getJSONString("song_title");

						s.setStartTime(start_time);
						s.setEndTime(end_time);
						if (!customTitle.isEmpty())
							s.setFullSongTitle(customTitle);

					}
				});
			}

			@Override
			public void onError(String error) {
				Notification.getInstance().createNotification("Playlist", "Could not match a playlist with the entered url! Please try again.", AlertType.ERROR);
			}
		});
	}

	private static Song getSong(Playlist playlist, String url) {
		for (Song song : playlist.getSongs()) {
			if (song.getURL().equals(url))
				return song;
		}
		return null;
	}
}