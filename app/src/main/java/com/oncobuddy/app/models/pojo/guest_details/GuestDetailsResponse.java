package com.oncobuddy.app.models.pojo.guest_details;

import com.google.gson.annotations.SerializedName;

public class GuestDetailsResponse{

	@SerializedName("payLoad")
	private GuestDetails guestDetails;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public void setPayLoad(GuestDetails guestDetails){
		this.guestDetails = guestDetails;
	}

	public GuestDetails getPayLoad(){
		return guestDetails;
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