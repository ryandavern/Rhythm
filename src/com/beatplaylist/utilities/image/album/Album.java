package com.beatplaylist.utilities.image.album;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Album {

	// NOT CURRENTLY IN USE.

	private String album_name;
	private List<ImageView> images;
	private int current_id;

	public Album(String album_name) {
		this.album_name = album_name;
		this.images = new ArrayList<>();
	}

	public void addPictureToAlbum(ImageView image_view) {
		this.images.add(image_view);
	}

	public void removePictureFromAlbum(ImageView image_view) {
		this.images.remove(image_view);
	}

	public void removePictureFromId(int id) {
		this.images.remove(id);
	}

	public List<ImageView> getAlbum() {
		return this.images;
	}

	public void addPictureFromURL(String url) {
		new Thread(new Runnable() {
			public void run() {
				ImageView image_view = new ImageView(new Image(url));
				images.add(image_view);
			}
		}).start();
	}

	public String getAlbumName() {
		return this.album_name;
	}

	public void setCurrentImageId(boolean previous) {
		if (previous)
			this.current_id--;
		else
			this.current_id++;
	}

	public int getCurrentImageId() {
		return this.current_id;
	}

	public ImageView getNextImage(int current_id) {
		return this.images.get(current_id + 1);
	}

	public ImageView getPreviousImage(int current_id) {
		return this.images.get(current_id - 1);
	}
}