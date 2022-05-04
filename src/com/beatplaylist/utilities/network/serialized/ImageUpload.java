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

package com.beatplaylist.utilities.network.serialized;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;

import com.beatplaylist.utilities.user.UserManager;

public class ImageUpload implements Serializable {

	private static final long serialVersionUID = 2936785276359716261L;

	private byte[] image_bytes = null;
	private double file_length;
	private boolean failed = false;
	private String username = "", accessToken = "", failMessage = "", version = "", imageURL = "";

	public void setDetails() {
		this.username = UserManager.getInstance().getUser().username;
		this.accessToken = UserManager.getInstance().getUser().accessToken;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return this.version;
	}

	public void setImageURL(String url) {
		this.imageURL = url;
	}

	public String getImageURL() {
		return this.imageURL;
	}

	public String getUsername() {
		return this.username;
	}

	public String getAccessToken() {
		return this.accessToken;
	}

	public void setImage(File file) {
		try {
			BufferedImage image = ImageIO.read(file);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "jpg", baos);

			this.image_bytes = baos.toByteArray();
			this.file_length = file.length();

			image.flush();
			baos.flush();
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
			this.image_bytes = null;
		}
	}

	public double getFileByteLength() {
		return this.file_length;
	}

	public double getFileKBLength() {
		return this.file_length / 1024;
	}

	public double getFileMBLength() {
		return ((this.file_length / 1024) / 1024);
	}

	public byte[] getImageBytes() {
		return this.image_bytes;
	}

	public void setFailed(String message) {
		this.failMessage = message;
		this.failed = true;
	}

	public void setFailMessage(String message) {
		this.failMessage = message;
	}

	public void setBytes(byte[] b) {
		this.image_bytes = b;
	}

	public void setFailed(boolean failed) {
		this.failed = failed;
	}

	public String getFailMessage() {
		return this.failMessage;
	}

	public boolean hasFailed() {
		return this.failed;
	}

	public boolean hasBytes() {
		return this.image_bytes != null;
	}

	public byte[] getBytes() {
		return this.image_bytes;
	}
}