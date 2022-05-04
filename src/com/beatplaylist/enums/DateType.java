package com.beatplaylist.enums;

import java.util.Calendar;

import com.beatplaylist.utilities.user.UserManager;

public enum DateType {

	JANUARY(1, "January"), //
	FEBRUARY(2, "February"), //
	MARCH(3, "March"), //
	APRIL(4, "April"), //
	MAY(5, "May"), //
	JUNE(6, "June"), //
	JULY(7, "July"), //
	AUGUST(8, "August"), //
	SEPTEMBER(9, "September"), //
	OCTOBER(10, "October"), //
	NOVEMBER(11, "November"), //
	DECEMBER(12, "December");

	private String name;
	private int id;

	DateType(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public static DateType getName(String value) {
		DateType type = DateType.valueOf(value.toUpperCase());
		return type;
	}

	public static int getEasterSundayMonth(int year) {
		int a = year % 19, b = year / 100, c = year % 100, d = b / 4, e = b % 4, g = (8 * b + 13) / 25, h = (19 * a + b - d - g + 15) % 30, j = c / 4, k = c % 4, m = (a + 11 * h) / 319, r = (2 * e + 2 * j - k - h + m + 32) % 7, n = (h - m + r + 90) / 25;
		int result;
		switch (n) {
		case 1:
			result = 1;
			break;
		case 2:
			result = 2;
			break;
		case 3:
			result = 3;
			break;
		case 4:
			result = 4;
			break;
		case 5:
			result = 5;
			break;
		case 6:
			result = 6;
			break;
		case 7:
			result = 7;
			break;
		case 8:
			result = 8;
			break;
		case 9:
			result = 9;
			break;
		case 10:
			result = 10;
			break;
		case 11:
			result = 11;
			break;
		case 12:
			result = 12;
			break;
		default:
			result = 0;
		}
		return result;
	}

	public static int getEasterSundayDate(int year) {
		int a = year % 19, b = year / 100, c = year % 100, d = b / 4, e = b % 4, g = (8 * b + 13) / 25, h = (19 * a + b - d - g + 15) % 30, j = c / 4, k = c % 4, m = (a + 11 * h) / 319, r = (2 * e + 2 * j - k - h + m + 32) % 7, n = (h - m + r + 90) / 25, p = (h - m + r + n + 19) % 32;
		return p;
	}

	public static boolean isEaster() {
		Calendar calender = Calendar.getInstance();
		int year = calender.get(Calendar.YEAR), month = calender.get(Calendar.MONTH), day = calender.get(Calendar.DAY_OF_MONTH);
		return getEasterSundayMonth(year) == month && getEasterSundayDate(year) == day;
	}

	public static boolean isChristmas() {
		Calendar calender = Calendar.getInstance();
		int month = calender.get(Calendar.MONTH), day = calender.get(Calendar.DAY_OF_MONTH);
		return month == Calendar.DECEMBER && day == 24;
	}

	public static boolean isBirthday() {
		int day = UserManager.getInstance().getUser().birthDay, month = UserManager.getInstance().getUser().birthMonth;
		Calendar calender = Calendar.getInstance();
		int calMonth = calender.get(Calendar.MONTH), calDay = calender.get(Calendar.DAY_OF_MONTH);
		return day == calDay && month == calMonth;
	}
}