package com.beatplaylist.utilities.pins;

public class Pin {

	private String name, image_url, description, unique_pin_id;
	private boolean tradeable;
	private int slot;

	public void setName(String value) {
		this.name = value;
	}

	public void setImageURL(String value) {
		this.image_url = value;
	}

	public void setDescription(String value) {
		this.description = value;
	}

	public void setUniquePinId(String value) {
		this.unique_pin_id = value;
	}

	public void setTradeable(boolean value) {
		this.tradeable = value;
	}

	public void setSlot(int value) {
		this.slot = value;
	}

	public String getName() {
		return this.name;
	}

	public String getImageURL() {
		return this.image_url;
	}

	public String getDescription() {
		return this.description;
	}

	public String getUniquePinId() {
		return this.unique_pin_id;
	}

	public boolean isTradeable() {
		return this.tradeable;
	}

	public int getSlot() {
		return this.slot;
	}
}