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

import com.beatplaylist.chromium.adblock.AdvertManager;
import com.beatplaylist.chromium.adblock.AdvertTerm;
import com.beatplaylist.chromium.adblock.TermType;
import com.beatplaylist.utilities.events.SocketReceiveEvent;
import com.beatplaylist.utilities.network.netty.NETTYClient;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.network.serialized.PacketType;
import com.beatplaylist.utilities.network.serialized.Post;

import javafx.application.Platform;

public class GetAdblock {

	public static void send() {
		Post post = new Post();
		post.setDetails();
		post.setPacketType(PacketType.GET_ADBLOCK);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {

			@Override
			public void onSuccess(Post post) {
				Platform.runLater(() -> {

					JSONWrapper message = new JSONWrapper(post.getJSONMessage());

					Iterator<JSONObject> iterator = message.getJSONArray("blocked");

					while (iterator.hasNext()) {
						JSONWrapper blocked = new JSONWrapper(iterator.next());
						String state = blocked.getJSONString("term_state");
						boolean starts_with = blocked.getJSONBoolean("term_starts_with");
						String term = blocked.getJSONString("term_parameter");

						// System.out.println(term + ":" + state);

						TermType term_type = TermType.ALLOWED;

						if (state.equals("BLOCKED"))
							term_type = TermType.BLOCKED;

						AdvertTerm advert_term = new AdvertTerm(term, term_type, starts_with);
						AdvertManager.getInstance().addAdvertTerm(advert_term);
					}
				});
			}

			@Override
			public void onError(String error) {

			}
		});
	}
}