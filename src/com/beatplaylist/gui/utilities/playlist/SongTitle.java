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

package com.beatplaylist.gui.utilities.playlist;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;

import com.beatplaylist.utilities.data.Data;

public class SongTitle {
	//
	// public static String forceReplacedTitle(String title) {
	// for (String words : Data.getInstance().song_video_keywords)
	// title = title.replace(words, "");
	// for (String words : Data.getInstance().song_audio_keywords)
	// title = title.replace(words, "");
	//
	// title = title.replace("\\u2013", "-").replace("&quot;", "\"").replace("&#44;", ",").replace("&#44\\;", ",").replace("&#39;", "'").replace(" - YouTube", "");
	//
	// title = title.replaceAll("\\s*\\*[^\\*]*\\*\\s*", " ").replaceAll("\\s*\\[[^\\]]*\\]\\s*", " ").replaceAll("\\s*\\{[^\\}]*\\}\\s*", " ").replaceAll("\\s*\\([^\\)]*\\)\\s*", " ");
	// if (title.startsWith(" - "))
	// title = title.replaceFirst(" - ", "");
	//
	// return title;
	// }

	public static String getReplacedTitle(String title, boolean genius_search) {
		title = title.toLowerCase();
		for (String words : Data.getInstance().song_video_keywords)
			title = title.replace(words, "");
		for (String words : Data.getInstance().song_audio_keywords)
			title = title.replace(words, "");
		title = title.replace("\\u2013", "-").replace("?", "").replace("!", "").replace("&quot;", "\"").replace("&#44;", ",").replace("&#44\\;", ",").replace("&#39;", "'").replace(" - YouTube", "");
		title = title.replaceAll("\\s*\\[[^\\]]*\\]\\s*", " ").replaceAll("\\s*\\{[^\\}]*\\}\\s*", " ").replaceAll("\\s*\\*[^\\*]*\\*\\s*", " ").replaceAll("\\s*\\([^\\)]*\\)\\s*", " ").replaceAll("\\s*\\*[^\\*]*\\)\\s*", " ");
		if (title.startsWith(" - "))
			title = title.replaceFirst(" - ", "");

		if (title.endsWith("-"))
			title = title.substring(0, title.length() - 1).trim();
		title = title.trim();
		if (title.contains("ft.")) {
			if (title.split("ft\\.").length > 0) {
				if (title.toLowerCase().split("ft\\.")[1].trim().contains("-")) {
					title = title.replaceAll(" ft.", "").trim();
				} else {
					title = title.split("ft\\.")[0].trim();
				}
			} else {
				title = title.split("ft\\.")[0].trim();
			}
			title = title.replace("\\u2013", "-").replaceAll("\\.", "").replaceAll("\"", "").replaceAll("\'", "").replaceAll(" - ", "-").replaceAll("\\s", "-").replaceAll("&", "and").replaceAll("\\$", "-");
			if (title.endsWith("-"))
				title = title.substring(0, title.length() - 1);
			// System.out.println(title);

			return title.trim();
		} else if (title.contains("feat.")) {
			if (title.split("feat\\.").length > 0) {
				if (title.toLowerCase().split("feat\\.")[1].trim().contains("-")) {
					title = title.replaceAll(" feat.", "").trim();
				} else {
					title = title.split("feat\\.")[0].trim();
				}
			} else {
				title = title.split("feat\\.")[0].trim();
			}
			title = title.replace("\\u2013", "-").replaceAll("\\.", "").replaceAll("\"", "").replaceAll("\'", "").replaceAll(" - ", "-").replaceAll("\\s", "-").replaceAll("&", "and").replaceAll("\\$", "-");
			if (title.endsWith("-"))
				title = title.substring(0, title.length() - 1);
			// System.out.println(title);

			return title.trim();
		} else {
			title = title.replace("\\u2013", "-").replaceAll("\\.", "").replaceAll(",", "").replaceAll("\"", "").replaceAll("\'", "").replaceAll(" - ", "-").replaceAll("\\s", "-").replaceAll("&", "and").replaceAll("\\$", "-");
			if (title.endsWith("-"))
				title = title.substring(0, title.length() - 1);
			// System.out.println(title);
			return title.trim();
		}
	}

	public static String formatSongTitle(String title) {
		for (String words : Data.getInstance().song_video_keywords)
			title = title.replace(words, "");
		for (String words : Data.getInstance().song_audio_keywords)
			title = title.replace(words, "");

		title = title.replace("–", "-").replace("\\u2013", "-").replace("”", "").replace("“", "").replace("\"", "").replace("&quot;", "").replace("&#44;", ",").replace("&#44\\;", ",").replace("&#39;", "'").replace(" - YouTube", "");

		if (title.startsWith(" - "))
			title = title.replaceFirst(" - ", "");
		else if (title.startsWith("- "))
			title = title.replaceFirst("- ", "");
		return title.trim().replace(" \\u2013 ", " - ").replace("\\u2013", "-").replaceAll("[ ]{2,}", " ").replace("&quot;", "\"").replace("&#44;", ",").replace("&#44\\;", ",").replace("&#39;", "'").replace(" - YouTube", "").replace("&amp;", "&");
	}

	public static JSONObject getSongInformationFromTitle(String title) {
		// Song name
		// Artist
		// Featuring
		boolean isRemix = false;

		if (title.toLowerCase().contains("remix")) {
			title = title.replaceAll("(?i)remix", "");
			isRemix = true;
		}

		title = formatSongTitle(title);

		// If song title doesn't contain a - then an artist cannot be found, but we can still find the featuring
		if (!title.contains(" - ") && !title.contains("- ") && !title.contains(" -")) {
			return getWithoutArtist(title, isRemix);
		}
		try {
			String song_name = "", artist = "", featuring = "";
			if (title.contains(" - ")) {
				artist = title.split(" - ")[0];
				song_name = title.split(" - ")[1];
			} else if (title.contains("- ")) {
				artist = title.split("- ")[0];
				song_name = title.split("- ")[1];
			} else if (title.contains(" -")) {
				artist = title.split(" -")[0];
				song_name = title.split(" -")[1];
			}

			// If featuring is found in Artist Text (E.g "Travis Scott ft. Quavo - Hello")
			if (artist.toLowerCase().contains("ft.") || artist.toLowerCase().contains("feat.") || artist.toLowerCase().contains("(feat.")) {
				if (artist.toLowerCase().contains("ft.")) {
					featuring = artist.split("(?i)ft.")[1];
					artist = artist.split("(?i)ft.")[0];
				} else {
					featuring = artist.split("(?i)feat.")[1];
					artist = artist.split("(?i)feat.")[0];
				}
			} // ----- handleFeatureFromTitle should replace this

			// If featuring is found in song name text (E.g. "Travis Scott - Hello ft. Quavo")
			if (song_name.toLowerCase().contains("ft.") || song_name.toLowerCase().contains("feat.") || song_name.toLowerCase().contains("(feat.")) {
				if (song_name.toLowerCase().contains("ft.")) {
					featuring = song_name.split("(?i)ft.")[1];
					song_name = song_name.split("(?i)ft.")[0];
				} else {
					featuring = song_name.split("(?i)feat.")[1];
					song_name = song_name.split("(?i)feat.")[0];
				}
			} // ----- handleFeatureFromTitle should replace this

			// If artist text doesn't contain feat. or ft. but contains the word ft or feat without a dot, we will try find the featuring based on the words in the artist text.
			String[] artistReturn = handleFeatureFromWord(artist, featuring, song_name, false);
			if (artistReturn != null) {
				artist = artistReturn[0];
				featuring = artistReturn[1];
			}

			// If song name text doesn't contain feat. or ft. but contains the word ft or feat without a dot, we will try find the featuring based on the words in the song name.
			String[] songReturn = handleFeatureFromWord(song_name, featuring, song_name, true);
			if (songReturn != null) {
				song_name = songReturn[0];
				featuring = songReturn[1];
			}

			if (getWithoutBracket(song_name).trim().isEmpty()) {
				return getWithoutArtist(title, isRemix);
			}

			JSONObject details = new JSONObject();
			details.put("artist", getWithoutBracket(artist.trim()));
			details.put("song_name", getWithoutBracket(song_name.trim()));
			details.put("featuring", getWithoutBracket(featuring.trim()));
			details.put("isRemix", isRemix);

			return details;
		} catch (Exception e) {
			JSONObject object = new JSONObject();
			object.put("song_name", formatSongTitle(getWithoutBracket(title)));
			return object;
		}
	}

	public static String getWithoutBracket(String str) {
		return str.replaceAll("\\s*\\*[^\\*]*\\*\\s*", " ").replaceAll("\\s*\\[[^\\]]*\\]\\s*", " ").replaceAll("\\s*\\{[^\\}]*\\}\\s*", " ").replaceAll("\\s*\\([^\\)]*\\)\\s*", " ").replace("(", "").replace(")", "");
	}

	private static JSONObject getWithoutArtist(String title, boolean isRemix) {
		String featuring = "";
		String song_name = title;

		if (song_name.toLowerCase().contains("ft.") || song_name.toLowerCase().contains("feat.") || song_name.toLowerCase().contains("(feat.")) {
			if (song_name.toLowerCase().contains("ft.")) {
				featuring = song_name.split("(?i)ft.")[1];
				song_name = song_name.split("(?i)ft.")[0];
			} else {
				featuring = song_name.split("(?i)feat.")[1];
				song_name = song_name.split("(?i)feat.")[0];
			}
		} // ----- handleFeatureFromTitle should replace this
			// If song name text doesn't contain feat. or ft. but contains the word ft or feat without a dot, we will try find the featuring based on the words in the song name.
		String[] songReturn = handleFeatureFromWord(song_name, featuring, song_name, true);
		if (songReturn != null) {
			song_name = songReturn[0];
			featuring = songReturn[1];
		}

		JSONObject object = new JSONObject();
		object.put("song_name", getWithoutBracket(song_name.trim()));
		object.put("featuring", getWithoutBracket(featuring.trim()));
		object.put("isRemix", isRemix);
		return object;
	}

	private static String[] handleFeatureFromWord(String splitWord, String featuring, String songName, boolean isFromSongName) {
		if (splitWord.toLowerCase().contains("ft") || splitWord.toLowerCase().contains("feat") || splitWord.toLowerCase().contains("(feat")) {
			String[] words = splitWord.split(" ");
			int index = 0;
			for (String word : words) {
				index++;
				if (word.toLowerCase().equals("ft") || word.toLowerCase().equals("feat") || word.toLowerCase().equals("(feat")) {
					featuring = splitWord.split(" ")[index];
					featuring = splitWord.substring(splitWord.indexOf(featuring), splitWord.length());
					if (isFromSongName)
						songName = songName.substring(0, songName.indexOf(featuring) - (songName.toLowerCase().contains("feat") ? 5 : 3));
					else
						songName = splitWord.substring(0, splitWord.indexOf(featuring) - 3);

					return new String[] { songName, featuring };
				}
			}
		}
		return null;
	}

	// Formats urls to get the video url. Some urls can contain playlists which need to be removed from the final url.
	public static String getCorrectYouTubeURL(String url) {
		url = url.replace("music.youtube.com", "www.youtube.com");
		if (url.contains("&list=")) {
			url = url.split("&list=")[0];
		}
		return url;
	}

	public static String getVideoIDFromURL(String url) {
		url = url.replaceAll(Pattern.quote("https://www.youtube.com/watch?v="), Matcher.quoteReplacement("")).replaceAll(Pattern.quote("https://youtube.com/watch?v="), Matcher.quoteReplacement("")).replaceAll(Pattern.quote("http://www.youtube.com/watch?v="), Matcher.quoteReplacement("")).replaceAll(Pattern.quote("http://youtube.com/watch?v="), Matcher.quoteReplacement(""));
		return url;
	}

	// public static JSONObject getSongInformationFromTitle2(String title) {
	// // Song name
	// // Artist
	// // Featuring
	// boolean isRemix = false;
	//
	// if (title.toLowerCase().contains("remix")) {
	// title = title.replaceAll("(?i)remix", "");
	// isRemix = true;
	// }
	//
	// title = getReplacedTitle(title).replace("–", "-");
	//
	// // If song title doesn't contain a - then an artist cannot be found, but we can still find the featuring
	// if (!title.contains(" - ") || !title.contains("- ") || !title.contains(" -")) {
	// String featuring = "";
	// String song_name = title;
	//
	// if (song_name.toLowerCase().contains("ft.") || song_name.toLowerCase().contains("feat.")) {
	// if (song_name.toLowerCase().contains("ft.")) {
	// featuring = song_name.split("(?i)ft.")[1];
	// song_name = song_name.split("(?i)ft.")[0];
	// } else {
	// featuring = song_name.split("(?i)feat.")[1];
	// song_name = song_name.split("(?i)feat.")[0];
	// }
	// }
	// if (song_name.toLowerCase().contains("ft") || song_name.toLowerCase().contains("feat")) {
	// String[] words = song_name.split(" ");
	// int index = 0;
	// for (String word : words) {
	// index++;
	// if (word.toLowerCase().equals("ft") || word.toLowerCase().equals("feat")) {
	// featuring = song_name.split(" ")[index];
	// featuring = song_name.substring(song_name.indexOf(featuring), song_name.length());
	// song_name = song_name.substring(0, song_name.indexOf(featuring) - (song_name.toLowerCase().contains("feat") ? 5 : 3));
	// }
	// }
	// }
	//
	// JSONObject object = new JSONObject();
	// object.put("song_name", song_name.trim());
	// object.put("featuring", featuring.trim());
	// object.put("isRemix", isRemix);
	//
	// return object;
	// }
	// try {
	// String artist = "";
	// if (title.contains(" - ")) {
	// artist = title.split(" - ")[0];
	// } else if (title.contains("- ")) {
	// artist = title.split("- ")[0];
	// } else if (title.contains(" -")) {
	// artist = title.split(" -")[0];
	// }
	//
	// String featuring = "";
	//
	// if (artist.toLowerCase().contains("ft.") || artist.toLowerCase().contains("feat.")) {
	// if (artist.toLowerCase().contains("ft.")) {
	// featuring = artist.split("(?i)ft.")[1];
	// artist = artist.split("(?i)ft.")[0];
	// } else {
	// featuring = artist.split("(?i)feat.")[1];
	// artist = artist.split("(?i)feat.")[0];
	// }
	// }
	//
	// String song_name = "";
	// if (title.contains(" - ")) {
	// song_name = title.split(" - ")[1];
	// } else if (title.contains("- ")) {
	// song_name = title.split("- ")[1];
	// } else if (title.contains(" -")) {
	// song_name = title.split(" -")[1];
	// }
	//
	// if (song_name.toLowerCase().contains("ft.") || song_name.toLowerCase().contains("feat.")) {
	// if (song_name.toLowerCase().contains("ft.")) {
	// featuring = song_name.split("(?i)ft.")[1];
	// song_name = song_name.split("(?i)ft.")[0];
	// } else {
	// featuring = song_name.split("(?i)feat.")[1];
	// song_name = song_name.split("(?i)feat.")[0];
	// }
	// }
	// if (artist.toLowerCase().contains("ft") || artist.toLowerCase().contains("feat")) {
	// String[] words = artist.split(" ");
	// int index = 0;
	// for (String word : words) {
	// index++;
	// if (word.toLowerCase().equals("ft") || word.toLowerCase().equals("feat")) {
	// featuring = artist.split(" ")[index];
	// featuring = artist.substring(artist.indexOf(featuring), artist.length());
	// song_name = artist.substring(0, artist.indexOf(featuring) - 3);
	// }
	// }
	// }
	//
	// if (song_name.toLowerCase().contains("ft") || song_name.toLowerCase().contains("feat")) {
	// String[] words = song_name.split(" ");
	// int index = 0;
	// for (String word : words) {
	// index++;
	// if (word.toLowerCase().equals("ft") || word.toLowerCase().equals("feat")) {
	// featuring = song_name.split(" ")[index];
	// featuring = song_name.substring(song_name.indexOf(featuring), song_name.length());
	// song_name = song_name.substring(0, song_name.indexOf(featuring) - (song_name.toLowerCase().contains("feat") ? 5 : 3));
	// }
	// }
	// }
	//
	// JSONObject details = new JSONObject();
	// details.put("artist", artist.trim());
	// details.put("song_name", song_name.trim());
	// details.put("featuring", featuring.trim());
	// details.put("isRemix", isRemix);
	//
	// return details;
	// } catch (Exception e) {
	// JSONObject object = new JSONObject();
	// object.put("song_name", getReplacedTitle(title));
	// return object;
	// }
	// }

}