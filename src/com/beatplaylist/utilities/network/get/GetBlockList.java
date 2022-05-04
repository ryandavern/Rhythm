package com.beatplaylist.utilities.network.get;

import java.util.Iterator;

import org.json.simple.JSONObject;

import com.beatplaylist.utilities.events.SocketReceiveEvent;
import com.beatplaylist.utilities.network.netty.NETTYClient;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.network.serialized.PacketType;
import com.beatplaylist.utilities.network.serialized.Post;
import com.beatplaylist.utilities.profile.BlockedUsers;

public class GetBlockList {

	public GetBlockList() {
	}

	public static void send() {

		Post post = new Post();
		post.setDetails();
		post.setPacketType(PacketType.GET_USER_BLOCK_LIST);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {

			@Override
			public void onSuccess(Post post) {

				JSONWrapper response = new JSONWrapper(post.getJSONMessage());

				Iterator<JSONObject> blockList = response.getJSONArray("blockList");

				while (blockList.hasNext()) {
					JSONWrapper blocked_user = new JSONWrapper(blockList.next());

					int id = blocked_user.getJSONInteger("user_id");
					String username = blocked_user.getJSONString("username");
					String profile_image = blocked_user.getJSONString("profile_image");
					String display_name = blocked_user.getJSONString("display_name");

					BlockedUsers.getInstance().block_list.put(username, id + "," + profile_image + "," + display_name);
				}
			}

			@Override
			public void onError(String error) {
			}
		});
	}
}