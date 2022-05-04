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

import com.beatplaylist.enums.SizeType;
import com.beatplaylist.gui.module.layout.sidebar.TabType;
import com.beatplaylist.utilities.popup.control.PopupHBox;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class UIResize {

	private static UIResize instance = new UIResize();

	public static UIResize getInstance() {
		return instance;
	}

	public void addResizeListener(boolean resizeDisabled) {
		Stage stage = GUIManager.getInstance().getStage();
		ResizeListener resizeListener = new ResizeListener(stage, resizeDisabled);
		stage.getScene().addEventHandler(MouseEvent.MOUSE_MOVED, resizeListener);
		stage.getScene().addEventHandler(MouseEvent.MOUSE_PRESSED, resizeListener);
		stage.getScene().addEventHandler(MouseEvent.MOUSE_DRAGGED, resizeListener);
		stage.getScene().addEventHandler(MouseEvent.MOUSE_EXITED, resizeListener);
		stage.getScene().addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, resizeListener);

		ObservableList<Node> children = stage.getScene().getRoot().getChildrenUnmodifiable();
		for (Node child : children) {
			if (!(child instanceof ScrollBar) && !(child instanceof Slider) && !(child instanceof Button) && !(child instanceof ImageView) && !(child instanceof HBox)) {
				addListenerDeeply(child, resizeListener);
			}
		}
	}

	private void addListenerDeeply(Node node, EventHandler<MouseEvent> listener) {
		node.addEventHandler(MouseEvent.MOUSE_MOVED, listener);
		node.addEventHandler(MouseEvent.MOUSE_PRESSED, listener);
		node.addEventHandler(MouseEvent.MOUSE_DRAGGED, listener);
		node.addEventHandler(MouseEvent.MOUSE_EXITED, listener);
		node.addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, listener);
		if (node instanceof Parent) {
			Parent parent = (Parent) node;
			ObservableList<Node> children = parent.getChildrenUnmodifiable();
			for (Node child : children) {
				if (!(child instanceof ScrollBar) && !(child instanceof Slider) && !(child instanceof Button) && !(child instanceof ImageView) && !(child instanceof HBox)) {
					addListenerDeeply(child, listener);
				}
			}
		}
	}

	private class ResizeListener implements EventHandler<MouseEvent> {
		private Stage stage;
		private Cursor cursorEvent = Cursor.DEFAULT;
		private boolean resizing = true, resizeDisabled = false;
		private int border = 8;
		private double startX = 0, startY = 0, screenOffsetX = 0, screenOffsetY = 0;
		private double dragY = 0;

		public ResizeListener(Stage stage, boolean resizeDisabled) {
			this.stage = stage;
			this.resizeDisabled = resizeDisabled;
		}

		@Override
		public void handle(MouseEvent mouseEvent) {
			EventType<? extends MouseEvent> mouseEventType = mouseEvent.getEventType();
			Scene scene = this.stage.getScene();
			mouseEvent.consume();
			double mouseEventX = mouseEvent.getSceneX(), mouseEventY = mouseEvent.getSceneY(), sceneWidth = scene.getWidth(), sceneHeight = scene.getHeight();

			if (MouseEvent.MOUSE_MOVED.equals(mouseEventType) && !this.stage.isMaximized() && !this.resizeDisabled) {
				if (mouseEventX < this.border && mouseEventY < this.border) {
					this.cursorEvent = Cursor.NW_RESIZE;
				} else if (mouseEventX < this.border && mouseEventY > sceneHeight - this.border) {
					this.cursorEvent = Cursor.SW_RESIZE;
				} else if (mouseEventX > sceneWidth - this.border && mouseEventY < this.border) {
					this.cursorEvent = Cursor.NE_RESIZE;
				} else if (mouseEventX > sceneWidth - this.border && mouseEventY > sceneHeight - this.border) {
					this.cursorEvent = Cursor.SE_RESIZE;
				} else if (mouseEventX < this.border) {
					this.cursorEvent = Cursor.W_RESIZE;
				} else if (mouseEventX > sceneWidth - this.border) {
					this.cursorEvent = Cursor.E_RESIZE;
				} else if (mouseEventY < this.border) {
					this.cursorEvent = Cursor.N_RESIZE;
				} else if (mouseEventY > sceneHeight - this.border) {
					this.cursorEvent = Cursor.S_RESIZE;
				} else {
					this.cursorEvent = Cursor.DEFAULT;
				}
				scene.setCursor(this.cursorEvent);
			} else if (MouseEvent.MOUSE_EXITED.equals(mouseEventType) || MouseEvent.MOUSE_EXITED_TARGET.equals(mouseEventType)) {
				scene.setCursor(Cursor.DEFAULT);
			} else if (MouseEvent.MOUSE_PRESSED.equals(mouseEventType)) {
				this.startX = this.stage.getWidth() - mouseEventX;
				this.startY = this.stage.getHeight() - mouseEventY;
			} else if (MouseEvent.MOUSE_DRAGGED.equals(mouseEventType)) {
				if (!Cursor.DEFAULT.equals(cursorEvent) && !this.resizeDisabled) {
					this.resizing = true;
					double minHeight = SizeType.SIZE_TYPE1200X620.getHeight() + 1;
					double minWidth = SizeType.SIZE_TYPESMALLEST.getWidth() + 5;

					if (!Cursor.W_RESIZE.equals(this.cursorEvent) && !Cursor.E_RESIZE.equals(this.cursorEvent)) {
						if (Cursor.NW_RESIZE.equals(this.cursorEvent) || Cursor.N_RESIZE.equals(this.cursorEvent) || Cursor.NE_RESIZE.equals(this.cursorEvent)) {
							if (this.stage.getHeight() > minHeight || mouseEventY < 0) {
								setStageHeight(this.stage.getY() - mouseEvent.getScreenY() + this.stage.getHeight());
								this.stage.setY(mouseEvent.getScreenY());
								// if (stage.getHeight() > SizeType.SIZE_TYPE1620X720.getHeight()) {
								// stage.setHeight(SizeType.SIZE_TYPE1620X720.getHeight());
								// }
							}
						} else {
							if (this.stage.getHeight() > minHeight || mouseEventY + this.startY - this.stage.getHeight() > 0) {
								setStageHeight(mouseEventY + this.startY);
								// if (this.stage.getHeight() > SizeType.SIZE_TYPE1620X720.getHeight()) {
								// this.stage.setHeight(SizeType.SIZE_TYPE1620X720.getHeight());
								// }
							}
						}
					}

					if (!Cursor.N_RESIZE.equals(this.cursorEvent) && !Cursor.S_RESIZE.equals(this.cursorEvent)) {
						if (Cursor.NW_RESIZE.equals(this.cursorEvent) || Cursor.W_RESIZE.equals(this.cursorEvent) || Cursor.SW_RESIZE.equals(this.cursorEvent)) {
							if (this.stage.getWidth() > minWidth || mouseEventX < 0) {
								setStageWidth(this.stage.getX() - mouseEvent.getScreenX() + this.stage.getWidth());
								if (mouseEvent.getTarget() instanceof TextField)
									return;
								this.stage.setX(mouseEvent.getScreenX());
								// if (this.stage.getWidth() > SizeType.SIZE_TYPE1920X1080.getWidth()) {
								// this.stage.setWidth(SizeType.SIZE_TYPE1920X1080.getWidth());
								// }
							}
						} else {
							if (this.stage.getWidth() > minWidth || mouseEventX + this.startX - this.stage.getWidth() > 0) {
								setStageWidth(mouseEventX + this.startX);
								// if (this.stage.getWidth() > SizeType.SIZE_TYPE1920X1080.getWidth()) {
								// this.stage.setWidth(SizeType.SIZE_TYPE1920X1080.getWidth());
								// }
							}
						}
					}
					this.resizing = false;
				}
			}

			if (MouseEvent.MOUSE_PRESSED.equals(mouseEventType) && Cursor.DEFAULT.equals(this.cursorEvent)) {
				this.resizing = false;
				this.screenOffsetX = this.stage.getX() - mouseEvent.getScreenX();
				this.screenOffsetY = this.stage.getY() - mouseEvent.getScreenY();
			}

			if (MouseEvent.MOUSE_DRAGGED.equals(mouseEventType) && Cursor.DEFAULT.equals(this.cursorEvent) && this.resizing == false) {
				Bounds allScreenBounds = computeAllScreenBounds();
				double y = this.stage.getY();

				if (mouseEvent.getTarget() instanceof PopupHBox || mouseEvent.getTarget() instanceof Button || mouseEvent.getTarget() instanceof TextField || mouseEvent.getTarget() instanceof Text || (GUIManager.getInstance().sideBar != null && GUIManager.getInstance().sideBar.profileMenu != null && GUIManager.getInstance().sideBar.profileMenu.profileContainerHBox != null && mouseEvent.getTarget() == GUIManager.getInstance().sideBar.profileMenu.profileContainerHBox) || (GUIManager.getInstance().sideBar != null && GUIManager.getInstance().sideBar.profileMenu != null && mouseEvent.getTarget() == GUIManager.getInstance().sideBar.profileMenu.name_vbox) || mouseEvent.getTarget() instanceof ImageView)
					return;
				if (mouseEvent.getTarget() instanceof Pane) {
					Pane pane = (Pane) mouseEvent.getTarget();
					if (mouseEvent.getTarget() == GUIManager.getInstance().audioBar.audioBarPane) {
						return;
					}
					for (Node nodes : pane.getChildren()) {

						if (nodes instanceof Text) {
							Text text = (Text) nodes;
							if (text.getText().equals("SEARCH"))
								return;
						}
					}
				}
				if (y <= allScreenBounds.getMinY() && mouseEvent.getY() >= this.dragY) {
					GUIManager.getInstance().applicationLastMoved = System.currentTimeMillis();
					if (mouseEvent.getY() >= this.dragY) {
						if (GUIManager.getInstance().topBar.titleBar.lastWasMaximise) {
							GUIManager.getInstance().topBar.titleBar.unfullscreen();
						}
						this.stage.setY(mouseEvent.getScreenY() + this.screenOffsetY);
						return;
					} else
						this.stage.setY(allScreenBounds.getMinY());
					return;
				}
				// if (y + h > allScreenBounds.getMaxY() && mouseEvent.getY() <= this.dragY) {
				// GUIManager.getInstance().applicationLastMoved = System.currentTimeMillis();
				// this.stage.setY(allScreenBounds.getMaxY() - h);
				// return;
				// }
				// System.out.println(mouseEvent.getY());

				if (mouseEvent.getY() < 80) {
					this.stage.setX(mouseEvent.getScreenX() + this.screenOffsetX);
					this.stage.setY(mouseEvent.getScreenY() + this.screenOffsetY);
					GUIManager.getInstance().applicationLastMoved = System.currentTimeMillis();
				}
			}
			this.dragY = mouseEvent.getY();
		}

		private void setStageWidth(double width) {
			width = Math.min(width, SizeType.SIZE_TYPE1920X1080.getWidth());
			width = Math.max(width, SizeType.SIZE_TYPESMALLEST.getWidth());
			this.stage.setWidth(width);

			if (width < 1250) {
				if (GUIManager.getInstance().audioBar.nowPlayingButton.minWidthProperty().get() != 1.3) {
					GUIManager.getInstance().audioBar.nowPlayingButton.layoutXProperty().bind(GUIManager.getInstance().sideBar.sidebarPane.widthProperty().divide(8));
					GUIManager.getInstance().audioBar.nowPlayingButton.minWidthProperty().bind(GUIManager.getInstance().sideBar.sidebarPane.widthProperty().divide(1.3));
					GUIManager.getInstance().audioBar.nowPlayingButton.maxWidthProperty().bind(GUIManager.getInstance().sideBar.sidebarPane.widthProperty().divide(1.3));
				}
			} else {
				if (GUIManager.getInstance().audioBar.nowPlayingButton.minWidthProperty().get() != 1.5) {
					GUIManager.getInstance().audioBar.nowPlayingButton.layoutXProperty().bind(GUIManager.getInstance().sideBar.sidebarPane.widthProperty().divide(6));
					GUIManager.getInstance().audioBar.nowPlayingButton.minWidthProperty().bind(GUIManager.getInstance().sideBar.sidebarPane.widthProperty().divide(1.5));
					GUIManager.getInstance().audioBar.nowPlayingButton.maxWidthProperty().bind(GUIManager.getInstance().sideBar.sidebarPane.widthProperty().divide(1.5));
				}
			}
		}

		private void setStageHeight(double height) {
			height = Math.min(height, SizeType.SIZE_TYPE1920X1080.getHeight());
			height = Math.max(height, SizeType.SIZE_TYPESMALLEST.getHeight());
			this.stage.setHeight(height);

			handleHeightResize(height);
		}

	}

	public static void handleHeightResize(double height) {
		String centerID = "", rightID = "";
		double center, right;
		if (height > 900) {
			centerID = "centerVBox1920";
			rightID = "rightVBox1920";
			center = 5;
			right = 2;
		} else if (height < 700) {
			centerID = "centerVBoxSmall";
			rightID = "rightVBoxSmall";
			center = 7;
			right = 1.6;
			if (GUIManager.getInstance().currentTab == null || (GUIManager.getInstance().currentTab != null && GUIManager.getInstance().currentTab.tab != TabType.BROWSE))
				GUIManager.getInstance().topBar.titleBar.searchTextfield.setMinHeight(25);
			else {
				GUIManager.getInstance().topBar.titleBar.searchTextfield.layoutYProperty().unbind();
				GUIManager.getInstance().topBar.titleBar.searchTextfield.setLayoutY(15);
			}
		} else {
			centerID = "centerVBox";
			rightID = "rightVBox";
			center = 4.5;
			right = 1.6;
			if (GUIManager.getInstance().currentTab == null || (GUIManager.getInstance().currentTab != null && GUIManager.getInstance().currentTab.tab != TabType.BROWSE))
				GUIManager.getInstance().topBar.titleBar.searchTextfield.setMinHeight(30);
			else {
				GUIManager.getInstance().topBar.titleBar.searchTextfield.layoutYProperty().unbind();
				GUIManager.getInstance().topBar.titleBar.searchTextfield.layoutYProperty().bind(GUIManager.getInstance().topBar.topPane.heightProperty().divide(2.4));
			}
		}
		if (!GUIManager.getInstance().audioBar.audioBar.center_vbox.getId().equals(centerID)) {
			GUIManager.getInstance().audioBar.audioBar.center_vbox.layoutYProperty().unbind();
			GUIManager.getInstance().audioBar.audioBar.center_vbox.layoutYProperty().bind(GUIManager.getInstance().audioBar.audioBarPane.heightProperty().divide(center));
			GUIManager.getInstance().audioBar.audioBar.center_vbox.setId(centerID);
		}
		if (!GUIManager.getInstance().audioBar.audioBar.right_vbox.getId().equals(rightID)) {
			GUIManager.getInstance().audioBar.audioBar.right_vbox.layoutYProperty().unbind();
			GUIManager.getInstance().audioBar.audioBar.right_vbox.layoutYProperty().bind(GUIManager.getInstance().audioBar.audioBarPane.heightProperty().divide(right));
			GUIManager.getInstance().audioBar.audioBar.right_vbox.setId(rightID);
		}
	}

	private Bounds computeAllScreenBounds() {
		double minX = Double.POSITIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		Screen screen = getRhythmScreen();
		Rectangle2D screenBounds = screen.getBounds();
		if (screenBounds.getMinX() < minX) {
			minX = screenBounds.getMinX();
		}
		if (screenBounds.getMinY() < minY) {
			minY = screenBounds.getMinY();
		}
		if (screenBounds.getMaxX() > maxX) {
			maxX = screenBounds.getMaxX();
		}
		if (screenBounds.getMaxY() > maxY) {
			maxY = screenBounds.getMaxY();
		}

		return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
	}

	private Screen getRhythmScreen() {
		for (Screen screen : Screen.getScreensForRectangle(GUIManager.getInstance().stage.getX(), GUIManager.getInstance().stage.getY(), 1., 1.)) {
			return screen;
		}
		return Screen.getPrimary();
	}
}