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

import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.module.layout.sidebar.TabType;
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

// Called when a user links a social networking account to their profile.
public class LinkSocialNetworkingAccount {

	public static void send(String pin, String social_type) {
		if (LinkValidation.getInstance().isLinkTimerStillValid())
			return;
		LinkValidation.getInstance().resetLinkTimer();
		Post post = new Post();
		post.setDetails();
		post.setPacketType(PacketType.CONFIRM_SOCIAL);

		JSONObject object = new JSONObject();
		object.put("pin", JSONValue.escape(pin));
		object.put("social_type", JSONValue.escape(social_type.toUpperCase()));

		post.setJSONArray(object);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {
			@Override
			public void onSuccess(Post post) {
				Platform.runLater(() -> {

					JSONWrapper response = new JSONWrapper(post.getJSONMessage());

					if (response.getJSONString("response").equals("success")) {
						UserManager.getInstance().user.addLinkedSocialAccount(SocialType.getName(social_type), response.getJSONString("account_username"), response.getJSONBoolean("isVerified"), true);
						Notification.getInstance().createNotification("Social Account", "You have connected your " + social_type.substring(0, 1).toUpperCase() + social_type.substring(1).toLowerCase() + " account!", AlertType.SUCCESS);
						GUIManager.getInstance().sideBar.sideBarTab.changeTab(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.SETTINGS), "");
					}
				});
			}

			@Override
			public void onError(String error) {
			}
		});
	}
}
