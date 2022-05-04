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

import java.text.DecimalFormat;

public class NumberFormat {

	// Returns a smaller formatted value of a number. E.g converts the value 1,420,222 to 1.42M
	public static String getFormattedNumber(double value) {
		DecimalFormat format = new DecimalFormat("0.0");
		if (value < 1000)
			return String.valueOf((int) value);
		else if (value >= 1000 && value < 10000)
			return String.valueOf(format.format(value / 1000)) + "K";
		else if (value >= 10000 && value < 100000)
			return String.valueOf(format.format(value / 1000)) + "K";
		else if (value >= 100000 && value < 1000000)
			return String.valueOf(format.format(value / 1000)) + "K";
		else if (value >= 1000000 && value < 1000000000)
			return String.valueOf(format.format(value / 1000000)) + "M";
		else if (value >= 1000000000)
			return String.valueOf(format.format(value / 1000000000)) + "B";
		else
			return "Result not found.";
	}
}