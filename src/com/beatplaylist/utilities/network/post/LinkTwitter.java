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

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.beatplaylist.utilities.events.SocketReceiveEvent;
import com.beatplaylist.utilities.network.netty.NETTYClient;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.network.serialized.PacketType;
import com.beatplaylist.utilities.network.serialized.Post;
import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;
import com.beatplaylist.utilities.popup.Popup;

import javafx.application.Platform;

public class LinkTwitter {

	public static void send(String social_type) {
		Post post = new Post();
		post.setDetails();
		post.setPacketType(PacketType.REQUEST_SOCIAL_URL);

		JSONObject object = new JSONObject();
		object.put("social_type", JSONValue.escape(social_type));

		post.setJSONArray(object);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {

			@Override
			public void onSuccess(Post post) {
				Platform.runLater(() -> {
					try {
						JSONWrapper response = new JSONWrapper(post.getJSONMessage());
						Desktop.getDesktop().browse(new URI(response.getJSONString("url")));
						Popup.twitterAuthenticationConfirmationCode();
					} catch (IOException | URISyntaxException e) {
						e.printStackTrace();
						Notification.getInstance().createNotification("Link account", "Could not link account at this time!", AlertType.ERROR);
					}
				});
			}

			@Override
			public void onError(String error) {
			}
		});
	}
}
