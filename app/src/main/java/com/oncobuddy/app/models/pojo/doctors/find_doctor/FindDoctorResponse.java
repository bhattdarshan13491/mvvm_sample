package com.oncobuddy.app.models.pojo.doctors.find_doctor;

import com.google.gson.annotations.SerializedName;

public class FindDoctorResponse {

	@SerializedName("payLoad")
	private FindDoctorDetail findDoctorDetail;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public void setPayLoad(FindDoctorDetail findDoctorDetail){
		this.findDoctorDetail = findDoctorDetail;
	}

	public FindDoctorDetail getPayLoad(){
		return findDoctorDetail;
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