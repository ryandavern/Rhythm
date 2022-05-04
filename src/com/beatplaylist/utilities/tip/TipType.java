package com.beatplaylist.utilities.tip;

public enum TipType {

	SKIP_PREVIOUS_BUTTON("Pressing and holding the skip/previous buttons on your keyboard will let you skip ahead or go back 10 seconds.", false), //
	EXTRA_PLAYLIST_PUBLISH("Upgrade to premium to increase your playlist limit.", true), //
	PARTY_CREATE("Upgrade to premium to create an event and house party!", true), //
	SUBMIT_IDEA_BUG("Want to report a bug or submit an idea to BeatPlaylist? Head over to https://beatplaylist.com/contact!", false), //
	BETA("We are currently in Beta! Please be aware that bugs may occur!", true);

	private String message;
	private boolean non_premium_message;

	TipType(String message, boolean non_premium_message) {
		this.message = message;
		this.non_premium_message = non_premium_message;
	}

	public String getMessage() {
		return this.message;
	}

	public boolean isNonPremiumMessage() {
		return this.non_premium_message;
	}
}