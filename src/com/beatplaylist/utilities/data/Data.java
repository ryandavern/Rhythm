package com.beatplaylist.utilities.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.beatplaylist.enums.SizeType;
import com.beatplaylist.enums.StatusType;

public class Data {

	private static Data instance = new Data();

	public static Data getInstance() {
		return instance;
	}

	// Stores volume and current song listening length;
	public double volumeLevel = 0.40, listenLength = -1, lastTotalDuration = -1;

	// Last track submitted to the database, so we don't duplicate entries.
	// Last liked song check request.
	public String lastSubmittedTrackURL = "", lastLikedSongURLCheck = "";

	// Stores cached user liked music count.
	public int cachedLikedMusicCount = -1, cachedRecentlyAddedMusicCount = -1;

	// Stores the country from the ip address associated with the account and computer mac address.
	public String country = "", macAddress = "";

	// Stores reported posts in the users current session. This is to stop duplicate post reports.
	public List<String> reported_posts = new ArrayList<>();

	// Is the user connected to a server or did an error occur?
	public StatusType current_status = StatusType.OFFLINE;

	// Stores login and application scene sizes.
	public SizeType loginSize = SizeType.SIZE_LOGIN, mainSize = SizeType.SIZE_TYPE1920X1080; // SIZE_TYPE1620X720 SIZE_TYPE1280X620

	// Stores company and my personal twitter and instagram accounts.
	public String company_twitter = "", personal_twitter = "", company_instagram = "", personal_instagram = "";

	// isPaused = Has the user paused their music.
	// repeat = Does the user want to repeat the song they are listening to?
	// shuffle = Does the user want to listen to random music in their playlist?
	// playing_queue = is the user listening to music from the queue?
	public boolean isPaused = false, repeat = false, shuffle = false, playing_queue = false, isFirstBrowser = true, hasPlayedOneSong = false;

	// Keywords to define if a the video a user is watching / listen to is a song / music video or just a random video.
	public List<String> song_video_keywords = Arrays.asList("( Official Music Video )", "(Video)", "(Music Video)", "[Official Dance Video]", "(Official HD Music Video)", "[OFFICIAL VIDEO]", "(Official Video)", "(Official Music Video)", "[Official Music Video]", "[Official Video]", " Official Music Video", "Music Video", "MUSIC VIDEO", " - Official Video", "Official Video");
	public List<String> song_audio_keywords = Arrays.asList("(Audio Only)", "[Official Audio]", "[Official Lyric Video]", "[OFFICIAL AUDIO]", "(Audio)", "(Lyric Video)", "(Official Audio)", "[Lyric Video]", "[Lyrics]", "[Offical Audio]", "(Lyric)", "(Lyrics)", "Lyrics", "Lyric", " - Lyrics", "- Lyrics", "- Lyric", " - Official Audio");
}