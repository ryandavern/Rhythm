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

package com.beatplaylist.utilities.update;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.beatplaylist.utilities.events.ProgressUpdateEvent;
import com.beatplaylist.utilities.network.post.SendErrorReport;

public class Downloader {

	private String file_name, download_url;
	private boolean deleteOnFinish;

	public Downloader(String download_url, String file_name) {
		this.download_url = download_url;
		this.file_name = file_name;
		this.deleteOnFinish = false;
	}

	public Downloader(String download_url, String file_name, boolean deleteOnFinish) {
		this.download_url = download_url;
		this.file_name = file_name;
		this.deleteOnFinish = deleteOnFinish;
	}

	public void downloadFile(ProgressUpdateEvent event) {
		BufferedInputStream input_stream = null;
		FileOutputStream file_output_stream = null;
		BufferedOutputStream output_stream = null;
		try {
			File file = new File(this.file_name);

			URL url = new URL(this.download_url);
			HttpsURLConnection http = (HttpsURLConnection) url.openConnection();
			http.setRequestProperty("User-Agent", "Mozilla/5.0");
			double fileSize = (double) http.getContentLengthLong();

			input_stream = new BufferedInputStream(http.getInputStream());
			file_output_stream = new FileOutputStream(file);
			output_stream = new BufferedOutputStream(file_output_stream, 1024);

			// The buffer size sets how many bytes per request. (Download speed)
			byte[] buffer = new byte[256000];

			double download_progress = 0.00, percentage_downloaded = 0.00;
			int read = 0;

			while ((read = input_stream.read(buffer, 0, 256000)) >= 0) {
				output_stream.write(buffer, 0, read);
				download_progress += read;
				percentage_downloaded = (download_progress * 100) / fileSize;
				// String percentage = String.format("%.0f", percentage_downloaded);
				event.onProgressUpdate(Integer.valueOf(String.format("%.0f", percentage_downloaded)));
				// System.out.println("Download Percentage: " + percentage);
			}

			if (fileSize != download_progress)
				System.out.println("File size incorrect. Download might not run.");

			event.onFinish();

		} catch (Exception e) {
			event.onError();
			new SendErrorReport().send(e, Downloader.class);
			e.printStackTrace();
		} finally {
			try {
				input_stream.close();
				file_output_stream.close();
				output_stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean deleteOnFinish() {
		return this.deleteOnFinish;
	}
}