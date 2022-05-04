package com.beatplaylist.utilities.network.incoming;

public class IncomingMessageHandler {

	// public void handle(Post post) {
	// Platform.runLater(new Runnable() {
	// public void run() {
	// TabType current_tab = Data.getInstance().currentTab;
	//
	// String username = post.getList().get(0);
	// String date = post.getList().get(1);
	// String message_type = post.getList().get(2);
	// String image_url = post.getList().get(3);
	// String content = post.getList().get(4);
	// System.out.println(post.getList());
	//
	// if (User.getInstance().getUserId().equals(username))
	// return;
	//
	// if (!User.getInstance().getUserId().equals(username))
	// new GetConversations().send(false);
	//
	// if (ConversationManager.getInstance().getConversation(username) == null) {
	// new GetConversations().send(false);
	// NotificationManager.getInstance().setHasMessages(1);
	// } else {
	// Conversation conversation = ConversationManager.getInstance().getConversation(username);
	// Message message = new Message();
	// message.set(username, content, image_url, date, MessageType.getName(message_type));
	// conversation.getMessages().add(0, message);
	// conversation.setLatestMessage(content);
	// conversation.setDate(date);
	// if (current_tab == TabType.MESSAGES) {
	// if (ConversationManager.getInstance().getCurrentConversation().getUsername().equals(username)) {
	// MessageManager.getInstance().showConversations(false);
	// MessageManager.getInstance().addMessage(message, true);
	// }
	// }
	// NotificationManager.getInstance().setHasMessages(1);
	// }
	// }
	// });
	// }
}