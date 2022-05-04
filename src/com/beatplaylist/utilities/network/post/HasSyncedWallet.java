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

import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.module.layout.sidebar.TabType;
import com.beatplaylist.utilities.events.SocketReceiveEvent;
import com.beatplaylist.utilities.network.netty.NETTYClient;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.network.serialized.PacketType;
import com.beatplaylist.utilities.network.serialized.Post;
import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;
import com.beatplaylist.utilities.user.UserManager;

import javafx.application.Platform;

public class HasSyncedWallet {

	// Send any caught exceptions to the server.
	public static void send() {

		Post post = new Post();
		post.setDetails();
		post.setPacketType(PacketType.HAS_SYNCED_WALLET);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {
			@Override
			public void onSuccess(Post post) {

				JSONWrapper object = new JSONWrapper(post.getJSONMessage());

				String walletAddress = object.getJSONString("walletAddress");
				
				UserManager.getInstance().getUser().walletAddress = walletAddress;

				Notification.getInstance().createNotification("Wallet Connect", "Your wallet " + walletAddress + " has successfully synced!", AlertType.SUCCESS);

				Platform.runLater(() -> {
					if (GUIManager.getInstance().currentTab.tab == TabType.WALLET_CONNECT) {
						GUIManager.getInstance().sideBar.sideBarTab.changeTab(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.WALLET_CONNECT));
					}
				});
			}

			@Override
			public void onError(String error) {
				Notification.getInstance().createNotification("Wallet Connect", "Please sync your wallet on the BeatPlaylist Hub!", AlertType.ERROR);
			}
		});
	}

}
