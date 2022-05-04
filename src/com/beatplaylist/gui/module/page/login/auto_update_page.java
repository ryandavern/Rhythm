package com.beatplaylist.gui.module.page.login;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.beatplaylist.Options;
import com.beatplaylist.enums.FontType;
import com.beatplaylist.gui.CenterBox;
import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.utilities.CustomColor;
import com.beatplaylist.utilities.events.ProgressUpdateEvent;
import com.beatplaylist.utilities.filemanager.FileUtilities;
import com.beatplaylist.utilities.filemanager.OperatingSystem;
import com.beatplaylist.utilities.update.Downloader;
import com.beatplaylist.utilities.update.StartupData;

import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import net.lingala.zip4j.core.ZipFile;

public class auto_update_page {

	private Text update_text, update_percentage;
	private ProgressBar progress_bar;
	private Timer auto_update_reschedule_timer;
	private int seconds_until_auto_update_restart = 6, restart_attempts = 0;
	private TimerTask auto_update_reschedule_task;

	private CenterBox centerBox;

	public auto_update_page() {
		this.centerBox = new CenterBox();

		this.update_text = new Text("New version update in progress");
		this.update_percentage = new Text();
		this.progress_bar = new ProgressBar();
	}

	public void run() {
		GUIManager.getInstance().getPane().getChildren().clear();
		add();
		listen();

		update();
	}

	public void unzip(File updateFile, String destinationFolderPath) throws Exception {
		ZipFile zipFile = new ZipFile(updateFile);
		zipFile.extractAll(destinationFolderPath);
	}

	private void add() {
		this.update_text.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		this.update_text.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 14));
		this.update_text.wrappingWidthProperty().bind(this.centerBox.wrapperVBox.widthProperty().subtract(20));

		this.update_percentage.setFill(Color.web(CustomColor.WHITE.getColorHex()));
		this.update_percentage.setFont(Font.font(FontType.DEFAULT.getName(), FontWeight.BOLD, 14));

		this.progress_bar.setMinHeight(15);
		this.progress_bar.setMinWidth(350);
		this.progress_bar.setProgress(0);
		this.progress_bar.setStyle("-fx-accent: #2ECC71;");

		this.centerBox.getContentVBox().getChildren().addAll(this.update_text, this.progress_bar, this.update_percentage);

		if (!GUIManager.getInstance().getPane().getChildren().contains(this.centerBox.getWrapperHBox()))
			GUIManager.getInstance().getPane().getChildren().add(this.centerBox.getWrapperHBox());

	}

	private void update() {
		new Thread(() -> {
			String directory = "C:\\Users\\OEM\\Desktop\\";
			if (!Options.test_mode)
				directory = FileUtilities.getInstance().getAutoUpdateLocation();

			String url = StartupData.getInstance().update_url;
			String extension = ".exe";

			if (FileUtilities.getInstance().getOperatingSystem() == OperatingSystem.MACOS)
				extension = ".zip";

			final String dir = directory;
			final String ext = extension;

			long now = System.currentTimeMillis();
			new Downloader(url, directory + "update" + extension).downloadFile(new ProgressUpdateEvent() {

				@Override
				public void onProgressUpdate(double progress) {
					Platform.runLater(() -> {
						progress_bar.setProgress(progress / 100);
						update_percentage.setText(progress + "%");
					});
				}

				@Override
				public void onFinish() {
					Platform.runLater(() -> {
						progress_bar.setProgress(100);
						update_text.setText("Update Downloaded");
						update_percentage.setText("Download Complete");
						System.out.println("Download Time: " + (System.currentTimeMillis() - now) / 1000 + " seconds.");
						if (FileUtilities.getInstance().getOperatingSystem() == OperatingSystem.WINDOWS) {
							ProcessBuilder processBuilder = new ProcessBuilder(dir + "update" + ext);
							try {
								processBuilder.start();
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else {
							try {
								unzip(new File(dir + "update" + ext), dir);
								String command = System.getProperty("install4j.appDir").replace("/Resources/app/", "").replace("/java/app/", "") + File.separator + "PlugIns" + File.separator + "jre.bundle" + File.separator + "Contents" + File.separator + "Home" + File.separator + "bin" + File.separator + "java -jar " + dir + "RhythmUpdater.jar \"" + dir.replace("/Resources/app/", "") + "\"";
								Runtime.getRuntime().exec(command);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						System.exit(0);
					});

				}

				@Override
				public void onError() {
					Platform.runLater(() -> {
						if (restart_attempts >= 3) {
							update_text.setText("Could not update Rhythm after 3 attempts. If this issue continues, please re-download the program at https://rhythm.cc/.");
							return;
						}
						auto_update_reschedule_timer = new Timer();

						auto_update_reschedule_task = new TimerTask() {
							public void run() {
								seconds_until_auto_update_restart--;
								update_text.setText("An error has occured while downloading the latest version of Rhythm. Auto-update restarting in " + seconds_until_auto_update_restart + " seconds. If this issue continues, please re-download the program at https://rhythm.cc/.");

								if (seconds_until_auto_update_restart == 0) {
									restart_attempts++;
									seconds_until_auto_update_restart = 6;
									auto_update_reschedule_timer.cancel();
									update();
								}
							}
						};
						auto_update_reschedule_timer.schedule(auto_update_reschedule_task, 0, 1000);
					});
				}
			});
		}).start();
	}

	private void listen() {

	}
}