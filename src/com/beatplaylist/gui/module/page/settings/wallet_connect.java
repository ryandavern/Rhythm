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

package com.beatplaylist.gui.module.page.settings;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;

import com.beatplaylist.Main;
import com.beatplaylist.enums.FontType;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.module.layout.sidebar.TabType;
import com.beatplaylist.gui.utilities.FXUtilities;
import com.beatplaylist.utilities.CustomColor;
import com.beatplaylist.utilities.CustomToolTip;
import com.beatplaylist.utilities.control.EvenSpacedHBox;
import com.beatplaylist.utilities.events.CompleteEvent;
import com.beatplaylist.utilities.format.NumberFormat;
import com.beatplaylist.utilities.network.get.GetBalance;
import com.beatplaylist.utilities.network.post.DisconnectWallet;
import com.beatplaylist.utilities.network.post.HasSyncedWallet;
import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;
import com.beatplaylist.utilities.popup.PopupBuilder;
import com.beatplaylist.utilities.user.UserManager;
import com.beatplaylist.utilities.web3.PartneredContract;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

public class wallet_connect {

	public ScrollPane scrollPane;
	public HBox hbox, buttonHBox;
	public VBox leftVBox, portfolioVBox, balanceVBox, rewardVBox;
	public Button connectWallet, goHub;
	public Text partnerHeaderText, walletAddress, walletHeaderText, rewardsHeaderText, rewardsComingSoon, loadingPartnersText, tierValueText;
	public TextFlow walletHeaderTextFlow;

	public wallet_connect() {
		this.scrollPane = new ScrollPane();
		this.hbox = new HBox();
		this.buttonHBox = new HBox(10);
		this.leftVBox = new VBox(25);
		this.rewardVBox = new VBox(5);
		this.portfolioVBox = new VBox(5);
		this.balanceVBox = new VBox(5);

		this.connectWallet = new Button("Connect Wallet");
		this.goHub = new Button("Open Rhythm Hub");

		this.walletHeaderText = new Text("YOUR WALLET");
		this.partnerHeaderText = new Text("OUR PARTNERS");
		this.rewardsHeaderText = new Text("CLAIM REWARDS");
		this.rewardsComingSoon = new Text("Rhythm rewards coming soon...");
		this.loadingPartnersText = new Text("Loading...");
		this.tierValueText = new Text();

		this.walletHeaderTextFlow = new TextFlow(this.walletHeaderText, this.tierValueText);

		this.walletAddress = new Text(UserManager.getInstance().getUser().walletAddress);

		run();
	}

	public void run() {
		add();
		listen();

		GetBalance.send(new CompleteEvent() {

			@Override
			public void onSuccess() {
				balanceVBox.getChildren().clear();

				balanceVBox.getChildren().add(partnerHeaderText);

				for (PartneredContract contract : PartneredContract.values()) {

					String contractBalance = "";

					if (contract == PartneredContract.BNB) {
						contractBalance = UserManager.getInstance().getUser().bnbBalance;
					} else {
						contractBalance = UserManager.getInstance().user.getContractBalance(contract);
					}

					if (contractBalance.contains(".") && contract != PartneredContract.BNB) {
						contractBalance = contractBalance.split("\\.")[0];
					}

					if (contract.hasWalletPerks()) {
						if (UserManager.getInstance().getUser().walletAddress.isEmpty()) {
							tierValueText.setText(" - No Wallet Connected");
							tierValueText.setFill(Color.web(CustomColor.RED.getColorHex()));
						} else {
							double balance = Double.valueOf(contractBalance);
							if (balance > 50000000) {
								tierValueText.setText(" - Emerald Tier");
								tierValueText.setFill(Color.web(CustomColor.GREEN.getColorHex()));
							} else if (balance > 25000000) {
								tierValueText.setText(" - Diamond Tier");
								tierValueText.setFill(Color.web(CustomColor.DIAMOND.getColorHex()));
							} else if (balance > 5000000) {
								tierValueText.setText(" - Gold Tier");
								tierValueText.setFill(Color.web(CustomColor.GOLD.getColorHex()));
							} else if (balance > 500000) {
								tierValueText.setText(" - Silver Tier");
								tierValueText.setFill(Color.web(CustomColor.SILVER.getColorHex()));
							} else {
								tierValueText.setText(" - Bronze Tier");
								tierValueText.setFill(Color.web(CustomColor.GRAY.getColorHex()));
							}
						}
					}

					// if (contractBalance.equals("0")) {
					// continue;
					// }

					VBox vbox = new VBox(3);
					HBox hbox = new HBox(10), titleHBox = new HBox(5);
					Text titleText = new Text(contract.getName()), contractAddress = new Text(contract.getContractAddress()), balanceText = new Text("Balance: "), balanceAmountText = new Text();
					TextFlow balanceFlow = new TextFlow(balanceText, balanceAmountText);
					Button buy = new Button("Buy"), website = new Button("Website");
					ImageView imageView = null;

					if (contract == PartneredContract.BNB) {
						balanceAmountText.setText(String.format("%.3f", Double.valueOf(contractBalance)));
						imageView = new ImageView(new Image(Main.class.getResource("/resources/partners/bnb.png").toExternalForm(), 32, 32, false, false));
					} else {
						if (contract == PartneredContract.RHYTHM)
							imageView = new ImageView(new Image(Main.class.getResource("/resources/partners/rhythm.png").toExternalForm(), 32, 32, false, false));
						else if (contract == PartneredContract.SAFEMOON)
							imageView = new ImageView(new Image(Main.class.getResource("/resources/partners/safemoon.png").toExternalForm(), 32, 32, false, false));
						else if (contract == PartneredContract.HODL)
							imageView = new ImageView(new Image(Main.class.getResource("/resources/partners/hodl.png").toExternalForm(), 32, 32, false, false));
						else if (contract == PartneredContract.BABYSWAP)
							imageView = new ImageView(new Image(Main.class.getResource("/resources/partners/babyswap.png").toExternalForm(), 32, 32, false, false));
						else if (contract == PartneredContract.WSPP)
							imageView = new ImageView(new Image(Main.class.getResource("/resources/partners/wspp.png").toExternalForm(), 32, 32, false, false));

						System.out.println(contract + " " + contractBalance);

						balanceAmountText.setText(NumberFormat.getFormattedNumber(Integer.valueOf(contractBalance.replace("null", "0"))));
					}

					titleHBox.setAlignment(Pos.CENTER_LEFT);

					vbox.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-padding: 10px; -fx-background-radius: 10px;");

					titleText.setFill(Color.web(CustomColor.WHITE.getColorHex()));
					titleText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 18));

					contractAddress.setFill(Color.web(CustomColor.GRAY.getColorHex()));
					contractAddress.setFont(Font.font(FontType.DEFAULT.getName(), 16));
					contractAddress.setCursor(Cursor.HAND);
					CustomToolTip.install(contractAddress, new CustomToolTip("Copy contract address"));

					balanceText.setFill(Color.web(CustomColor.GRAY.getColorHex()));
					balanceText.setFont(Font.font(FontType.DEFAULT.getName(), 16));

					balanceAmountText.setFill(Color.web(CustomColor.GRAY.getColorHex()));
					balanceAmountText.setFont(Font.font(FontType.DEFAULT.getName(), 16));

					buy.setStyle("-fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-background-radius: 10px;");
					buy.setTextFill(Color.web(CustomColor.GRAY.getColorHex()));
					buy.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));
					buy.setMinSize(200, 40);
					buy.setCursor(Cursor.HAND);

					website.setStyle("-fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-background-radius: 10px;");
					website.setTextFill(Color.web(CustomColor.GRAY.getColorHex()));
					website.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));
					website.setMinSize(200, 40);
					website.setCursor(Cursor.HAND);

					contractAddress.setOnMouseEntered(event -> {
						contractAddress.setFill(Color.web(CustomColor.GREEN.getColorHex()));
					});
					contractAddress.setOnMouseExited(event -> {
						contractAddress.setFill(Color.web(CustomColor.GRAY.getColorHex()));
					});
					contractAddress.setOnMouseClicked(event -> {
						FXUtilities.copyToClipboard(contract.getContractAddress());
						Notification.getInstance().createNotification("Contract", "Contract has been copied", AlertType.SUCCESS);
					});
					buy.setOnMouseEntered(event -> {
						buy.setTextFill(Color.web(CustomColor.GREEN.getColorHex()));
					});
					buy.setOnMouseExited(event -> {
						buy.setTextFill(Color.web(CustomColor.GRAY.getColorHex()));
					});
					buy.setOnMouseClicked(event -> {
						try {
							if (contract == PartneredContract.BNB) {
								Desktop.getDesktop().browse(new URI("https://binance.com/"));
							} else if (contract == PartneredContract.BABYSWAP) {
								Desktop.getDesktop().browse(new URI("https://exchange.babyswap.finance/#/swap?outputCurrency=" + contract.getContractAddress()));
							} else {
								Desktop.getDesktop().browse(new URI("https://pancakeswap.finance/swap?outputCurrency=" + contract.getContractAddress()));
							}
						} catch (IOException | URISyntaxException e) {
							e.printStackTrace();
						}
					});
					website.setOnMouseEntered(event -> {
						website.setTextFill(Color.web(CustomColor.GREEN.getColorHex()));
					});
					website.setOnMouseExited(event -> {
						website.setTextFill(Color.web(CustomColor.GRAY.getColorHex()));
					});
					website.setOnMouseClicked(event -> {
						try {
							Desktop.getDesktop().browse(new URI(contract.getWebsite()));
						} catch (IOException | URISyntaxException e) {
							e.printStackTrace();
						}
					});

					hbox.getChildren().addAll(buy, website);
					titleHBox.getChildren().addAll(imageView, titleText);
					if (contract == PartneredContract.BNB)
						vbox.getChildren().addAll(titleHBox, balanceFlow, hbox);
					else
						vbox.getChildren().addAll(titleHBox, contractAddress, balanceFlow, hbox);

					balanceVBox.getChildren().add(vbox);
				}
			}

			@Override
			public void onFail(String error) {
				System.out.println(error);
			}
		});
	}

	private void add() {
		FXUtilities.setNodePadding(this.hbox, GUIManager.getInstance().padding);

		this.scrollPane.setContent(this.hbox);
		this.scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		this.scrollPane.setStyle("-fx-background-color: transparent;");
		this.scrollPane.minWidthProperty().bind(GUIManager.getInstance().contentManager.contentPane.widthProperty().subtract(100));
		this.scrollPane.maxWidthProperty().bind(GUIManager.getInstance().contentManager.contentPane.widthProperty().subtract(100));
		this.scrollPane.maxHeightProperty().bind(GUIManager.getInstance().contentManager.contentPane.heightProperty().subtract(10));

		this.hbox.minWidthProperty().bind(this.scrollPane.widthProperty());
		this.hbox.maxWidthProperty().bind(this.scrollPane.widthProperty());

		this.leftVBox.minWidthProperty().bind(this.scrollPane.widthProperty().divide(2));
		this.leftVBox.maxWidthProperty().bind(this.scrollPane.widthProperty().divide(2));

		this.walletHeaderText.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		this.walletHeaderText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 18));

		this.partnerHeaderText.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		this.partnerHeaderText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 18));

		this.loadingPartnersText.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		this.loadingPartnersText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));

		this.rewardsHeaderText.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		this.rewardsHeaderText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 18));

		this.rewardsComingSoon.setFill(Color.web(CustomColor.GRAY.getColorHex()));
		this.rewardsComingSoon.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));

		this.walletAddress.setFill(Color.web(CustomColor.GRAY.getColorHex()));
		this.walletAddress.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));
		this.walletAddress.setCursor(Cursor.HAND);

		this.connectWallet.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-background-radius: 10px;");
		this.connectWallet.setTextFill(Color.web(CustomColor.GRAY.getColorHex()));
		this.connectWallet.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));
		this.connectWallet.setMinSize(200, 40);
		this.connectWallet.setCursor(Cursor.HAND);
		if (!UserManager.getInstance().getUser().walletAddress.isEmpty()) {
			this.connectWallet.setText("Disconnect Wallet");
		}

		this.goHub.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-background-radius: 10px;");
		this.goHub.setTextFill(Color.web(CustomColor.GRAY.getColorHex()));
		this.goHub.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));
		this.goHub.setMinSize(200, 40);
		this.goHub.setCursor(Cursor.HAND);

		this.tierValueText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 18));

		this.rewardVBox.getChildren().addAll(this.rewardsHeaderText);
		this.buttonHBox.getChildren().addAll(this.goHub, this.connectWallet);
		this.portfolioVBox.getChildren().addAll(this.walletHeaderTextFlow, this.walletAddress, this.buttonHBox);
		this.balanceVBox.getChildren().addAll(this.partnerHeaderText, this.loadingPartnersText);

		this.leftVBox.getChildren().addAll(this.portfolioVBox, this.rewardVBox);

		this.hbox.getChildren().addAll(this.leftVBox, this.balanceVBox);

		GUIManager.getInstance().contentManager.contentPane.getChildren().add(this.scrollPane);

		listenToEarn();

		FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), this.scrollPane);
		fadeTransition.setFromValue(0.0);
		fadeTransition.setToValue(1.0);
		fadeTransition.play();
	}

	private void listen() {
		this.hbox.setOnScroll(event -> {
			handleScroll(event);
		});
		this.connectWallet.setOnMouseEntered(event -> {
			this.connectWallet.setTextFill(Color.web(CustomColor.GREEN.getColorHex()));
		});
		this.connectWallet.setOnMouseExited(event -> {
			this.connectWallet.setTextFill(Color.web(CustomColor.GRAY.getColorHex()));
		});
		this.connectWallet.setOnAction(event -> {
			if (UserManager.getInstance().getUser().walletAddress.isEmpty()) {
				connectWalletPopup();
			} else {
				// Disconnect
				confirmWalletDisconnect();
			}
		});
		this.goHub.setOnMouseEntered(event -> {
			this.goHub.setTextFill(Color.web(CustomColor.GREEN.getColorHex()));
		});
		this.goHub.setOnMouseExited(event -> {
			this.goHub.setTextFill(Color.web(CustomColor.GRAY.getColorHex()));
		});
		this.goHub.setOnAction(event -> {
			try {
				Desktop.getDesktop().browse(new URI("https://hub.rhythm.cc/login?authCode=" + UserManager.getInstance().getUser().accessToken + "&userID=" + UserManager.getInstance().getUser().userID));
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		});
		this.walletAddress.setOnMouseEntered(event -> {
			this.walletAddress.setFill(Color.web(CustomColor.GREEN.getColorHex()));
		});
		this.walletAddress.setOnMouseExited(event -> {
			this.walletAddress.setFill(Color.web(CustomColor.GRAY.getColorHex()));
		});
		this.walletAddress.setOnMouseClicked(event -> {
			FXUtilities.copyToClipboard(UserManager.getInstance().getUser().walletAddress);
			Notification.getInstance().createNotification("Contract", "Your wallet address has been copied to your clipboard", AlertType.SUCCESS);
		});
	}

	public void confirmWalletDisconnect() {
		PopupBuilder popup = new PopupBuilder();
		popup.setWrapperSize(470, 0, 470, 550);
		popup.setHeaderText("Are you sure you want to disconnect your wallet?");

		Text text = new Text(UserManager.getInstance().getUser().walletAddress);
		text.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		text.setFont(Font.font(FontType.DEFAULT.getName(), 16));

		popup.getContentVBox().getChildren().add(text);

		popup.onConfirm(event -> {
			DisconnectWallet.send(new CompleteEvent() {

				@Override
				public void onSuccess() {
					popup.quickClose();
					GUIManager.getInstance().sideBar.sideBarTab.changeTab(GUIManager.getInstance().sideBar.sideBarTab.getTab(TabType.WALLET_CONNECT));
				}

				@Override
				public void onFail(String error) {
				}
			});

		});
		popup.open();
	}

	public void connectWalletPopup() {
		PopupBuilder popup = new PopupBuilder();
		popup.setHeaderText("Choose an option");
		popup.hideButtonBar();
		popup.getContentVBox().setSpacing(15);

		Button connectWallet = new Button("I want to connect a new wallet"), alreadyConnected = new Button("I have already connected my wallet"), videoDemonstration = new Button("Watch video demonstration");

		connectWallet.setStyle("-fx-background-color: " + CustomColor.GREEN.getColorHex() + "; -fx-background-radius: 15px;");
		connectWallet.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
		connectWallet.setMinSize(400, 35);
		connectWallet.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 20));
		connectWallet.setCursor(Cursor.HAND);

		alreadyConnected.setStyle("-fx-background-color: " + CustomColor.RED.getColorHex() + "; -fx-background-radius: 15px;");
		alreadyConnected.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
		alreadyConnected.setMinSize(400, 35);
		alreadyConnected.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 20));
		alreadyConnected.setCursor(Cursor.HAND);

		videoDemonstration.setStyle("-fx-background-color: #9b59b6; -fx-background-radius: 15px;");
		videoDemonstration.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
		videoDemonstration.setMinSize(400, 35);
		videoDemonstration.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 20));
		videoDemonstration.setCursor(Cursor.HAND);

		popup.getContentVBox().getChildren().addAll(connectWallet, alreadyConnected, videoDemonstration);

		connectWallet.setOnMouseEntered(event -> {
			connectWallet.setStyle("-fx-background-color: " + CustomColor.DARK_GREEN.getColorHex() + "; -fx-background-radius: 15px;");
		});
		connectWallet.setOnMouseExited(event -> {
			connectWallet.setStyle("-fx-background-color: " + CustomColor.GREEN.getColorHex() + "; -fx-background-radius: 15px;");
		});
		connectWallet.setOnAction(event -> {
			try {
				Desktop.getDesktop().browse(new URI("https://hub.rhythm.cc/login?authCode=" + UserManager.getInstance().getUser().accessToken + "&userID=" + UserManager.getInstance().getUser().userID));
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		});

		alreadyConnected.setOnMouseEntered(event -> {
			alreadyConnected.setStyle("-fx-background-color: " + CustomColor.DARK_RED.getColorHex() + "; -fx-background-radius: 15px;");
		});
		alreadyConnected.setOnMouseExited(event -> {
			alreadyConnected.setStyle("-fx-background-color: " + CustomColor.RED.getColorHex() + "; -fx-background-radius: 15px;");
		});
		alreadyConnected.setOnAction(event -> {
			Notification.getInstance().createNotification("Wallet Connect", "We are checking the wallet sync and will let you know when your wallet is synced!", AlertType.WARNING);
			HasSyncedWallet.send();
			popup.close();
		});

		videoDemonstration.setOnMouseEntered(event -> {
			videoDemonstration.setStyle("-fx-background-color: #8e44ad; -fx-background-radius: 15px;");
		});
		videoDemonstration.setOnMouseExited(event -> {
			videoDemonstration.setStyle("-fx-background-color: #9b59b6; -fx-background-radius: 15px;");
		});
		videoDemonstration.setOnAction(event -> {
			try {
				Desktop.getDesktop().browse(new URI("https://youtu.be/YPWGdeGlK6s"));
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		});

		popup.open();
	}

	private void listenToEarn() {
		VBox vbox = new VBox(10);
		HBox hbox = new HBox(40);

		EvenSpacedHBox claimable = new EvenSpacedHBox();

		DecimalFormat formatter = new DecimalFormat("#,###");

		Text text = new Text("Listen to Earn"), claimableRhythmText = new Text("Claimable $RHYTHM"), rhythmAmountText = new Text(formatter.format(UserManager.getInstance().getUser().claimableRhythm));
		Button claim = new Button("Claim");

		vbox.setStyle("-fx-background-color: " + CustomColor.BACKGROUND.getColorHex() + "; -fx-padding: 15px; -fx-background-radius: 15px;");
		vbox.setMaxWidth(400);
		hbox.setAlignment(Pos.CENTER_LEFT);

		text.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 22));
		text.setFill(Color.web(CustomColor.WHITE.getColorHex()));

		claimableRhythmText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));
		claimableRhythmText.setFill(Color.web(CustomColor.GRAY.getColorHex()));

		rhythmAmountText.setFill(Color.web(CustomColor.GRAY.getColorHex()));
		rhythmAmountText.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));

		claim.setStyle("-fx-background-color: " + CustomColor.DARK_BACKGROUND.getColorHex() + "; -fx-padding: 7px; -fx-background-radius: 7px;");
		claim.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
		claim.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 16));
		claim.setMinSize(100, 35);
		claim.setCursor(Cursor.HAND);
		claim.setDisable(true);

		claim.setOnMouseEntered(event -> {
			claim.setTextFill(Color.web(CustomColor.RHYTHM.getColorHex()));
		});
		claim.setOnMouseEntered(event -> {
			claim.setTextFill(Color.web(CustomColor.WHITE.getColorHex()));
		});

		claimable.getLeft().getChildren().add(claimableRhythmText);
		claimable.getRight().getChildren().add(rhythmAmountText);

		vbox.getChildren().addAll(text, claimable.getContainer(), claim);

		this.rewardVBox.getChildren().add(vbox);
	}

	private void handleScroll(ScrollEvent event) {
		double deltaY = event.getDeltaY() * 4;
		double height = this.scrollPane.getContent().getBoundsInLocal().getHeight();
		double vvalue = this.scrollPane.getVvalue();
		if (height < 1000)
			this.scrollPane.setVvalue(vvalue + -deltaY / height);
	}
}