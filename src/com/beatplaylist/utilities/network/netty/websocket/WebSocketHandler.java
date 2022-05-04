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

package com.beatplaylist.utilities.network.netty.websocket;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.beatplaylist.utilities.network.post.LinkGoogleAccount;
import com.beatplaylist.utilities.network.post.LinkSocialNetworkingAccount;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;
import com.beatplaylist.utilities.notification.NotificationManager;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class WebSocketHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext channel_context, Object message) {
		try {
			if (message instanceof WebSocketFrame) {
				if (message instanceof TextWebSocketFrame) {
					JSONParser parser = new JSONParser();
					JSONObject json = (JSONObject) parser.parse(((TextWebSocketFrame) message).text());

					if (json.get("message_type").equals("LINK_INSTAGRAM")) {
						String token = new JSONWrapper(json).getJSONString("token");
						if (token.isEmpty() || token == null || token.length() < 10) {
							Notification.getInstance().createNotification("Instagram", "An error occured while linking your Instagram account. Please try again.", AlertType.ERROR);
							return;
						}
						LinkSocialNetworkingAccount.send(token, "INSTAGRAM");
					}
					if (json.get("message_type").equals("NEW_MESSAGE")) {
						System.out.println("NEW MESSAGE " + json.toJSONString());

					}
					if (json.get("message_type").equals("NEW_NOTIFICATION")) {
						NotificationManager.getInstance().setHasNotifications(NotificationManager.getInstance().unread_notifications += 1);
					}
					if (json.get("message_type").equals("LINK_GOOGLE")) {
						System.out.println(json.toJSONString());
						System.out.println(json.get("accessToken"));
						System.out.println(json.get("refreshToken"));
						new LinkGoogleAccount().send(json.get("accessToken").toString(), json.get("refreshToken").toString());
					}
				} else {
					System.out.println("Unsupported WebSocketFrame");
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			channel_context.channel().flush();
			channel_context.close();
		}
	}
}