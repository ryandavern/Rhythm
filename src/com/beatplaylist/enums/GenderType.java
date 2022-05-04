package com.beatplaylist.enums;

public enum GenderType {

	MALE(0, "Male"), //
	FEMALE(1, "Female"), //
	OTHER(2, "Other");

	private int id;
	private String name;

	GenderType(int id, String name) {
		this.name = name;
	}
	
	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public static GenderType getName(String value) {
		GenderType type = GenderType.valueOf(value.toUpperCase());
		return type;
	}
}