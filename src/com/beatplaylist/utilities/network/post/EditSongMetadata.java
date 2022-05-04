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
import com.beatplaylist.utilities.events.SocketReceiveEvent;
import com.beatplaylist.utilities.network.netty.NETTYClient;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.network.serialized.PacketType;
import com.beatplaylist.utilities.network.serialized.Post;
import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;
import com.beatplaylist.utilities.playlist.Playlist;

import javafx.application.Platform;

public class EditSongMetadata {

	public static void send(Playlist playlist, String song_url, String videoTitle, String start_time, String end_time) {
		if (song_url.isEmpty())
			return;

		Post post = new Post();
		post.setDetails();
		post.setPacketType(PacketType.EDIT_SONG_METADATA);

		JSONObject object = new JSONObject();
		
		object.put("song_url", JSONValue.escape(SongTitle.getVideoIDFromURL(song_url)));
		object.put("playlist_url", JSONValue.escape(playlist.getURL()));
		object.put("videoTitle", JSONValue.escape(videoTitle));
		object.put("start_time", JSONValue.escape(start_time));
		object.put("end_time", JSONValue.escape(end_time));

		post.setJSONArray(object);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {

			@Override
			public void onSuccess(Post post) {
				Platform.runLater(() -> {
					JSONWrapper response = new JSONWrapper(post.getJSONMessage());
					if (!response.getJSONString("response").equals("success")) {
						Notification.getInstance().createNotification("Playlist", "You do not have permission to modify this playlist!", AlertType.ERROR);
					}
				});
			}

			@Override
			public void onError(String error) {
			}
		});
	}
}