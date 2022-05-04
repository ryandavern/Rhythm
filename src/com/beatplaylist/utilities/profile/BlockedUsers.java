package com.beatplaylist.utilities.profile;

import java.util.HashMap;
import java.util.Map;

public class BlockedUsers {

	private static BlockedUsers instance = new BlockedUsers();

	public static BlockedUsers getInstance() {
		return instance;
	}

	// public List<String> block_list = new ArrayList<>();
	public Map<String, String> block_list = new HashMap<>();

}