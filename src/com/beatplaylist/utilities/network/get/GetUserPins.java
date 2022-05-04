package com.beatplaylist.utilities.network.get;

import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.beatplaylist.Main;
import com.beatplaylist.utilities.events.SocketReceiveEvent;
import com.beatplaylist.utilities.image.album.ImageHandler;
import com.beatplaylist.utilities.network.netty.NETTYClient;
import com.beatplaylist.utilities.network.serialized.FailType;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.network.serialized.PacketType;
import com.beatplaylist.utilities.network.serialized.Post;
import com.beatplaylist.utilities.pins.Pin;
import com.beatplaylist.utilities.pins.PinManager;
import com.beatplaylist.utilities.user.UserManager;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class GetUserPins {

	public GetUserPins() {
	}

	public static void send() {
		Post post = new Post();
		post.setDetails();
		post.setPacketType(PacketType.GET_PINS);

		JSONObject object = new JSONObject();
		object.put("search", JSONValue.escape(UserManager.getInstance().getUser().username));

		post.setJSONArray(object);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {
			@Override
			public void onSuccess(Post post) {
				JSONWrapper message = new JSONWrapper(post.getJSONMessage());

				Iterator<JSONObject> pins = message.getJSONArray("pins");

				while (pins.hasNext()) {
					JSONWrapper pin_object = new JSONWrapper(pins.next());

					String pin_name = pin_object.getJSONString("pin_name");
					String pin_image_url = pin_object.getJSONString("pin_image_url");
					boolean pin_can_trade = pin_object.getJSONBoolean("isTradeable");
					String pin_description = pin_object.getJSONString("pin_description");
					String unique_pin_id = pin_object.getJSONString("pin_uuid");
					int pin_slot = pin_object.getJSONInteger("pin_slot");

					Pin pin = new Pin();
					pin.setName(pin_name);
					pin.setImageURL(pin_image_url);
					pin.setTradeable(pin_can_trade);
					pin.setDescription(pin_description);
					pin.setUniquePinId(unique_pin_id);
					pin.setSlot(pin_slot);
					PinManager.getInstance().addPinToUser(pin_slot, pin);
				}
			}

			@Override
			public void onError(String error) {
				if (error.equals(FailType.RESULT_EMPTY.name()))
					return;
			}
		});
	}

	public static void send(VBox global_vbox, String user) {
		Post post = new Post();
		post.setDetails();
		post.setPacketType(PacketType.GET_PINS);

		JSONObject object = new JSONObject();
		object.put("search", JSONValue.escape(user));

		post.setJSONArray(object);

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {
			@Override
			public void onSuccess(Post post) {
				Platform.runLater(() -> {
					displayPins(global_vbox, post.getJSONMessage());
				});
			}

			@Override
			public void onError(String error) {
			}
		});
	}

	private static void displayPins(VBox global_vbox, JSONObject object) {

		// ProfileData.getInstance().pin_hbox.getChildren().clear();
		//
		// ProfileData.getInstance().pin_scrollpane.setVbarPolicy(ScrollBarPolicy.NEVER);
		// ProfileData.getInstance().pin_scrollpane.setHbarPolicy(ScrollBarPolicy.NEVER);
		// ProfileData.getInstance().pin_scrollpane.setMinWidth(200);
		// ProfileData.getInstance().pin_scrollpane.setMaxWidth(200);
		// ProfileData.getInstance().pin_scrollpane.setMinHeight(55);
		// ProfileData.getInstance().pin_scrollpane.setMaxHeight(55);
		// ProfileData.getInstance().pin_scrollpane.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + ";");
		//
		// ProfileData.getInstance().pin_hbox.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + ";");
		// ProfileData.getInstance().pin_hbox.setMinWidth(200);
		// ProfileData.getInstance().pin_hbox.setMinHeight(60);
		// ProfileData.getInstance().pin_hbox.setMaxHeight(60);

		JSONWrapper message = new JSONWrapper(object);

		Iterator<JSONObject> pins = message.getJSONArray("pins");

		while (pins.hasNext()) {
			JSONWrapper pin_object = new JSONWrapper(pins.next());

			String pin_name = pin_object.getJSONString("pin_name");
			String pin_image_url = pin_object.getJSONString("pin_image_url");
			boolean pin_can_trade = pin_object.getJSONBoolean("isTradeable");
			String pin_description = pin_object.getJSONString("pin_description");
			int pin_slot = pin_object.getJSONInteger("pin_slot");

			ImageView image_view = new ImageView();
			Image image = new Image(Main.class.getResource("/resources/pins/" + pin_image_url + "_small.png").toExternalForm());

			image_view.setFitHeight(45);
			image_view.setFitWidth(45);
			image_view.setStyle("-fx-cursor: hand;");
			image_view.setImage(image);

			image_view.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event) {
					JSONArray pins = (JSONArray) message.getJSONObject().get("pins");
					int position = pin_slot - 1;
					// ImageHandler.getInstance().showPinCollection(pins, position, ProfileData.getInstance().pin_hbox);
				}
			});

			Tooltip.install(image_view, new Tooltip(pin_name + "\n" + pin_description + "\nTradeable: " + pin_can_trade));
			// ProfileData.getInstance().pin_hbox.getChildren().add(image_view);
		}
		// ProfileData.getInstance().pin_scrollpane.setContent(ProfileData.getInstance().pin_hbox);
		// if (!global_vbox.getChildren().contains(ProfileData.getInstance().pin_scrollpane))
		// global_vbox.getChildren().add(0, ProfileData.getInstance().pin_scrollpane);
	}
}