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
import com.beatplaylist.utilities.network.serialized.PacketType;
import com.beatplaylist.utilities.network.serialized.Post;

public class RemoveLinkedSocialNetworkingAccount {

	public RemoveLinkedSocialNetworkingAccount() {
	}

	public void send(String social_type, boolean showOnProfile, boolean displayOnAccount) {
		Post post = new Post();
		post.setDetails();
		post.setPacketType(PacketType.REVOKE_ACCOUNT);

		JSONObject object = new JSONObject();
		object.put("social_type", JSONValue.escape(social_type.toUpperCase()));
		object.put("show_on_profile", showOnProfile);
		object.put("display_on_account", displayOnAccount);

		post.setJSONArray(object);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {

			@Override
			public void onSuccess(Post post) {
				
			}

			@Override
			public void onError(String error) {
			}
		});
	}
}