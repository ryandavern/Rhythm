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

package com.beatplaylist.gui.module.layout.sidebar;

import com.beatplaylist.Main;
import com.beatplaylist.enums.FontType;
import com.beatplaylist.enums.StatusType;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.settings.Settings;
import com.beatplaylist.utilities.CustomColor;
import com.beatplaylist.utilities.CustomToolTip;
import com.beatplaylist.utilities.notification.NotificationManager;
import com.beatplaylist.utilities.user.UserManager;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class SideTab {

	// Stores the specific tab we are dealing with.
	public TabType tab;

	// Stores tab text and tab icon. The HBox is a fake button.
	public HBox tabHbox;

	public Text tabButtonText, notificationCountText;

	public ImageView tabIconImageView;

	public StackPane notificationStackPane;

	public Circle notificationCircle;

	public boolean disabled = false;

	public SideTab(TabType tab) {
		this.tab = tab;

		this.tabHbox = new HBox(10);
		this.tabButtonText = new Text(tab.getName().toUpperCase());
		this.notificationCountText = new Text("9+");

		if (!tab.getIconURL().isEmpty()) {
			this.tabIconImageView = new ImageView(new Image(Main.class.getResource("/resources/tab_icons/" + tab.getIconURL() + ".png").toExternalForm()));
			this.tabHbox.getChildren().add(this.tabIconImageView);
		}

		this.tabHbox.setMinWidth(250);
		this.tabHbox.setMaxWidth(250);
		this.tabHbox.setMinHeight(60);
		this.tabHbox.setAlignment(Pos.CENTER_LEFT);
		this.tabHbox.setStyle("-fx-cursor: hand; -fx-background-color: transparent; -fx-padding: 0px;");

		if (StatusType.isStatus(StatusType.OFFLINE) || !tab.isEnabled() || (tab.isEnabled() && UserManager.getInstance().user.accountType.isSmaller(tab.getAccountType()))) {
			this.tabHbox.setOpacity(0.5);
			this.tabButtonText.setOpacity(0.5);
			this.tabIconImageView.setOpacity(0.5);
			CustomToolTip.install(this.tabHbox, new CustomToolTip("This tab is currently disabled and will be enabled in a future update"));
		}
		this.tabButtonText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 15));
		this.tabButtonText.setFill(Color.web("#95A5A6"));

		this.tabHbox.getChildren().add(this.tabButtonText);

		if (tab == TabType.INBOX || tab == TabType.NOTIFICATION) {
			this.notificationCircle = new Circle(10);

			this.notificationCircle.setFill(Color.web(CustomColor.RED.getColorHex()));

			this.notificationCountText.setFill(Color.web(CustomColor.WHITE.getColorHex()));
			this.notificationCountText.setFont(Font.font(FontType.DEFAULT.getName(), 12));

			this.notificationStackPane = new StackPane();
			this.notificationStackPane.getChildren().addAll(this.notificationCircle, this.notificationCountText);
			this.notificationStackPane.setVisible(false);

			this.tabHbox.getChildren().add(this.notificationStackPane);
		}

		this.tabHbox.setOnMouseClicked(event -> {
			if (isDisabled() || !tab.isEnabled() || (GUIManager.getInstance().currentTab != null && GUIManager.getInstance().currentTab.getTabType() == tab) || (tab.isEnabled() && UserManager.getInstance().user.accountType.isSmaller(tab.getAccountType())))
				return;
			GUIManager.getInstance().sideBar.sideBarTab.changeTab(this);
		});
		this.tabHbox.setOnMouseEntered(event -> {
			if (isDisabled() || !tab.isEnabled() || (GUIManager.getInstance().currentTab != null && GUIManager.getInstance().currentTab.getTabType() == tab) || (tab.isEnabled() && UserManager.getInstance().user.accountType.isSmaller(tab.getAccountType())))
				return;
			if (tab == TabType.INBOX && NotificationManager.getInstance().unread_messages > 0 || tab == TabType.NOTIFICATION && Settings.getInstance().hasNotification())
				return;

			this.tabButtonText.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		});
		this.tabHbox.setOnMouseExited(event -> {
			if (isDisabled() || !tab.isEnabled() || (GUIManager.getInstance().currentTab != null && GUIManager.getInstance().currentTab.getTabType() == tab) || (tab.isEnabled() && UserManager.getInstance().user.accountType.isSmaller(tab.getAccountType())))
				return;

			if (tab == TabType.INBOX && NotificationManager.getInstance().unread_messages > 0 || tab == TabType.NOTIFICATION && Settings.getInstance().hasNotification()) {
				this.tabButtonText.setFill(Color.web(CustomColor.NOTIFICATION.getColorHex()));
			} else {
				this.tabButtonText.setFill(Color.web("#95A5A6"));
			}
		});
	}

	// Called when the tab is changed.
	public void selectTab() {
		if (GUIManager.getInstance().currentTab != null) {
			GUIManager.getInstance().currentTab.unselectTab();
		}
		GUIManager.getInstance().currentTab = this;
		this.tabButtonText.setFill(Color.web(CustomColor.WHITE.getColorHex()));
	}

	public void unselectTab() {
		this.tabButtonText.setFill(Color.web("#95A5A6"));
	}

	public void disableTab() {
		this.disabled = true;
	}

	public void enableTab() {
		this.disabled = false;
	}

	public boolean isDisabled() {
		return this.disabled;
	}

	public TabType getTabType() {
		return this.tab;
	}

	public HBox getTabHBox() {
		return this.tabHbox;
	}
}