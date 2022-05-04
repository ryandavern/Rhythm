package com.beatplaylist.utilities;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import com.beatplaylist.chromium.YouTube;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.utilities.data.Data;
import com.beatplaylist.utilities.playlist.PlaylistManager;

import javafx.application.Platform;

public class KeyboardHookListener implements NativeKeyListener {

	public KeyboardHookListener() {
	}

	public void nativeKeyPressed(NativeKeyEvent event) {

	}

	public void nativeKeyReleased(NativeKeyEvent event) {
		if (event.getKeyCode() == NativeKeyEvent.VC_MEDIA_PLAY) {
			Platform.runLater(() -> {
				if (Data.getInstance().isPaused)
					YouTube.setVideoPauseState(false);
				else
					YouTube.setVideoPauseState(true);
			});
		} else if (event.getKeyCode() == NativeKeyEvent.VC_MEDIA_PREVIOUS) {
			Platform.runLater(() -> {
				if (PlaylistManager.getInstance().current_playlist != null)
					GUIManager.getInstance().audioBar.audioListener.selectSong(PlaylistManager.getInstance().current_playlist.getPosition() - 1, PlaylistManager.getInstance().current_playlist);
			});

		} else if (event.getKeyCode() == NativeKeyEvent.VC_MEDIA_NEXT) {
			Platform.runLater(() -> {
				if (PlaylistManager.getInstance().current_playlist != null)
					GUIManager.getInstance().audioBar.audioListener.selectSong(PlaylistManager.getInstance().current_playlist.getPosition() + 1, PlaylistManager.getInstance().current_playlist);
			});
		} else if (event.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
			Platform.runLater(() -> {
				if (GUIManager.getInstance().stage.isFullScreen()) {
					GUIManager.getInstance().videoBrowser.fullscreenHandler.onFullScreenExit();
					GUIManager.getInstance().videoBrowser.getWebEngine().mainFrame().get().executeJavaScript("document.exitFullscreen();");
				}
			});
		}
	}

	public void nativeKeyTyped(NativeKeyEvent event) {
	}
}