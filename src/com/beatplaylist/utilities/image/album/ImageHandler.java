package com.beatplaylist.utilities.image.album;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.beatplaylist.Main;
import com.beatplaylist.enums.FontType;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.utilities.CustomColor;
import com.beatplaylist.utilities.control.Tooltip;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.popup.PopupBuilder;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ImageHandler {

	private static ImageHandler instance = new ImageHandler();

	public static ImageHandler getInstance() {
		return instance;
	}

	private int i;
	private PopupBuilder popup;

	public void showPinCollection(JSONArray pins, int start_int, HBox hbox) {
		this.popup = new PopupBuilder();

		VBox vbox = new VBox(5);
		Label next = new Label(">"), previous = new Label("<");

		this.i = start_int;

		JSONObject object = (JSONObject) pins.get(start_int);

		showPin(vbox, object, next, previous, hbox);
		next.setOnMouseClicked(event -> {
			this.i++;
			int size = pins.size();
			if (this.i >= size)
				this.i = 0;
			vbox.getChildren().clear();
			JSONObject pin_object = (JSONObject) pins.get(i);
			showPin(vbox, pin_object, next, previous, hbox);
		});
		previous.setOnMouseClicked(event -> {
			this.i--;
			if (this.i < 0) {
				int size = pins.size();
				this.i = size - 1;
			}
			vbox.getChildren().clear();
			JSONObject pin_object = (JSONObject) pins.get(i);
			showPin(vbox, pin_object, next, previous, hbox);
		});
	}

	private void showPin(VBox vbox, JSONObject pin, Label next, Label previous, HBox global_hbox) {
		this.popup.getContentVBox().setSpacing(7);
		this.popup.getContentVBox().setMinHeight(250);
		this.popup.getWrappedVBox().setStyle("-fx-padding: 35 15 35 15; -fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + ";");

		HBox hbox = new HBox(10);
		hbox.setAlignment(Pos.CENTER);

		JSONWrapper pin_info = new JSONWrapper(pin);

		String pin_name = pin_info.getJSONString("pin_name");
		String pin_image_url = pin_info.getJSONString("pin_image_url");
		boolean pin_can_trade = pin_info.getJSONBoolean("isTradeable");
		String pin_description = pin_info.getJSONString("pin_description");

		this.popup.setHeaderText(pin_name);
		this.popup.getHeaderText().setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 18));
		//this.popup.getHeaderText().setFill(Color.web(CustomColor.LIME.getColorHex()));

		Image image = new Image(Main.class.getResource("/resources/pins/" + pin_image_url + "_large.png").toExternalForm());
		ImageView pin_image = new ImageView();
		pin_image.setImage(image);
		pin_image.setFitHeight(175);
		pin_image.setFitWidth(175);
		pin_image.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0); -fx-border-color: " + CustomColor.WHITE.getColorHex() + "; -fx-border-insets: 3px; -fx-border-radius: 7px; -fx-border-width: 1.0");

		styleImageButton(next, pin_image.getFitHeight() + 600, pin_image.getFitHeight() / 2);
		styleImageButton(previous, pin_image.getFitWidth() - 200, pin_image.getFitWidth() / 2);

		hbox.getChildren().addAll(previous, pin_image, next);

		TextFlow trade_flow = new TextFlow();
		Text description = new Text(pin_description), tradeable_text = new Text("Tradeable: "), tradeable_value_text = new Text(String.valueOf(pin_can_trade));

		description.setFont(Font.font(FontType.DEFAULT.getName(), 16));
		description.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		description.wrappingWidthProperty().bind(this.popup.getContentVBox().widthProperty().subtract(15));

		tradeable_text.setFont(Font.font(FontType.DEFAULT.getName(), 16));
		tradeable_text.setFill(Color.web(CustomColor.WHITE.getColorHex()));

		Tooltip.install(pin_image, new Tooltip(pin_description));
		Tooltip.install(this.popup.getHeaderText(), new Tooltip(pin_description));

		tradeable_value_text.setFont(Font.font(FontType.DEFAULT.getName(), 16));
		if (pin_can_trade)
			tradeable_value_text.setFill(Color.web(CustomColor.RHYTHM.getColorHex()));
		else
			tradeable_value_text.setFill(Color.web(CustomColor.ERROR.getColorHex()));

		trade_flow.getChildren().addAll(tradeable_text, tradeable_value_text);

		vbox.getChildren().addAll(hbox, trade_flow);

		if (this.popup.getContentVBox().getChildren().isEmpty()) {
			this.popup.getContentVBox().getChildren().add(vbox);
			this.popup.open();
		}
	}

	private void styleImageButton(Label button, double x, double y) {
		button.setStyle("-fx-font-size: 6em; -fx-cursor: hand; -fx-background-color: transparent; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + ";");
		button.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.EXTRA_BOLD, 26));
		button.setOnMouseEntered(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				button.setStyle("-fx-font-size: 6em; -fx-cursor: hand; -fx-background-color: transparent; -fx-text-fill: " + CustomColor.RHYTHM.getColorHex() + ";");
			}
		});
		button.setOnMouseExited(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				button.setStyle("-fx-font-size: 6em; -fx-cursor: hand; -fx-background-color: transparent; -fx-text-fill: " + CustomColor.WHITE.getColorHex() + ";");
			}
		});
		getPane().getChildren().add(button);
	}

	public Pane getPane() {
		return GUIManager.getInstance().getPane();
	}
}