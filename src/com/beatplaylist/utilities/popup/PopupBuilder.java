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

package com.beatplaylist.utilities.popup;

import com.beatplaylist.chromium.BrowserManager;
import com.beatplaylist.enums.FontType;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.utilities.CustomColor;
import com.beatplaylist.utilities.animation.BounceIn;
import com.beatplaylist.utilities.control.ToggleSwitch;
import com.beatplaylist.utilities.events.CompleteEvent;
import com.beatplaylist.utilities.events.PopupCloseEvent;
import com.beatplaylist.utilities.events.PopupOpenEvent;
import com.beatplaylist.utilities.popup.control.PopupHBox;
import com.beatplaylist.utilities.popup.control.PopupVBox;

import javafx.animation.FadeTransition;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class PopupBuilder {

	private HBox hbox;
	private PopupHBox headerHBox, buttonHBox;
	private VBox global_vbox;
	private PopupVBox wrapped_vbox, header_vbox, content_vbox, footer_vbox;
	private ScrollPane scroll_pane;
	private Text header_text;
	private CompleteEvent event;
	private Button confirmButton, cancelButton;

	public PopupBuilder() {
		new PopupOpenEvent();
		PopupManager.getInstance().activePopup.add(this);

		this.global_vbox = new VBox();
		this.hbox = new HBox();
		this.headerHBox = new PopupHBox();
		this.buttonHBox = new PopupHBox(25);

		this.wrapped_vbox = new PopupVBox(3);
		this.header_vbox = new PopupVBox(3);
		this.content_vbox = new PopupVBox(3);
		this.footer_vbox = new PopupVBox(3);

		this.header_text = new Text();

		this.confirmButton = new Button("Confirm") {
			public void requestFocus() {

			}
		};

		this.cancelButton = new Button("Cancel") {
			public void requestFocus() {

			}
		};

		this.hbox.minWidthProperty().bind(GUIManager.getInstance().backgroundPane.widthProperty());
		this.hbox.maxWidthProperty().bind(GUIManager.getInstance().backgroundPane.widthProperty());
		this.hbox.minHeightProperty().bind(GUIManager.getInstance().backgroundPane.heightProperty());
		this.hbox.maxHeightProperty().bind(GUIManager.getInstance().backgroundPane.heightProperty());
		this.hbox.setAlignment(Pos.CENTER);

		this.wrapped_vbox.setStyle("-fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-padding: 15px; -fx-background-radius: 10px; -fx-border-radius: 10px;");
		this.content_vbox.setStyle("-fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-background-radius: 10px; -fx-border-radius: 10px;");
		this.header_vbox.setStyle("-fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-background-radius: 10px; -fx-border-radius: 10px;");

		this.hbox.setOnMouseClicked(event -> {
			if (System.currentTimeMillis() - GUIManager.getInstance().applicationLastMoved < 1000 || event.getTarget() instanceof StackPane || event.getTarget() instanceof Rectangle || event.getTarget() instanceof ToggleSwitch || event.getTarget() instanceof Circle || event.getTarget() instanceof Rectangle || event.getTarget() instanceof PopupHBox || event.getTarget() instanceof ComboBox || event.getTarget() instanceof ImageView || event.getTarget() instanceof Text || event.getTarget() instanceof ScrollPane || event.getTarget() instanceof PopupVBox || event.getTarget() instanceof ComboBox)
				return;
			close();
		});

		this.buttonHBox.getChildren().addAll(this.cancelButton, this.confirmButton);
		configure();
	}

	private void configure() {
		this.global_vbox.setAlignment(Pos.CENTER);
		this.buttonHBox.setAlignment(Pos.BASELINE_LEFT);

		this.wrapped_vbox.setMinWidth(400);

		this.header_text.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.EXTRA_BOLD, 20));
		this.header_text.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		this.header_text.setWrappingWidth(400);

		this.headerHBox.minWidthProperty().bind(this.getContentVBox().widthProperty());
		this.headerHBox.maxWidthProperty().bind(this.getContentVBox().widthProperty());

		this.buttonHBox.minWidthProperty().bind(this.getContentVBox().widthProperty());
		this.buttonHBox.maxWidthProperty().bind(this.getContentVBox().widthProperty());

		this.cancelButton.setStyle("-fx-background-color: " + CustomColor.RED.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-background-radius: 15px; -fx-border-radius: 15px;");
		this.cancelButton.setMinWidth(150);
		this.cancelButton.setMaxWidth(150);
		this.cancelButton.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 14));
		this.cancelButton.setCursor(Cursor.HAND);

		this.confirmButton.setStyle("-fx-background-color: " + CustomColor.RHYTHM.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-background-radius: 15px; -fx-border-radius: 15px;");
		this.confirmButton.setMinWidth(150);
		this.confirmButton.setMaxWidth(150);
		this.confirmButton.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 14));
		this.confirmButton.setCursor(Cursor.HAND);

		this.cancelButton.setOnMouseEntered(event -> {
			this.cancelButton.setStyle("-fx-background-color: " + CustomColor.DARK_RED.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-background-radius: 15px; -fx-border-radius: 15px;");
		});
		this.cancelButton.setOnMouseExited(event -> {
			this.cancelButton.setStyle("-fx-background-color: " + CustomColor.RED.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-background-radius: 15px; -fx-border-radius: 15px;");
		});

		this.confirmButton.setOnMouseEntered(event -> {
			this.confirmButton.setStyle("-fx-background-color: " + CustomColor.DARK_GREEN.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-background-radius: 15px; -fx-border-radius: 15px;");
		});
		this.confirmButton.setOnMouseExited(event -> {
			this.confirmButton.setStyle("-fx-background-color: " + CustomColor.RHYTHM.getColorHex() + "; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + "; -fx-background-radius: 15px; -fx-border-radius: 15px;");
		});

		// Can be overridden later.
		this.onCancel(event -> {
			close();
		});
	}

	public void onConfirm(EventHandler<MouseEvent> event) {
		this.confirmButton.setOnMouseClicked(event);
	}

	public void onCancel(EventHandler<MouseEvent> event) {
		this.cancelButton.setOnMouseClicked(event);
	}

	// Set confirm and cancel buttons state
	public void setButtonsDisabled(boolean disabled) {
		this.confirmButton.setDisable(disabled);
		this.cancelButton.setDisable(disabled);
	}

	// Calls the onConfirm event.
	public void runConfirmPress() {
		this.confirmButton.fire();
	}

	public void setConfirmButtonText(String value) {
		this.confirmButton.setText(value);
	}
	
	public void setCancelButtonText(String value) {
		this.cancelButton.setText(value);
	}
	
	public String getCancelText() {
		return this.cancelButton.getText();
	}

	// Hide cancel and confirm buttons.
	public void hideButtonBar() {
		this.confirmButton.setVisible(false);
		this.cancelButton.setVisible(false);
	}

	// Hide cancel button.
	public void hideCancelButton() {
		this.buttonHBox.getChildren().remove(this.cancelButton);
		centerButtonBar();
	}

	public void centerButtonBar() {
		this.buttonHBox.setAlignment(Pos.CENTER);
	}

	public void setWrapperSize(double min_width, double min_height, double max_width, double max_height) {
		this.wrapped_vbox.setMinSize(min_width, min_height);
		this.wrapped_vbox.setMaxSize(max_width, max_height);
	}

	public void setWrapperMinimumSize(double min_width, double min_height) {
		this.wrapped_vbox.setMinSize(min_width, min_height);
	}

	public void setHeaderText(String value) {
		this.header_text.setText(value);
	}

	public void setScrollPaneEnabled(boolean value, double min_width, double min_height, double max_width, double max_height) {
		if (value) {
			if (this.scroll_pane != null)
				return;
			this.scroll_pane = new ScrollPane() {
				public void requestFocus() {
				}
			};
			this.scroll_pane.setStyle("-fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + ";");
			this.scroll_pane.setHbarPolicy(ScrollBarPolicy.NEVER);
			this.scroll_pane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);

			this.scroll_pane.setMinSize(min_width, min_height);
			this.scroll_pane.setMaxSize(max_width, max_height);

			this.content_vbox.setMinSize(min_width, min_height - 2);
			this.content_vbox.setMaxWidth(max_width);

			this.header_vbox.setMaxWidth(max_width);
			this.header_vbox.setMinWidth(max_width);

			this.scroll_pane.setContent(this.content_vbox);

			this.content_vbox.setOnScroll(event -> {
				double deltaY = event.getDeltaY() * 6; // *6 to make the scrolling a bit faster
				double height = this.scroll_pane.getContent().getBoundsInLocal().getHeight();
				double vvalue = this.scroll_pane.getVvalue();
				this.scroll_pane.setVvalue(vvalue + -deltaY / height); // deltaY/width to make the scrolling equally fast regardless of the actual height of the component
			});
		} else
			this.scroll_pane = null;
	}

	public void setCustomScrollPaneDefaults(ScrollPane scrollPane, VBox vbox, double min_width, double min_height, double max_width, double max_height) {
		scrollPane.setStyle("-fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + ";");
		scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);

		scrollPane.setMinSize(min_width, min_height);
		scrollPane.setMaxSize(max_width, max_height);

		scrollPane.setContent(vbox);

		vbox.setOnScroll(event -> {
			double deltaY = event.getDeltaY() * 6; // *6 to make the scrolling a bit faster
			double height = scrollPane.getContent().getBoundsInLocal().getHeight();
			double vvalue = scrollPane.getVvalue();
			scrollPane.setVvalue(vvalue + -deltaY / height); // deltaY/width to make the scrolling equally fast regardless of the actual height of the component
		});
	}

	public void open() {
		open(false);
		new BounceIn(this.getGlobalVBox()).play();
		this.wrapped_vbox.setEffect(null);
	}

	// This is called in the PopupOld class - This will be removed.
	public void open(CompleteEvent event) {
		open(false);
		new BounceIn(this.getGlobalVBox()).play();
		this.wrapped_vbox.setEffect(null);
		this.event = event;
	}

	public void open(boolean noEffect) {
		// Add buttons to popup.
		getContentVBox().getChildren().add(this.buttonHBox);

		if (!this.header_text.getText().isEmpty()) {
			this.headerHBox.getChildren().add(this.header_text);
			this.wrapped_vbox.getChildren().add(this.headerHBox);
		}

		if (!this.header_vbox.getChildren().isEmpty())
			this.wrapped_vbox.getChildren().add(this.header_vbox);

		if (this.scroll_pane == null)
			this.wrapped_vbox.getChildren().add(this.content_vbox);
		else
			this.wrapped_vbox.getChildren().add(this.scroll_pane);

		if (!this.footer_vbox.getChildren().isEmpty())
			this.wrapped_vbox.getChildren().add(this.footer_vbox);

		this.global_vbox.getChildren().add(this.wrapped_vbox);
		this.hbox.getChildren().add(this.global_vbox);
		if (!GUIManager.getInstance().backgroundPane.getChildren().contains(this.hbox))
			GUIManager.getInstance().backgroundPane.getChildren().add(this.hbox);
	}

	public void add() {
		GUIManager.getInstance().backgroundPane.getChildren().remove(this.hbox);
		new PopupOpenEvent();
		GUIManager.getInstance().backgroundPane.getChildren().add(this.hbox);
	}

	public void close() {
		FadeTransition faid_transition = new FadeTransition(Duration.millis(200), this.getGlobalVBox());
		faid_transition.setFromValue(1);
		faid_transition.setToValue(0);
		faid_transition.play();
		faid_transition.setOnFinished(event -> {
			this.hbox.getChildren().clear();
			GUIManager.getInstance().backgroundPane.getChildren().remove(this.hbox);
			BrowserManager.getInstance().getCurrentBrowser().web_view.setVisible(true);
			GUIManager.getInstance().searchBrowser.web_view.setVisible(true);
			if (PopupManager.getInstance().activePopup.contains(this))
				PopupManager.getInstance().activePopup.remove(this);
			new PopupCloseEvent();
			if (this.event != null)
				this.event.onSuccess();
		});
	}

	public void centerHeaderText() {
		this.headerHBox.setAlignment(Pos.CENTER);
		this.header_vbox.setAlignment(Pos.CENTER);
		this.header_text.setTextAlignment(TextAlignment.CENTER);
	}

	public void quickClose() {
		this.hbox.getChildren().clear();
		GUIManager.getInstance().backgroundPane.getChildren().remove(this.hbox);
		BrowserManager.getInstance().getCurrentBrowser().web_view.setVisible(true);
		GUIManager.getInstance().searchBrowser.web_view.setVisible(true);
		if (PopupManager.getInstance().activePopup.contains(this))
			PopupManager.getInstance().activePopup.remove(this);
		new PopupCloseEvent();
		if (this.event != null)
			this.event.onSuccess();
	}

	public void quickCloseWithoutEvent() {
		this.hbox.getChildren().clear();
		GUIManager.getInstance().backgroundPane.getChildren().remove(this.hbox);
		BrowserManager.getInstance().getCurrentBrowser().web_view.setVisible(true);
		GUIManager.getInstance().searchBrowser.web_view.setVisible(true);
	}

	public Text getHeaderText() {
		return this.header_text;
	}

	public ScrollPane getScrollPane() {
		return this.scroll_pane;
	}

	public VBox getHeaderVBox() {
		return this.header_vbox;
	}

	public VBox getGlobalVBox() {
		return this.global_vbox;
	}

	public VBox getWrappedVBox() {
		return this.wrapped_vbox;
	}

	public VBox getContentVBox() {
		return this.content_vbox;
	}

	public VBox getFooterVBox() {
		return this.footer_vbox;
	}

	public HBox getButtonHBox() {
		return this.buttonHBox;
	}
}