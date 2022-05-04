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
import com.beatplaylist.gui.module.layout.sidebar.TabType;
import com.teamdev.jxbrowser.browser.event.FullScreenEntered;
import com.teamdev.jxbrowser.view.javafx.BrowserView;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public class FullscreenHandler implements FullScreenEntered {

	private Pane pane;
	private BrowserView view;

	public FullscreenHandler(BrowserView view) {
		this.pane = new Pane();
		this.view = view;

		// GUIManager.getInstance().stage.focusedProperty().addListener((observable, oldValue, newValue) -> {
		// onFullScreenExit();
		// });
	}

	public void onFullScreenEnter() {
		Platform.runLater(() -> {
			unbind();
			this.view.setLayoutX(0);
			this.view.setLayoutY(0);

			this.view.minWidthProperty().bind(this.pane.widthProperty());
			this.view.maxWidthProperty().bind(this.pane.widthProperty());
			this.view.minHeightProperty().bind(this.pane.heightProperty());
			this.view.maxHeightProperty().bind(this.pane.heightProperty());

			// we need to get screen that program is on rather than primary screen
			if (!this.pane.getChildren().contains(this.view))
				this.pane.getChildren().add(this.view);

			if (this.pane.getScene() == null)
				GUIManager.getInstance().stage.setScene(new Scene(this.pane));
			else
				GUIManager.getInstance().stage.setScene(this.pane.getScene());
			GUIManager.getInstance().stage.setFullScreen(true);
			GUIManager.getInstance().stage.setFullScreenExitHint("");
			//GUIManager.getInstance().stage.setFullScreenExitKeyCombination(KeyCombination.keyCombination("Esc"));
		});
	}

	public void onFullScreenExit() {
		Platform.runLater(() -> {
			GUIManager.getInstance().stage.setFullScreen(false);
			GUIManager.getInstance().stage.setScene(GUIManager.getInstance().backgroundPane.getScene());
			GUIManager.getInstance().topBar.titleBar.searchTextfield.requestFocus();

			this.pane.getChildren().clear();

			unbind();

			if (GUIManager.getInstance().currentTab.tab == TabType.NOW_PLAYING) {

				BrowserManager.getInstance().getCurrentBrowser().web_view.minWidthProperty().bind(GUIManager.getInstance().contentManager.contentPane.widthProperty().divide(1.8));
				BrowserManager.getInstance().getCurrentBrowser().web_view.maxWidthProperty().bind(GUIManager.getInstance().contentManager.contentPane.widthProperty().divide(1.8));
				BrowserManager.getInstance().getCurrentBrowser().web_view.setMinHeight(372);
				BrowserManager.getInstance().getCurrentBrowser().web_view.setMaxHeight(372);
				GUIManager.getInstance().sideBar.sideBarTab.changeTab(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.NOW_PLAYING));
			} else {
				BrowserManager.getInstance().getCurrentBrowser().web_view.setMinSize(5, 5);
				BrowserManager.getInstance().getCurrentBrowser().web_view.setMaxSize(5, 5);
			}
			GUIManager.getInstance().videoBrowser.web_view.requestFocus();
		});
	}

	private void unbind() {
		BrowserManager.getInstance().getCurrentBrowser().web_view.minWidthProperty().unbind();
		BrowserManager.getInstance().getCurrentBrowser().web_view.maxWidthProperty().unbind();
		BrowserManager.getInstance().getCurrentBrowser().web_view.minHeightProperty().unbind();
		BrowserManager.getInstance().getCurrentBrowser().web_view.maxHeightProperty().unbind();
	}
}