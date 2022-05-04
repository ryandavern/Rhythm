package com.beatplaylist.utilities.network.post;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.beatplaylist.enums.GenreType;
import com.beatplaylist.utilities.events.SocketReceiveEvent;
import com.beatplaylist.utilities.events.SocketResponseEvent;
import com.beatplaylist.utilities.network.netty.NETTYClient;
import com.beatplaylist.utilities.network.serialized.PacketType;
import com.beatplaylist.utilities.network.serialized.Post;
import com.beatplaylist.utilities.user.UserManager;

import javafx.application.Platform;

public class EditProfileInformation {

	public static void send(String newUsername, String email, String display_name, String location, String birth_year, int birth_month, String birth_day, String bio, String genre, SocketResponseEvent event) {

		JSONObject object = new JSONObject();
		object.put("new_username", JSONValue.escape(newUsername));
		object.put("display_name", JSONValue.escape(display_name));
		object.put("email", JSONValue.escape(email));
		object.put("location", JSONValue.escape(location));
		object.put("birth_year", JSONValue.escape(birth_year));
		object.put("birth_month", JSONValue.escape(String.valueOf(birth_month)));
		object.put("birth_day", JSONValue.escape(birth_day));
		object.put("bio", JSONValue.escape(bio));
		object.put("genre", JSONValue.escape(genre));

		Post post = new Post();
		post.setDetails();
		post.setPacketType(PacketType.UPDATE_ACCOUNT);
		post.setJSONArray(object);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {
			@Override
			public void onSuccess(Post post) {
				Platform.runLater(() -> {
					if (!newUsername.isEmpty()) {
						UserManager.getInstance().getUser().username = newUsername;
					}
					UserManager.getInstance().getUser().email = email;
					UserManager.getInstance().getUser().displayName = display_name;
					UserManager.getInstance().getUser().location = location;
					UserManager.getInstance().getUser().bio = bio;
					UserManager.getInstance().getUser().favouriteGenre = GenreType.getName(genre);
					if (!birth_day.isEmpty())
						UserManager.getInstance().getUser().birthDay = Integer.valueOf(birth_day);
					UserManager.getInstance().getUser().birthMonth = Integer.valueOf(birth_month);
					if (!birth_year.isEmpty())
						UserManager.getInstance().getUser().birthYear = Integer.valueOf(birth_year);
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