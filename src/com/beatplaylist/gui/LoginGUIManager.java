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

package com.beatplaylist.gui;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import com.beatplaylist.Main;
import com.beatplaylist.enums.LoginErrorType;
import com.beatplaylist.gui.module.page.login.auto_update_page;
import com.beatplaylist.gui.module.page.login.login_page;
import com.beatplaylist.settings.Settings;
import com.beatplaylist.utilities.data.Data;
import com.beatplaylist.utilities.filemanager.FileUtilities;
import com.beatplaylist.utilities.filemanager.OperatingSystem;
import com.beatplaylist.utilities.image.ImageBuilder;
import com.beatplaylist.utilities.network.utilities.NetworkManager;
import com.beatplaylist.utilities.update.StartupData;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class LoginGUIManager {

	private static LoginGUIManager instance = new LoginGUIManager();

	public static LoginGUIManager getInstance() {
		return instance;
	}

	public Pane backgroundPane;
	public Stage stage;

	// Initialize Stage and Panes.
	// THIS METHOD SHOULD ONLY BE CALLED ON ONCE.
	public void initializeStage() {
		this.stage = new Stage();
		this.backgroundPane = new Pane();

		// Set stage scene, stage width and height
		this.stage.setScene(new Scene(this.backgroundPane));
		this.stage.setWidth(Data.getInstance().loginSize.getWidth());
		this.stage.setHeight(Data.getInstance().loginSize.getHeight());

		// Set stage properties
		this.stage.initStyle(StageStyle.UNDECORATED);
		this.stage.setTitle("Rhythm");
		this.stage.setResizable(false); // - Login Interface does not allow resizing.
		if (Settings.getInstance().launchMinimized())
			this.stage.setIconified(true);

		this.stage.getIcons().add(new Image(Main.class.getResource("/resources/logo.png").toExternalForm()));
		this.stage.getScene().getStylesheets().add(getClass().getResource("/resources/styles.css").toExternalForm());
		this.stage.show();

		GUIManager.getInstance().handleTaskBarMinimizeApplication();

		UIResize.getInstance().addResizeListener(true);

		if (!NetworkManager.hasInternetConnection())
			new login_page(LoginErrorType.NETWORK_CONNECTION_ERROR).run();
		else {
			if (StartupData.getInstance().pendingUpdate) {
				new auto_update_page().run();
			} else {
				new login_page().run();
			}
		}
		loginTitleBar();
	}

	// Called when the user is logged in successfully. We dispose of the login stage and login gui class and transition to the GUIManager class.
	public void handleLoginToBrowserTransition() {
		this.backgroundPane.getChildren().clear();
		this.stage.close();

		this.backgroundPane = null;
		this.stage = null;

		GUIManager.getInstance().initializeStage();
	}

	public void loginTitleBar() {
		BorderPane border_pane = new BorderPane();
		border_pane.minWidthProperty().bind(this.backgroundPane.widthProperty());
		border_pane.maxWidthProperty().bind(this.backgroundPane.widthProperty());

		BorderPane.setAlignment(border_pane, Pos.CENTER_RIGHT);
		HBox button_hbox = new HBox(5);

		ImageBuilder close_builder = new ImageBuilder(new Image(Main.class.getResource("/resources/title_bar_icon/exit.png").toExternalForm(), 15, 15, false, false), new Image(Main.class.getResource("/resources/title_bar_icon/exit_hover.png").toExternalForm(), 15, 15, false, false));
		ImageBuilder minimise_builder = new ImageBuilder(new Image(Main.class.getResource("/resources/title_bar_icon/minimise.png").toExternalForm(), 15, 15, false, false), new Image(Main.class.getResource("/resources/title_bar_icon/minimise_hover.png").toExternalForm(), 15, 15, false, false));

		close_builder.getHBox().setMinWidth(50);
		close_builder.getHBox().setAlignment(Pos.CENTER);
		close_builder.getHBox().setStyle("-fx-cursor: hand;");

		minimise_builder.getHBox().setMinWidth(50);
		minimise_builder.getHBox().setAlignment(Pos.CENTER);
		minimise_builder.getHBox().setStyle("-fx-cursor: hand;");

		minimise_builder.getHBox().setOnMouseClicked(event -> {
			this.stage.setIconified(true);
		});
		close_builder.getHBox().setOnMouseClicked(event -> {
			System.exit(0);
			this.stage.fireEvent(new WindowEvent(this.stage, WindowEvent.WINDOW_CLOSE_REQUEST));
			Platform.exit();
			if (FileUtilities.getInstance().getOperatingSystem() == OperatingSystem.WINDOWS) {
				try {
					if (GlobalScreen.isNativeHookRegistered())
						GlobalScreen.unregisterNativeHook();
				} catch (NativeHookException e) {
					e.printStackTrace();
				}
			}

			System.exit(0);
			Runtime.getRuntime().exit(0);
		});

		button_hbox.getChildren().addAll(minimise_builder.getHBox(), close_builder.getHBox());

		border_pane.setRight(button_hbox);
		border_pane.setStyle("-fx-padding: 10px;");
		this.backgroundPane.getChildren().add(border_pane);
	}

	// Application Start Method:

	// Check for an internet connection
	// - If a connection cannot be found, open the login page with the network_connection_failed error

	// If a connection is found, check for an update
	// - If an update is found, open the auto_update page

	// If no update is found, open the login page - If servers are under maintenance display correct error message

}