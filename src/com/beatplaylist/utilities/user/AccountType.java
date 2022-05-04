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

public enum AccountType {

	DEFAULT(0), //
	PREMIUM(1), //
	VERIFIED(2), //
	CREATOR(3), //
	MODERATOR(4), //
	DEVELOPER(5), //
	ADMIN(6);

	private int id;

	AccountType(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public boolean isSmaller(AccountType account_type) {
		return this.getId() < account_type.getId();
	}

	public boolean isLarger(AccountType account_type) {
		return this.getId() > account_type.getId();
	}

	public boolean isEqual(AccountType account_type) {
		return this.getId() == account_type.getId();
	}

	public boolean isEqualOrLarger(AccountType account_type) {
		return this.getId() >= account_type.getId();
	}

	public boolean isEqualOrSmaller(AccountType account_type) {
		return this.getId() <= account_type.getId();
	}

	public static AccountType getName(String name) {
		AccountType account_type = AccountType.valueOf(name.toUpperCase());
		return account_type;
	}
}