package com.beatplaylist.utilities.network.post;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.beatplaylist.chromium.BrowserManager;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.utilities.playlist.SongTitle;
import com.beatplaylist.settings.Settings;
import com.beatplaylist.utilities.data.Data;
import com.beatplaylist.utilities.events.SocketReceiveEvent;
import com.beatplaylist.utilities.network.netty.NETTYClient;
import com.beatplaylist.utilities.network.serialized.PacketType;
import com.beatplaylist.utilities.network.serialized.Post;

public class SendCurrentSong {

	public static void send(double listen_time) {
		if (BrowserManager.getInstance().getCurrentBrowser() == null || BrowserManager.getInstance().getVideoURL().equals(Data.getInstance().lastSubmittedTrackURL) || Data.getInstance().isPaused || !BrowserManager.getInstance().getVideoURL().contains("watch")) {
			return;
		}
		Data.getInstance().lastSubmittedTrackURL = BrowserManager.getInstance().getVideoURL();

		String url = BrowserManager.getInstance().getVideoURL();

		Post post = new Post();
		post.setDetails();
		post.setPacketType(PacketType.STORE_ANALYTIC);

		JSONObject json = new JSONObject();
		json.put("song_url", JSONValue.escape(SongTitle.getVideoIDFromURL(url)));
		json.put("listen_time", JSONValue.escape(String.valueOf(listen_time)));
		json.put("displayMusic", JSONValue.escape(String.valueOf(Settings.getInstance().canDisplayMusic())));
		json.put("country", JSONValue.escape(Data.getInstance().country));

		post.setJSONArray(json);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {
			@Override
			public void onSuccess(Post post) {
			}

			@Override
			public void onError(String error) {
			}
		});
	}
}