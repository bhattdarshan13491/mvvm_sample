package com.oncobuddy.app.models.pojo.patient_details_by_cg;

import com.google.gson.annotations.SerializedName;

public class PatientDetailsByCGResponse{

	@SerializedName("payLoad")
	private PatientDetails patientDetails;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public PatientDetails getPayLoad(){
		return patientDetails;
	}

	public boolean isSuccess(){
		return success;
	}

	public String getMessage(){
		return message;
	}
}