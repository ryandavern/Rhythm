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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

import com.beatplaylist.enums.StatusType;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.module.layout.sidebar.TabType;
import com.beatplaylist.utilities.Utilities;
import com.beatplaylist.utilities.events.ImageUploadEvent;
import com.beatplaylist.utilities.events.SocketReceiveEvent;
import com.beatplaylist.utilities.logger.Log;
import com.beatplaylist.utilities.network.netty.websocket.HTTPInitializer;
import com.beatplaylist.utilities.network.post.SendErrorReport;
import com.beatplaylist.utilities.network.serialized.ImageUpload;
import com.beatplaylist.utilities.network.serialized.Post;
import com.beatplaylist.utilities.network.utilities.ServerUtilities;
import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;
import com.beatplaylist.utilities.update.UpdateManager;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import javafx.application.Platform;

public class NETTYClient {

	public final boolean ssl = false;
	public final int port = Integer.parseInt(System.getProperty("port", "8880"));
	public final int image_port = Integer.parseInt(System.getProperty("port", "8009"));

	public static NETTYClient instance = new NETTYClient();

	public static NETTYClient getInstance() {
		return instance;
	}

	private EventLoopGroup group = null;

	public void send(Post post, SocketReceiveEvent event) {
		send(post, event, false);
	}

	public void send(Post post, SocketReceiveEvent event, boolean writeOnly) {
		if (this.group == null || this.group.isShutdown() || this.group.isShuttingDown() || this.group.isTerminated()) {
			this.group = new NioEventLoopGroup();
		}
		long now = System.currentTimeMillis();
		post.setVersion(UpdateManager.getClientVersion());
		setStatus(StatusType.LOADING);
		new Thread(() -> {
			try {
				// Configure SSL.
				SslContext ssl_context;

				if (ssl) {
					ssl_context = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).sslProvider(SslProvider.JDK).build();
				} else {
					ssl_context = null;
				}
				try {
					Bootstrap bootstrap = new Bootstrap();

					bootstrap.option(NioChannelOption.TCP_NODELAY, true);
					bootstrap.option(NioChannelOption.SO_REUSEADDR, true);
					bootstrap.option(NioChannelOption.SO_LINGER, 0);
					// bootstrap.option(NioChannelOption.TCP_NODELAY, false);
					bootstrap.option(NioChannelOption.SO_REUSEADDR, true);
					bootstrap.option(NioChannelOption.SO_KEEPALIVE, true);
					bootstrap.option(NioChannelOption.SO_LINGER, 0);
					bootstrap.option(NioChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
					// bootstrap.option(NioChannelOption.SO_RCVBUF, 1048576);
					// bootstrap.option(NioChannelOption.SO_SNDBUF, 1048576);
					bootstrap.option(NioChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 10 * 65536);
					bootstrap.option(NioChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 2 * 65536);
					bootstrap.option(NioChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

					bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel channel) throws Exception {
							ChannelPipeline pipeline = channel.pipeline();
							pipeline.addFirst(new ErrorHandler());

							if (ssl) {
								pipeline.addLast(ssl_context.newHandler(channel.alloc(), ServerUtilities.getInstance().getReadHost(), port));
							}
							// channel.pipeline().addFirst(new ChannelTrafficShapingHandler(0, 0, 1000) {
							// @Override
							// protected void doAccounting(TrafficCounter counter) {
							// if (counter.lastReadBytes() > 0)
							// System.out.println("read: " + counter.lastReadBytes());
							// if (counter.lastWrittenBytes() > 0)
							// System.out.println("write: " + counter.lastWrittenBytes());
							// }
							// });
							pipeline.addLast(new ObjectEncoder(), new ObjectDecoder(ClassResolvers.cacheDisabled(getClass().getClassLoader())), new PacketHandler(now, post, event));

							// pipeline.addLast("gzipdeflater", ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP));
							// pipeline.addLast("gzipinflater", ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));
						}
					});

					// Start the connection attempt.
					bootstrap.connect(ServerUtilities.getInstance().getReadHost(), port).syncUninterruptibly().channel().closeFuture().syncUninterruptibly();

				} catch (Exception e) {
					e.printStackTrace();
					if (e.getMessage().contains("Connection refused")) {
						Platform.runLater(() -> {
							if (GUIManager.getInstance().currentTab != null && GUIManager.getInstance().currentTab.getTabType() != TabType.BEATPLAYLIST_OFFLINE)
								GUIManager.getInstance().sideBar.sideBarTab.changeTab(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.BEATPLAYLIST_OFFLINE), "");
							Notification.getInstance().createNotification("Server", "Our servers are currently offline! Our development team will fix that ASAP!", AlertType.ERROR);
						});
					} else {
						e.printStackTrace();
						new SendErrorReport().send(e, this.getClass());
					}
				} finally {
					// group.shutdownGracefully();
					setStatus(StatusType.ONLINE);
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.getInstance().getLogger().log(Level.SEVERE, e.getStackTrace().toString());
				new SendErrorReport().send(e, this.getClass());
			}
		}).start();
	}

	public void uploadImage(ImageUpload post, ImageUploadEvent event) {
		if (post.getFileMBLength() > 4.00) {
			Notification.getInstance().createNotification("Image Upload Fail", "You cannot upload images larger than a file size of 4MB", AlertType.ERROR);
			return;
		}
		Thread thread = new Thread(() -> {
			try {
				// Configure SSL.
				// SslContext ssl_context;
				// if (ssl) {
				// ssl_context = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
				// } else {
				// ssl_context = null;
				// }

				EventLoopGroup group = new NioEventLoopGroup();
				try {
					Bootstrap bootstrap = new Bootstrap();

					bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel channel) throws Exception {
							ChannelPipeline pipeline = channel.pipeline();
							// if (ssl_context != null) {
							// pipeline.addLast(ssl_context.newHandler(channel.alloc(), ServerUtilities.getInstance().getImageHost(), image_port));
							// }
							pipeline.addLast(new ObjectEncoder(), new ObjectDecoder(1000, ClassResolvers.cacheDisabled(null)), new SimpleChannelInboundHandler<Object>() {
								@Override
								public void channelActive(ChannelHandlerContext ctx) {
									ctx.writeAndFlush(post);
								}

								@Override
								public void channelReadComplete(ChannelHandlerContext channel_handler_context) {
									channel_handler_context.flush();
								}

								@Override
								public void exceptionCaught(ChannelHandlerContext channel_handler_context, Throwable cause) {
									channel_handler_context.close();
								}

								@Override
								protected void channelRead0(ChannelHandlerContext channel_handler_context, Object message) throws Exception {
									ImageUpload post = (ImageUpload) message;
									if (post.hasFailed()) {
										System.out.println(post.getFailMessage());
										event.onError(post.getFailMessage());
									} else {
										System.out.println(post.getFailMessage());
										if (!post.getFailMessage().isEmpty()) {
											event.onError(post.getFailMessage());
											return;
										}
										event.onSuccess(post);

									}
								}
							});
						}
					});

					// Start the connection attempt.
					bootstrap.connect(ServerUtilities.getInstance().getImageHost(), image_port).sync().channel().closeFuture().sync();
				} finally {
					group.shutdownGracefully();
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.getInstance().getLogger().log(Level.SEVERE, e.getStackTrace().toString());
			}
		});
		thread.start();
	}

	public void runWebSocketListener() {
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
			bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).handler(new LoggingHandler(LogLevel.INFO)).childHandler(new HTTPInitializer());

			Channel channel = bootstrap.bind(1339).sync().channel();

			channel.closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	public void waitForIncomingMessage() {
		new Thread(() -> {
			Socket socket = null;
			try {
				if (Utilities.getInstance().incoming_socket == null)
					Utilities.getInstance().incoming_socket = new ServerSocket(1218);
				while (true) {
					System.out.println(Utilities.getInstance().incoming_socket.isBound());

					socket = Utilities.getInstance().incoming_socket.accept();
					BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					String received = (in.readLine());
					System.out.println("Incoming Message from socket: " + socket + " Message: " + received);
					executeQuery(received);
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Server socket closed!");
			} finally {
				try {
					if (socket != null)
						socket.close();
				} catch (Exception e) {
					e.printStackTrace();
					Log.getInstance().getLogger().log(Level.SEVERE, e.getStackTrace().toString());
				}
			}
		}).start();
	}

	public void executeQuery(String message) {
		if (!message.startsWith("PHP-REQUEST"))
			System.out.println("An unknown incoming message was received and has been blocked.");
	}

	public void setStatus(StatusType status) {
		if (!StatusType.isStatus(status)) {
			StatusType.setStatus(status);
		}
	}
}