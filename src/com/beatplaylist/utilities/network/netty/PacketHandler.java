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
import com.beatplaylist.utilities.events.SocketReceiveEvent;
import com.beatplaylist.utilities.network.post.SendErrorReport;
import com.beatplaylist.utilities.network.serialized.FailType;
import com.beatplaylist.utilities.network.serialized.Post;
import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;
import com.beatplaylist.utilities.update.StartupData;
import com.beatplaylist.utilities.user.UserManager;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import javafx.application.Platform;

public class PacketHandler extends SimpleChannelInboundHandler<Post> {

	private Post post;
	private long start = System.currentTimeMillis();
	private SocketReceiveEvent event;

	public PacketHandler(long start, Post post, SocketReceiveEvent event) {
		this.post = post;
		this.event = event;
		this.start = start;
	}

	@Override
	public void channelActive(ChannelHandlerContext channel) {

		if (channel.channel().isWritable()) {
			channel.writeAndFlush(this.post, channel.voidPromise());
			// if (this.post.getPacketType() != null) {
			// System.out.println(this.post.getPacketType().name());
			// }
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext channel) {
		channel.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext channel, Throwable cause) {
		cause.printStackTrace();
		channel.close();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext channel, Post post) throws Exception {
		try {
			double ms = (double) (System.currentTimeMillis() - this.start);

			if (post == null) {
				this.event.onError("POST NULL");
			} else {
				if (post.hasFailed()) {
					System.out.println(post.getFailMessage());
					if (post.getFailMessage().equals(FailType.NO_PERMISSION.name())) {
						System.out.println("NO PERMISSIONS");
						Platform.runLater(() -> {
							if (!UserManager.getInstance().getUser().username.isEmpty())
								GUIManager.getInstance().sideBar.sideBarTab.changeTab(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.PLAYLISTS));
							Notification.getInstance().createNotification("No Permission", "This feature has been temporarily disabled! Please try again later!", AlertType.ERROR);
						});
					} else if (post.getFailMessage().equals(FailType.INVALID_VERSION.name()) || post.getFailMessage().equals(FailType.INVALID_ACCOUNT_KEY.name()) || post.getFailMessage().equals(FailType.OFFLINE.name())) {
						System.out.println("INVALID VERSION");
						Platform.runLater(() -> {
							StartupData.getInstance().getStartupData(); // Get the latest details about the version the user is on and start an auto-update if available.
							UserManager.getInstance().logoutWithoutAutoLoginChange();
						});
					} else
						this.event.onError(post.getFailMessage());
				} else {
					if (post.getJSONMessage().size() > 0)
						System.out.println(ms + "ms - " + post.getJSONMessage().toJSONString());

					this.event.onSuccess(post);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (e.toString().startsWith("io.netty.channel.ConnectTimeoutException:")) {
				Platform.runLater(() -> {
					Notification.getInstance().createNotification("Server", "Our servers are currently offline! Our development team will fix that ASAP!", AlertType.ERROR);
				});
				return;
			}
			new SendErrorReport().send(e, this.getClass());
		} finally {
			ReferenceCountUtil.release(post);
			channel.channel().pipeline().remove(this);
		}
	}
}