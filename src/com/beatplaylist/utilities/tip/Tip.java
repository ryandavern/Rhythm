package com.beatplaylist.utilities.tip;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;
import com.beatplaylist.utilities.user.UserManager;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Tip {

	// Unused - Will add back to UI soon.
	public static void runTipAlert() {
		List<TipType> tip_list = new ArrayList<>();

		for (TipType tips : TipType.values()) {
			if (UserManager.getInstance().getUser().isPremium() && !tips.isNonPremiumMessage()) {
				tip_list.add(tips);
			} else {
				tip_list.add(tips);
			}
		}
		Timeline timeline = new Timeline(new KeyFrame(Duration.minutes(3), event -> {
			Notification.getInstance().createNotification("Tip", tip_list.get(new Random().nextInt(tip_list.size())).getMessage(), AlertType.SUCCESS);
		}));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
	}
}