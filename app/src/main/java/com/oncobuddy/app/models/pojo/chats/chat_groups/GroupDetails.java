package com.oncobuddy.app.models.pojo.chats.chat_groups;

import com.google.gson.annotations.SerializedName;

public class GroupDetails {

	@SerializedName("userAppUserId")
	private int userAppUserId;

	@SerializedName("chatGroupId")
	private int chatGroupId;

	@SerializedName("numberOfUnreadMessages")
	private int numberOfUnreadMessages;

	@SerializedName("userDisplayPic")
	private String userDisplayPic;

	@SerializedName("lastMessageAgo")
	private int lastMessageAgo;

	@SerializedName("lastMessage")
	private String lastMessage;

	@SerializedName("userDisplayName")
	private String userDisplayName;

	@SerializedName("userVerified")
	private boolean userVerified;

	@SerializedName("userRole")
	private String userRole;

	public int getUserAppUserId(){
		return userAppUserId;
	}

	public int getChatGroupId(){
		return chatGroupId;
	}

	public int getNumberOfUnreadMessages(){
		return numberOfUnreadMessages;
	}

	public String getUserDisplayPic(){
		return userDisplayPic;
	}

	public int getLastMessageAgo(){
		return lastMessageAgo;
	}

	public String getLastMessage(){
		return lastMessage;
	}

	public String getUserDisplayName(){
		return userDisplayName;
	}

	public boolean isUserVerified(){
		return userVerified;
	}

	public String getUserRole(){
		return userRole;
	}
}