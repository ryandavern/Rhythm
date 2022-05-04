/**
 *  Disclaimer
 *  This project was created by Ryan Davern.
 *  Start Date: 30/03/2016.
 *  
 *  Copyright (C) 2017 Ryan Davern - All Rights Reserved.
 *  You may not use, distribute, monetize or modify this code under the terms of the Copyright Act 1994.
 *  You may use the compiled program, which can be downloaded at https://www.beatplaylist.com/. Any modified versions or versions uploaded to a different website is against TOS (https://www.beatplaylist.com/terms).
 *  
 *  For more information on the Copyright Act 1994, please visit http://www.legislation.govt.nz/act/public/1994/0143/latest/DLM345634.html.
 */

package com.beatplaylist.utilities.network.serialized;

import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONWrapper {

	private JSONObject jsonObject;

	public JSONWrapper(JSONObject object) {
		this.jsonObject = object;
	}

	public String getJSONString(String key) {
		return String.valueOf(this.getJSONObject().get(key));
	}

	public Integer getJSONInteger(String key) {
		return Integer.valueOf(String.valueOf(this.getJSONObject().get(key)));
	}

	public Boolean getJSONBoolean(String key) {
		return Boolean.valueOf(String.valueOf(this.getJSONObject().get(key)));
	}

	public int getJSONSize() {
		return this.jsonObject.size();
	}

	public boolean isJSONSizeNotEqual(int equalAmount) {
		return this.jsonObject.size() != equalAmount;
	}

	public boolean keyExists(String key) {
		return this.jsonObject.containsKey(key);
	}

	public Iterator<JSONObject> getJSONArray(String search) {
		JSONArray songs = (JSONArray) getJSONObject().get(search);
		Iterator<JSONObject> iterator = songs.iterator();
		return iterator;
	}

	public JSONObject getJSONObject() {
		return this.jsonObject;
	}
	
	public boolean isJSONInvalid() {
		if (this.jsonObject != null && !this.jsonObject.isEmpty())
			return false;
		return true;
	}
}