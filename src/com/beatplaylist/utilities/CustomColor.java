package com.beatplaylist.utilities;

public enum CustomColor {

	RED("#E74C3C"), //
	DARK_RED("#c0392b"), //
	ERROR("#FF0000"), //
	BACKGROUND("#262626"), // #141d26
	DARK_BACKGROUND("#1E1E1E"), // #263136
	WHITE("#FFFFFF"), //
	RHYTHM("#e056fd"), //
	GREEN("#2ecc71"), //
	DARK_GREEN("#d046ed"), //
	ORANGE("#F9BF3B"), //
	GRAY("#90949C"), //
	NOTIFICATION("#C0392B"), //
	DROPDOWN_MENU_COLOR("#282828"), //
	AUDIO_BAR_BACKGROUND_COLOR("#282828"), //
	POPUP_SUB_HEADER("#b3b3b3"), //
	TRANSPARENT("transparent"),
	DIAMOND("#B9F2FF"),
	DIAMOND_HOVER("#AAEEFE"),
	GOLD("#FFD700"),
	SILVER("#C0C0C0");

	private String color_hex;

	CustomColor(String color_hex) {
		this.color_hex = color_hex;
	}

	public String getColorHex() {
		return this.color_hex;
	}
}