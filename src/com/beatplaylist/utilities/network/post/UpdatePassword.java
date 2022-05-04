package com.beatplaylist.utilities.network.post;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.beatplaylist.utilities.events.CompleteEvent;
import com.beatplaylist.utilities.events.SocketReceiveEvent;
import com.beatplaylist.utilities.network.netty.NETTYClient;
import com.beatplaylist.utilities.network.serialized.PacketType;
import com.beatplaylist.utilities.network.serialized.Post;

import javafx.application.Platform;

public class UpdatePassword {

	public static void send(String currentPassword, String newPassword, CompleteEvent event) {

		Post post = new Post();
		post.setDetails();
		post.setPacketType(PacketType.UPDATE_PASSWORD);

		JSONObject object = new JSONObject();
		object.put("currentPassword", JSONValue.escape(currentPassword));
		object.put("newPassword", JSONValue.escape(newPassword));

		post.setJSONArray(object);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {

			@Override
			public void onSuccess(Post post) {
				Platform.runLater(() -> {
					event.onSuccess();
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