package com.oncobuddy.app.models.pojo.education_degrees;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class DegreesResponse{

	@SerializedName("payLoad")
	private List<Education> payLoad;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public void setPayLoad(List<Education> payLoad){
		this.payLoad = payLoad;
	}

	public List<Education> getPayLoad(){
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