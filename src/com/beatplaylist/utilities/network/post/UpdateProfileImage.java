package com.beatplaylist.utilities.network.post;

import java.io.File;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.beatplaylist.gui.GUIManager;
import com.beatplaylist.gui.module.layout.sidebar.TabType;
import com.beatplaylist.utilities.CustomColor;
import com.beatplaylist.utilities.events.ImageResponseEvent;
import com.beatplaylist.utilities.events.ImageUploadEvent;
import com.beatplaylist.utilities.events.SocketReceiveEvent;
import com.beatplaylist.utilities.network.netty.NETTYClient;
import com.beatplaylist.utilities.network.serialized.ImageUpload;
import com.beatplaylist.utilities.network.serialized.PacketType;
import com.beatplaylist.utilities.network.serialized.Post;
import com.beatplaylist.utilities.notification.AlertType;
import com.beatplaylist.utilities.notification.Notification;
import com.beatplaylist.utilities.popup.Popup;
import com.beatplaylist.utilities.user.UserManager;

import javafx.application.Platform;

public class UpdateProfileImage {

	public static void send(String directory, ImageResponseEvent event) {
		ImageUpload post = new ImageUpload();
		System.out.println(directory);
		try {
			post.setImage(new File(directory.replace("file:", "")));
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (post.getImageBytes() == null)
			return;

		post.setDetails();

		NETTYClient.getInstance().uploadImage(post, new ImageUploadEvent() {

			@Override
			public void onSuccess(ImageUpload image_upload) {
				Post post = new Post();
				post.setDetails();
				post.setPacketType(PacketType.SET_PROFILE_IMAGE);

				JSONObject object = new JSONObject();
				object.put("profile_image_url", JSONValue.escape(image_upload.getImageURL()));

				post.setJSONArray(object);

				NETTYClient.getInstance().send(post, new SocketReceiveEvent() {
					@Override
					public void onSuccess(Post post) {
						UserManager.getInstance().getUser().profileImageURL = image_upload.getImageURL();
						GUIManager.getInstance().sideBar.profileMenu.reloadProfilePicture();
						// if (Data.getInstance().currentTab == TabType.PROFILE || Data.getInstance().currentTab == TabType.OTHER_PROFILE) {
						// if (SearchUser.getInstance().getUserId().equals(User.getInstance().getUserId()))
						// ImageManager.getInstance().getImage(ProfileData.getInstance().profile_image, image_upload.getImageURL(), 200, 200);
						// }
						Platform.runLater(() -> {
							if (GUIManager.getInstance().currentTab.tab == TabType.PROFILE)
								event.onSuccess(image_upload.getImageURL());
							Popup.errorAlert("Profile Image", "Your profile image has been successfully updated.", CustomColor.RHYTHM);
						});
					}

					@Override
					public void onError(String error) {
						Notification.getInstance().createNotification("Image Upload Fail", "The image you uploaded has failed! Please try again.", AlertType.ERROR);
					}
				});
			}

			@Override
			public void onError(String error) {
				Platform.runLater(() -> {
					if (error.equals("IMAGE_UPLOAD_FAILED")) {
						Notification.getInstance().createNotification("Profile Picture", "The image you uploaded has failed! Please try again.", AlertType.ERROR);
					} else if (error.equals("FILE_SIZE_REACHED")) {
						Notification.getInstance().createNotification("Profile Picture", "You cannot upload images larger than a file size of 4MB", AlertType.ERROR);
					} else {
						Notification.getInstance().createNotification("Profile Picture", "The image you uploaded has failed! Please try again.", AlertType.ERROR);
					}
				});
			}
		});
	}
}