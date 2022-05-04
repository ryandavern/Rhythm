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

package com.beatplaylist.utilities.network.login;

public class LoginUtilities {

	private static LoginUtilities instance = new LoginUtilities();

	public static LoginUtilities getInstance() {
		return instance;
	}

	// Used in GetAccessKeyState class.
	public long last_access_key_request_sent;
	// Used in SendLoginCode class.
	public long last_login_code_sent;

	public boolean canSendAccessKeyRequest() {
		return (System.currentTimeMillis() - this.last_access_key_request_sent) > 3000;
	}

	public boolean canSendLoginCode() {
		return (System.currentTimeMillis() - this.last_login_code_sent) > 3000;
	}
}