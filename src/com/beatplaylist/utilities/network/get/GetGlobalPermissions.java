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

import org.json.simple.JSONObject;

import com.beatplaylist.utilities.events.SocketReceiveEvent;
import com.beatplaylist.utilities.network.netty.NETTYClient;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.network.serialized.PacketType;
import com.beatplaylist.utilities.network.serialized.Post;
import com.beatplaylist.utilities.permission.Permission;
import com.beatplaylist.utilities.permission.PermissionManager;
import com.beatplaylist.utilities.permission.PermissionState;

public class GetGlobalPermissions {

	public GetGlobalPermissions() {
	}

	public static void send() {
		Post post = new Post();
		post.setPacketType(PacketType.GET_USER_PERMISSIONS);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {

			@Override
			public void onSuccess(Post post) {
				JSONWrapper response = new JSONWrapper(post.getJSONMessage());

				Iterator<JSONObject> permissions = response.getJSONArray("permissions");

				while (permissions.hasNext()) {
					JSONWrapper permission = new JSONWrapper(permissions.next());

					String permission_name = permission.getJSONString("permission_name");
					String permission_state = permission.getJSONString("permission_state");

					PermissionManager.getInstance().addPermission(new Permission(permission_name, PermissionState.get(permission_state)));
				}
			}

			@Override
			public void onError(String error) {
			}
		});
	}
}