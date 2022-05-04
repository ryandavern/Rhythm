package com.beatplaylist.enums;

public enum FontType {

	ARIAL("arial"), //
	DEFAULT("Open Sans"), //
	VOLLKORN("Vollkorn"), //
	VERDANA("Verdana");

	private String name;

	FontType(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}