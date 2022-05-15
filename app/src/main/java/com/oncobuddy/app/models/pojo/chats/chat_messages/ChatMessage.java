package com.oncobuddy.app.models.pojo.chats.chat_messages;

import com.google.gson.annotations.SerializedName;

public class ChatMessage {

	@SerializedName("messageStatus")
	private String messageStatus;

	@SerializedName("id")
	private int id;

	@SerializedName("lastModified")
	private String lastModified;

	@SerializedName("message")
	private String message;

	@SerializedName("createdOn")
	private String createdOn;

	@SerializedName("senderAppUserId")
	private int senderAppUserId;

	public void setMessageStatus(String messageStatus){
		this.messageStatus = messageStatus;
	}

	public String getMessageStatus(){
		return messageStatus;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setLastModified(String lastModified){
		this.lastModified = lastModified;
	}

	public String getLastModified(){
		return lastModified;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setCreatedOn(String createdOn){
		this.createdOn = createdOn;
	}

	public String getCreatedOn(){
		return createdOn;
	}

	public void setSenderAppUserId(int senderAppUserId){
		this.senderAppUserId = senderAppUserId;
	}

	public int getSenderAppUserId(){
		return senderAppUserId;
	}
}