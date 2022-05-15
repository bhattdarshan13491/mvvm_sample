package com.oncobuddy.app.models.pojo.care_giver_details;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CareGiverResponse{

	@SerializedName("payLoad")
	private List<CareGiverDetails> careGiverDetails;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public List<CareGiverDetails> getCareGiverDetails() {
		return careGiverDetails;
	}

	public void setCareGiverDetails(List<CareGiverDetails> careGiverDetails) {
		this.careGiverDetails = careGiverDetails;
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