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

import com.beatplaylist.settings.SocialType;
import com.beatplaylist.utilities.events.SocketReceiveEvent;
import com.beatplaylist.utilities.network.netty.NETTYClient;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.network.serialized.PacketType;
import com.beatplaylist.utilities.network.serialized.Post;
import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;
import com.beatplaylist.utilities.user.UserManager;
import com.beatplaylist.utilities.validation.LinkValidation;

import javafx.application.Platform;

public class LinkGoogleAccount {

	public static void send(String accessToken, String refreshToken) {
		if (LinkValidation.getInstance().isLinkTimerStillValid())
			return;
		LinkValidation.getInstance().resetLinkTimer();
		Post post = new Post();
		post.setDetails();
		post.setPacketType(PacketType.LINK_GOOGLE);

		JSONObject object = new JSONObject();

		object.put("accessToken", accessToken);
		object.put("refreshToken", refreshToken);

		post.setJSONArray(object);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {

			@Override
			public void onSuccess(Post post) {
				Platform.runLater(() -> {
					JSONWrapper wrapper = new JSONWrapper(post.getJSONMessage());
					if (wrapper.getJSONString("response").equals("success")) {
						UserManager.getInstance().user.addLinkedSocialAccount(SocialType.YOUTUBE, "", false, false);
						Notification.getInstance().createNotification("Social Account", "You have connected your YouTube account!", AlertType.SUCCESS);
					}
				});
			}

			@Override
			public void onError(String error) {
				Notification.getInstance().createNotification("Social Account", "An error occurred while linking your account. Sorry, I know this is a bad error message but this shouldn't be happening.", AlertType.ERROR);
			}
		});
	}
}