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

package com.beatplaylist.utilities.image.cache;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.ImageView;

public class StoredImage {

	private static StoredImage instance = new StoredImage();

	public static StoredImage getInstance() {
		return instance;
	}

	private Map<String, DownloadedImage> profile_images = new HashMap<>();

	public void addImage(String image_url, ImageView image_view) {
		DownloadedImage image = new DownloadedImage();
		image.setImage(image_view.getImage());
		image.setImageView(image_view);
		this.profile_images.put(image_url, image);
	}

	public DownloadedImage getImage(String image_url) {
		return this.profile_images.get(image_url);
	}
}