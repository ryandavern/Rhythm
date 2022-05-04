package com.beatplaylist.utilities.network.serialized;

public enum FailType {

	INVALID_LIST_SIZE, //
	PARAM_MISSING, //
	INVALID_ACCOUNT_KEY, //
	INVALID_VERSION, //
	OFFLINE, //
	RESULT_EMPTY, //
	INVALID_OWNER, // User doesn't own item they are trying to update, edit, delete, etc.
	INVALID_URL, // User requested item by URL does not exist. E.g. playlist doesn't exist, post doesn't exist, user doesn't exist.
	ERROR, //
	NO_PERMISSION;

}