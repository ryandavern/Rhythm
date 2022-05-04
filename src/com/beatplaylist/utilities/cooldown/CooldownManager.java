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

package com.beatplaylist.utilities.cooldown;

import java.util.HashMap;
import java.util.Map;

import com.beatplaylist.utilities.format.TimeFormat;

public class CooldownManager {

	private static CooldownManager instance = new CooldownManager();

	public static CooldownManager getInstance() {
		return instance;
	}

	// Does a user have a cooldown in a certain area of the application?
	private Map<String, Long> cooldown = new HashMap<>();

	public void setCooldown(String cooldownName, int length, String unit) {
		long end = System.currentTimeMillis() + TimeFormat.getTime(Integer.valueOf(length), unit);
		if (end - System.currentTimeMillis() > 0)
			this.getCooldowns().put(cooldownName, end);
	}

	public boolean hasCooldown(String cooldownName) {
		if (this.getCooldowns().containsKey(cooldownName))
			return (this.getCooldowns().get(cooldownName) - System.currentTimeMillis() > 0);
		return false;
	}

	public String getCooldownTimeRemaining(String cooldownName) {
		long end = this.getCooldowns().get(cooldownName);
		if (end - System.currentTimeMillis() > 0)
			return TimeFormat.getRemainingTime(end);
		return "0";
	}

	private Map<String, Long> getCooldowns() {
		return this.cooldown;
	}
}