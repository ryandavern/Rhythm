package com.beatplaylist.utilities.notification;

import java.util.ArrayList;
import java.util.List;

import com.beatplaylist.enums.FontType;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.utilities.CustomColor;
import com.beatplaylist.utilities.animation.BounceIn;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class Notification {

	private static Notification instance = new Notification();

	public static Notification getInstance() {
		return instance;
	}

	private List<VBox> currentNotification = new ArrayList<>();
	private Timeline timeline, relocate_timeline, end_timer;

	public void createNotification(String title, String message, AlertType alertType) {
		Platform.runLater(() -> {
			if (this.currentNotification.size() > 0) {
				GUIManager.getInstance().backgroundPane.getChildren().remove(this.currentNotification.get(0));
				this.currentNotification.clear();
			}
			if (this.timeline != null) {
				this.timeline.stop();
				this.timeline = null;
			}
			VBox global_vbox = new VBox(), header_vbox = new VBox(), body_vbox = new VBox();

			global_vbox.setLayoutX(8);
			global_vbox.layoutYProperty().bind(GUIManager.getInstance().getStage().heightProperty().subtract(250));

			Text title_text = new Text(title.toUpperCase()), body_text = new Text(message);
			title_text.setTextAlignment(TextAlignment.CENTER);
			title_text.setFont(Font.font(FontType.VERDANA.getName(), FontWeight.BOLD, 14));
			title_text.setFill(Color.web(CustomColor.WHITE.getColorHex()));
			title_text.setWrappingWidth(195);

			body_text.setTextAlignment(TextAlignment.CENTER);
			body_text.setFont(Font.font(FontType.VERDANA.getName(), 13));
			body_text.setFill(Color.web(CustomColor.WHITE.getColorHex()));
			body_text.setWrappingWidth(195);

			String header_color = "#27AE60", body_color = CustomColor.RHYTHM.getColorHex();
			if (alertType == AlertType.ERROR) {
				header_color = "#ff3939";
				body_color = "#ff3939";
			} else if (alertType == AlertType.WARNING) {
				header_color = "#f9a025";
				body_color = "#f9a025";
			} else {
				header_color = "#43b649";
				body_color = "#43b649";
			}
			header_vbox.setStyle("-fx-border-radius: 10 10 0 0; -fx-background-radius: 10 10 0 0; -fx-cursor: hand; -fx-background-color: " + header_color + "; -fx-padding: 5px;");
			header_vbox.setMinWidth(198);
			header_vbox.setMaxWidth(210);

			body_vbox.setStyle("-fx-border-radius: 0 0 10 10; -fx-background-radius: 0 0 10 10; -fx-cursor: hand; -fx-background-color: " + body_color + "; -fx-padding: 7px;");
			body_vbox.setMinWidth(198);
			body_vbox.setMaxWidth(210);

			global_vbox.setOnMouseClicked(event -> {
				createAnimationTimer(global_vbox);
			});
			this.timeline = new Timeline(new KeyFrame(Duration.seconds(7), event -> {
				if (this.currentNotification.isEmpty())
					return;
				createAnimationTimer(global_vbox);
			}));

			this.timeline.play();
			header_vbox.getChildren().add(title_text);
			body_vbox.getChildren().add(body_text);
			global_vbox.getChildren().addAll(header_vbox, body_vbox);
			GUIManager.getInstance().getPane().getChildren().add(global_vbox);
			new BounceIn(global_vbox).play();
			this.currentNotification.add(global_vbox);
		});
	}

	private void createAnimationTimer(VBox button) {
		this.relocate_timeline = new Timeline(new KeyFrame(Duration.millis(1), event -> {
			button.layoutYProperty().unbind();
			button.relocate(button.getLayoutX() - 2, button.getLayoutY());
		}));
		this.end_timer = new Timeline(new KeyFrame(Duration.millis(200), event -> {
			GUIManager.getInstance().getPane().getChildren().remove(button);
			this.relocate_timeline.stop();
			this.end_timer.stop();
		}));
		this.relocate_timeline.setCycleCount(Timeline.INDEFINITE);
		this.relocate_timeline.play();
		if (this.end_timer != null)
			this.end_timer.play();
		if (this.timeline != null)
			this.timeline.stop();
		this.currentNotification.clear();
	}
}