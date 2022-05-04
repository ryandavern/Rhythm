package com.beatplaylist.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Tooltip;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class Utilities {

	private static Utilities instance = new Utilities();

	public static Utilities getInstance() {
		return instance;
	}

	private int loginAttempts;
	public String host = "localhost";
	public MediaPlayer mediaPlayer;
	public ServerSocket incoming_socket;
	private Timeline loginTimer;

	public void closeServerSocket(ServerSocket socket) {
		if (socket == null)
			return;
		try {
			socket.close();
			socket = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getCountry() {
		BufferedReader in = null;
		try {
			URL url = new URL("https://beatplaylist.com/api/geo");
			URLConnection connection = url.openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux x86_64; en-GB; rv:1.8.1.6) Gecko/20070723 Iceweasel/2.0.0.6 (Debian-2.0.0.6-0etch1)");
			connection.setRequestProperty("Accept-Charset", "UTF-8");
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String ip = in.readLine();
			if (ip.equals("XX"))
				return "US";
			return ip;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return "US";
	}

	public String getLocalDirectory() {
		if (System.getProperty("os.name").toLowerCase().startsWith("win"))
			return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\BeatPlaylist";
		else
			return "~\\Library\\Application Support\\BeatPlaylist";
	}

	public String getDesktop() {
		if (System.getProperty("os.name").toLowerCase().startsWith("win"))
			return "C:\\Users\\" + System.getProperty("user.name") + "\\Desktop\\";
		else
			return "~\\Desktop\\";
	}

	public String getRandomCode(int size) {
		String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuilder stringBuilder = new StringBuilder(size);
		for (int i = 0; i < size; i++)
			stringBuilder.append(chars.charAt(new Random().nextInt(chars.length())));
		return stringBuilder.toString();
	}

	public void setToolTipTime(Tooltip tooltip) {
		try {
			Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
			fieldBehavior.setAccessible(true);
			Object objBehavior = fieldBehavior.get(tooltip);

			Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
			fieldTimer.setAccessible(true);
			Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

			objTimer.getKeyFrames().clear();
			objTimer.getKeyFrames().add(new KeyFrame(new Duration(250)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public MediaPlayer getMediaPlayer() {
		return this.mediaPlayer;
	}

	public void startLoginTimer() {

		if (this.loginTimer == null) {
			this.loginTimer = new Timeline(new KeyFrame(Duration.seconds(7), event -> {
				decrementLoginAttempt();
			}));
			this.loginTimer.play();
			this.loginTimer.setCycleCount(Timeline.INDEFINITE);
		} else {
			if (this.loginTimer.getStatus() == Status.STOPPED) {
				this.loginTimer.play();
				return;
			}
		}
	}

	public void decrementLoginAttempt() {
		this.loginAttempts--;
		System.out.println(this.loginAttempts);
		if (this.loginAttempts <= 0) {
			this.loginTimer.stop();
		}
	}

	public void incrementLoginAttempt() {
		this.loginAttempts++;
	}

	public int getLoginAttempts() {
		return this.loginAttempts;
	}

	public void resetLoginAttempt() {
		this.loginAttempts = 0;
		if (this.loginTimer != null) {
			this.loginTimer.stop();
			this.loginTimer = null;
		}
	}

	// private void setupCustomTooltipBehavior(int openDelayInMillis, int visibleDurationInMillis, int closeDelayInMillis) {
	// try {
	// Class<?> TTBehaviourClass = null;
	// Class<?>[] declaredClasses = Tooltip.class.getDeclaredClasses();
	// for (Class<?> c : declaredClasses) {
	// if (c.getCanonicalName().equals("javafx.scene.control.Tooltip.TooltipBehavior")) {
	// TTBehaviourClass = c;
	// break;
	// }
	// }
	// if (TTBehaviourClass == null)
	// return;
	// Constructor<?> constructor = TTBehaviourClass.getDeclaredConstructor(Duration.class, Duration.class, Duration.class, boolean.class);
	// if (constructor == null)
	// return;
	// constructor.setAccessible(true);
	// Object newTTBehaviour = constructor.newInstance(new Duration(openDelayInMillis), new Duration(visibleDurationInMillis), new Duration(closeDelayInMillis), false);
	// if (newTTBehaviour == null)
	// return;
	// Field ttbehaviourField = Tooltip.class.getDeclaredField("BEHAVIOR");
	// if (ttbehaviourField == null)
	// return;
	// ttbehaviourField.setAccessible(true);
	// // Cache the default behavior if needed.
	// // Object defaultTTBehavior = ttbehaviourField.get(Tooltip.class);
	// ttbehaviourField.set(Tooltip.class, newTTBehaviour);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
}