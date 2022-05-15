package com.oncobuddy.app.models.pojo.doctor_profile.doctor_availibility;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class DoctorAvailibilityResponse{

	@SerializedName("payLoad")
	List<Weekday> payLoad;

	@SerializedName("success")
	public boolean success;

	@SerializedName("message")
	private String message;

	public void setPayLoad(List<Weekday> payLoad){
		this.payLoad = payLoad;
	}

	public List<Weekday> getPayLoad(){
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