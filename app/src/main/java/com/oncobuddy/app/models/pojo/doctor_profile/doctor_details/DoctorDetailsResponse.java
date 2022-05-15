package com.oncobuddy.app.models.pojo.doctor_profile.doctor_details;

import com.google.gson.annotations.SerializedName;

public class DoctorDetailsResponse{

	@SerializedName("payLoad")
	private DoctorDetails doctorDetails;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public DoctorDetails getPayLoad(){
		return doctorDetails;
	}

	public boolean isSuccess(){
		return success;
	}

	public String getMessage(){
		return message;
	}
}