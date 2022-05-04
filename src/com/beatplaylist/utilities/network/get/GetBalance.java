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

import com.beatplaylist.utilities.events.CompleteEvent;
import com.beatplaylist.utilities.events.SocketReceiveEvent;
import com.beatplaylist.utilities.network.netty.NETTYClient;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.network.serialized.PacketType;
import com.beatplaylist.utilities.network.serialized.Post;
import com.beatplaylist.utilities.user.UserManager;
import com.beatplaylist.utilities.web3.PartneredContract;

import javafx.application.Platform;

public class GetBalance {

	public static void send(CompleteEvent event) {
		Post post = new Post();
		post.setDetails();
		post.setPacketType(PacketType.GET_BALANCE);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {

			@Override
			public void onSuccess(Post post) {
				Platform.runLater(() -> {
					JSONWrapper message = new JSONWrapper(post.getJSONMessage());

					UserManager.getInstance().user.bnbBalance = message.getJSONString("bnbBalance");
					UserManager.getInstance().user.claimableRhythm = Double.valueOf(message.getJSONString("l2eBalance"));

					for (PartneredContract contract : PartneredContract.values()) {
						String contractBalance = message.getJSONString(contract.name().toLowerCase() + "Balance");

						UserManager.getInstance().user.setContractBalance(contract, contractBalance);
					}
					event.onSuccess();
				});
			}

			@Override
			public void onError(String error) {
				Platform.runLater(() -> {
					UserManager.getInstance().user.bnbBalance = "0";
					UserManager.getInstance().user.claimableRhythm = 0;

					for (PartneredContract contract : PartneredContract.values()) {
						UserManager.getInstance().user.setContractBalance(contract, "0");
					}
					event.onSuccess();
				});
			}
		});
	}
}