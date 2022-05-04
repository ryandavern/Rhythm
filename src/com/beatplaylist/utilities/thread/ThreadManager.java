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

package com.beatplaylist.utilities.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadManager {

	private static ThreadManager instance = new ThreadManager();

	public static ThreadManager getInstance() {
		return instance;
	}

	private ExecutorService threads, socketPool;

	public void initialize() {
		this.threads = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1); // Was 8
		this.socketPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1); // Was 12
	}

	public ExecutorService getThreadPool() {
		return this.threads;
	}

	public ExecutorService getSocketPool() {
		return this.socketPool;
	}
}