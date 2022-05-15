package com.oncobuddy.app.models.pojo.chats.chat_groups;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ChatGroupResponse{

	@SerializedName("payLoad")
	private List<GroupDetails> payLoad;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public List<GroupDetails> getPayLoad(){
		return payLoad;
	}

	public boolean isSuccess(){
		return success;
	}

	public String getMessage(){
		return message;
	}
}