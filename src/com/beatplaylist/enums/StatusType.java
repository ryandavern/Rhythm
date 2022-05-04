package com.beatplaylist.enums;

import com.beatplaylist.utilities.CustomColor;
import com.beatplaylist.utilities.data.Data;

public enum StatusType {

	MOVING("Moving...", CustomColor.ORANGE.getColorHex()), //
	CONNECTING("Connecting...", CustomColor.ORANGE.getColorHex()), //
	OFFLINE("Offline", CustomColor.RED.getColorHex()), //
	LOADING("Loading...", CustomColor.ORANGE.getColorHex()), //
	UPLOADING("Uploading...", CustomColor.ORANGE.getColorHex()), //
	ONLINE("Online", CustomColor.RHYTHM.getColorHex());

	private String name, color;

	StatusType(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public String getName() {
		return this.name;
	}

	public String getColor() {
		return this.color;
	}

	public static void setStatus(StatusType status) {
		Data.getInstance().current_status = status;
	}

	public static StatusType getStatus() {
		return Data.getInstance().current_status;
	}

	public static boolean isStatus(StatusType status) {
		return Data.getInstance().current_status == status;
	}
}