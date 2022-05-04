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

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

public class HTTPServerHandler extends ChannelInboundHandlerAdapter {

	private WebSocketServerHandshaker handshaker;
	private boolean debug = false;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {

		if (msg instanceof HttpRequest) {

			HttpRequest httpRequest = (HttpRequest) msg;
			debug("Http Request Received");

			HttpHeaders headers = httpRequest.headers();
			debug("Connection : " + headers.get("Connection"));
			debug("Upgrade : " + headers.get("Upgrade"));

			if ("Upgrade".equalsIgnoreCase(headers.get(HttpHeaderNames.CONNECTION)) && "WebSocket".equalsIgnoreCase(headers.get(HttpHeaderNames.UPGRADE))) {

				// Adding new handler to the existing pipeline to handle WebSocket Messages
				ctx.pipeline().replace(this, "websocketHandler", new WebSocketHandler());

				debug("WebSocketHandler added to the pipeline");
				debug("Opened Channel : " + ctx.channel());
				debug("Handshaking....");
				// Do the Handshake to upgrade connection from HTTP to WebSocket protocol
				handleHandshake(ctx, httpRequest);
				debug("Handshake is done");
			}
		} else {
			debug("Incoming request is unknown");
		}
	}

	private void debug(String message) {
		if (this.debug)
			System.out.println(message);
	}

	/* Do the handshaking for WebSocket request */
	protected void handleHandshake(ChannelHandlerContext ctx, HttpRequest req) {
		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketURL(req), null, true);
		handshaker = wsFactory.newHandshaker(req);
		if (handshaker == null) {
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
		} else {
			handshaker.handshake(ctx.channel(), req);
		}
	}

	protected String getWebSocketURL(HttpRequest req) {
		debug("Req URI : " + req.getUri());
		String url = "ws://" + req.headers().get("Host") + req.getUri();
		debug("Constructed URL : " + url);
		return url;
	}
}