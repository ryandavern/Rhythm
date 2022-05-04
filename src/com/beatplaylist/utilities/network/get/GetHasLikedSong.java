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

import org.json.simple.JSONObject;

import com.beatplaylist.Main;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.utilities.CustomToolTip;
import com.beatplaylist.utilities.events.SocketReceiveEvent;
import com.beatplaylist.utilities.network.netty.NETTYClient;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.network.serialized.PacketType;
import com.beatplaylist.utilities.network.serialized.Post;

import javafx.scene.image.Image;

public class GetHasLikedSong {

	public GetHasLikedSong() {
	}

	public static void send(String url) {

		Post post = new Post();
		post.setDetails();
		post.setPacketType(PacketType.HAS_LIKED_SONG);

		JSONObject object = new JSONObject();
		object.put("song_url", url);

		post.setJSONArray(object);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {

			@Override
			public void onSuccess(Post post) {

				JSONWrapper response = new JSONWrapper(post.getJSONMessage());

				if (response.getJSONString("response").equals("success")) {
					GUIManager.getInstance().audioBar.audioBar.likeMusicImage.changeImages(new Image(Main.class.getResource("/resources/icons/v2/like_music_fill.png").toExternalForm(), 18, 18, false, false), new Image(Main.class.getResource("/resources/icons/v2/like_music.png").toExternalForm(), 18, 18, false, false));
					CustomToolTip.install(GUIManager.getInstance().audioBar.audioBar.likeMusicImage.getHBox(), new CustomToolTip("Remove current playing song from liked music."));
				} else {
					GUIManager.getInstance().audioBar.audioBar.likeMusicImage.changeImages(new Image(Main.class.getResource("/resources/icons/v2/like_music.png").toExternalForm(), 18, 18, false, false), new Image(Main.class.getResource("/resources/icons/v2/like_music_hover.png").toExternalForm(), 18, 18, false, false));
					CustomToolTip.install(GUIManager.getInstance().audioBar.audioBar.likeMusicImage.getHBox(), new CustomToolTip("Add current playing song to liked music."));
				}
			}

			@Override
			public void onError(String error) {
			}
		});
	}
}