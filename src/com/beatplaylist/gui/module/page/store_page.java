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

package com.beatplaylist.gui.module.page;

import java.util.Iterator;

import org.json.simple.JSONObject;

import com.beatplaylist.Main;
import com.beatplaylist.enums.FontType;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.utilities.CustomColor;
import com.beatplaylist.utilities.CustomToolTip;
import com.beatplaylist.utilities.control.SpacedText;
import com.beatplaylist.utilities.events.SocketReceiveEvent;
import com.beatplaylist.utilities.network.netty.NETTYClient;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.network.serialized.PacketType;
import com.beatplaylist.utilities.network.serialized.Post;
import com.beatplaylist.utilities.pins.PinManager;
import com.beatplaylist.utilities.user.UserManager;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

public class store_page extends Page {

	private VBox containerVBox, premiumVBox, pinVBox;
	private ScrollPane containerScrollPane;

	private HBox premiumHBox;
	private FlowPane pinFlowPane;

	private SpacedText premiumHeaderText, pinHeaderText;

	public store_page() {
		this.containerVBox = new VBox(45);
		this.premiumVBox = new VBox(10);
		this.pinVBox = new VBox(10);

		this.premiumHBox = new HBox(15);

		this.containerScrollPane = new ScrollPane() {
			public void requestFocus() {

			}
		};
		this.pinFlowPane = new FlowPane();

		this.premiumHeaderText = new SpacedText("PREMIUM SUBSCRIPTIONS", 2.7);
		this.pinHeaderText = new SpacedText("PINS", 2.7);

	}

	public void run() {
		configure();
		handleResize();
	}

	private void configure() {
		this.containerVBox.setStyle("-fx-padding: " + GUIManager.getInstance().padding + "px;");
		this.containerScrollPane.setContent(this.containerVBox);
		this.containerScrollPane.setStyle("-fx-background-color: transparent;");
		this.containerScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		this.containerScrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		this.containerScrollPane.minWidthProperty().bind(GUIManager.getInstance().contentManager.contentPane.widthProperty().subtract(250));
		this.containerScrollPane.maxWidthProperty().bind(GUIManager.getInstance().contentManager.contentPane.widthProperty().subtract(250));
		this.containerScrollPane.minHeightProperty().bind(GUIManager.getInstance().contentManager.contentPane.heightProperty().subtract(60));
		this.containerScrollPane.maxHeightProperty().bind(GUIManager.getInstance().contentManager.contentPane.heightProperty().subtract(60));

		this.pinFlowPane.setHgap(15);
		this.pinFlowPane.setVgap(15);
		this.pinFlowPane.prefWidthProperty().bind(this.containerScrollPane.widthProperty());

		this.premiumHeaderText.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		this.premiumHeaderText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 24));

		this.pinHeaderText.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		this.pinHeaderText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 24));

		loadStore();

		this.premiumVBox.getChildren().addAll(this.premiumHeaderText, this.premiumHBox);
		this.pinVBox.getChildren().addAll(this.pinHeaderText, this.pinFlowPane);

		this.containerVBox.getChildren().addAll(this.premiumVBox, this.pinVBox);
		GUIManager.getInstance().contentManager.contentPane.getChildren().add(this.containerScrollPane);

		FadeTransition fadeTransition = new FadeTransition(Duration.millis(250), this.containerScrollPane);
		fadeTransition.setFromValue(0.0);
		fadeTransition.setToValue(1.0);
		fadeTransition.play();
	}

	@Override
	public void onResize(boolean height) {
		System.out.println("STORE");
	}

	private void loadStore() {
		this.premiumHBox.getChildren().clear();
		this.pinFlowPane.getChildren().clear();

		Post post = new Post();
		post.setDetails();
		post.setPacketType(PacketType.GET_SHOP);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {
			@Override
			public void onSuccess(Post post) {
				JSONWrapper response = new JSONWrapper(post.getJSONMessage());
				Iterator<JSONObject> storeItems = response.getJSONArray("storeItems");

				while (storeItems.hasNext()) {
					JSONWrapper wrapper = new JSONWrapper(storeItems.next());
					Platform.runLater(() -> {
						addItem(wrapper);
					});
				}
			}

			@Override
			public void onError(String error) {
			}
		});
	}

	private void addItem(JSONWrapper store_item) {
		String item_name = store_item.getJSONString("item_name");
		String item_description = store_item.getJSONString("item_description");
		String item_id = store_item.getJSONString("item_id");
		String item_price = store_item.getJSONString("item_price");
		String image_url = store_item.getJSONString("image_url");
		String item_type = store_item.getJSONString("item_type");
		String available = store_item.getJSONString("item_state");

		VBox vbox = new VBox(5);

		TextFlow title_text_flow = new TextFlow(), price_text_flow = new TextFlow();
		Text title = new Text(item_name), availability = new Text(), price_text = new Text("$" + item_price + "0 (" + UserManager.getInstance().getUser().currency.name() + ")");

		ImageView image_view = new ImageView();

		Button purchase_button = new Button("Purchase") {
			public void requestFocus() {

			}
		};

		// Style vbox wrapper.
		vbox.setStyle("-fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + ";");

		// Style title and availability text.
		if (available.equals("AVAILABLE"))
			title.setFill(Color.web("#FDE3A7"));
		else if (available.equals("LIMITED")) {
			title.setFill(Color.web("#FDE3A7"));
			availability.setText(" - Limited");
			availability.setFill(Color.web(CustomColor.RED.getColorHex()));
		} else
			title.setFill(Color.web("#E74C3C"));

		title.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 18));
		availability.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.THIN, 16));

		// Style price text
		price_text.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 14));
		price_text.setFill(Color.web(CustomColor.WHITE.getColorHex()));

		// Add price_text to price_text_flow
		price_text_flow.getChildren().add(price_text);

		if (item_id.toLowerCase().contains("subscription")) {
			Text subscription_text = new Text(" per month");
			subscription_text.setFill(Color.web(CustomColor.RED.getColorHex()));
			subscription_text.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 14));

			price_text_flow.getChildren().add(subscription_text);
		}

		// Load store item image.
		Image image = null;
		if (item_type.equals("PIN"))
			image = new Image(Main.class.getResource("/resources/pins/" + image_url + "_large.png").toExternalForm(), 175, 175, false, false);
		else if (item_type.equals("PREMIUM"))
			image = new Image(Main.class.getResource("/resources/shop/" + image_url + ".png").toExternalForm(), 175, 175, false, false);

		image_view.setImage(image);
		image_view.setPreserveRatio(true);
		CustomToolTip.install(image_view, new CustomToolTip(item_description));

		if (item_type.equals("PIN") && PinManager.getInstance().hasPin(item_name)) {
			purchase_button.setText("Owned");
			purchase_button.setDisable(true);
		} else if (available.equals("SOLD_OUT")) {
			purchase_button.setText("Sold Out");
			purchase_button.setDisable(true);
		}
		// Style purchase button.
		purchase_button.minWidthProperty().bind(vbox.widthProperty());
		purchase_button.setMinHeight(35);
		purchase_button.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 14));
		purchase_button.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
		purchase_button.setCursor(Cursor.HAND);
		purchase_button.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + ";");

		if (item_id.toLowerCase().contains("subscription")) {
			if (UserManager.getInstance().getUser().isPremium()) {
				purchase_button.setText("Cancel Subscription");
				purchase_button.setStyle("-fx-background-color: " + CustomColor.RED.getColorHex() + ";");
				purchase_button.setOnMouseEntered(event -> {
					purchase_button.setStyle("-fx-background-color: #C0392B;");
				});
				purchase_button.setOnMouseExited(event -> {
					purchase_button.setStyle("-fx-background-color: " + CustomColor.RED.getColorHex() + ";");
				});
			}
		} else {
			purchase_button.setOnMouseEntered(event -> {
				purchase_button.setTextFill(Color.web(CustomColor.RHYTHM.getColorHex()));
			});
			purchase_button.setOnMouseExited(event -> {
				purchase_button.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
			});
		}

		title_text_flow.getChildren().addAll(title);
		if (available.equals("LIMITED"))
			title_text_flow.getChildren().addAll(availability);

		vbox.getChildren().addAll(title_text_flow, price_text_flow, image_view, purchase_button);

		if (item_type.equals("PREMIUM"))
			this.premiumHBox.getChildren().add(vbox);
		else
			this.pinFlowPane.getChildren().add(vbox);
	}
}