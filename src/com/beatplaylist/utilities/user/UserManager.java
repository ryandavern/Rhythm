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

package com.beatplaylist.utilities.user;

import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.LoginGUIManager;
import com.beatplaylist.settings.Settings;

public class UserManager {

	private static UserManager instance = new UserManager();

	public static UserManager getInstance() {
		return instance;
	}

	// Stores the logged in user information.
	public LoginUser user;

	// Stores currently loaded profile page user details.
	public BaseUser profileUser;

	public void initializeLoginUser() {
		if (this.user == null) {
			this.user = new LoginUser();
		}
	}

	public LoginUser getUser() {
		return this.user;
	}

	// User class that holds the current loaded user profile.
	public BaseUser getProfileUser() {
		return this.profileUser;
	}

	// Logout function
	public void logoutWithoutAutoLoginChange() {
		this.user = null;
		this.profileUser = null;
		initializeLoginUser();

		GUIManager.getInstance().logout();
		LoginGUIManager.getInstance().initializeStage();
	}

	public void logout() {
		Settings.getInstance().setAutoLogin(false, true);
		logoutWithoutAutoLoginChange();
	}
}