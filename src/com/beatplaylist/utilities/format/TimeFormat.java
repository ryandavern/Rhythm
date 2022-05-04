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

package com.beatplaylist.utilities.format;

public class TimeFormat {

	public static String getRemainingTime(long timeRemaining) {
		String message = "";
		long now = System.currentTimeMillis();
		long diff = timeRemaining - now;
		int seconds = (int) (diff / 1000);
		if (seconds >= 60 * 60 * 24 * 30 * 365) {
			int days = seconds / (60 * 60 * 24 * 30 * 365);
			seconds = seconds % (60 * 60 * 24 * 30 * 365);
			message += days + " years(s) ";
		}
		if (seconds >= 60 * 60 * 24 * 30) {
			int days = seconds / (60 * 60 * 24 * 30);
			seconds = seconds % (60 * 60 * 24 * 30);
			message += days + " months(s) ";
		}
		if (seconds >= 60 * 60 * 24) {
			int days = seconds / (60 * 60 * 24);
			seconds = seconds % (60 * 60 * 24);
			message += days + " day(s) ";
		}
		if (seconds >= 60 * 60) {
			int hours = seconds / (60 * 60);
			seconds = seconds % (60 * 60);
			message += hours + " hour(s) ";
		}
		if (seconds >= 60) {
			int min = seconds / 60;
			seconds = seconds % 60;
			message += min + " minute(s) ";
		}
		if (seconds >= 0)
			message += seconds + " second(s) ";
		return message;
	}

	public static long getTime(long amount, String name) {
		if (name.equalsIgnoreCase("second") || name.equalsIgnoreCase("seconds") || name.equalsIgnoreCase("sec"))
			return amount * 1000;
		if (name.equalsIgnoreCase("minute") || name.equalsIgnoreCase("minutes") || name.equalsIgnoreCase("min"))
			return amount * 1000 * 60;
		if (name.equalsIgnoreCase("hour") || name.equalsIgnoreCase("hours") || name.equalsIgnoreCase("h"))
			return amount * 1000 * 60 * 60;
		if (name.equalsIgnoreCase("day") || name.equalsIgnoreCase("days") || name.equalsIgnoreCase("d"))
			return amount * 1000 * 60 * 60 * 24;
		if (name.equalsIgnoreCase("week") || name.equalsIgnoreCase("weeks") || name.equalsIgnoreCase("w"))
			return amount * 1000 * 60 * 60 * 24 * 7;
		if (name.equalsIgnoreCase("month") || name.equalsIgnoreCase("months") || name.equalsIgnoreCase("m"))
			return amount * 1000 * 60 * 60 * 24 * 30;
		if (name.equalsIgnoreCase("year") || name.equalsIgnoreCase("years") || name.equalsIgnoreCase("y"))
			return amount * 1000 * 60 * 60 * 24 * 30 * 365;
		return 0;
	}

}
