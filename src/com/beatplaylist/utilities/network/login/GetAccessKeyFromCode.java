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

package com.beatplaylist.utilities.network.login;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.beatplaylist.settings.Settings;
import com.beatplaylist.utilities.data.Data;
import com.beatplaylist.utilities.events.CompleteEvent;
import com.beatplaylist.utilities.events.SocketReceiveEvent;
import com.beatplaylist.utilities.network.netty.NETTYClient;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.network.serialized.PacketType;
import com.beatplaylist.utilities.network.serialized.Post;
import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;
import com.beatplaylist.utilities.user.UserManager;

import javafx.application.Platform;

public class GetAccessKeyFromCode {

	public static void send(String code, boolean isTOTP, CompleteEvent event) {
		if (!LoginUtilities.getInstance().canSendAccessKeyRequest()) {
			// Notification.getInstance().createCustomNotification("Login", "Please wait before logging in.", true, false);
			event.onFail("");
			return;
		}
		LoginUtilities.getInstance().last_access_key_request_sent = System.currentTimeMillis();

		JSONObject object = new JSONObject();
		object.put("email", JSONValue.escape(UserManager.getInstance().user.email));
		object.put("code", JSONValue.escape(code));
		object.put("isTOTP", JSONValue.escape(String.valueOf(isTOTP)));
		object.put("location", JSONValue.escape(Data.getInstance().country));
		object.put("mac_address", JSONValue.escape(Data.getInstance().macAddress));
		object.put("os", JSONValue.escape(System.getProperty("os.name")));

		Post post = new Post();
		post.setPacketType(PacketType.GET_ACCESS_KEY);
		post.setJSONArray(object);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {

			@Override
			public void onSuccess(Post post) {
				JSONWrapper response = new JSONWrapper(post.getJSONMessage());

				String accessKey = response.getJSONString("accessToken");
				Platform.runLater(() -> {
					Settings.getInstance().setAccessToken(accessKey, true);
					event.onSuccess();

					SendLoginRequest.send(event);
				});
			}

			@Override
			public void onError(String error) {
				Platform.runLater(() -> {
					Notification.getInstance().createNotification("Code", "The code you entered was invalid!", AlertType.ERROR);
					event.onFail(error);
				});
			}
		});
	}
}