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
import com.beatplaylist.utilities.playlist.Playlist;
import com.beatplaylist.utilities.playlist.PlaylistManager;
import com.beatplaylist.utilities.playlist.RoleType;
import com.beatplaylist.utilities.user.UserManager;

import javafx.application.Platform;

public class CreatePlaylist {

	public CreatePlaylist() {
	}

	public static void send(Playlist playlist, String name, String description, String visibility, boolean syncToYouTube, CompleteEvent event) {
		if (name.isEmpty()) {
			Notification.getInstance().createNotification("Playlist", "Please enter a name for your playlist.", AlertType.ERROR);
			return;
		}

		Post post = new Post();
		post.setDetails();
		post.setPacketType(PacketType.CREATE_PLAYLIST);

		JSONObject object = new JSONObject();
		object.put("playlist_name", JSONValue.escape(name));
		object.put("playlist_description", JSONValue.escape(description));
		object.put("playlist_visibility", JSONValue.escape(visibility));
		object.put("syncToYouTube", syncToYouTube);

		post.setJSONArray(object);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {

			@Override
			public void onSuccess(Post post) {
				Platform.runLater(() -> {
					JSONWrapper response = new JSONWrapper(post.getJSONMessage());

					if (response.getJSONString("response").equals("success")) {
						Notification.getInstance().createNotification("Playlist", "Your playlist was successfully created!", AlertType.SUCCESS);
						playlist.setURL(response.getJSONString("playlist_url"));
						playlist.setName(name);
						playlist.setCreatorUsername(UserManager.getInstance().getUser().username);
						playlist.setRole(RoleType.EDIT);

						PlaylistManager.getInstance().addPlaylist(playlist);
						PlaylistManager.getInstance().printPlaylists();
						if (event != null)
							event.onSuccess();
					} else
						Notification.getInstance().createNotification("Playlist", "An error occured while creating your playlist! Please try again.", AlertType.ERROR);
				});
			}

			@Override
			public void onError(String error) {
				event.onFail(error);
				if (error.equals("LIMIT EXCEEDED"))
					Notification.getInstance().createNotification("PLAYLIST", "You have reached the limit of 30 playlists! Upgrade to premium to create up to 100 playlists!", AlertType.ERROR);
				else if (error.equals("LIMIT PREMIUM EXCEEDED"))
					Notification.getInstance().createNotification("PLAYLIST", "You have reached the limit of 100 playlists! We will increase this limit in the near future!", AlertType.ERROR);
				else
					Notification.getInstance().createNotification("Playlist", "An error occured while creating your playlist! Please try again.", AlertType.ERROR);
			}
		});
	}
}