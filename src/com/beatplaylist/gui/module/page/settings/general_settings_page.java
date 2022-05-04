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

package com.beatplaylist.gui.module.page.settings;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import com.beatplaylist.Main;
import com.beatplaylist.Options;
import com.beatplaylist.chromium.BrowserManager;
import com.beatplaylist.chromium.EngineBrowser;
import com.beatplaylist.enums.FontType;
import com.beatplaylist.enums.ShuffleType;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.module.layout.sidebar.TabType;
import com.beatplaylist.gui.utilities.FXUtilities;
import com.beatplaylist.settings.Settings;
import com.beatplaylist.settings.SocialType;
import com.beatplaylist.utilities.CustomColor;
import com.beatplaylist.utilities.CustomToolTip;
import com.beatplaylist.utilities.KeyboardHookListener;
import com.beatplaylist.utilities.control.ToggleSwitch;
import com.beatplaylist.utilities.control.Tooltip;
import com.beatplaylist.utilities.data.Data;
import com.beatplaylist.utilities.events.ComboBoxSetting;
import com.beatplaylist.utilities.events.CompleteEvent;
import com.beatplaylist.utilities.events.SettingCreateEvent;
import com.beatplaylist.utilities.filemanager.FileUtilities;
import com.beatplaylist.utilities.filemanager.OperatingSystem;
import com.beatplaylist.utilities.image.ImageBuilder;
import com.beatplaylist.utilities.network.post.Generate2FACode;
import com.beatplaylist.utilities.network.post.LinkTwitter;
import com.beatplaylist.utilities.network.post.Remove2FA;
import com.beatplaylist.utilities.network.post.RemoveLinkedSocialNetworkingAccount;
import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;
import com.beatplaylist.utilities.popup.Popup;
import com.beatplaylist.utilities.user.SocialAccount;
import com.beatplaylist.utilities.user.UserManager;
import com.install4j.api.windows.RegistryRoot;
import com.install4j.api.windows.WinRegistry;
import com.install4j.runtime.installer.helper.InstallerUtil;
import com.teamdev.jxbrowser.cookie.CookieStore;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

public class general_settings_page {

	public ScrollPane scrollPane;
	public HBox scrollPaneHBox;
	public VBox leftVBox, generalVBox, securityVBox, socialAccountVBox;
	public Button twoFactorAuthenticationButton, changePasswordButton;
	public Text generalSettingsHeaderText, securitySettingsHeaderText, premiumExpiryDateText;

	public general_settings_page() {
		this.scrollPane = new ScrollPane() {
			public void requestFocus() {

			}
		};
		this.scrollPaneHBox = new HBox(150);
		this.leftVBox = new VBox(25);
		this.generalVBox = new VBox(15);
		this.securityVBox = new VBox(10);
		this.socialAccountVBox = new VBox(25);

		this.twoFactorAuthenticationButton = new Button("Add 2-Step Verification") {
			public void requestFocus() {

			}
		};
		this.changePasswordButton = new Button("Change Password") {
			public void requestFocus() {

			}
		};
		this.generalSettingsHeaderText = new Text("GENERAL SETTINGS");
		this.securitySettingsHeaderText = new Text("SECURITY SETTINGS");
		this.premiumExpiryDateText = new Text();

		configure();
		listen();
	}

	private void configure() {
		FXUtilities.setNodePadding(this.scrollPaneHBox, GUIManager.getInstance().padding);

		this.scrollPane.setContent(this.scrollPaneHBox);
		this.scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		this.scrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
		this.scrollPane.setStyle("-fx-background-color: transparent;");
		this.scrollPane.minWidthProperty().bind(GUIManager.getInstance().contentManager.contentPane.widthProperty().subtract(100));
		this.scrollPane.maxWidthProperty().bind(GUIManager.getInstance().contentManager.contentPane.widthProperty().subtract(100));
		this.scrollPane.maxHeightProperty().bind(GUIManager.getInstance().contentManager.contentPane.heightProperty().subtract(10));

		// Two Factor Button styles
		if (UserManager.getInstance().user.has2FA)
			this.twoFactorAuthenticationButton.setText("Remove 2-Step Verification");
		this.twoFactorAuthenticationButton.setMinSize(200, 35);
		this.twoFactorAuthenticationButton.setFont(Font.font(FontType.DEFAULT.getName(), 14));
		this.twoFactorAuthenticationButton.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-border-radius: 5px; -fx-background-radius: 5px;");
		this.twoFactorAuthenticationButton.setCursor(Cursor.HAND);
		this.twoFactorAuthenticationButton.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));

		this.changePasswordButton.setMinSize(200, 35);
		this.changePasswordButton.setFont(Font.font(FontType.DEFAULT.getName(), 14));
		this.changePasswordButton.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-border-radius: 5px; -fx-background-radius: 5px;");
		this.changePasswordButton.setCursor(Cursor.HAND);
		this.changePasswordButton.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));

		this.generalSettingsHeaderText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 24));
		this.generalSettingsHeaderText.setFill(Color.web(CustomColor.WHITE.getColorHex()));

		this.securitySettingsHeaderText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 24));
		this.securitySettingsHeaderText.setFill(Color.web(CustomColor.WHITE.getColorHex()));

		this.premiumExpiryDateText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 14));
		this.premiumExpiryDateText.setFill(Color.web(CustomColor.GRAY.getColorHex()));

		// Premium expiry styles
		if (!UserManager.getInstance().user.premiumExpiry.contains("-")) {
			this.premiumExpiryDateText.setText("Premium Expiry: " + UserManager.getInstance().user.premiumExpiry);
		} else {
			try {
				Date date = new SimpleDateFormat("yyyy-MM-dd").parse(UserManager.getInstance().user.premiumExpiry);
				String outputText = new SimpleDateFormat("d MMMMMMMM, yyyy").format(date);
				this.premiumExpiryDateText.setText("Premium Expiry: " + outputText);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		addSetting("Display music on profile", Settings.getInstance().canDisplayMusic(), false, new SettingCreateEvent() {
			public void onCreate(ToggleSwitch toggleSwitch) {
				// Tooltip.install(button, new CustomToolTip("Display the music you are listening to on your profile!\nThis is recommended for music sharing!"));
			}

			@Override
			public void onClick(ToggleSwitch toggleSwitch) {
				toggleSwitch.setEnabled(Settings.getInstance().canDisplayMusic() ? false : true);
				Settings.getInstance().setDisplayMusic(Settings.getInstance().canDisplayMusic() ? false : true, true);
			}
		});
		if (FileUtilities.getInstance().getOperatingSystem() == OperatingSystem.WINDOWS) {
			addSetting("Open Rhythm on computer startup", Settings.getInstance().programLaunchOnComputerStartup(), false, new SettingCreateEvent() {
				public void onCreate(ToggleSwitch toggleSwitch) {
				}

				@Override
				public void onClick(ToggleSwitch toggleSwitch) {
					if (!Options.test_mode) {
						if (!Settings.getInstance().programLaunchOnComputerStartup()) {
							String value = InstallerUtil.getInstallerFile("Rhythm.exe").getAbsolutePath();
							// REG ADD "HKCU\SOFTWARE\Microsoft\Windows\CurrentVersion\Run" /V "My App" /t REG_SZ /F /D "C:\MyAppPath\MyApp.exe"
							WinRegistry.setValue(RegistryRoot.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Run", "Rhythm", value);
							// new BackgroundTransition(button, Color.web(CustomColor.RED.getColorHex()), Color.web(CustomColor.GREEN.getColorHex()));
							toggleSwitch.setEnabled(true);
							Settings.getInstance().setProgramLaunchOnComputerStartup(true);
						} else {
							WinRegistry.deleteValue(RegistryRoot.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Run", "Rhythm");
							// new BackgroundTransition(button, Color.web(CustomColor.GREEN.getColorHex()), Color.web(CustomColor.RED.getColorHex()));
							toggleSwitch.setEnabled(false);
							Settings.getInstance().setProgramLaunchOnComputerStartup(false);
						}
					}
				}
			});
		}
		addSetting("Launch Rhythm Minimized", Settings.getInstance().launchMinimized(), false, new SettingCreateEvent() {
			public void onCreate(ToggleSwitch toggleSwitch) {
			}

			@Override
			public void onClick(ToggleSwitch toggleSwitch) {
				toggleSwitch.setEnabled(Settings.getInstance().launchMinimized() ? false : true);
				Settings.getInstance().setLaunchMinimized(Settings.getInstance().launchMinimized() ? false : true, true);
			}
		});
		addSetting("Show video adverts", Settings.getInstance().showVideoAdverts(), true, new SettingCreateEvent() {
			public void onCreate(ToggleSwitch toggleSwitch) {
			}

			@Override
			public void onClick(ToggleSwitch toggleSwitch) {
				toggleSwitch.setEnabled(Settings.getInstance().showVideoAdverts() ? false : true);
				Settings.getInstance().setShowVideoAdverts(Settings.getInstance().showVideoAdverts() ? false : true, true);
			}
		});
		addSetting("Loudness Equalization", Settings.getInstance().hasLoudnessEqualization(), true, new SettingCreateEvent() {
			public void onCreate(ToggleSwitch toggleSwitch) {
			}

			@Override
			public void onClick(ToggleSwitch toggleSwitch) {
				toggleSwitch.setEnabled(Settings.getInstance().hasLoudnessEqualization() ? false : true);
				Settings.getInstance().setLoudnessEqualization(Settings.getInstance().hasLoudnessEqualization() ? false : true, true);
				BrowserManager.getInstance().getCurrentBrowser().getWebEngine().mainFrame().get().executeJavaScript("setVolumeEqualization(" + Settings.getInstance().hasLoudnessEqualization() + ");");
				BrowserManager.getInstance().getCurrentBrowser().getWebEngine().mainFrame().get().executeJavaScript("setCustomVolume(" + Data.getInstance().volumeLevel + ");");
			}
		});
		addSetting("HiDPI Monitor", Settings.getInstance().hidpi(), true, new SettingCreateEvent() {
			public void onCreate(ToggleSwitch toggleSwitch) {
			}

			@Override
			public void onClick(ToggleSwitch toggleSwitch) {
				toggleSwitch.setEnabled(Settings.getInstance().hidpi() ? false : true);
				Settings.getInstance().setHiDPI(Settings.getInstance().hidpi() ? false : true, true);
				System.setProperty("prism.allowhidpi", String.valueOf(Settings.getInstance().hidpi()));
			}
		});
		addSetting("Hardware Acceleration", Settings.getInstance().isHardwareAccelerated(), true, new SettingCreateEvent() {
			public void onCreate(ToggleSwitch toggleSwitch) {
			}

			@Override
			public void onClick(ToggleSwitch toggleSwitch) {
				toggleSwitch.setEnabled(Settings.getInstance().isHardwareAccelerated() ? false : true);
				Settings.getInstance().setHardwareAccelerated(Settings.getInstance().isHardwareAccelerated() ? false : true, true);
				Notification.getInstance().createNotification("Hardware Acceleration", "You have " + (Settings.getInstance().isHardwareAccelerated() ? "enabled" : "disabled") + " hardware acceleration! Please restart Rhythm to apply changes!", AlertType.WARNING);
			}
		});
		if (FileUtilities.getInstance().getOperatingSystem() == OperatingSystem.WINDOWS) {
			this.addSetting("Media Keys", Settings.getInstance().hasMediaKeysEnabled(), true, new SettingCreateEvent() {
				@Override
				public void onCreate(final ToggleSwitch toggleSwitch) {
				}

				@Override
				public void onClick(final ToggleSwitch toggleSwitch) {
					toggleSwitch.setEnabled(!Settings.getInstance().hasMediaKeysEnabled());
					Settings.getInstance().setMediaKeysEnabled(!Settings.getInstance().hasMediaKeysEnabled(), true);
					Notification.getInstance().createNotification("Media Keys", "You have " + (Settings.getInstance().hasMediaKeysEnabled() ? "enabled" : "disabled") + " media keys!", AlertType.SUCCESS);
					if (Settings.getInstance().hasMediaKeysEnabled()) {
						try {
							if (!GlobalScreen.isNativeHookRegistered()) {
								GlobalScreen.registerNativeHook();
								GlobalScreen.addNativeKeyListener(new KeyboardHookListener());
							}
						} catch (NativeHookException ex) {
							ex.printStackTrace();
							System.out.println("Global keyboard commands could not connect and were disabled.");
						}
					} else {
						try {
							if (GlobalScreen.isNativeHookRegistered()) {
								GlobalScreen.unregisterNativeHook();
							}
						} catch (NativeHookException e) {
							e.printStackTrace();
						}
					}
				}
			});
		}
		addComboBoxSetting("Shuffle Type", true, new ComboBoxSetting() {
			@Override
			public void onSelectionUpdate(String value) {
				Settings.getInstance().setShuffleType(value, true);
				Notification.getInstance().createNotification("Shuffle Type", "Shuffle Type changed to " + value, AlertType.SUCCESS);
			}

			@Override
			public void onCreate(ComboBox<String> comboBox) {
				for (ShuffleType shuffleType : ShuffleType.values()) {
					comboBox.getItems().add(shuffleType.getName());
				}
				comboBox.setPromptText(Settings.getInstance().getShuffleType().getName());
			}
		});
		// addSetting("Crossfade songs", Settings.getInstance().crossFadeSongEnabled(), false, new SettingCreateEvent() {
		// public void onCreate(ToggleSwitch toggleSwitch) {
		// if (UserManager.getInstance().user.accountType.isSmaller(AccountType.DEVELOPER)) {
		// toggleSwitch.setDisable(true);
		// CustomToolTip.install(toggleSwitch, new CustomToolTip("This feature will be enabled in a future update"));
		// }
		// }
		//
		// @Override
		// public void onClick(ToggleSwitch toggleSwitch) {
		// if (GUIManager.getInstance().songFadeBrowser == null)
		// GUIManager.getInstance().songFadeBrowser = new VideoBrowser();
		// toggleSwitch.setEnabled(Settings.getInstance().crossFadeSongEnabled() ? false : true);
		// Settings.getInstance().setCrossFadeSongs(Settings.getInstance().crossFadeSongEnabled() ? false : true, true);
		// }
		// });

		this.generalVBox.getChildren().addAll(this.premiumExpiryDateText);
		this.generalVBox.getChildren().add(0, this.generalSettingsHeaderText);

		this.securityVBox.getChildren().addAll(this.securitySettingsHeaderText, this.twoFactorAuthenticationButton, this.changePasswordButton);

		this.leftVBox.getChildren().addAll(this.generalVBox, this.securityVBox);
		this.scrollPaneHBox.getChildren().add(this.leftVBox);

		displayLinkSocialAccounts();
		addSubscriptionBox();

		GUIManager.getInstance().contentManager.contentPane.getChildren().add(this.scrollPane);

		FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), this.scrollPane);
		fadeTransition.setFromValue(0.0);
		fadeTransition.setToValue(1.0);
		fadeTransition.play();
	}

	private void listen() {
		this.changePasswordButton.setOnMouseEntered(event -> this.changePasswordButton.setTextFill(Color.web(CustomColor.RHYTHM.getColorHex())));
		this.changePasswordButton.setOnMouseExited(event -> this.changePasswordButton.setTextFill(Color.web(CustomColor.WHITE.getColorHex())));
		this.changePasswordButton.setOnAction(event -> Popup.changePassword());

		this.twoFactorAuthenticationButton.setOnMouseEntered(event -> this.twoFactorAuthenticationButton.setTextFill(Color.web(CustomColor.RHYTHM.getColorHex())));
		this.twoFactorAuthenticationButton.setOnMouseExited(event -> this.twoFactorAuthenticationButton.setTextFill(Color.web(CustomColor.WHITE.getColorHex())));
		this.twoFactorAuthenticationButton.setOnMouseClicked(event -> {
			if (UserManager.getInstance().user.has2FA) {
				Popup.remove2FA(new CompleteEvent() {
					@Override
					public void onSuccess() {
						Remove2FA.send(new CompleteEvent() {

							@Override
							public void onSuccess() {
								twoFactorAuthenticationButton.setText("Add 2-Step Verification");
								UserManager.getInstance().getUser().has2FA = false;
								Notification.getInstance().createNotification("2FA", "2FA is now removed from your account.", AlertType.SUCCESS);
							}

							@Override
							public void onFail(String error) {
								Notification.getInstance().createNotification("2FA", "An error occured while removing 2FA.", AlertType.ERROR);
							}
						});
					}

					@Override
					public void onFail(String error) {
					}
				});
			} else {
				Generate2FACode.send(this.twoFactorAuthenticationButton);
			}
		});
	}

	private void addSetting(String setting_description, boolean enabled, boolean premium, SettingCreateEvent setting_event) {
		HBox hbox = new HBox(), leftHBox = new HBox(), rightHBox = new HBox();

		leftHBox.setMinWidth(325);
		leftHBox.setAlignment(Pos.CENTER_LEFT);
		rightHBox.setAlignment(Pos.CENTER_RIGHT);

		Text text = new Text(setting_description);
		text.setTextAlignment(TextAlignment.LEFT);

		text.setFont(Font.font(FontType.DEFAULT.getName(), 14));
		text.setFill(Color.web(CustomColor.GRAY.getColorHex()));

		ToggleSwitch toggle = new ToggleSwitch();
		toggle.setEnabled(enabled);

		toggle.setOnMouseClicked(event -> {
			setting_event.onClick(toggle);
		});

		hbox.setAlignment(Pos.CENTER_LEFT);
		leftHBox.getChildren().addAll(text);
		rightHBox.getChildren().addAll(toggle);
		hbox.getChildren().addAll(leftHBox, rightHBox);
		setting_event.onCreate(toggle);

		if (!premium)
			this.generalVBox.getChildren().add(hbox);
		else if (premium && UserManager.getInstance().user.isPremium()) {
			this.generalVBox.getChildren().add(hbox);
		}
	}

	private void addComboBoxSetting(String setting_description, boolean premium, ComboBoxSetting setting_event) {
		HBox hbox = new HBox(), leftHBox = new HBox(), rightHBox = new HBox();

		leftHBox.setMinWidth(325);
		leftHBox.setAlignment(Pos.CENTER_LEFT);
		rightHBox.setAlignment(Pos.CENTER_RIGHT);

		Text text = new Text(setting_description);
		text.setTextAlignment(TextAlignment.LEFT);

		text.setFont(Font.font(FontType.DEFAULT.getName(), 14));
		text.setFill(Color.web(CustomColor.GRAY.getColorHex()));

		ComboBox<String> comboBox = new ComboBox<>();
		comboBox.setStyle("-fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-border-color: #333;");
		comboBox.setMaxWidth(200);
		comboBox.setMinHeight(40);
		comboBox.setCursor(Cursor.HAND);

		comboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			setting_event.onSelectionUpdate(newValue);
		});

		hbox.setAlignment(Pos.CENTER_LEFT);
		leftHBox.getChildren().addAll(text);
		rightHBox.getChildren().addAll(comboBox);
		hbox.getChildren().addAll(leftHBox, rightHBox);
		setting_event.onCreate(comboBox);

		if (!premium)
			this.generalVBox.getChildren().add(hbox);
		else if (premium && UserManager.getInstance().user.isPremium()) {
			this.generalVBox.getChildren().add(hbox);
		}
	}

	private void displayLinkSocialAccounts() {
		VBox linkNewAccountVBox = new VBox(5), connectedAccountTextVBox = new VBox(1), linkedAccountVBox = new VBox(7);

		Text connectAccountText = new Text("Connect your accounts"), connectAccountSubText = new Text("Connect these accounts and unlock special Rhythm integrations."), authorizedAccountText = new Text("Authorized accounts");
		HBox account_hbox = new HBox(10);

		connectAccountText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 24));
		connectAccountText.setFill(Color.web(CustomColor.WHITE.getColorHex()));

		connectAccountSubText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.THIN, 14));
		connectAccountSubText.setFill(Color.web(CustomColor.GRAY.getColorHex()));
		connectAccountSubText.setWrappingWidth(300);

		authorizedAccountText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 24));
		authorizedAccountText.setFill(Color.web(CustomColor.WHITE.getColorHex()));

		account_hbox.setAlignment(Pos.CENTER_LEFT);

		for (SocialType social_type : SocialType.values()) {
			if (social_type == null)
				continue;
			ImageBuilder imageBuilder = new ImageBuilder(new Image(Main.class.getResource("/resources/company_logo/" + social_type.getName().toLowerCase() + "_icon.png").toExternalForm(), 35, 35, false, false));

			if (social_type.isDisabled()) {
				imageBuilder.getHBox().setOpacity(0.2);
				imageBuilder.getImageView().setOpacity(0.2);
				Tooltip.install(imageBuilder.getHBox(), new CustomToolTip(social_type.getName() + " account linking is currently disabled."));
			}
			imageBuilder.getHBox().setCursor(Cursor.HAND);

			imageBuilder.getHBox().setOnMouseClicked(event -> {
				if (social_type == null || social_type.isDisabled())
					return;
				String url = "";

				if (social_type == SocialType.TWITTER) {
					LinkTwitter.send(social_type.getName().toUpperCase());
				} else if (social_type == SocialType.INSTAGRAM)
					url = "https://rhythm.cc/api/v1/instagram/connect";
				else if (social_type == SocialType.YOUTUBE)
					url = "https://rhythm.cc/api/v1/google/connect";

				if (!url.isEmpty()) {
					try {
						if (url.contains("google")) {
							GUIManager.getInstance().searchBrowser.getWebEngine().navigation().loadUrl(url);
							GUIManager.getInstance().sideBar.sideBarTab.changeTab(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.BROWSE), "DISPLAY");
						} else {
							Desktop.getDesktop().browse(new URI(url));
						}
					} catch (URISyntaxException | IOException e) {
						e.printStackTrace();
					}
				}
			});

			account_hbox.getChildren().add(imageBuilder.getHBox());
		}
		connectedAccountTextVBox.getChildren().addAll(connectAccountText, connectAccountSubText);
		linkNewAccountVBox.getChildren().addAll(connectedAccountTextVBox, account_hbox);

		if (!UserManager.getInstance().user.linkedSocialAccounts.isEmpty()) {
			linkNewAccountVBox.getChildren().add(authorizedAccountText);

			for (SocialAccount account : UserManager.getInstance().user.linkedSocialAccounts) {

				VBox content_vbox = new VBox(15);
				content_vbox.setStyle("-fx-padding: 15px; -fx-background-radius: 10px; -fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + ";");
				content_vbox.setMinSize(200, 50);
				HBox header_hbox = new HBox(25), name_hbox = new HBox(1), footer_hbox = new HBox(25);

				ImageView social_image_view = new ImageView(new Image(Main.class.getResource("/resources/company_logo/" + account.getSocialType().getName().toLowerCase() + "_icon_small.png").toExternalForm(), 20, 20, false, false));

				Text username_text = new Text("@" + account.getUsername());
				username_text.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.THIN, 14));
				username_text.setFill(Color.web(CustomColor.GRAY.getColorHex()));
				if (account.getSocialType() == SocialType.YOUTUBE)
					username_text.setText("YouTube connected");

				name_hbox.getChildren().addAll(username_text);
				name_hbox.setAlignment(Pos.CENTER_LEFT);

				if (account.isVerified()) {
					ImageView image_view = new ImageView(new Image(Main.class.getResource("/resources/company_logo/verified_" + account.getSocialType().getName().toLowerCase() + ".png").toExternalForm(), 20, 20, false, false));
					image_view.setStyle("-fx-cursor: hand;");
					Tooltip.install(image_view, new CustomToolTip("Verified on " + account.getSocialType().getName()));
					name_hbox.getChildren().add(image_view);
				}

				Button remove = new Button("Remove from account") {
					public void requestFocus() {

					}
				};
				Text displayOnProfileText = new Text("  Display on Profile");
				ToggleSwitch displayOnProfileToggle = new ToggleSwitch();
				displayOnProfileToggle.setEnabled(account.isVisibleOnProfile());

				displayOnProfileText.setFill(Color.web(CustomColor.WHITE.getColorHex()));
				displayOnProfileText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.SEMI_BOLD, 16));

				remove.setStyle("-fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + ";");
				remove.setFont(Font.font(FontType.VERDANA.getName(), 14));
				remove.setMinSize(100, 30);
				remove.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
				remove.setCursor(Cursor.HAND);

				remove.setOnMouseEntered(event -> {
					remove.setTextFill(Color.web(Settings.getInstance().getDefaultColor()));
				});
				remove.setOnMouseExited(event -> {
					remove.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
				});

				displayOnProfileToggle.setOnMouseClicked(event -> {
					if (account.isVisibleOnProfile()) {
						new RemoveLinkedSocialNetworkingAccount().send(account.getSocialType().getName().toUpperCase(), false, true);
						displayOnProfileToggle.setEnabled(false);
						account.setIsVisibleOnProfile(false);
					} else {
						new RemoveLinkedSocialNetworkingAccount().send(account.getSocialType().getName().toUpperCase(), true, true);
						displayOnProfileToggle.setEnabled(true);
						account.setIsVisibleOnProfile(true);
					}
				});

				remove.setOnAction(event -> {
					Popup.confirmRemoveSocialMediaAccount(account.getSocialType().getName(), new CompleteEvent() {

						@Override
						public void onSuccess() {
							new RemoveLinkedSocialNetworkingAccount().send(account.getSocialType().getName().toUpperCase(), false, false);
							UserManager.getInstance().user.linkedSocialAccounts.remove(account);
							if (linkedAccountVBox.getChildren().contains(content_vbox))
								linkedAccountVBox.getChildren().remove(content_vbox);

							if (account.getSocialType() == SocialType.YOUTUBE) {
								CookieStore videoCookieStorage = EngineBrowser.getInstance().engine.profiles().defaultProfile().cookieStore();

								videoCookieStorage.deleteAll();
								videoCookieStorage.persist();
							}
						}

						@Override
						public void onFail(String error) {

						}
					});

				});

				header_hbox.getChildren().addAll(social_image_view, name_hbox);
				if (account.getSocialType() != SocialType.YOUTUBE)
					footer_hbox.getChildren().addAll(displayOnProfileText, displayOnProfileToggle);

				content_vbox.getChildren().addAll(header_hbox, footer_hbox, remove);
				linkedAccountVBox.getChildren().add(content_vbox);
			}
		}
		this.socialAccountVBox.getChildren().addAll(linkNewAccountVBox, linkedAccountVBox);
		this.scrollPaneHBox.getChildren().addAll(this.socialAccountVBox);
	}

	// Displays user subscriptions, including linked cards.
	public void addSubscriptionBox() {
		if (!UserManager.getInstance().user.subscriptionState.equals("VALID"))
			return;

		VBox global_vbox = new VBox(3), container_vbox = new VBox(15), vbox = new VBox();
		HBox card_hbox = new HBox(15);
		Text headerText = new Text("SUBSCRIPTIONS");

		card_hbox.setAlignment(Pos.CENTER_LEFT);
		container_vbox.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-padding: 10px; -fx-background-radius: 10px;");

		headerText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 24));
		headerText.setFill(Color.web(CustomColor.WHITE.getColorHex()));

		try {
			SimpleDateFormat simple_date_format = new SimpleDateFormat("yyyy-MM-dd");
			Date next_charge_date = simple_date_format.parse(UserManager.getInstance().user.subscriptionNextCharge);

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(next_charge_date);

			String monthString = new DateFormatSymbols().getMonths()[calendar.get(Calendar.MONTH) - 1];
			monthString = monthString.substring(0, 1).toUpperCase() + monthString.substring(1);

			TextFlow next_charge_text_flow = new TextFlow();
			Text subscription_next_charge_header = new Text("Next Subscription Charge: "), subscription_next_charge_text = new Text(calendar.get(Calendar.DAY_OF_MONTH) + getDayOfMonthSuffix(calendar.get(Calendar.DAY_OF_MONTH)) + " " + monthString + ", " + calendar.get(Calendar.YEAR)), last_four_text = new Text("•••• " + UserManager.getInstance().user.subscriptionLastFour), expiration_date_text = new Text();

			Button cancel_subscription_button = new Button("Cancel Subscription") {
				public void requestFocus() {

				}
			};

			if (Integer.valueOf(UserManager.getInstance().user.subscriptionCardExpiryMonth) < 10)
				expiration_date_text.setText("0" + UserManager.getInstance().user.subscriptionCardExpiryMonth + " / " + UserManager.getInstance().user.subscriptionCardExpiryYear);
			else
				expiration_date_text.setText(UserManager.getInstance().user.subscriptionCardExpiryMonth + " / " + UserManager.getInstance().user.subscriptionCardExpiryYear);

			ImageView cardImageView = new ImageView(new Image(Main.class.getResource("/resources/icons/credit_card.png").toExternalForm()));

			subscription_next_charge_header.setFill(Color.web(CustomColor.WHITE.getColorHex()));
			subscription_next_charge_header.setFont(Font.font(FontType.DEFAULT.getName(), 14));

			subscription_next_charge_text.setFill(Color.web(CustomColor.GRAY.getColorHex()));
			subscription_next_charge_text.setFont(Font.font(FontType.DEFAULT.getName(), 14));

			last_four_text.setFill(Color.web(CustomColor.GRAY.getColorHex()));
			last_four_text.setFont(Font.font(FontType.DEFAULT.getName(), 14));

			expiration_date_text.setFill(Color.web(CustomColor.GRAY.getColorHex()));
			expiration_date_text.setFont(Font.font(FontType.DEFAULT.getName(), 14));

			cancel_subscription_button.setFont(Font.font(FontType.DEFAULT.getName(), 14));
			cancel_subscription_button.setStyle("-fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + ";");
			cancel_subscription_button.setCursor(Cursor.HAND);
			cancel_subscription_button.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));

			cancel_subscription_button.setOnMouseClicked(event -> {
				Popup.displayCancelPremiumSubscription(new CompleteEvent() {
					public void onSuccess() {
						leftVBox.getChildren().remove(global_vbox);
					}

					@Override
					public void onFail(String error) {
					}
				});
			});
			cancel_subscription_button.setOnMouseEntered(event -> {
				cancel_subscription_button.setTextFill(Color.web(CustomColor.RHYTHM.getColorHex()));
			});
			cancel_subscription_button.setOnMouseExited(event -> {
				cancel_subscription_button.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
			});

			card_hbox.getChildren().addAll(cardImageView, last_four_text, expiration_date_text);
			next_charge_text_flow.getChildren().addAll(subscription_next_charge_header, subscription_next_charge_text);

			vbox.getChildren().addAll(card_hbox, next_charge_text_flow);
			container_vbox.getChildren().addAll(vbox, cancel_subscription_button);
		} catch (Exception e) {
			e.printStackTrace();
		}
		global_vbox.getChildren().addAll(headerText, container_vbox);
		this.leftVBox.getChildren().add(global_vbox);
	}

	private String getDayOfMonthSuffix(final int n) {
		if (n < 1 || n > 31)
			return "";
		if (n >= 11 && n <= 13) {
			return "th";
		}
		switch (n % 10) {
		case 1:
			return "st";
		case 2:
			return "nd";
		case 3:
			return "rd";
		default:
			return "th";
		}
	}
}