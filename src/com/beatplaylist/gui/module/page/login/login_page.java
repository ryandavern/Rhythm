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

package com.beatplaylist.gui.module.page.login;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.beatplaylist.enums.FontType;
import com.beatplaylist.enums.LoginErrorType;
import com.beatplaylist.gui.CenterBox;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.LoginGUIManager;
import com.beatplaylist.gui.module.page.register.register_page;
import com.beatplaylist.settings.Settings;
import com.beatplaylist.utilities.CustomColor;
import com.beatplaylist.utilities.Utilities;
import com.beatplaylist.utilities.control.PasswordField;
import com.beatplaylist.utilities.control.TextField;
import com.beatplaylist.utilities.events.CompleteEvent;
import com.beatplaylist.utilities.network.login.GetAccessKeyState;
import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;
import com.beatplaylist.utilities.update.StartupData;
import com.beatplaylist.utilities.user.UserManager;
import com.beatplaylist.utilities.validation.ValidateManager;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class login_page {

	private TextField email;
	private PasswordField password;
	public Button login, register, resetPassword;
	private Text email_text, password_text, announcement_text, errorMessageText;
	private HBox announcement_hbox;
	private VBox information_vbox, email_vbox, password_vbox, button_vbox;
	private LoginErrorType error_type;
	private CenterBox centerBox;

	public login_page() {
		initialize();
	}

	public login_page(LoginErrorType error_type) {
		initialize();
		this.error_type = error_type;

		if (error_type == LoginErrorType.SUSPENDED || error_type == LoginErrorType.INCORRECT_CREDENTIAL || error_type == LoginErrorType.NETWORK_CONNECTION_ERROR) {
			Text errorMessageText = new Text(error_type.getErrorMessage());
			errorMessageText.setFill(Color.web(CustomColor.RED.getColorHex()));
			errorMessageText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 12));
			errorMessageText.wrappingWidthProperty().bind(this.centerBox.getContentVBox().widthProperty().subtract(10));

			this.centerBox.getContentVBox().getChildren().add(errorMessageText);

		} else if (error_type == LoginErrorType.ACCOUNT_CREATED) {
			Notification.getInstance().createNotification("Account Created", "Welcome to Rhythm!", AlertType.SUCCESS);
		}

	}

	private void initialize() {
		this.centerBox = new CenterBox();

		this.announcement_hbox = new HBox();
		this.information_vbox = new VBox(15);
		this.email_vbox = new VBox(3);
		this.password_vbox = new VBox(3);
		this.button_vbox = new VBox(10);

		this.email = new TextField("");
		this.password = new PasswordField();

		this.login = new Button("Log in");
		this.register = new Button("Create Account");
		this.resetPassword = new Button("Reset Password");

		this.email_text = new Text("EMAIL");
		this.password_text = new Text("PASSWORD");
		this.announcement_text = new Text("");
		this.errorMessageText = new Text();
	}

	public void run() {
		GUIManager.getInstance().getPane().getChildren().clear();
		add();
		listen();
		LoginGUIManager.getInstance().loginTitleBar();

		String server_status = StartupData.getInstance().server_status_number;

		if (!server_status.equals("4") || this.error_type == LoginErrorType.SERVER_OFFLINE) {
			this.errorMessageText = new Text("The BeatPlaylist servers are currently offline. Click here to view live updates from our Twitter.");
			this.errorMessageText.setFill(Color.web(CustomColor.RED.getColorHex()));
			this.errorMessageText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 12));
			this.errorMessageText.wrappingWidthProperty().bind(this.centerBox.getContentVBox().widthProperty().subtract(10));
			this.errorMessageText.setStyle("-fx-cursor: hand;");

			this.register.setDisable(true);
			this.login.setDisable(true);
			this.login.setText("Offline");
			if (server_status.equals("2")) {
				this.login.setText("Offline (Scheduled Downtime)");
				this.errorMessageText.setText("The BeatPlaylist servers are currently offline for scheduled downtime. Click here to view live updates from our Twitter.");
			} else if (server_status.equals("3")) {
				this.login.setText("Offline (Under Maintenance)");
				this.errorMessageText.setText("The BeatPlaylist servers are currently under maintenance. Click here to view live updates from our Twitter.");
			}

			this.errorMessageText.setOnMouseClicked(event -> {
				try {
					Desktop.getDesktop().browse(new URI("https://twitter.com/beatplaylist"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			});

			if (!this.centerBox.getContentVBox().getChildren().contains(this.errorMessageText))
				this.centerBox.getContentVBox().getChildren().add(0, this.errorMessageText);
		}
		this.announcement_text.setText(StartupData.getInstance().announcement);
		if (Settings.getInstance().hasAutoLogin() && !Settings.getInstance().getCachedEmail().equals("null") && !Settings.getInstance().getCachedPassword().equals("null") && !Settings.getInstance().getCachedEmail().isEmpty() && !Settings.getInstance().getCachedPassword().isEmpty()) {
			login();
		}
	}

	private void add() {
		this.email.setPromptText("Email");
		this.password.setPromptText("Password");
		this.password.setDisable(true);
		this.login.setDisable(true);

		this.email.setMinSize(300, 35);
		this.email.setStyle("-fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-text-fill: white; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
		this.email.setFont(Font.font(FontType.DEFAULT.getName(), 12));
		if (this.error_type != LoginErrorType.SUSPENDED && !Settings.getInstance().getCachedEmail().equals("null"))
			this.email.setText(Settings.getInstance().getCachedEmail());

		this.password.setMinSize(300, 35);
		this.password.setStyle("-fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-text-fill: white; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
		this.password.setFont(Font.font(FontType.DEFAULT.getName(), 12));
		if (this.error_type != LoginErrorType.SUSPENDED && !Settings.getInstance().getCachedPassword().equals("null")) {
			this.password.setText(Settings.getInstance().getCachedPassword());
			this.password.setDisable(false);
			this.login.setDisable(false);
		}

		this.email_text.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.NORMAL, 12));
		this.email_text.setFill(Color.web(CustomColor.GRAY.getColorHex()));

		this.password_text.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.NORMAL, 12));
		this.password_text.setFill(Color.web(CustomColor.GRAY.getColorHex()));

		styleButton(this.login);
		styleButton(this.register);
		styleButton(this.resetPassword);

		this.announcement_hbox.setStyle("-fx-background-color: " + Settings.getInstance().getDefaultColor() + "; -fx-background-radius: 10px; -fx-padding: 10px;");
		this.announcement_hbox.setAlignment(Pos.CENTER);
		this.announcement_hbox.setMinHeight(50);
		this.announcement_text.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		this.announcement_text.setFont(Font.font(FontType.DEFAULT.getName(), 14));
		this.announcement_text.setTextAlignment(TextAlignment.CENTER);
		this.announcement_text.setWrappingWidth(300);

		this.button_vbox.setStyle("-fx-border-color: " + Settings.getInstance().getDefaultColor() + "; -fx-border-width: 2 0 0 0; -fx-padding: 15px 0px;");

		this.email_vbox.getChildren().addAll(this.email_text, this.email);
		this.password_vbox.getChildren().addAll(this.password_text, this.password);
		this.information_vbox.getChildren().addAll(this.email_vbox, this.password_vbox);
		this.button_vbox.getChildren().addAll(this.register, this.resetPassword);
		this.centerBox.getContentVBox().getChildren().addAll(this.information_vbox, this.login);

		this.announcement_hbox.getChildren().add(this.announcement_text);

		this.centerBox.getContentVBox().getChildren().addAll(this.button_vbox);

		if (!StartupData.getInstance().announcement.isEmpty())
			this.centerBox.getWrapperVBox().getChildren().addAll(this.announcement_hbox);

		if (!GUIManager.getInstance().getPane().getChildren().contains(this.centerBox.getWrapperHBox()))
			GUIManager.getInstance().getPane().getChildren().add(this.centerBox.getWrapperHBox());

		FadeTransition fadeTransition = new FadeTransition(Duration.millis(250), this.centerBox.getContentVBox());
		fadeTransition.setFromValue(0.0);
		fadeTransition.setToValue(1.0);
		fadeTransition.play();
	}

	private void listen() {
		GUIManager.getInstance().getPane().onKeyPressedProperty().unbind();
		this.password.setOnKeyPressed(event -> {
			if (event.getCode().equals(KeyCode.ENTER))
				login();
		});
		this.email.setOnKeyPressed(event -> {
			if (event.getCode().equals(KeyCode.ENTER))
				login();
		});
		this.email.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!ValidateManager.doesEmailPassValidation(newValue)) {
				this.email.setText(oldValue);
				return;
			}
			if (this.email.getText().contains("@") && this.email.getText().contains("."))
				this.password.setDisable(false);
			else
				this.password.setDisable(true);

		});
		this.password.textProperty().addListener((observable, oldValue, newValue) -> {
			if (this.password.getText().length() >= 8)
				this.login.setDisable(false);
			else
				this.login.setDisable(true);
		});
		this.login.setOnAction(event -> {
			if (Utilities.getInstance().getLoginAttempts() >= 5) {
				Notification.getInstance().createNotification("Login Exceeded", "You have attempted to login to this account to many times! You can try again in 7 seconds.", AlertType.ERROR);
				Utilities.getInstance().startLoginTimer();
				return;
			}
			login();
		});
		this.register.setOnAction(event -> {
			GUIManager.getInstance().getPane().getChildren().clear();
			new register_page().run();
		});
		this.resetPassword.setOnAction(event -> {
			try {
				Desktop.getDesktop().browse(new URI("https://beatplaylist.com/reset"));
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		});
	}

	private void login() {
		if (!StartupData.getInstance().server_status_number.equals("4"))
			return;
		String entered_email = this.email.getText();
		UserManager.getInstance().user.email = this.email.getText();
		UserManager.getInstance().user.password = this.password.getText();
		this.login.setDisable(true);
		System.out.println("LOGGING IN");
		GetAccessKeyState.send(new CompleteEvent() {
			@Override
			public void onSuccess() {

			}

			@Override
			public void onFail(String error) {
				Platform.runLater(() -> {
					login.setDisable(false);
					email.setText(entered_email);
					password.setText("");

					errorMessageText.setFill(Color.web(CustomColor.RED.getColorHex()));
					errorMessageText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 12));
					errorMessageText.wrappingWidthProperty().bind(centerBox.getContentVBox().widthProperty().subtract(10));

					if (error.equals("PASSWORD_INVALID") || error.equals("LOGIN DENIED") || error.equals("ACCESS_KEY DENIED")) {
						errorMessageText.setText("You entered an invalid email or password.");
					} else if (error.equals("LOGIN_ATTEMPT_EXCEEDED")) {
						errorMessageText.setText("Login attempts exceeded. Please wait.");
					} else {
						errorMessageText.setText("This account has been suspended from BeatPlaylist.");
					}
					if (!centerBox.getContentVBox().getChildren().contains(errorMessageText))
						centerBox.getContentVBox().getChildren().add(0, errorMessageText);
				});

			}
		});

		// new AuthenticateLoginSocket().send(this.login, this.register, this.use_offline_mode, this.resetPassword);
		this.password.setText("");
	}

	private void styleButton(Button button) {
		button.setMinSize(300, 35);
		button.setStyle("-fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-background-radius: 10px; -fx-border-radius: 10px;");
		button.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 14));
		button.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
		button.setCursor(Cursor.HAND);
		button.setOnMouseEntered(event -> {
			button.setTextFill(Color.web(CustomColor.RHYTHM.getColorHex()));
		});
		button.setOnMouseExited(event -> {
			button.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
		});
	}
}