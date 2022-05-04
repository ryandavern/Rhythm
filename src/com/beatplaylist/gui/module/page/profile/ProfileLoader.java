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

package com.beatplaylist.gui.module.page.profile;

import java.util.Calendar;

import org.json.simple.JSONObject;

import com.beatplaylist.enums.GenreType;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.module.layout.sidebar.TabType;
import com.beatplaylist.utilities.events.SocketResponseEvent;
import com.beatplaylist.utilities.network.get.GetProfile;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.network.serialized.Post;
import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;
import com.beatplaylist.utilities.user.AccountType;
import com.beatplaylist.utilities.user.BaseUser;
import com.beatplaylist.utilities.user.UserManager;

// Called when a user wants to load a profile page. This class handles everything profile related.
public class ProfileLoader {

	// Loads login users profile page.
	public static void loadProfile() {
		loadProfile(UserManager.getInstance().user.username);
	}

	// Loads profile page of param @username
	public static void loadProfile(String username) {
		if (UserManager.getInstance().profileUser == null) {
			UserManager.getInstance().profileUser = new BaseUser();
		}

		GetProfile.send(username, new SocketResponseEvent() {

			@Override
			public void onSuccess(Post post) {
				JSONWrapper response = new JSONWrapper(post.getJSONMessage());

				JSONWrapper location = new JSONWrapper((JSONObject) response.getJSONObject().get("location"));
				JSONWrapper birthday = new JSONWrapper((JSONObject) response.getJSONObject().get("birthday"));

				BaseUser profileUser = UserManager.getInstance().profileUser;

				profileUser.username = username;
				profileUser.displayName = response.getJSONString("display_name");
				profileUser.profileImageURL = response.getJSONString("profile_image_url");
				profileUser.accountType = AccountType.getName(response.getJSONString("userRole"));
				profileUser.bio = response.getJSONString("bio");
				profileUser.favouriteGenre = GenreType.getName(response.getJSONString("genre"));
				profileUser.registrationDate = response.getJSONString("registrationDate");
				profileUser.isFollowing = response.getJSONBoolean("isFollowing");
				profileUser.followerCount = response.getJSONInteger("followerCount");
				profileUser.followingCount = response.getJSONInteger("followingCount");
				profileUser.playlistCount = response.getJSONInteger("playlistCount");

				// Set birthday values
				profileUser.birthDay = birthday.getJSONInteger("day");
				profileUser.birthMonth = birthday.getJSONInteger("month");
				profileUser.birthYear = birthday.getJSONInteger("year");
				profileUser.age = Calendar.getInstance().get(Calendar.YEAR) - profileUser.birthYear;

				// Set location values
				profileUser.location = location.getJSONString("location");

				GUIManager.getInstance().sideBar.sideBarTab.changeTab(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.PROFILE));
			}

			@Override
			public void onFail(String error) {
				Notification.getInstance().createNotification("Account", "Sorry! But the account you were looking for could not be found!", AlertType.ERROR);
			}
		});
	}
}