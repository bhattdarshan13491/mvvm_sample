package com.oncobuddy.app.models.pojo.genetic_report;

import com.google.gson.annotations.SerializedName;

public class GeneticResponse{

	@SerializedName("payLoad")
	private GeneticReportList payLoad;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public GeneticReportList getPayLoad(){
		return payLoad;
	}

	public boolean isSuccess(){
		return success;
	}

	public String getMessage(){
		return message;
	}
}