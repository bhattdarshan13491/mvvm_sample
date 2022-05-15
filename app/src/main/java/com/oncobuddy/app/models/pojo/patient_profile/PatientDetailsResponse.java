package com.oncobuddy.app.models.pojo.patient_profile;

import com.google.gson.annotations.SerializedName;

public class PatientDetailsResponse{

	@SerializedName("payLoad")
	private PatientDetails payLoad;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public void setPayLoad(PatientDetails payLoad){
		this.payLoad = payLoad;
	}

	public PatientDetails getPayLoad(){
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