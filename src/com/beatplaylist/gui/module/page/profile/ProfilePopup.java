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

package com.beatplaylist.gui.module.page.profile;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.Calendar;

import javax.imageio.ImageIO;

import com.beatplaylist.Main;
import com.beatplaylist.controller.ClickDragY;
import com.beatplaylist.controller.ProfileImageSelector;
import com.beatplaylist.enums.DateType;
import com.beatplaylist.enums.FontType;
import com.beatplaylist.enums.GenreType;
import com.beatplaylist.enums.ResourceIcon;
import com.beatplaylist.settings.Settings;
import com.beatplaylist.utilities.CustomColor;
import com.beatplaylist.utilities.control.ControlledTextArea;
import com.beatplaylist.utilities.control.ControlledTextField;
import com.beatplaylist.utilities.control.TextField;
import com.beatplaylist.utilities.events.ImageResponseEvent;
import com.beatplaylist.utilities.events.SocketResponseEvent;
import com.beatplaylist.utilities.image.ImageManager;
import com.beatplaylist.utilities.network.post.EditProfileInformation;
import com.beatplaylist.utilities.network.post.ResetProfilePicture;
import com.beatplaylist.utilities.network.post.UpdateProfileImage;
import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;
import com.beatplaylist.utilities.popup.Popup;
import com.beatplaylist.utilities.popup.PopupBuilder;
import com.beatplaylist.utilities.popup.control.PopupHBox;
import com.beatplaylist.utilities.user.UserManager;
import com.beatplaylist.utilities.validation.ValidateManager;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class ProfilePopup {

	public static void editProfile(ImageResponseEvent imageEvent, SocketResponseEvent socketEvent) {
		PopupBuilder popup = new PopupBuilder();
		popup.setScrollPaneEnabled(true, 500, 0, 500, 550);
		popup.getContentVBox().setSpacing(15);
		popup.setHeaderText("Edit your profile");
		popup.setConfirmButtonText("Save Changes");

		ImageView profileImageView = new ImageView();
		Calendar calender = Calendar.getInstance();
		SimpleBooleanProperty hasResetProfilePicture = new SimpleBooleanProperty(false);

		PopupHBox profileImageHBox = new PopupHBox(5), birthdayHBox = new PopupHBox(15);

		ControlledTextField displayNameTextField = new ControlledTextField(50), usernameTextField = new ControlledTextField(20), emailTextField = new ControlledTextField(254), locationTextField = new ControlledTextField(90);
		ControlledTextArea bioTextArea = new ControlledTextArea(150);

		ComboBox<String> genre_box = new ComboBox<>(), birth_year_combobox = new ComboBox<>(), birth_month_combobox = new ComboBox<>(), birth_day_combobox = new ComboBox<>();

		Button editButton = new Button("Edit") {
			public void requestFocus() {

			}
		}, resetProfilePicture = new Button("Reset") {
			public void requestFocus() {

			}
		};

		ImageManager.getProfileImage(profileImageView, UserManager.getInstance().user.profileImageURL, 50, 50);

		editButton.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-cursor: hand;");
		editButton.setMinSize(75, 25);
		editButton.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
		editButton.setOnAction(event -> {
			popup.quickClose();
			editProfileImage(imageEvent);
		});
		editButton.setOnMouseEntered(event -> {
			editButton.setTextFill(Color.web(Settings.getInstance().getDefaultColor()));
		});
		editButton.setOnMouseExited(event -> {
			editButton.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
		});

		resetProfilePicture.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-cursor: hand;");
		resetProfilePicture.setMinSize(75, 25);
		resetProfilePicture.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
		resetProfilePicture.setOnAction(event -> {
			if (hasResetProfilePicture.getValue()) {
				return;
			}
			confirmResetProfilePicture(profileImageView, hasResetProfilePicture);
		});
		resetProfilePicture.setOnMouseEntered(event -> {
			resetProfilePicture.setTextFill(Color.web(Settings.getInstance().getDefaultColor()));
		});
		resetProfilePicture.setOnMouseExited(event -> {
			resetProfilePicture.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
		});

		configureTextField(usernameTextField.getTextField());
		usernameTextField.getTextField().setPromptText("Username");
		usernameTextField.setText(UserManager.getInstance().user.username);

		configureTextField(displayNameTextField.getTextField());
		displayNameTextField.getTextField().setPromptText("Display Name");
		displayNameTextField.setText(UserManager.getInstance().user.displayName);

		configureTextField(emailTextField.getTextField());
		emailTextField.getTextField().setPromptText("Email address");
		emailTextField.setText(UserManager.getInstance().user.email);

		configureTextField(locationTextField.getTextField());
		locationTextField.getTextField().setPromptText("Location");
		locationTextField.setText(UserManager.getInstance().user.location);

		// Bio TextArea Styles
		bioTextArea.getTextArea().setMinSize(400, 75);
		bioTextArea.getTextArea().setMaxSize(400, 75);
		bioTextArea.getTextArea().setPromptText("Write something about yourself.");
		bioTextArea.getTextArea().setStyle("-fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
		bioTextArea.getTextArea().setWrapText(true);
		if (!UserManager.getInstance().user.bio.isEmpty())
			bioTextArea.getTextArea().setText(UserManager.getInstance().user.bio);

		// Setup Genre Styles
		for (GenreType genres : GenreType.values())
			genre_box.getItems().add(genres.getName());
		genre_box.setValue(UserManager.getInstance().user.favouriteGenre.getName());
		genre_box.setMinWidth(200);
		genre_box.setMaxWidth(200);
		genre_box.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-border-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-cursor: hand;");

		// Birth Year Styles
		int year = calender.get(Calendar.YEAR);
		for (int i = year - 6; i > year - 101; i--) {
			birth_year_combobox.getItems().add(String.valueOf(i));
		}
		birth_year_combobox.setValue((UserManager.getInstance().user.birthYear > 0 ? String.valueOf(UserManager.getInstance().user.birthYear) : "Birth Year"));
		birth_year_combobox.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-border-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-cursor: hand;");

		// Birth Month Styles
		birth_month_combobox.getItems().addAll("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
		birth_month_combobox.setValue((UserManager.getInstance().user.birthMonth > 0 ? new DateFormatSymbols().getMonths()[UserManager.getInstance().user.birthMonth - 1] : "Birth Month"));
		birth_month_combobox.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-border-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-cursor: hand;");

		// Birth Day Styles
		for (int i = 1; i < 32; i++)
			birth_day_combobox.getItems().add(String.valueOf(i));
		birth_day_combobox.setValue((UserManager.getInstance().user.birthDay > 0 ? String.valueOf(UserManager.getInstance().user.birthDay) : "Birth Day"));
		birth_day_combobox.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-focus-color: none; -fx-border-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-cursor: hand;");

		popup.onConfirm(event -> {
			if (emailTextField.getText().equals(UserManager.getInstance().user.email) //
					&& displayNameTextField.getText().equals(UserManager.getInstance().user.displayName) //
					&& locationTextField.getText().equals(UserManager.getInstance().user.location) //
					&& Integer.valueOf(birth_year_combobox.getValue()) == UserManager.getInstance().user.birthYear //
					&& birth_month_combobox.getValue().equals(new DateFormatSymbols().getMonths()[UserManager.getInstance().user.birthMonth - 1]) //
					&& Integer.valueOf(birth_day_combobox.getValue()) == UserManager.getInstance().user.birthDay //
					&& bioTextArea.getText().equals(UserManager.getInstance().user.bio) //
					&& genre_box.getValue().equals(UserManager.getInstance().user.favouriteGenre.getName())) {
				Notification.getInstance().createNotification("Edit Profile", "Your account details are the same as the entry fields", AlertType.ERROR);
				return;
			}
			if (usernameTextField.getText().isEmpty() || !ValidateManager.doesUsernamePassValidation(usernameTextField.getText()) || usernameTextField.getText().length() > usernameTextField.getMaxCharacters()) {
				Notification.getInstance().createNotification("Edit Profile", "Please enter a valid username", AlertType.ERROR);
				return;
			}
			if (emailTextField.getText().isEmpty() || !emailTextField.getText().contains("@") || !emailTextField.getText().contains(".") || !ValidateManager.doesEmailPassValidation(emailTextField.getText()) || emailTextField.getText().length() > emailTextField.getMaxCharacters()) {
				Notification.getInstance().createNotification("Edit Profile", "Please enter a valid email address", AlertType.ERROR);
				return;
			}
			if (displayNameTextField.getText().isEmpty() || displayNameTextField.getText().length() > displayNameTextField.getMaxCharacters()) {
				Notification.getInstance().createNotification("Edit Profile", "Please enter a valid display name", AlertType.ERROR);
				return;
			}

			int birth_month = 0;
			if (!birth_month_combobox.getValue().equals("Birth Month"))
				birth_month = Integer.valueOf(DateType.getName(birth_month_combobox.getValue().replace("Birth Month", "")).getId());

			String newUsername = usernameTextField.getText();
			if (newUsername.equals(UserManager.getInstance().user.username))
				newUsername = "";

			String genre = String.valueOf(GenreType.getGenreFromValue(genre_box.getValue()));

			EditProfileInformation.send(newUsername, emailTextField.getText(), displayNameTextField.getText(), locationTextField.getText(), birth_year_combobox.getValue().replace("Birth Year", ""), birth_month, birth_day_combobox.getValue().replace("Birth Day", ""), bioTextArea.getText(), genre, socketEvent);
			popup.close();
		});
		// Add an additional textproperty listener
		usernameTextField.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
			if (!ValidateManager.doesUsernamePassValidation(newValue)) {
				usernameTextField.setText(oldValue);
				return;
			}
		});
		emailTextField.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
			if (!ValidateManager.doesEmailPassValidation(newValue))
				emailTextField.setText(oldValue);
		});

		profileImageHBox.setAlignment(Pos.BOTTOM_LEFT);

		birthdayHBox.getChildren().addAll(birth_year_combobox, birth_month_combobox, birth_day_combobox);
		profileImageHBox.getChildren().addAll(profileImageView, editButton, resetProfilePicture);

		popup.getContentVBox().getChildren().addAll(profileImageHBox, usernameTextField, displayNameTextField, emailTextField, bioTextArea, locationTextField, birthdayHBox, genre_box);
		popup.open();
	}

	public static void editProfileImage(ImageResponseEvent imageEvent) {
		PopupBuilder popup = new PopupBuilder();
		popup.getContentVBox().setSpacing(5);
		popup.setWrapperMinimumSize(400, 150);
		popup.setHeaderText("Edit Profile Picture");
		popup.hideButtonBar();

		ProfileImageSelector profileImageSelector = new ProfileImageSelector();
		VBox vbox = new VBox(5);

		Text acceptedFileType = new Text("Accepted File Types: png / jpg"), maxFileSize = new Text("Max File Size: 4mb");
		acceptedFileType.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));
		acceptedFileType.setFill(Color.web(CustomColor.GRAY.getColorHex()));

		maxFileSize.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));
		maxFileSize.setFill(Color.web(CustomColor.GRAY.getColorHex()));

		HBox hbox = new HBox();

		hbox.setAlignment(Pos.CENTER);

		StackPane stackPane = new StackPane();

		stackPane.setCursor(Cursor.HAND);
		stackPane.setMaxWidth(128);
		stackPane.setMaxHeight(128);
		StackPane.setAlignment(stackPane, Pos.CENTER);

		ImageView profileImageView = new ImageView(), cameraImageView = new ImageView(new Image(Main.class.getResource(ResourceIcon.PROFILE.getPath() + "camera.png").toExternalForm(), 36, 36, false, false));
		ImageManager.getProfileImage(profileImageView, UserManager.getInstance().user.profileImageURL, 128, 128);

		stackPane.getChildren().addAll(profileImageView, cameraImageView);
		hbox.getChildren().add(stackPane);
		vbox.getChildren().addAll(acceptedFileType, maxFileSize, hbox);
		popup.getContentVBox().getChildren().add(vbox);

		profileImageSelector.selectImageView(profileImageView, "");

		stackPane.setOnMouseEntered(event -> {
			cameraImageView.setImage(new Image(Main.class.getResource(ResourceIcon.PROFILE.getPath() + "camera_hover.png").toExternalForm(), 36, 36, false, false));
		});
		stackPane.setOnMouseExited(event -> {
			cameraImageView.setImage(new Image(Main.class.getResource(ResourceIcon.PROFILE.getPath() + "camera.png").toExternalForm(), 36, 36, false, false));
		});
		stackPane.setOnMouseClicked(event -> {
			openDirectorySelection(popup, profileImageSelector, imageEvent);
		});

		popup.onCancel(event -> {
			popup.close();
		});

		popup.open();
	}

	public static void cropImage(ProfileImageSelector profileImageSelector, ImageResponseEvent imageEvent) {
		PopupBuilder popup = new PopupBuilder();
		popup.getContentVBox().setSpacing(5);
		popup.setWrapperMinimumSize(500, 500);
		popup.setHeaderText("Edit Profile Picture");
		popup.setConfirmButtonText("Apply");

		ClickDragY clickY = new ClickDragY();

		ScrollPane scrollPane = new ScrollPane();
		VBox vbox = new VBox(5);

		popup.setCustomScrollPaneDefaults(scrollPane, vbox, 450, 400, 450, 450);

		vbox.setMinWidth(450);
		vbox.setMinHeight(450);

		HBox hbox = new HBox();

		hbox.setAlignment(Pos.CENTER);
		hbox.setMinWidth(450);
		hbox.setMinHeight(400);

		Group group = new Group();

		group.setCursor(Cursor.HAND);

		ImageView profileImageView = new ImageView(profileImageSelector.getImagePath());
		profileImageView.setFitWidth(400);
		profileImageView.setPreserveRatio(true);

		Rectangle rect = new Rectangle();
		rect.setStroke(Color.web(CustomColor.RHYTHM.getColorHex()));
		rect.setStrokeWidth(4);
		rect.setStrokeLineCap(StrokeLineCap.ROUND);
		rect.setFill(Color.TRANSPARENT);
		rect.setX(0);
		rect.setY(0);
		rect.setWidth(400);
		rect.setHeight(400);

		rect.setOnMouseClicked(event -> {
			clickY.setClickY(event.getSceneY());
		});
		rect.setOnMouseDragged(event -> {

			double newValue = event.getSceneY() - clickY.getClickY();

			if (newValue <= 0) {
				rect.setY(0);
				return;
			}
			if ((event.getY() + 400) >= profileImageView.getImage().getHeight()) {
				rect.setY(profileImageView.getImage().getHeight() - 600);
				return;
			}
			clickY.setClickY(event.getSceneY() - clickY.getClickY());
			rect.setY(event.getSceneY() - clickY.getClickY());
		});

		group.getChildren().addAll(profileImageView, rect);
		hbox.getChildren().add(group);

		vbox.getChildren().addAll(hbox);

		popup.getContentVBox().getChildren().add(scrollPane);

		popup.onCancel(event -> {
			popup.close();
		});
		popup.onConfirm(event -> {
			Bounds bounds = rect.getBoundsInParent();
			int width = (int) bounds.getWidth();
			int height = (int) bounds.getHeight();
			SnapshotParameters parameters = new SnapshotParameters();
			parameters.setFill(Color.TRANSPARENT);
			parameters.setViewport(new Rectangle2D(bounds.getMinX(), bounds.getMinY(), width, height));

			WritableImage wi = new WritableImage(width, height);
			profileImageView.snapshot(parameters, wi);

			BufferedImage bufImageARGB = SwingFXUtils.fromFXImage(wi, null);
			BufferedImage bufImageRGB = new BufferedImage(bufImageARGB.getWidth(), bufImageARGB.getHeight(), BufferedImage.OPAQUE);

			Graphics2D graphics = bufImageRGB.createGraphics();
			graphics.drawImage(bufImageARGB, 0, 0, null);

			try {

				if (profileImageSelector.getImagePath().contains(".jpg"))
					ImageIO.write(bufImageRGB, "jpg", new File("C:\\Users\\OEM\\Desktop\\test.jpg"));
				else
					ImageIO.write(bufImageRGB, "png", new File("C:\\Users\\OEM\\Desktop\\test.png"));

			} catch (IOException e) {
				e.printStackTrace();
			}

			graphics.dispose();

			// if (profileImageSelector.hasNewProfileImageSelected()) {
			// UpdateProfileImage.send(profileImageSelector.getImagePath(), imageEvent);
			// }
		});

		popup.open();
	}

	private static void configureTextField(TextField textField) {
		textField.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);");
		textField.setMinWidth(400);
		textField.setMaxWidth(400);
		textField.setFont(Font.font(FontType.DEFAULT.getName(), 14));
	}

	private static void openDirectorySelection(PopupBuilder popup, ProfileImageSelector profileImageSelector, ImageResponseEvent imageEvent) {
		String path = "C:\\Users\\" + System.getProperty("user.name") + "\\Desktop";
		FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");

		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(imageFilter);
		fc.setTitle("Select an image");
		File file = fc.showOpenDialog(null);
		if (file == null)
			return;
		// long file_size = file.length();
		// long kb_size = file_size / 1024;
		// long megabyte_size = kb_size / 1024;
		double fileSize = file.length();
		double kbSize = fileSize / 1024;
		double mbSize = kbSize / 1024;
		if (mbSize > 4.00) {
			Notification.getInstance().createNotification("Profile Picture", "You cannot upload images larger than a file size of 4MB", AlertType.ERROR);
			return;
		}

		path = file.getAbsolutePath();
		if (path.isEmpty())
			return;
		if (file.getName().toLowerCase().endsWith(".jpg") || file.getName().toLowerCase().endsWith(".png")) {
			profileImageSelector.selectNewImageView("file:" + path);
			profileImageSelector.getImageView().setImage(new Image("file:" + path));
			popup.quickClose();
			//cropImage(profileImageSelector, imageEvent);
			
			if (profileImageSelector.hasNewProfileImageSelected()) {
				UpdateProfileImage.send(profileImageSelector.getImagePath(), imageEvent);
			}
		} else {
			Notification.getInstance().createNotification("Profile Image", "We currently only support .jpg and .png files. More extensions will be added in the future.", AlertType.ERROR);
		}
	}

	public static void confirmResetProfilePicture(ImageView imageView, SimpleBooleanProperty hasResetProfilePicture) {
		if (hasResetProfilePicture.getValue()) {
			return;
		}

		PopupBuilder popup = new PopupBuilder();
		popup.getContentVBox().setSpacing(30);
		popup.setHeaderText("Are you sure you want to reset your profile picture?");

		popup.onConfirm(event -> {
			popup.quickClose();
			imageView.setImage(new Image(Main.class.getResource("/resources/default_profile.png").toExternalForm(), 50, 50, false, false));
			hasResetProfilePicture.set(true);
			ResetProfilePicture.send();
		});

		popup.open();
	}
}