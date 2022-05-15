package com.oncobuddy.app.models.pojo.chats;

import com.google.gson.annotations.SerializedName;

public class MessageInput{

	@SerializedName("groupId")
	private int groupId;

	@SerializedName("message")
	private String message;

	public int getGroupId(){
		return groupId;
	}

	public String getMessage(){
		return message;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}