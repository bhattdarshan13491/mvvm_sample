package com.oncobuddy.app.models.pojo.nov_signup;

import com.google.gson.annotations.SerializedName;
import com.oncobuddy.app.models.pojo.login_response.LoginDetails;

public class Body{

	@SerializedName("payLoad")
	private LoginDetails payLoad;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public void setPayLoad(LoginDetails payLoad){
		this.payLoad = payLoad;
	}

	public LoginDetails getPayLoad(){
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