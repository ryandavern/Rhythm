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

package com.beatplaylist.chromium;

import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.utilities.CustomColor;
import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.engine.Language;
import com.teamdev.jxbrowser.engine.ProprietaryFeature;
import com.teamdev.jxbrowser.engine.RenderingMode;
import com.teamdev.jxbrowser.view.javafx.BrowserView;

import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Inspector {

	private static Inspector instance = new Inspector();

	public static Inspector getInstance() {
		return instance;
	}

	public Pane pane;
	public Stage stage;
	public Engine engine;
	public Browser browserEngine;
	public BrowserView browserView;
	public VBox vbox;

	public TextField textField;

	public void inspectInitialize() {
		this.stage = new Stage();
		this.pane = new Pane();
		this.textField = new TextField();
		this.vbox = new VBox(10);
		
		this.engine = EngineBrowser.getInstance().engine;
		this.browserEngine = this.engine.newBrowser();
		this.browserView = BrowserView.newInstance(this.browserEngine);

		this.browserView.setMinSize(1000, 500);
		this.browserView.setMaxSize(1000, 500);

		this.stage.setScene(new Scene(this.pane, 1000, 600));

		this.textField.setMinWidth(200);

		this.textField.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				this.browserEngine.navigation().loadUrl(this.textField.getText());
			}
		});
		this.pane.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + ";");
		this.vbox.getChildren().addAll(this.textField, this.browserView);
		this.pane.getChildren().add(this.vbox);
	}

	public void inspect(boolean video) {
		String inspectURL = GUIManager.getInstance().videoBrowser.getWebEngine().devTools().remoteDebuggingUrl().get();
		if (!video)
			inspectURL = GUIManager.getInstance().searchBrowser.web_engine.devTools().remoteDebuggingUrl().get();
		System.out.println("LOADING " + inspectURL);
		this.browserEngine.navigation().loadUrl(inspectURL);
		this.stage.show();
	}

}