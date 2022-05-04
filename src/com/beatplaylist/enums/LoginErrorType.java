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

package com.beatplaylist.enums;

public enum LoginErrorType {

	SUSPENDED("This account has been suspended from BeatPlaylist"), //
	INCORRECT_CREDENTIAL("You entered an invalid email or password"), //
	SERVER_OFFLINE(""), //
	ACCOUNT_CREATED(""), //
	NETWORK_CONNECTION_ERROR("You are currently disconnected from the internet. Please re-connect and try again.");

	private String errorMessage;

	LoginErrorType(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}
}