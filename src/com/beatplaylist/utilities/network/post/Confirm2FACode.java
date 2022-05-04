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
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.network.serialized.PacketType;
import com.beatplaylist.utilities.network.serialized.Post;
import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;
import com.beatplaylist.utilities.popup.PopupBuilder;
import com.beatplaylist.utilities.user.UserManager;

import javafx.application.Platform;
import javafx.scene.control.Button;

public class Confirm2FACode {

	public static void send(String code, PopupBuilder popup, Button two_factor_button) {
		if (UserManager.getInstance().getUser().has2FA)
			return;
		Post post = new Post();
		post.setDetails();
		post.setPacketType(PacketType.CONFIRM_2FA_CODE);

		JSONObject object = new JSONObject();
		object.put("code", JSONValue.escape(code));

		post.setJSONArray(object);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {
			@Override
			public void onSuccess(Post post) {
				Platform.runLater(() -> {
					JSONWrapper response = new JSONWrapper(post.getJSONMessage());
					if (response.getJSONString("response").equals("success")) {
						UserManager.getInstance().getUser().has2FA = true;
						Notification.getInstance().createNotification("2FA", "2FA is now connected to your account.", AlertType.SUCCESS);
						popup.close();
						two_factor_button.setText("Remove 2FA");
					} else {
						popup.setButtonsDisabled(false);
						Notification.getInstance().createNotification("2FA", "Incorrect code entered", AlertType.ERROR);
					}
				});
			}

			@Override
			public void onError(String error) {
				Platform.runLater(() -> {
					popup.setButtonsDisabled(false);
					Notification.getInstance().createNotification("2FA", "Incorrect code entered", AlertType.ERROR);
				});
			}
		});
	}
}