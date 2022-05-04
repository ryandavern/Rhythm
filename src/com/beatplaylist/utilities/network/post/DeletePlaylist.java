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

import com.beatplaylist.utilities.events.CompleteEvent;
import com.beatplaylist.utilities.events.SocketReceiveEvent;
import com.beatplaylist.utilities.network.netty.NETTYClient;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.network.serialized.PacketType;
import com.beatplaylist.utilities.network.serialized.Post;
import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;

import javafx.application.Platform;

public class DeletePlaylist {

	public DeletePlaylist() {
	}

	public static void send(String playlist_url, CompleteEvent event) {
		Post post = new Post();
		post.setDetails();
		post.setPacketType(PacketType.DELETE_PLAYLIST);

		JSONObject object = new JSONObject();
		object.put("playlist_url", JSONValue.escape(playlist_url));

		post.setJSONArray(object);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {

			@Override
			public void onSuccess(Post post) {
				Platform.runLater(() -> {
					JSONWrapper response = new JSONWrapper(post.getJSONMessage());

					if (response.getJSONString("response").equals("success")) {
						event.onSuccess();
					} else
						Notification.getInstance().createNotification("Playlist", "This playlist could not be deleted", AlertType.ERROR);

				});
			}

			@Override
			public void onError(String error) {
				Platform.runLater(() -> {
					if (error.equals("INCORRECT_PLAYLIST_OWNER"))
						Notification.getInstance().createNotification("Playlist", "You do not own this playlist", AlertType.ERROR);
				});
			}
		});
	}
}