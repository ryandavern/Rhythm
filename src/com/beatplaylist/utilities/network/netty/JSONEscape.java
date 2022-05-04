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

package com.beatplaylist.utilities.network.netty;

public class JSONEscape {

	public static String escape(final String string) {
		if (string == null)
			return null;
		final StringBuffer sb = new StringBuffer();
		escape(string, sb);
		return sb.toString();
	}

	public static void escape(final String string, final StringBuffer stringBuffer) {
		for (int i = 0; i < string.length(); ++i) {
			final char character = string.charAt(i);
			switch (character) {
			case 34: {
				stringBuffer.append("\\\"");
				break;
			}
			case 92: {
				stringBuffer.append("\\\\");
				break;
			}
			case 8: {
				stringBuffer.append("\\b");
				break;
			}
			case 12: {
				stringBuffer.append("\\f");
				break;
			}
			case 10: {
				stringBuffer.append("\\n");
				break;
			}
			case 13: {
				stringBuffer.append("\\r");
				break;
			}
			case 9: {
				stringBuffer.append("\\t");
				break;
			}
			default: {
				if ((character >= '\0' && character <= '\u001f') || (character >= '\u007f' && character <= '\u009f') || (character >= '\u2000' && character <= '\u20ff')) {
					final String hexString = Integer.toHexString(character);
					stringBuffer.append("\\u");
					for (int j = 0; j < 4 - hexString.length(); ++j)
						stringBuffer.append('0');
					stringBuffer.append(hexString.toUpperCase());
					break;
				}
				stringBuffer.append(character);
				break;
			}
			}
		}
	}
}