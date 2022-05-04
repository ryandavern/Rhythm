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

package com.beatplaylist.utilities.update;

public enum UpdateCategory {

	VERSION(""), //
	COMING_SOON("Coming Soon"), //
	NEW_FEATURE("New Features"), //
	BUG_FIX("Bug Fixes"), //
	IMPROVEMENT("Improvements");

	String name;

	UpdateCategory(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}