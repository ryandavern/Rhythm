package com.beatplaylist.settings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.beatplaylist.Options;
import com.beatplaylist.enums.ShuffleType;
import com.beatplaylist.utilities.CustomColor;
import com.beatplaylist.utilities.Utilities;
import com.beatplaylist.utilities.data.Data;
import com.beatplaylist.utilities.filemanager.FileLoader;
import com.beatplaylist.utilities.filemanager.FileUtilities;
import com.beatplaylist.utilities.filemanager.OperatingSystem;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.playlist.PlaylistOrderType;
import com.beatplaylist.utilities.security.FileCipher;
import com.beatplaylist.utilities.update.UpdateManager;
import com.beatplaylist.utilities.validation.ValidateManager;
import com.install4j.api.windows.RegistryRoot;
import com.install4j.api.windows.WinRegistry;

public class Settings {

	private boolean mediaKeysEnabled = false, hardwareAccelerated = false, crossFadeSongs = false, launchMinimized = false, autoLogin = false, programLaunchOnComputerStartup = false, loudness_equalization = true, display_current_song = true, show_adverts = true, hasCheckedUpdate = false, hasNotification = false, hasUnreadMessage = false, overlay = false, hidpi = true;
	private String music_directory = "", accessToken = "", last_announcement = "", last_account_key = "", playlist_song_order = "";
	private PlaylistOrderType createdPlaylistOrder, followedPlaylistOrder;
	private int crossFadeSeconds = 0;
	private ShuffleType shuffleType = ShuffleType.PLAYLIST_SORT;
	private double lastVolume = -1;

	public Map<String, String> playlistColors = new HashMap<>();

	// YouTube
	private boolean hide_browser;

	private static Settings instance = new Settings();

	public static Settings getInstance() {
		return instance;
	}

	public void setHiDPI(boolean hidpi, boolean save) {
		this.hidpi = hidpi;
		if (save)
			save();
	}
	
	public boolean hidpi() {
		return this.hidpi;
	}

	public void updateVolume(boolean save) {
		this.lastVolume = Data.getInstance().volumeLevel;
		if (save)
			save();
	}

	public double getLastVolume() {
		return this.lastVolume;
	}

	public void setLaunchMinimized(boolean value, boolean save) {
		this.launchMinimized = value;
		if (save)
			save();
	}

	public boolean launchMinimized() {
		return this.launchMinimized;
	}

	public void setAutoLogin(boolean value, boolean save) {
		this.autoLogin = value;
		if (save)
			save();
	}

	public void setPlaylistOrder(String value, boolean save) {
		this.playlist_song_order = value;
		if (save)
			save();
	}

	public String getPlaylistOrder() {
		return this.playlist_song_order;
	}

	public void setMediaKeysEnabled(boolean value, boolean save) {
		this.mediaKeysEnabled = value;
		if (save)
			save();
	}

	public boolean hasMediaKeysEnabled() {
		return this.mediaKeysEnabled;
	}

	public void setCreatedPlaylistOrder(PlaylistOrderType value, boolean save) {
		this.createdPlaylistOrder = value;
		if (save)
			save();
	}

	public void setFollowedPlaylistOrder(PlaylistOrderType value, boolean save) {
		this.followedPlaylistOrder = value;
		if (save)
			save();
	}

	public PlaylistOrderType getCreatedPlaylistOrder() {
		return this.createdPlaylistOrder;
	}

	public PlaylistOrderType getFollowedPlaylistOrder() {
		return this.followedPlaylistOrder;
	}

	public boolean hasAutoLogin() {
		return this.autoLogin;
	}

	public void setCrossFadeSongs(boolean value, boolean save) {
		this.crossFadeSongs = value;
		if (save)
			save();
	}

	public boolean crossFadeSongEnabled() {
		return this.crossFadeSongs;
	}

	public void setCrossFadeLength(int value, boolean save) {
		this.crossFadeSeconds = value;
		if (save)
			save();
	}

	public int getCrossFadeLength() {
		return this.crossFadeSeconds;
	}

	public void setHasNotification(boolean value, boolean save) {
		this.hasNotification = value;
		if (save)
			save();
	}

	public void setHasUnreadMessage(boolean value, boolean save) {
		this.hasUnreadMessage = value;
		if (save)
			save();
	}

	public void setLoudnessEqualization(boolean value, boolean save) {
		this.loudness_equalization = value;
		if (save)
			save();
	}

	public boolean hasLoudnessEqualization() {
		return this.loudness_equalization;
	}

	public boolean hasNotification() {
		return this.hasNotification;
	}

	public boolean hasUnreadMessage() {
		return this.hasUnreadMessage;
	}

	public void setLastAccountKey(String value, boolean save) {
		this.last_account_key = value;
		if (save)
			save();
	}

	public void setHideBrowser(boolean value, boolean save) {
		this.hide_browser = value;
		if (save)
			save();
	}

	public void setMusicDirectory(String value, boolean save) {
		this.music_directory = value;
		if (save)
			save();
	}

	public void setOverlayEnabled(boolean value, boolean save) {
		this.overlay = value;
		if (save)
			save();
	}

	public void setAccountCredentials(String email, String password) {
		set("cached_email", FileCipher.getInstance().encode(email, 12));
		set("cached_password", FileCipher.getInstance().encode(password, 12));
	}

	public void setAccountEmail(String email) {
		set("cached_email", FileCipher.getInstance().encode(email, 12));
	}

	public void setAccountPassword(String password) {
		set("cached_password", FileCipher.getInstance().encode(password, 12));
	}

	public void setDisplayMusic(boolean value, boolean save) {
		this.display_current_song = value;
		if (save)
			save();
	}

	public void setShowVideoAdverts(boolean value, boolean save) {
		this.show_adverts = value;
		if (save)
			save();
	}

	public void setHasCheckedUpdate(boolean value, boolean save) {
		this.hasCheckedUpdate = value;
		if (save)
			save();
	}

	public void setLastAnnouncement(String value, boolean save) {
		this.last_announcement = value;
		if (save)
			save();
	}

	public void setHardwareAccelerated(boolean value, boolean save) {
		this.hardwareAccelerated = value;
		if (save)
			save();
	}

	public boolean isHardwareAccelerated() {
		return this.hardwareAccelerated;
	}

	public void setProgramLaunchOnComputerStartup(boolean value) {
		this.programLaunchOnComputerStartup = value;
		this.programLaunchOnComputerStartup = WinRegistry.getValue(RegistryRoot.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Run", "Rhythm") != null;
	}

	public String getLastAnnouncement() {
		return this.last_announcement;
	}

	public String getLastAccountKey() {
		return this.last_account_key;
	}

	public boolean hasCheckedUpdate() {
		return this.hasCheckedUpdate;
	}

	public boolean showVideoAdverts() {
		return this.show_adverts;
	}

	public boolean canDisplayMusic() {
		return this.display_current_song;
	}

	public boolean isBrowserHidden() {
		return this.hide_browser;
	}

	public boolean overlayEnabled() {
		return this.overlay;
	}

	public boolean programLaunchOnComputerStartup() {
		return this.programLaunchOnComputerStartup;
	}

	public String getMusicDirectory() {
		if (this.music_directory == null || this.music_directory.isEmpty())
			return "C:\\Users\\" + System.getProperty("user.name") + "\\Desktop";
		if (this.music_directory.endsWith("\\"))
			return this.music_directory;
		else
			return this.music_directory + "\\";
	}

	public String getDefaultColor() {
		// if (this.color == null || this.color.isEmpty())
		// this.color = CustomColor.GREEN.getColorHex();
		// if (!isHex(this.color.replace("/", "").replace(";", "")))
		// return CustomColor.GREEN.getColorHex();
		//
		// return this.color.replace("/", "").replace(";", "");
		return CustomColor.RHYTHM.getColorHex();
	}

	public boolean isHex(String hex) {
		String HEX_PATTERN = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
		Pattern pattern = Pattern.compile(HEX_PATTERN);
		Matcher matcher = pattern.matcher(hex);
		return matcher.matches();
	}

	public String getAccessToken() {
		return this.accessToken;
	}

	public void setAccessToken(String accessToken, boolean save) {
		this.accessToken = accessToken;
		if (save)
			save();
	}

	public void setShuffleType(String shuffleType, boolean save) {
		ShuffleType type = ShuffleType.getTypeByLongName(shuffleType);

		this.shuffleType = type;
		if (save)
			save();
	}

	public ShuffleType getShuffleType() {
		return this.shuffleType;
	}

	public String getCachedEmail() {
		JSONWrapper settings = new JSONWrapper(FileUtilities.getInstance().settingsData);

		if (!settings.keyExists("cached_email"))
			return "";

		return FileCipher.getInstance().decode(settings.getJSONString("cached_email"), 12);
	}

	public String getCachedPassword() {
		JSONWrapper settings = new JSONWrapper(FileUtilities.getInstance().settingsData);
		if (!settings.keyExists("cached_password"))
			return "";

		return FileCipher.getInstance().decode(settings.getJSONString("cached_password"), 12);
	}

	public void createDirectory(String directory) {
		File file = new File(Utilities.getInstance().getLocalDirectory() + directory);
		if (!file.exists())
			file.mkdir();
	}

	public void loadSettings() {
		// createDirectory("//playlists//");

		FileLoader.getInstance().loadSettings();
		FileLoader.getInstance().loadPlaylistColorSettings();

		JSONWrapper settings = new JSONWrapper(FileUtilities.getInstance().settingsData);

		for (SettingType setting : SettingType.values()) {
			if (!settings.keyExists(setting.getSettingName()))
				set(setting.getSettingName(), setting.getDefaultValue());
		}

		setMusicDirectory(settings.getJSONString("music_directory"), false);
		setDisplayMusic(settings.getJSONBoolean("display_music"), false);
		setHideBrowser(settings.getJSONBoolean("hide_browser"), false);
		setShowVideoAdverts(settings.getJSONBoolean("show_video_adverts"), false);
		setHasNotification(settings.getJSONBoolean("notification"), false);
		setHasUnreadMessage(settings.getJSONBoolean("unread_message"), false);
		setOverlayEnabled(settings.getJSONBoolean("overlay_enabled"), false);
		setLoudnessEqualization(settings.getJSONBoolean("loudness_equalization"), false);
		setAutoLogin(settings.getJSONBoolean("autoLogin"), false);
		setLastAnnouncement(settings.getJSONString("lastAnnouncement"), false);
		setAccessToken(settings.getJSONString("accessKey"), false);
		setPlaylistOrder(settings.getJSONString("playlist_order"), false);

		if (settings.getJSONString("created_playlist_order") != null && !settings.getJSONString("created_playlist_order").equals("null") && PlaylistOrderType.getOrderByName(settings.getJSONString("created_playlist_order")) != null)
			setCreatedPlaylistOrder(PlaylistOrderType.getOrderByName(settings.getJSONString("created_playlist_order")), false);
		else
			setCreatedPlaylistOrder(PlaylistOrderType.PLAYLIST_INDEX_REVERSE, false);

		if (settings.getJSONString("followed_playlist_order") != null && !settings.getJSONString("followed_playlist_order").equals("null") && PlaylistOrderType.getOrderByName(settings.getJSONString("followed_playlist_order")) != null)
			setFollowedPlaylistOrder(PlaylistOrderType.getOrderByName(settings.getJSONString("followed_playlist_order")), false);
		else
			setFollowedPlaylistOrder(PlaylistOrderType.PLAYLIST_INDEX_REVERSE, false);

		String version = settings.getJSONString("checked_update_version");
		if (version != null && version.equals(UpdateManager.getClientVersion()))
			setHasCheckedUpdate(true, false);
		else
			setHasCheckedUpdate(false, false);
		setLastAccountKey(settings.getJSONString("last_account_key"), false);
		setLaunchMinimized(settings.getJSONBoolean("launchMinimized"), false);
		setCrossFadeSongs(settings.getJSONBoolean("crossFadeSongs"), false);
		setCrossFadeLength(settings.getJSONInteger("crossFadeLength"), false);
		setShuffleType(settings.getJSONString("shuffleType"), false);
		setHardwareAccelerated(settings.getJSONBoolean("hardwareAcceleration"), false);
		setMediaKeysEnabled(settings.getJSONBoolean("mediaKeysEnabled"), false);
		setHiDPI(settings.getJSONBoolean("hidpi"), false);
		try {
			System.out.println("VOLUME: " + Double.valueOf(settings.getJSONString("lastVolume")));
			Data.getInstance().volumeLevel = Double.valueOf(settings.getJSONString("lastVolume"));
		} catch (Exception e) {
			Data.getInstance().volumeLevel = 0.4;
		}
		updateVolume(false);

		JSONWrapper playlistColorJSON = new JSONWrapper(FileLoader.getInstance().loadPlaylistColorSettings());

		if (playlistColorJSON.getJSONObject() != null) {
			if (playlistColorJSON.getJSONObject().get("playlistColors") != null) {
				Iterator<JSONObject> playlistColors = playlistColorJSON.getJSONArray("playlistColors");

				while (playlistColors.hasNext()) {
					JSONWrapper playlistColor = new JSONWrapper(playlistColors.next());

					this.playlistColors.put(playlistColor.getJSONString("playlistURL"), playlistColor.getJSONString("color"));
				}
				System.out.println("Loaded Playlist Colors: " + this.playlistColors.size());
			}
		}
		if (!Options.test_mode) {
			if (FileUtilities.getInstance().getOperatingSystem() == OperatingSystem.WINDOWS) {
				// if (WinRegistry.getValue(RegistryRoot.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Run", "BeatPlaylist") != null) {
				// WinRegistry.deleteValue(RegistryRoot.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Run", "BeatPlaylist");
				// }
				System.err.println(WinRegistry.getValue(RegistryRoot.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Run", "Rhythm"));
				this.programLaunchOnComputerStartup = WinRegistry.getValue(RegistryRoot.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Run", "Rhythm") != null;
			}
		}
	}

	public void set(String key, String value) {
		FileUtilities.getInstance().settingsData.put(key, value);
		try {
			FileWriter file = new FileWriter(getDirectory() + "settings.txt");

			file.write(FileUtilities.getInstance().settingsData.toJSONString());
			file.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean playlistHasCustomColor(String url) {
		return this.playlistColors.get(url) != null;
	}

	public String getPlaylistColor(String url) {
		return this.playlistColors.get(url);
	}

	public void addPlaylistColor(String url, String color) {
		this.playlistColors.put(url, color);

		updatePlaylistColors(true);
	}

	public void updatePlaylistColors(boolean save) {
		try {
			JSONObject object = new JSONObject();
			JSONArray array = new JSONArray();

			object.put("playlistColors", array);

			for (Entry<String, String> colors : this.playlistColors.entrySet()) {
				JSONObject obj = new JSONObject();

				obj.put("playlistURL", colors.getKey());
				obj.put("color", colors.getValue());

				array.add(obj);
			}

			FileWriter file = new FileWriter(getDirectory() + "playlistColors.json");

			file.write(object.toJSONString());
			file.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void save() {
		set("default_profile_color", getDefaultColor());
		set("music_directory", getMusicDirectory());
		set("display_music", String.valueOf(canDisplayMusic()));
		set("hide_browser", String.valueOf(isBrowserHidden()));
		set("show_video_adverts", String.valueOf(showVideoAdverts()));
		set("checked_update_version", UpdateManager.getClientVersion());
		set("last_account_key", getLastAccountKey());
		set("notification", String.valueOf(hasNotification()));
		set("unread_message", String.valueOf(hasUnreadMessage()));
		set("overlay_enabled", String.valueOf(overlayEnabled()));
		set("loudness_equalization", String.valueOf(hasLoudnessEqualization()));
		set("autoLogin", String.valueOf(hasAutoLogin()));
		set("lastAnnouncement", getLastAnnouncement());
		set("accessKey", getAccessToken());
		set("playlist_order", getPlaylistOrder());
		set("created_playlist_order", getCreatedPlaylistOrder().name());
		set("followed_playlist_order", getFollowedPlaylistOrder().name());
		set("launchMinimized", String.valueOf(launchMinimized()));
		set("crossFadeSongs", String.valueOf(crossFadeSongEnabled()));
		set("crossFadeLength", String.valueOf(getCrossFadeLength()));
		set("shuffleType", getShuffleType().name());
		set("hardwareAcceleration", String.valueOf(isHardwareAccelerated()));
		set("mediaKeysEnabled", String.valueOf(hasMediaKeysEnabled()));
		set("lastVolume", String.valueOf(getLastVolume()));
		set("hidpi", String.valueOf(hidpi()));
	}

	private String getDirectory() {
		String directory = "";
		if (FileUtilities.getInstance().getOperatingSystem() == OperatingSystem.WINDOWS)
			directory = FileUtilities.getInstance().getAppData() + "\\BeatPlaylist" + File.separator;
		else if (FileUtilities.getInstance().getOperatingSystem() == OperatingSystem.MACOS)
			directory = File.separator + "Applications" + File.separator + "Rhythm.app" + File.separator + "Contents" + File.separator + "Settings" + File.separator;
		return directory;
	}
}