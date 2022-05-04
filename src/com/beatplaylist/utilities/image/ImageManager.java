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

package com.beatplaylist.utilities.image;

import com.beatplaylist.Main;
import com.beatplaylist.utilities.image.cache.StoredImage;
import com.beatplaylist.utilities.thread.ThreadManager;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

public class ImageManager {

	private static String cdn = "https://beatplaylist.sfo2.cdn.digitaloceanspaces.com/";

	public static void getAlbumImage(ImageView imageView, String imageURL, int width, int height) {
		if (StoredImage.getInstance().getImage(imageURL) != null) {
			imageView.setImage(StoredImage.getInstance().getImage(imageURL).getImage());
			return;
		}
		imageView.setImage(new Image(Main.class.getResource("/resources/default_profile.png").toExternalForm(), width, height, false, false));
		ThreadManager.getInstance().getThreadPool().execute(() -> {
			imageView.setImage(new Image(cdn + "album/" + imageURL + ".jpg", width, height, false, false));
		});
	}

	public static void getProfileImage(ImageView image_view, String image, double width, double height) {
		String image_url = image + String.valueOf(width) + "x" + String.valueOf(height);
		Rectangle clip = new Rectangle(width, height);
		clip.setArcWidth(Math.min(width, height));
		clip.setArcHeight(Math.min(width, height));

		image_view.setClip(clip);

		if (StoredImage.getInstance().getImage(image_url) != null) {
			image_view.setImage(StoredImage.getInstance().getImage(image_url).getImage());
			image_view.setFitHeight(height);
			image_view.setFitWidth(width);
			return;
		}

		if (width == 45)
			image_view.setImage(new Image(Main.class.getResource("/resources/default_profile/default_profile_45px.png").toExternalForm(), width, height, false, false));
		else if (width == 50)
			image_view.setImage(new Image(Main.class.getResource("/resources/default_profile/default_profile_50px.png").toExternalForm(), width, height, false, false));
		else if (width == 75)
			image_view.setImage(new Image(Main.class.getResource("/resources/default_profile/default_profile_75px.png").toExternalForm(), width, height, false, false));
		else
			image_view.setImage(new Image(Main.class.getResource("/resources/default_profile/default_profile.png").toExternalForm(), width, height, false, false));

		if (image.equals("null") || image.isEmpty())
			return;

		ThreadManager.getInstance().getThreadPool().execute(() -> {
			image_view.setImage(new Image(cdn + "image/a/" + image + ".jpg", width, height, false, false));
			StoredImage.getInstance().addImage(image_url, image_view);
		});
	}

	public static void getImage(ImageView image_view, String image) {
		if (StoredImage.getInstance().getImage(image) != null) {
			image_view.setImage(StoredImage.getInstance().getImage(image).getImage());
			return;
		}
		image_view.setImage(new Image(Main.class.getResource("/resources/default_profile.png").toExternalForm()));

		if (image.equals("NULL") || image.isEmpty()) {
			return;
		}

		ThreadManager.getInstance().getThreadPool().execute(() -> {
			image_view.setImage(new Image(cdn + "image/a/" + image + ".jpg"));
		});
	}

	public static void getArtistPageImage(ImageView image_view, String url) {
		double width = 60, height = 60;
		ThreadManager.getInstance().getThreadPool().execute(() -> {
			image_view.setImage(new Image(url.replace("=s48", "=s60"), width, height, false, false));
		});
	}

	// Unused right now
	// public static void getImage(ImageView image_view, String image, double width, double height) {
	// if (StoredImage.getInstance().getImage(image) != null) {
	// image_view.setImage(StoredImage.getInstance().getImage(image).getImage());
	// return;
	// }
	// image_view.setImage(new Image(Main.class.getResource("/resources/default_profile.png").toExternalForm()));
	//
	// if (image.equals("NULL") || image.isEmpty()) {
	// return;
	// }
	//
	// Thread thread = new Thread(() -> {
	// image_view.setImage(new Image(cdn + "image/a/" + image + ".jpg", width, height, false, false));
	// });
	// thread.start();
	// }
	//
	// // Unused right now
	// public static void getThumbnail(ImageView image_view, String video_id) {
	// if (StoredImage.getInstance().getImage(video_id) != null) {
	// image_view.setImage(StoredImage.getInstance().getImage(video_id).getImage());
	// return;
	// }
	// Thread thread = new Thread(() -> {
	// image_view.setImage(new Image("https://img.youtube.com/vi/" + video_id + "/mqdefault.jpg", 175, 75, false, false));
	// StoredImage.getInstance().addImage(video_id, image_view);
	// });
	// thread.start();
	// }
}