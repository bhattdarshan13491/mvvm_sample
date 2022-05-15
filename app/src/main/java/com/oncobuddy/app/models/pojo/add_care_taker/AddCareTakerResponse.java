package com.oncobuddy.app.models.pojo.add_care_taker;

import com.google.gson.annotations.SerializedName;

public class AddCareTakerResponse{

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