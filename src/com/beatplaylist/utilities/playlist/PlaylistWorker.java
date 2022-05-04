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

import com.beatplaylist.gui.utilities.playlist.SongView;
import com.beatplaylist.utilities.events.CompleteEvent;
import com.beatplaylist.utilities.network.post.AddOrRemoveSongFromPlaylist;
import com.beatplaylist.utilities.network.post.CreatePlaylist;
import com.beatplaylist.utilities.network.post.DeletePlaylist;
import com.beatplaylist.utilities.user.UserManager;

public class PlaylistWorker {

	public static void updateSongInPlaylist(Playlist playlist, SongView songView, boolean remove, CompleteEvent event) {
		if (playlist.getCreatorUsername().equals(UserManager.getInstance().getUser().username) || playlist.getRole() == RoleType.EDIT) {
			AddOrRemoveSongFromPlaylist.send(playlist, songView.song, remove, event);
		}
	}

	// Called in Popup class, addSongToPlaylist function.
	public static void updateSongInPlaylist(Playlist playlist, Song song, boolean remove, CompleteEvent event) {
		if (playlist.getCreatorUsername().equals(UserManager.getInstance().getUser().username) || playlist.getRole() == RoleType.EDIT) {
			AddOrRemoveSongFromPlaylist.send(playlist, song, remove, event);
		}
	}

	public static void deletePlaylist(String url, CompleteEvent event) {
		DeletePlaylist.send(url, event);
	}

	public static void createPlaylist(Playlist playlist, String name, String description, String visibility, boolean syncToPlaylist, CompleteEvent event) {
		CreatePlaylist.send(playlist, name, description, visibility, syncToPlaylist, event);
	}
}