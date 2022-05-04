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

package com.beatplaylist.utilities.network.netty;

import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.module.layout.sidebar.TabType;
import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import javafx.application.Platform;

public class ErrorHandle extends ChannelDuplexHandler {

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (cause.getMessage().contains("was thrown by a user handler's exceptionCaught()") || cause.getMessage() == null || cause.getMessage().equals("null"))
			return;

		if (cause.getMessage().contains("An existing connection was forcibly closed by the remote host")) {
			Platform.runLater(() -> {
				if (GUIManager.getInstance().currentTab != null && GUIManager.getInstance().currentTab.getTabType() != TabType.BEATPLAYLIST_OFFLINE)
					GUIManager.getInstance().sideBar.sideBarTab.changeTab(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.BEATPLAYLIST_OFFLINE), "");
				Notification.getInstance().createNotification("Server", "Our servers are currently offline! Our development team will fix that ASAP!", AlertType.ERROR);
			});
		}
		super.exceptionCaught(ctx, cause);
	}
}
