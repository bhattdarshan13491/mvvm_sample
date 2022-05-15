package com.oncobuddy.app.models.pojo.forums.shorts;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ShortsListResponse{

	@SerializedName("payLoad")
	private List<ShortDetails> payLoad;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public void setPayLoad(List<ShortDetails> payLoad){
		this.payLoad = payLoad;
	}

	public List<ShortDetails> getPayLoad(){
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