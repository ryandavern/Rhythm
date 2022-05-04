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

package com.beatplaylist.utilities.network.serialized;

import java.io.Serializable;

import org.json.simple.JSONObject;

import com.beatplaylist.utilities.user.UserManager;

public class Post implements Serializable {

	//private static final long serialVersionUID = -2965609407360708051L;

	/**
	 * 
	 */
	private static final long serialVersionUID = 514467731061858595L;
	private String username = "", account_key = "", fail_message = "", version = "";
	private JSONObject json_message = new JSONObject();
	private PacketType packet_type;

	public void setPacketType(PacketType packet_type) {
		this.packet_type = packet_type;
	}

	public void setDetails() {
		this.username = UserManager.getInstance().getUser().username;
		this.account_key = UserManager.getInstance().getUser().accessToken;
	}

	public PacketType getPacketType() {
		return this.packet_type;
	}

	public String getUsername() {
		return this.username;
	}

	public String getAccountKey() {
		return this.account_key;
	}

	public JSONObject getJSONMessage() {
		return this.json_message;
	}

	public void setJSONArray(JSONObject array) {
		this.json_message = array;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return this.version;
	}

	public void setFailed(String message) {
		this.fail_message = message;
	}

	public void setFailed(FailType failType) {
		this.fail_message = failType.name();
	}

	public String getFailMessage() {
		return this.fail_message;
	}

	public boolean hasFailed() {
		return !this.fail_message.isEmpty();
	}
}