package com.oncobuddy.app.models.pojo.doctor_availibility;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class DoctorAvailibilityResponse{

	@SerializedName("payLoad")
	private List<Day> payLoad;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public void setPayLoad(List<Day> payLoad){
		this.payLoad = payLoad;
	}

	public List<Day> getPayLoad(){
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