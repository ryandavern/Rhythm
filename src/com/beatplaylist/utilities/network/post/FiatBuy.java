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
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.network.serialized.PacketType;
import com.beatplaylist.utilities.network.serialized.Post;
import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;

import javafx.application.Platform;

public class FiatBuy {

	public static void send(String cardNumber, String cvv, String expiryMonth, String expiryYear, String purchaseID) {
		Post post = new Post();
		post.setDetails();
		post.setPacketType(PacketType.FIAT_BUY);

		JSONObject object = new JSONObject();
		object.put("cardNumber", JSONValue.escape(cardNumber));
		object.put("cvv", JSONValue.escape(cvv));
		object.put("expiryMonth", JSONValue.escape(expiryMonth));
		object.put("expiryYear", JSONValue.escape(expiryYear));
		object.put("purchaseAmount", purchaseID);

		post.setJSONArray(object);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {

			@Override
			public void onSuccess(Post post) {
				Platform.runLater(() -> {
					JSONWrapper response = new JSONWrapper(post.getJSONMessage());

				});
			}

			@Override
			public void onError(String error) {
				Platform.runLater(() -> {
					Notification.getInstance().createNotification("Playlist", "", AlertType.ERROR);
				});
			}
		});
	}

}