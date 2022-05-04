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

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.beatplaylist.Main;
import com.beatplaylist.enums.FontType;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.module.page.profile.ProfileLoader;
import com.beatplaylist.settings.Settings;
import com.beatplaylist.utilities.CustomColor;
import com.beatplaylist.utilities.control.ContextItem;
import com.beatplaylist.utilities.image.ImageManager;
import com.beatplaylist.utilities.popup.Popup;
import com.beatplaylist.utilities.user.UserManager;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class ProfileMenu {

	// SideBar profile display which holds the user profile image, display name and premium tag.
	public HBox profileContainerHBox;

	// Holds display name text and premium logo if a user is premium.
	public VBox name_vbox;

	public ImageView profileImageView, premiumLogoImageView, downArrowIconImageView;
	public Text profileDisplayNameText;

	// Displays settings and logout buttons.
	public ContextMenu profileContextMenu;

	public boolean isDisabled = false;

	public ProfileMenu() {
		this.profileContainerHBox = new HBox(5);
		this.name_vbox = new VBox();
		this.profileImageView = new ImageView();
		this.premiumLogoImageView = new ImageView();
		this.profileDisplayNameText = new Text(UserManager.getInstance().user.displayName);
		this.profileContextMenu = getContextMenu();

		configure();
		listen();
	}

	private void configure() {

		this.name_vbox.setAlignment(Pos.CENTER_LEFT);
		this.profileContainerHBox.minHeightProperty().bind(GUIManager.getInstance().sideBar.sidebarPane.heightProperty().multiply(0.15));
		this.profileContainerHBox.minWidthProperty().bind(GUIManager.getInstance().sideBar.sidebarPane.widthProperty());
		this.profileContainerHBox.maxWidthProperty().bind(GUIManager.getInstance().sideBar.sidebarPane.widthProperty());
		this.profileContainerHBox.setStyle("-fx-background-color: linear-gradient(from 95% 95% to 0% 0%, #3a3a3a, #211f20); -fx-border-color: #211f20; -fx-border-width: 0 0 2 0;");
		this.profileContainerHBox.setPadding(new Insets(this.profileContainerHBox.getPadding().getTop(), this.profileContainerHBox.getPadding().getRight(), this.profileContainerHBox.getPadding().getBottom(), this.profileContainerHBox.getPadding().getLeft() + 35));
		this.profileContainerHBox.setAlignment(Pos.CENTER_LEFT);

		ImageManager.getProfileImage(this.profileImageView, UserManager.getInstance().user.profileImageURL, 45, 45);

		this.profileDisplayNameText.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		this.profileDisplayNameText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));

		this.name_vbox.getChildren().add(this.profileDisplayNameText);

		if (UserManager.getInstance().getUser().isPremium()) {
			if (UserManager.getInstance().getUser().isCreator()) {
				this.premiumLogoImageView.setImage(new Image(Main.class.getResource("/resources/tab_icons/creator.png").toExternalForm(), 53, 12, false, false));
			} else if (UserManager.getInstance().getUser().isVerified()) {
				this.premiumLogoImageView.setImage(new Image(Main.class.getResource("/resources/tab_icons/verified.png").toExternalForm(), 53, 12, false, false));
			} else {
				this.premiumLogoImageView.setImage(new Image(Main.class.getResource("/resources/tab_icons/premium.png").toExternalForm(), 53, 12, false, false));
			}
			this.name_vbox.getChildren().add(this.premiumLogoImageView);
		}

		this.profileContainerHBox.getChildren().addAll(this.profileImageView, this.name_vbox);

		GUIManager.getInstance().sideBar.sidebarPane.getChildren().add(this.profileContainerHBox);
	}

	private void listen() {
		this.profileContainerHBox.setOnMouseEntered(event -> {
			if (isProfileMenuDisabled())
				return;
			this.profileContainerHBox.setStyle("-fx-background-color: #211f20; -fx-border-color: #FFF; -fx-border-width: 0 0 2 0; -fx-cursor: hand;");
		});
		this.profileContainerHBox.setOnMouseExited(event -> {
			if (isProfileMenuDisabled())
				return;
			this.profileContainerHBox.setStyle("-fx-background-color: linear-gradient(from 95% 95% to 0% 0%, #3a3a3a, #211f20); -fx-border-color: #211f20; -fx-border-width: 0 0 2 0;");
		});
		this.profileContainerHBox.setOnMouseClicked(event -> {
			if (isProfileMenuDisabled())
				return;
			if (this.profileContextMenu != null && this.profileContextMenu.isShowing()) {
				this.profileContextMenu.hide();
			} else {
				Bounds boundsInScreen = this.profileContainerHBox.localToScreen(this.profileContainerHBox.getBoundsInLocal());
				this.profileContextMenu.show(this.profileContainerHBox, boundsInScreen.getMinX() + 15, boundsInScreen.getMaxY());
			}
		});
		GUIManager.getInstance().stage.widthProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal.doubleValue() < 1200) {
				if (this.profileContainerHBox.getChildren().contains(this.name_vbox)) {
					this.profileContainerHBox.getChildren().remove(this.name_vbox);
					this.profileContainerHBox.setAlignment(Pos.CENTER);
				}
			} else {
				if (!this.profileContainerHBox.getChildren().contains(this.name_vbox)) {
					this.profileContainerHBox.getChildren().add(this.name_vbox);
					this.profileContainerHBox.setAlignment(Pos.CENTER_LEFT);
				}
			}
		});
	}

	private ContextMenu getContextMenu() {
		ContextMenu contextMenu = new ContextMenu();
		contextMenu.setStyle("-fx-cursor: hand; -fx-background-color: " + CustomColor.DROPDOWN_MENU_COLOR.getColorHex() + ";");

		ContextItem logout = new ContextItem("Log out @" + UserManager.getInstance().user.username), settings = new ContextItem("Settings"), //
				submitFeedback = new ContextItem("Submit Feedback"), reportBug = new ContextItem("Report a Bug"), viewUpdateLog = new ContextItem("View Change Log"), //
				profile = new ContextItem("My Profile"), walletConnect = new ContextItem("Wallet Connect");

		logout.hbox.minWidthProperty().bind(GUIManager.getInstance().sideBar.sidebarPane.widthProperty().subtract(50));
		logout.hbox.maxWidthProperty().bind(GUIManager.getInstance().sideBar.sidebarPane.widthProperty().subtract(50));
		logout.hbox.setMinHeight(35);

		settings.hbox.minWidthProperty().bind(GUIManager.getInstance().sideBar.sidebarPane.widthProperty().subtract(50));
		settings.hbox.maxWidthProperty().bind(GUIManager.getInstance().sideBar.sidebarPane.widthProperty().subtract(50));
		settings.hbox.setMinHeight(35);
		settings.getStyleClass().add("header");

		viewUpdateLog.hbox.minWidthProperty().bind(GUIManager.getInstance().sideBar.sidebarPane.widthProperty().subtract(50));
		viewUpdateLog.hbox.maxWidthProperty().bind(GUIManager.getInstance().sideBar.sidebarPane.widthProperty().subtract(50));
		viewUpdateLog.hbox.setMinHeight(35);

		submitFeedback.hbox.minWidthProperty().bind(GUIManager.getInstance().sideBar.sidebarPane.widthProperty().subtract(50));
		submitFeedback.hbox.maxWidthProperty().bind(GUIManager.getInstance().sideBar.sidebarPane.widthProperty().subtract(50));
		submitFeedback.hbox.setMinHeight(35);

		reportBug.hbox.minWidthProperty().bind(GUIManager.getInstance().sideBar.sidebarPane.widthProperty().subtract(50));
		reportBug.hbox.maxWidthProperty().bind(GUIManager.getInstance().sideBar.sidebarPane.widthProperty().subtract(50));
		reportBug.hbox.setMinHeight(35);
		reportBug.getStyleClass().add("header");

		profile.hbox.minWidthProperty().bind(GUIManager.getInstance().sideBar.sidebarPane.widthProperty().subtract(50));
		profile.hbox.maxWidthProperty().bind(GUIManager.getInstance().sideBar.sidebarPane.widthProperty().subtract(50));
		profile.hbox.setMinHeight(35);

		walletConnect.hbox.minWidthProperty().bind(GUIManager.getInstance().sideBar.sidebarPane.widthProperty().subtract(50));
		walletConnect.hbox.maxWidthProperty().bind(GUIManager.getInstance().sideBar.sidebarPane.widthProperty().subtract(50));
		walletConnect.hbox.setMinHeight(35);

		logout.setOnAction(event -> {
			Popup.confirmLogout();
		});
		settings.setOnAction(event -> {
			if (!TabType.SETTINGS.isEnabled() || (GUIManager.getInstance().currentTab != null && GUIManager.getInstance().currentTab.getTabType() == TabType.SETTINGS) || (TabType.SETTINGS.isEnabled() && UserManager.getInstance().user.accountType.isSmaller(TabType.SETTINGS.getAccountType())))
				return;
			GUIManager.getInstance().sideBar.sideBarTab.changeTab(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.SETTINGS));
		});
		viewUpdateLog.setOnAction(event -> {
			Settings.getInstance().setHasCheckedUpdate(false, true);
			Popup.showPatchNotes();
		});
		reportBug.setOnAction(event -> {
			try {
				Desktop.getDesktop().browse(new URI("https://beatplaylist.com/contact"));
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		});
		walletConnect.setOnAction(event -> {
			if (!TabType.WALLET_CONNECT.isEnabled() || (GUIManager.getInstance().currentTab != null && GUIManager.getInstance().currentTab.getTabType() == TabType.WALLET_CONNECT) || (TabType.WALLET_CONNECT.isEnabled() && UserManager.getInstance().user.accountType.isSmaller(TabType.WALLET_CONNECT.getAccountType())))
				return;
			GUIManager.getInstance().sideBar.sideBarTab.changeTab(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.WALLET_CONNECT));
		});
		profile.setOnAction(event -> {
			ProfileLoader.loadProfile();
		});
		submitFeedback.setOnAction(event -> {
			Popup.submitFeedback();
		});

		contextMenu.getItems().addAll(profile, walletConnect, settings, viewUpdateLog, submitFeedback, reportBug, logout);

		return contextMenu;
	}

	public void disableProfileMenu() {
		this.isDisabled = true;
	}

	public void enableProfileMenu() {
		this.isDisabled = false;
	}

	public boolean isProfileMenuDisabled() {
		return this.isDisabled;
	}

	public void reloadProfilePicture() {
		ImageManager.getProfileImage(this.profileImageView, UserManager.getInstance().user.profileImageURL, 45, 45);
	}
}