package com.beatplaylist.enums;

public enum LoginMessageType {

	LOGIN_1("YO YO YO"), //
	LOGIN_2("Welcome to AustinFFA's channel."), //
	LOGIN_3("300 St Clair St"), // CHANGE
	LOGIN_4("Have a snazzy day."), //
	LOGIN_5("Lets get roiiight into the tunessss."), //
	LOGIN_6("Loading Terrain..."), // CHANGE
	LOGIN_7("HAXXX!!!!"), // CHANGE
	LOGIN_8("Foam Pit."), // CHANGE
	LOGIN_9("DUDE! THEY MUST BE HACKING!"), // CHANGE
	LOGIN_10("YOU CAN'T SAY THAT ON STREAM!"), // CHANGE
	LOGIN_11("Austin Mayer."), //
	LOGIN_12("Add boppertankmachine on Skype!"), //
	LOGIN_13("THERE IS A GHOST IN MY HOUSE?!"), //
	LOGIN_14("STRAIGHT UP!"), //
	LOGIN_15("Why 70?"), //
	LOGIN_16("The beat go off?");

	private String message;

	LoginMessageType(String message) {
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}
}