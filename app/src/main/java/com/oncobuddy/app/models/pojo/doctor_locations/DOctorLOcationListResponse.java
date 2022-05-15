package com.oncobuddy.app.models.pojo.doctor_locations;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class DOctorLOcationListResponse{

	@SerializedName("payLoad")
	private List<Establishment> payLoad;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public void setPayLoad(List<Establishment> payLoad){
		this.payLoad = payLoad;
	}

	public List<Establishment> getPayLoad(){
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