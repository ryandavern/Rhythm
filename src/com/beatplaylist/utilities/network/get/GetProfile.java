package com.beatplaylist.utilities.network.get;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.beatplaylist.utilities.events.SocketReceiveEvent;
import com.beatplaylist.utilities.events.SocketResponseEvent;
import com.beatplaylist.utilities.network.netty.NETTYClient;
import com.beatplaylist.utilities.network.serialized.PacketType;
import com.beatplaylist.utilities.network.serialized.Post;

import javafx.application.Platform;

public class GetProfile {

	// Param username is the profile being loaded by username.
	public static void send(String username, SocketResponseEvent event) {

		Post post = new Post();
		post.setDetails();
		post.setPacketType(PacketType.GET_PROFILE);

		JSONObject object = new JSONObject();
		object.put("search", JSONValue.escape(username));

		post.setJSONArray(object);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {

			@Override
			public void onSuccess(Post post) {

				Platform.runLater(() -> {
					event.onSuccess(post);
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