package com.oncobuddy.app.models.pojo.response_sub_categories;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ResponseSubCategories{

	@SerializedName("payLoad")
	private List<String> payLoad;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public void setPayLoad(List<String> payLoad){
		this.payLoad = payLoad;
	}

	public List<String> getPayLoad(){
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