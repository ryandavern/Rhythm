package com.beatplaylist.utilities.network.post;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.beatplaylist.utilities.Utilities;
import com.beatplaylist.utilities.events.CompleteEvent;
import com.beatplaylist.utilities.events.SocketReceiveEvent;
import com.beatplaylist.utilities.network.netty.NETTYClient;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.network.serialized.PacketType;
import com.beatplaylist.utilities.network.serialized.Post;
import com.beatplaylist.utilities.user.LoginUser;

import javafx.application.Platform;

public class CreateAccount {

	private static boolean debugTest = false;

	public static void send(LoginUser user, CompleteEvent event) {
		if (debugTest) {
			System.out.println(user.username);
			System.out.println(user.email);
			System.out.println(user.password);
			return;
		}
		Post post = new Post();
		post.setPacketType(PacketType.CREATE_ACCOUNT);

		JSONObject object = new JSONObject();
		object.put("username", JSONValue.escape(user.username));
		object.put("email", JSONValue.escape(user.email));
		object.put("password", JSONValue.escape(user.password));
		object.put("country", Utilities.getInstance().getCountry());

		post.setJSONArray(object);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {

			@Override
			public void onSuccess(Post post) {
				JSONWrapper result = new JSONWrapper(post.getJSONMessage());

				if (result.getJSONString("response").equals("success")) {
					Platform.runLater(() -> {
						event.onSuccess();
					});
				}
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