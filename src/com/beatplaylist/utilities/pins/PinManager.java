package com.beatplaylist.utilities.pins;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class PinManager {

	private static PinManager instance = new PinManager();

	public static PinManager getInstance() {
		return instance;
	}

	public Map<Integer, Pin> user_pins = new HashMap<>();

	public void addPinToUser(Integer slot, Pin pin) {
		this.user_pins.put(slot, pin);
	}

	public Map<Integer, Pin> getUserPins() {
		return this.user_pins;
	}

	public boolean hasPin(String pin_name) {
		for (Entry<Integer, Pin> pins : user_pins.entrySet()) {
			if (pins.getValue().getName().equals(pin_name))
				return true;
		}
		return false;
	}

	public void setMap(Map<Integer, Pin> map) {
		this.user_pins = map;
	}
}