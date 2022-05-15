package com.oncobuddy.app.models.pojo.extract_doc_response;

import com.google.gson.annotations.SerializedName;

public class UploadDocResponse{

	@SerializedName("payLoad")
	private PayLoad payLoad;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public void setPayLoad(PayLoad payLoad){
		this.payLoad = payLoad;
	}

	public PayLoad getPayLoad(){
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