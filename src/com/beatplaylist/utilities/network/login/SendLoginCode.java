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

import java.util.Base64;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.beatplaylist.gui.module.page.login.confirm_login_page;
import com.beatplaylist.utilities.events.CompleteEvent;
import com.beatplaylist.utilities.events.SocketReceiveEvent;
import com.beatplaylist.utilities.network.netty.NETTYClient;
import com.beatplaylist.utilities.network.netty.security.SecurityManager;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.network.serialized.PacketType;
import com.beatplaylist.utilities.network.serialized.Post;
import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;
import com.beatplaylist.utilities.user.UserManager;

import javafx.application.Platform;

public class SendLoginCode {

	public static void send(CompleteEvent event) {
		if (!LoginUtilities.getInstance().canSendLoginCode()) {
			Notification.getInstance().createNotification("Login", "Please wait before sending another login code.", AlertType.ERROR);
			return;
		}
		LoginUtilities.getInstance().last_login_code_sent = System.currentTimeMillis();

		JSONObject object = new JSONObject();
		object.put("email", JSONValue.escape(UserManager.getInstance().user.email));
		object.put("password", Base64.getEncoder().encodeToString(SecurityManager.getInstance().encrypt(JSONValue.escape(UserManager.getInstance().user.password), SecurityManager.getInstance().publicKey)));
		
		Post post = new Post();
		post.setPacketType(PacketType.SEND_LOGIN_CODE);
		post.setJSONArray(object);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {

			@Override
			public void onSuccess(Post post) {
				JSONWrapper response = new JSONWrapper(post.getJSONMessage());

				String type = response.getJSONString("type");
				Platform.runLater(() -> {
					new confirm_login_page(type).run();
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