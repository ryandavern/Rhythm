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

import java.util.ArrayList;
import java.util.List;

import com.beatplaylist.enums.GenreType;
import com.beatplaylist.settings.SocialType;

public class BaseUser {

	// BaseUser account details.
	public String username = "", email = "", password = "", premiumExpiry = "", registrationDate = "";
	public AccountType accountType = AccountType.DEFAULT;

	// BaseUser profile details.
	public String displayName = "", bio = "", profileImageURL = "", location = "";
	public int birthYear = 0, birthMonth = 0, birthDay = 0, age = 0;

	public boolean isFollowing = false;
	public int followerCount = 0, followingCount = 0, playlistCount = 0;

	// BaseUser music details.
	public GenreType favouriteGenre = GenreType.ALL_TYPES;

	// BaseUser social account details.
	public List<SocialAccount> linkedSocialAccounts = new ArrayList<>();

	// Returns true if a user has the premium role or above.
	public boolean isPremium() {
		return this.accountType.isEqualOrLarger(AccountType.PREMIUM);
	}

	// Returns true if a user has the verified role or above.
	public boolean isVerified() {
		return this.accountType.isEqualOrLarger(AccountType.VERIFIED);
	}

	// Returns true if a user has the verified role or above.
	public boolean isCreator() {
		return this.accountType.isEqual(AccountType.CREATOR);
	}

	public void addLinkedSocialAccount(SocialType social_type, String username, boolean verified, boolean visible_on_profile) {
		SocialAccount social = getAccount(social_type);
		if (social == null)
			this.linkedSocialAccounts.add(new SocialAccount(username, social_type, verified, visible_on_profile));
		else {
			social = new SocialAccount(username, social_type, verified, visible_on_profile);
		}
	}

	public SocialAccount getAccount(SocialType social_type) {
		for (SocialAccount social : this.linkedSocialAccounts) {
			if (social.getSocialType() == social_type)
				return social;
		}
		return null;
	}
}