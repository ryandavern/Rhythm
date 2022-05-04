package com.beatplaylist.utilities.notification;

public class NotificationManager {

	private static NotificationManager instance = new NotificationManager();

	public static NotificationManager getInstance() {
		return instance;
	}

	public int unread_messages = 0, unread_notifications = 0;

	public void setHasNotifications(int notifications) {
		// if (notifications <= 0)
		// return;
		// Settings.getInstance().setHasNotification(true, true);
		// Button button = Data.getInstance().buttons.get(Data.getInstance().getTab(TabType.NOTIFICATION));
		// button.setStyle(button.getStyle() + "-fx-text-fill: " + CustomColor.NOTIFICATION.getColorHex() + ";");
		// unread_notifications += notifications;
//		if (unread_notifications > 9)
//			TabBuilder.getInstance().notification_circles.get(TabType.NOTIFICATION).setText("9+");
//		else
//			TabBuilder.getInstance().notification_circles.get(TabType.NOTIFICATION).setText(String.valueOf(unread_notifications));
//
//		TabBuilder.getInstance().notification_panes.get(TabType.NOTIFICATION).setVisible(true);
	}

	public void setHasMessages(int notifications) {
//		if (notifications <= 0)
//			return;
//		Settings.getInstance().setHasUnreadMessage(true, true);
//		Button button = Data.getInstance().buttons.get(Data.getInstance().getTab(TabType.MESSAGES));
//		button.setStyle(button.getStyle() + "-fx-text-fill: " + CustomColor.NOTIFICATION.getColorHex() + ";");
//		unread_messages += notifications;
//		if (unread_messages > 9)
//			TabBuilder.getInstance().notification_circles.get(TabType.MESSAGES).setText("9+");
//		else
//			TabBuilder.getInstance().notification_circles.get(TabType.MESSAGES).setText(String.valueOf(unread_messages));
//
//		TabBuilder.getInstance().notification_panes.get(TabType.MESSAGES).setVisible(true);
	}

	public void removeMessage(int notifications) {
//		unread_messages -= notifications;
//		if (unread_messages > 0) {
//			Settings.getInstance().setHasUnreadMessage(true, true);
//			Button button = Data.getInstance().buttons.get(Data.getInstance().getTab(TabType.MESSAGES));
//			button.setStyle(button.getStyle() + "-fx-text-fill: " + CustomColor.NOTIFICATION.getColorHex() + ";");
//			if (unread_messages > 9)
//				TabBuilder.getInstance().notification_circles.get(TabType.MESSAGES).setText("9+");
//			else
//				TabBuilder.getInstance().notification_circles.get(TabType.MESSAGES).setText(String.valueOf(unread_messages));
//
//			TabBuilder.getInstance().notification_panes.get(TabType.MESSAGES).setVisible(true);
//		} else {
//			Settings.getInstance().setHasUnreadMessage(true, true);
//			Button button = Data.getInstance().buttons.get(Data.getInstance().getTab(TabType.MESSAGES));
//			button.setStyle(button.getStyle() + "-fx-text-fill: " + CustomColor.GRAY.getColorHex() + ";");
//
//			TabBuilder.getInstance().notification_panes.get(TabType.MESSAGES).setVisible(false);
//		}
	}

}