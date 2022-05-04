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

package com.beatplaylist.utilities.playlist;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.beatplaylist.chromium.BrowserManager;
import com.beatplaylist.utilities.Utilities;
import com.beatplaylist.utilities.network.netty.JSONEscape;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LyricManager {

	private static LyricManager instance = new LyricManager();

	public static LyricManager getInstance() {
		return instance;
	}

	public void initialize() {
		loadCustomLyrics();
	}

	public Map<String, String> cached_lyrics = new HashMap<>();
	public StringProperty currentStoredLyrics = new SimpleStringProperty();

	// Stores the last searched lyric url from genius.
	public String lastQueriedGeniusURL = "", lastQueriedYouTubeURL = "";

	public Map<String, String> loadCustomLyrics() {
		Map<String, String> lyrics = new HashMap<>();

		JSONParser parser = new JSONParser();
		try {
			if (new File(Utilities.getInstance().getLocalDirectory() + "//lyric.json").length() == 0)
				return null;
			FileReader file = new FileReader(Utilities.getInstance().getLocalDirectory() + "//lyric.json");

			Object obj = parser.parse(file);
			JSONObject jsonObject = (JSONObject) obj;

			JSONArray song_array = (JSONArray) jsonObject.get("lyrics");
			if (song_array.isEmpty())
				return null;
			Iterator<JSONObject> iterator = song_array.iterator();
			while (iterator.hasNext()) {
				JSONObject song = (JSONObject) iterator.next();

				String song_url = String.valueOf(song.get("song_url"));
				String genius_url = String.valueOf(song.get("genius_url"));
				if ((genius_url.startsWith("https://genius.com/") || genius_url.startsWith("https://www.genius.com/") && (song_url.startsWith("https://youtube.com/") || song_url.startsWith("https://www.youtube.com/"))))
					lyrics.put(String.valueOf(song.get("song_url")), String.valueOf(song.get("genius_url")));
			}
			file.close();
			this.cached_lyrics = lyrics;
			return lyrics;
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean addLyric(String youtube_url, String genius_url) {
		if (genius_url.startsWith("https://genius.com/") || genius_url.startsWith("https://www.genius.com/")) {

			Map<String, String> lyrics = this.cached_lyrics;
			lyrics.put(youtube_url, genius_url);
			JSONArray array = new JSONArray();

			for (Entry<String, String> lyric : lyrics.entrySet()) {
				JSONObject newLyric = new JSONObject();
				newLyric.put("song_url", JSONEscape.escape(lyric.getKey()));
				newLyric.put("genius_url", JSONEscape.escape(lyric.getValue()));
				array.add(newLyric);
			}
			JSONObject obj = new JSONObject();
			obj.put("lyrics", array);
			try {

				FileWriter file = new FileWriter(Utilities.getInstance().getLocalDirectory() + "//lyric.json");

				file.flush();
				file.write(obj.toString());
				file.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		} else {
			return false;
		}
	}

	public void removeLyric(String youtube_url) {
		Map<String, String> lyrics = this.cached_lyrics;
		lyrics.remove(youtube_url);

		JSONArray array = new JSONArray();

		for (Entry<String, String> lyric : lyrics.entrySet()) {
			JSONObject newLyric = new JSONObject();
			newLyric.put("song_url", JSONEscape.escape(lyric.getKey()));
			newLyric.put("genius_url", JSONEscape.escape(lyric.getValue()));
			array.add(newLyric);
		}

		JSONObject obj = new JSONObject();
		obj.put("lyrics", array);
		try {

			FileWriter file = new FileWriter(Utilities.getInstance().getLocalDirectory() + "//lyric.json");

			file.flush();
			file.write(obj.toString());
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void getLyrics(String url) {
		// If genius url is invalid.
		if (url.equals("https://genius.com/youtube-lyrics"))
			return;
		
		// If the last stored genius url is the current url, just load the cached lyrics as we don't need to resend a url request.
		if (this.lastQueriedGeniusURL.equals(url))
			return;

		final String youtubeURL = BrowserManager.getInstance().getVideoURL();

		if (this.lastQueriedYouTubeURL.equals(youtubeURL))
			return;

		if (LyricManager.getInstance().cached_lyrics.containsKey(youtubeURL.replace("https://www.youtube.com/watch?v=", ""))) {
			url = LyricManager.getInstance().cached_lyrics.get(youtubeURL.replace("https://www.youtube.com/watch?v=", ""));
		}

		final String geniusSearchURL = url.replace("-–-", "-");

		if (!youtubeURL.contains("watch"))
			return;

		this.currentStoredLyrics.set("Loading Lyrics...");
		// Set the last genius url
		this.lastQueriedGeniusURL = url;
		this.lastQueriedYouTubeURL = youtubeURL;

		new Thread(() -> {
			try {
				Document doc = Jsoup.connect(geniusSearchURL).userAgent("Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Mobile Safari/537.36").get();
				Elements lyrics = doc.body().select("div[class=lyrics] > p");
				String html = lyrics.html().replaceAll("\"", "").replaceAll("<i>", "").replaceAll("</i>", "").replaceAll("<b>", "").replaceAll("</b>", "").replaceAll("\\s*\\<a[^\\>]*\\>\\s*", " ").replaceAll("</a>", "");
				String lyricHTMLToString = html.replaceAll("<br> ", "\n").replaceAll("<br>", "\n").replaceAll("&amp;", "&");
				Platform.runLater(() -> {
					if (!lyricHTMLToString.isEmpty()) {
						this.currentStoredLyrics.set(lyricHTMLToString);
					}
				});
			} catch (IOException e) {
				System.out.println("Genius URL that Failed: " + geniusSearchURL);
				this.currentStoredLyrics.set("No lyrics could be found. Click the 'Add Custom Lyrics' button to add your own lyrics.");
			}
		}).start();
	}
}