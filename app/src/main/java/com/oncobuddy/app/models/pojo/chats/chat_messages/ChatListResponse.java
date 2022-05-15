package com.oncobuddy.app.models.pojo.chats.chat_messages;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ChatListResponse{

	@SerializedName("payLoad")
	private List<ChatMessage> payLoad;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public void setPayLoad(List<ChatMessage> payLoad){
		this.payLoad = payLoad;
	}

	public List<ChatMessage> getPayLoad(){
		return payLoad;
	}

	public void setSuccess(boolean success){
		this.success = success;
	}

	public boolean isSuccess(){
		return success;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}
}