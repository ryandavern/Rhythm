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

import com.beatplaylist.utilities.CustomColor;
import com.beatplaylist.utilities.events.SocketReceiveEvent;
import com.beatplaylist.utilities.network.netty.NETTYClient;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.network.serialized.PacketType;
import com.beatplaylist.utilities.network.serialized.Post;
import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;
import com.beatplaylist.utilities.playlist.Playlist;

import javafx.application.Platform;
import javafx.scene.control.Button;

public class FollowPlaylist {

	public static void send(Playlist playlist, Button button) {
		if (button != null)
			button.setDisable(true);
		Post post = new Post();
		post.setDetails();
		post.setPacketType(PacketType.FOLLOW_PLAYLIST);

		JSONObject object = new JSONObject();
		object.put("playlist_url", JSONValue.escape(playlist.getURL()));

		post.setJSONArray(object);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {

			@Override
			public void onSuccess(Post post) {
				Platform.runLater(() -> {
					JSONWrapper response = new JSONWrapper(post.getJSONMessage());

					if (response.getJSONString("state").equals("FOLLOWED")) {
						playlist.setFollowing(true);
						Notification.getInstance().createNotification("Playlist", "You are now following the playlist '" + playlist.getName() + "'.", AlertType.SUCCESS);

						// Update playlist follow button
						if (button != null) {
							button.setStyle("-fx-background-color: " + CustomColor.RED.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-background-radius: 25px;");
							button.setText("UNFOLLOW");
						}
					} else {
						playlist.setFollowing(false);
						Notification.getInstance().createNotification("Playlist", "You are no longer following the playlist '" + playlist.getName() + "'.", AlertType.SUCCESS);
						if (button != null) {
							// Update playlist follow button
							button.setStyle("-fx-background-color: " + CustomColor.RHYTHM.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-background-radius: 25px;");
							button.setText("FOLLOW");
						}
					}
					if (button != null)
						button.setDisable(false);
				});
			}

			@Override
			public void onError(String error) {
				Platform.runLater(() -> {
					Notification.getInstance().createNotification("Playlist", "You cannot unfollow your own playlist.", AlertType.ERROR);
				});
			}
		});
	}
}