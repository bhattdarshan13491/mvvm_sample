package com.oncobuddy.app.models.pojo.auto_tags_response;

import com.google.gson.annotations.SerializedName;

public class AutoTagsResponse{

	@SerializedName("payLoad")
	private AutoTagsList autoTagsList;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public AutoTagsList getPayLoad(){
		return autoTagsList;
	}

	public boolean isSuccess(){
		return success;
	}

	public String getMessage(){
		return message;
	}
}