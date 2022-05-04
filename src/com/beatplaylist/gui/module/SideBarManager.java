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

package com.beatplaylist.gui.module;

import com.beatplaylist.Options;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.module.layout.sidebar.ProfileMenu;
import com.beatplaylist.gui.module.layout.sidebar.TabBuilder;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class SideBarManager {

	// Sidebar Pane
	public VBox sidebarPane;
	public ProfileMenu profileMenu;
	public TabBuilder sideBarTab;

	public void initializeSideBarPane() {
		this.sidebarPane = new VBox();
		if (Options.showLayoutConstraint)
			this.sidebarPane.setStyle("-fx-background-color: green;");
		else {
			// this.sidebarPane.setStyle("-fx-background-color: linear-gradient(to top right, #3a3a3a 10%, #211f20 100%);");
		}

		this.profileMenu = new ProfileMenu();
		this.sideBarTab = new TabBuilder();
	}

	public void configure() {
		this.sidebarPane.setLayoutX(0);
		this.sidebarPane.setLayoutY(0);

		// Set min/max bind values to the same.
		this.sidebarPane.minHeightProperty().bind(GUIManager.getInstance().stage.heightProperty().multiply(0.87)); // Bind SideBar height to 80% of backgroundPane height. (SideBar = 80% + AudioBar = 20% = 100%)
		this.sidebarPane.maxHeightProperty().bind(GUIManager.getInstance().stage.heightProperty().multiply(0.87)); // Bind SideBar height to 80% of backgroundPane height. (SideBar = 80% + AudioBar = 20% = 100%)

		this.sidebarPane.minWidthProperty().bind(GUIManager.getInstance().stage.widthProperty().multiply(0.18)); // Bind SideBar width to 25% of backgroundPane width.
		this.sidebarPane.maxWidthProperty().bind(GUIManager.getInstance().stage.widthProperty().multiply(0.18)); // Bind SideBar width to 25% of backgroundPane width.

		// Initialize and build the tab.
		this.sideBarTab.makeTab();
	}

	public Pane getSideBarPane() {
		return this.sidebarPane;
	}
}