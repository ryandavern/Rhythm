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

package com.beatplaylist.utilities.network.get;

import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.beatplaylist.settings.SocialType;
import com.beatplaylist.utilities.events.SocketReceiveEvent;
import com.beatplaylist.utilities.network.netty.NETTYClient;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.network.serialized.PacketType;
import com.beatplaylist.utilities.network.serialized.Post;
import com.beatplaylist.utilities.user.UserManager;

import javafx.application.Platform;

public class GetUserLinkedSocialAccounts {

	public static void send() {
		JSONObject json_object = new JSONObject();
		json_object.put("search", JSONValue.escape(UserManager.getInstance().getUser().username));
		json_object.put("for_settings", JSONValue.escape("true"));

		Post post = new Post();
		post.setDetails();
		post.setPacketType(PacketType.GET_SOCIAL_ACCOUNTS);
		post.setJSONArray(json_object);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {

			@Override
			public void onSuccess(Post post) {
				Platform.runLater(() -> {
					JSONWrapper wrapper = new JSONWrapper(post.getJSONMessage());
					JSONArray social_accounts = (JSONArray) wrapper.getJSONObject().get("social_accounts");
					Iterator<JSONObject> iterator = social_accounts.iterator();
					while (iterator.hasNext()) {
						JSONWrapper object = new JSONWrapper(iterator.next());

						String username = object.getJSONString("social_url");
						String social_type = object.getJSONString("social_type");
						boolean verified = object.getJSONBoolean("social_is_verified");
						boolean visible_on_profile = object.getJSONBoolean("social_visible_on_profile");
						UserManager.getInstance().user.addLinkedSocialAccount(SocialType.getName(social_type), username, verified, visible_on_profile);
					}
				});
			}

			@Override
			public void onError(String error) {
			}
		});
	}
}