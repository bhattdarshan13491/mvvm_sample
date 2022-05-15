package com.oncobuddy.app.models.pojo.response_categories;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ResponseCategories{

	@SerializedName("payLoad")
	private List<ReportCategory> payLoad;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public void setPayLoad(List<ReportCategory> payLoad){
		this.payLoad = payLoad;
	}

	public List<ReportCategory> getPayLoad(){
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