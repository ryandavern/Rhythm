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

import com.beatplaylist.utilities.events.SocketReceiveEvent;
import com.beatplaylist.utilities.network.netty.NETTYClient;
import com.beatplaylist.utilities.network.serialized.FailType;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.network.serialized.PacketType;
import com.beatplaylist.utilities.network.serialized.Post;
import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;
import com.beatplaylist.utilities.playlist.Playlist;

import javafx.application.Platform;

public class EditPlaylistInformation {

	public EditPlaylistInformation() {
	}

	public static void send(Playlist playlist, String name, String description, String visibility, boolean syncToYouTube, String url) {
		Post post = new Post();
		post.setDetails();
		post.setPacketType(PacketType.UPDATE_PLAYLIST);

		JSONObject object = new JSONObject();
		object.put("playlist_name", JSONValue.escape(name));
		object.put("playlist_description", JSONValue.escape(description));
		object.put("playlist_visibility", JSONValue.escape(visibility));
		object.put("playlist_url", JSONValue.escape(url));
		object.put("syncToYouTube", syncToYouTube);

		post.setJSONArray(object);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {

			@Override
			public void onSuccess(Post post) {
				Platform.runLater(() -> {
					JSONWrapper response = new JSONWrapper(post.getJSONMessage());
					if (response.getJSONString("response").equals("success")) {
						Notification.getInstance().createNotification("Playlist", "Your playlist settings have been updated.", AlertType.SUCCESS);
						playlist.setName(name);
						playlist.setDescription(description);
						playlist.setVisibility(visibility);
						playlist.setSyncToYouTube(syncToYouTube);
					} else
						Notification.getInstance().createNotification("Playlist", "An error occured, while updating your playlist settings.", AlertType.ERROR);
				});
			}

			@Override
			public void onError(String error) {
				Platform.runLater(() -> {
					if (error.equals(FailType.INVALID_URL.name())) {
						Notification.getInstance().createNotification("Playlist", "The playlist you tried to edit does not exist", AlertType.ERROR);
					} else if (error.equals(FailType.INVALID_OWNER.name())) {
						Notification.getInstance().createNotification("Playlist", "You do not have permission to edit this playlist", AlertType.ERROR);
					} else if (error.equals(FailType.PARAM_MISSING.name())) {
						Notification.getInstance().createNotification("Playlist", "Your playlist name cannot be empty", AlertType.ERROR);
					} else {
						Notification.getInstance().createNotification("Playlist", "An error occured, while updating your playlist settings", AlertType.ERROR);
					}
				});
			}
		});
	}
}