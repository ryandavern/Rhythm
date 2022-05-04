package com.beatplaylist.gui.module.page.register;

import org.json.simple.JSONObject;

import com.beatplaylist.enums.FontType;
import com.beatplaylist.enums.LoginErrorType;
import com.beatplaylist.gui.CenterBox;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.LoginGUIManager;
import com.beatplaylist.gui.module.page.login.login_page;
import com.beatplaylist.settings.Settings;
import com.beatplaylist.utilities.CustomColor;
import com.beatplaylist.utilities.control.ControlledPasswordField;
import com.beatplaylist.utilities.control.ControlledTextField;
import com.beatplaylist.utilities.events.CompleteEvent;
import com.beatplaylist.utilities.events.SocketReceiveEvent;
import com.beatplaylist.utilities.network.netty.NETTYClient;
import com.beatplaylist.utilities.network.post.CreateAccount;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.network.serialized.PacketType;
import com.beatplaylist.utilities.network.serialized.Post;
import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;
import com.beatplaylist.utilities.user.LoginUser;
import com.beatplaylist.utilities.validation.ValidateManager;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

public class register_page {

	private ControlledTextField usernameTextField, emailTextField;
	private ControlledPasswordField passwordTextField, confirmPasswordTextField;
	private Button signupButton, backToLoginButton;
	private TextFlow username_textflow;
	private Text createAccountHeader, username_text, usernameTakenText, emailHeaderText, passwordHeaderText;
	private VBox username_vbox, display_name_vbox, passwordVBox, buttonVBox;
	private CenterBox centerBox;

	// Stores last attempted username check against server.
	private String lastCheckedUsername = "";

	public register_page() {
		this.centerBox = new CenterBox();

		this.username_vbox = new VBox(3);
		this.display_name_vbox = new VBox(3);
		this.passwordVBox = new VBox(3);
		this.buttonVBox = new VBox(7);

		this.username_textflow = new TextFlow();

		this.usernameTextField = new ControlledTextField(20);
		this.emailTextField = new ControlledTextField(254);

		this.passwordTextField = new ControlledPasswordField(8, 30);
		this.confirmPasswordTextField = new ControlledPasswordField(8, 30);

		this.createAccountHeader = new Text("Create your account");
		this.username_text = new Text("USERNAME");
		this.emailHeaderText = new Text("EMAIL");
		this.passwordHeaderText = new Text("PASSWORD");
		this.usernameTakenText = new Text("");

		this.signupButton = new Button("Create Account") {
			public void requestFocus() {

			}
		};
		this.backToLoginButton = new Button("Back to Login") {
			public void requestFocus() {

			}
		};
	}

	public void run() {
		GUIManager.getInstance().getPane().setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + ";");
		GUIManager.getInstance().getPane().onKeyPressedProperty().unbind();
		GUIManager.getInstance().getStage().setTitle("BeatPlaylist | Register");
		configure();
		listen();
		LoginGUIManager.getInstance().loginTitleBar();
	}

	public void configure() {
		this.centerBox.getContentVBox().setSpacing(15);

		// Style Text objects
		this.createAccountHeader.setFont(Font.font(FontType.DEFAULT.getName(), 18));
		this.createAccountHeader.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		this.createAccountHeader.setTextAlignment(TextAlignment.LEFT);

		this.username_text.setFont(Font.font(FontType.DEFAULT.getName(), 12));
		this.username_text.setFill(Color.web(CustomColor.GRAY.getColorHex()));

		this.emailHeaderText.setFont(Font.font(FontType.DEFAULT.getName(), 12));
		this.emailHeaderText.setFill(Color.web(CustomColor.GRAY.getColorHex()));

		this.passwordHeaderText.setFont(Font.font(FontType.DEFAULT.getName(), 12));
		this.passwordHeaderText.setFill(Color.web(CustomColor.GRAY.getColorHex()));

		this.usernameTakenText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 12));
		this.usernameTakenText.setFill(Color.web(CustomColor.RED.getColorHex()));

		// Style TextField objects
		this.usernameTextField.getTextField().setMinHeight(35);
		this.usernameTextField.getTextField().minWidthProperty().bind(this.centerBox.getContentVBox().widthProperty());
		this.usernameTextField.getTextField().setStyle("-fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-border-width: 0 0 3 0; -fx-border-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
		this.usernameTextField.getTextField().setPromptText("Username, e.g. beatplaylist");

		this.emailTextField.getTextField().setStyle("-fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
		this.emailTextField.getTextField().setPromptText("Email");
		this.emailTextField.getTextField().setMinHeight(35);
		this.emailTextField.getTextField().minWidthProperty().bind(this.centerBox.getContentVBox().widthProperty());
		this.emailTextField.getTextField().setFont(Font.font(FontType.DEFAULT.getName(), 12));

		// Style password and confirm password textfield.
		this.passwordTextField.getPasswordField().setMinHeight(35);
		this.passwordTextField.getPasswordField().minWidthProperty().bind(this.centerBox.getContentVBox().widthProperty());
		this.passwordTextField.getPasswordField().setStyle("-fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
		this.passwordTextField.getPasswordField().setDisable(true);
		this.passwordTextField.getPasswordField().setPromptText("Password");
		this.passwordTextField.getPasswordField().setFont(Font.font(FontType.DEFAULT.getName(), 12));

		this.confirmPasswordTextField.getPasswordField().setMinHeight(35);
		this.confirmPasswordTextField.getPasswordField().minWidthProperty().bind(this.centerBox.getContentVBox().widthProperty());
		this.confirmPasswordTextField.getPasswordField().setStyle("-fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
		this.confirmPasswordTextField.getPasswordField().setDisable(true);
		this.confirmPasswordTextField.getPasswordField().setPromptText("Confirm Password");
		this.confirmPasswordTextField.getPasswordField().setFont(Font.font(FontType.DEFAULT.getName(), 12));

		// Style signup button
		styleButton(this.signupButton);
		this.signupButton.setDisable(true);

		// Style back to login button.
		styleButton(this.backToLoginButton);

		this.username_textflow.getChildren().addAll(this.username_text, this.usernameTakenText);
		this.username_vbox.getChildren().addAll(this.username_textflow, this.usernameTextField);
		this.display_name_vbox.getChildren().addAll(this.emailHeaderText, this.emailTextField);
		this.passwordVBox.getChildren().addAll(this.passwordHeaderText, this.passwordTextField, this.confirmPasswordTextField);
		this.buttonVBox.getChildren().addAll(this.signupButton, this.backToLoginButton);

		this.centerBox.getContentVBox().getChildren().addAll(this.createAccountHeader, this.username_vbox, this.display_name_vbox, this.passwordVBox, this.buttonVBox);

		if (!GUIManager.getInstance().getPane().getChildren().contains(this.centerBox.getWrapperHBox()))
			GUIManager.getInstance().getPane().getChildren().add(this.centerBox.getWrapperHBox());

		FadeTransition fadeTransition = new FadeTransition(Duration.millis(250), this.centerBox.getWrapperHBox());
		fadeTransition.setFromValue(0.0);
		fadeTransition.setToValue(1.0);
		fadeTransition.play();
	}

	public void listen() {
		this.backToLoginButton.setOnMouseClicked(event -> {
			Settings.getInstance().setAutoLogin(false, true);
			new login_page().run();
		});
		this.usernameTextField.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
			if (!ValidateManager.doesUsernamePassValidation(newValue)) {
				this.usernameTextField.setText(oldValue);
				return;
			}
			if (this.usernameTextField.getText().length() > 20) {
				if (this.usernameTextField.getText().length() <= 1)
					this.usernameTextField.setText("");
				else
					this.usernameTextField.setText(oldValue);
			}
			if (!this.usernameTextField.getText().isEmpty() && this.emailTextField.getText().contains("@") && this.emailTextField.getText().contains(".") && this.emailTextField.getText().length() > 5) {
				this.passwordTextField.getPasswordField().setDisable(false);
				this.confirmPasswordTextField.getPasswordField().setDisable(false);
			} else {
				this.passwordTextField.getPasswordField().setDisable(true);
				this.confirmPasswordTextField.getPasswordField().setDisable(true);
			}
		});
		this.emailTextField.focusedProperty().addListener((obs, oldVal, newVal) -> {
			checkForValidUsernameEntry(null);
		});
		this.passwordTextField.focusedProperty().addListener((obs, oldVal, newVal) -> {
			checkForValidUsernameEntry(null);
		});
		this.confirmPasswordTextField.focusedProperty().addListener((obs, oldVal, newVal) -> {
			checkForValidUsernameEntry(null);
		});
		this.emailTextField.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
			if (!ValidateManager.doesEmailPassValidation(newValue)) {
				this.emailTextField.setText(oldValue);
				return;
			}
			if (!this.usernameTextField.getText().isEmpty() && this.emailTextField.getText().contains("@") && this.emailTextField.getText().contains(".") && this.emailTextField.getText().length() > 5) {
				this.passwordTextField.getPasswordField().setDisable(false);
				this.confirmPasswordTextField.getPasswordField().setDisable(false);
			} else {
				this.passwordTextField.getPasswordField().setDisable(true);
				this.confirmPasswordTextField.getPasswordField().setDisable(true);
			}
			if (newValue.equals("@"))
				return;
			if (this.emailTextField.getText().length() > 254) {
				if (this.emailTextField.getText().length() <= 1)
					this.emailTextField.setText("");
				else
					this.emailTextField.setText(oldValue);
			}
		});
		this.confirmPasswordTextField.getPasswordField().textProperty().addListener((observable, oldValue, newValue) -> {
			if (this.confirmPasswordTextField.getText().length() < 8)
				this.signupButton.setDisable(true);
			else
				this.signupButton.setDisable(false);
		});
		this.signupButton.setOnAction(event -> {
			attemptSignup();
		});
		GUIManager.getInstance().getPane().setOnKeyPressed(event -> {
			if (event.getCode().equals(KeyCode.ENTER))
				attemptSignup();
		});
	}

	// Check if username user entered is valid.
	public void checkForValidUsernameEntry(LoginUser user) {
		String checkedUsername = this.usernameTextField.getText();
		if (checkedUsername.isEmpty() || (user == null && this.lastCheckedUsername.equals(this.usernameTextField.getText())))
			return;
		this.lastCheckedUsername = checkedUsername;

		new Thread(() -> {
			Post post = new Post();
			post.setPacketType(PacketType.CHECK_FOR_VALID_USERNAME);

			JSONObject object = new JSONObject();
			object.put("search_username", checkedUsername);

			post.setJSONArray(object);

			NETTYClient.getInstance().send(post, new SocketReceiveEvent() {

				@Override
				public void onSuccess(Post post) {
					Platform.runLater(() -> {
						JSONWrapper response = new JSONWrapper(post.getJSONMessage());

						if (response.getJSONString("response").equals("USERNAME TAKEN") || response.getJSONString("response").equals("USERNAME RESERVED")) {
							usernameTakenText.setText("  -  (TAKEN)");
							usernameTakenText.setFill(Color.web(CustomColor.RED.getColorHex()));
							usernameTextField.setStyle("-fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-border-width: 0 0 3 0; -fx-border-color: " + CustomColor.RED.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
						} else if (response.getJSONString("response").equals("USERNAME AVAILABLE")) {
							usernameTakenText.setText("  -  (AVAILABLE)");
							usernameTakenText.setFill(Color.web(CustomColor.RHYTHM.getColorHex()));
							if (user != null) {
								CreateAccount.send(user, new CompleteEvent() {

									@Override
									public void onSuccess() {
										new login_page(LoginErrorType.ACCOUNT_CREATED).run();
									}

									@Override
									public void onFail(String error) {
										if (error.equals("ACCOUNT USERNAME EXISTS")) {
											usernameTextField.setStyle("-fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-border-width: 0 0 3 0; -fx-border-color: " + CustomColor.RED.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
											Notification.getInstance().createNotification("Account", "The username you entered already exists!", AlertType.ERROR);
										} else if (error.equals("INVALID_USERNAME")) {
											usernameTextField.setStyle("-fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-border-width: 0 0 3 0; -fx-border-color: " + CustomColor.RED.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
											Notification.getInstance().createNotification("Account", "Your username can only contain Only use letters, numbers and '_'.", AlertType.ERROR);
										} else if (error.equals("INVALID_EMAIL")) {
											emailTextField.setStyle("-fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-border-width: 0 0 3 0; -fx-border-color: " + CustomColor.RED.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
											Notification.getInstance().createNotification("Account", "Please enter a valid email.", AlertType.ERROR);
										} else if (error.equals("EMAIL_IN_USE")) {
											emailTextField.setStyle("-fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-border-width: 0 0 3 0; -fx-border-color: " + CustomColor.RED.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
											Notification.getInstance().createNotification("Account", "The email you entered is already in use.", AlertType.ERROR);
										} else if (error.equals("PASSWORD_LENGTH")) {
											passwordTextField.setStyle("-fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-border-width: 0 0 3 0; -fx-border-color: " + CustomColor.RED.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
											Notification.getInstance().createNotification("Password", "Your password must be longer than 7 characters.", AlertType.ERROR);
										} else {
											Notification.getInstance().createNotification("Account", "A problem occured while creating your account!", AlertType.ERROR);
										}
									}
								});
							}
						}
					});
				}

				@Override
				public void onError(String error) {
				}
			});
		}).start();
	}

	public void attemptSignup() {
		boolean signupPassable = true;

		// Check if username is valid.
		if (this.usernameTextField.getText().isEmpty()) {
			this.usernameTextField.setStyle("-fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-border-width: 0 0 3 0; -fx-border-color: " + CustomColor.RED.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
			signupPassable = false;
		} else
			this.usernameTextField.setStyle("-fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-border-width: 0 0 3 0; -fx-border-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");

		// Check if email is valid.
		if (this.emailTextField.getText().isEmpty()) {
			this.emailTextField.setStyle("-fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-border-width: 0 0 3 0; -fx-border-color: " + CustomColor.RED.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
			signupPassable = false;
		} else
			this.emailTextField.setStyle("-fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-border-width: 0 0 3 0; -fx-border-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");

		// Check if password is valid.
		if (this.passwordTextField.getText().isEmpty() || this.passwordTextField.getText().length() < 8) {
			this.passwordTextField.setStyle("-fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-border-width: 0 0 3 0; -fx-border-color: " + CustomColor.RED.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
			signupPassable = false;
			if (this.passwordTextField.getText().length() < 8) {
				Notification.getInstance().createNotification("Password", "Your password must be longer than 7 characters.", AlertType.ERROR);
			}
		} else
			this.passwordTextField.setStyle("-fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-border-width: 0 0 3 0; -fx-border-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
		
		if (!this.confirmPasswordTextField.getText().equals(this.passwordTextField.getText())) {
			this.confirmPasswordTextField.setStyle("-fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-border-width: 0 0 3 0; -fx-border-color: " + CustomColor.RED.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
			signupPassable = false;
			Notification.getInstance().createNotification("Password", "Your confirm password doesn't match your password!", AlertType.ERROR);
		}
		
		LoginUser user = new LoginUser();
		user.username = this.usernameTextField.getText().replace("\\s", "").trim();
		user.email = this.emailTextField.getText().replace("\\s", "").trim();
		user.password = this.passwordTextField.getText().replace("\\s", "").trim();

		if (signupPassable) {
			checkForValidUsernameEntry(user);
		}
	}

	private void styleButton(Button button) {
		button.setMinSize(300, 35);
		button.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 14));
		button.setStyle("-fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-background-radius: 10px; -fx-border-radius: 10px;");
		button.setCursor(Cursor.HAND);
		button.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
		button.setOnMouseEntered(event -> {
			button.setTextFill(Color.web(CustomColor.RHYTHM.getColorHex()));
		});
		button.setOnMouseExited(event -> {
			button.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
		});
	}
}