package com.oncobuddy.app.models.pojo.genetic_report_response;

import com.google.gson.annotations.SerializedName;

public class GeneticReportResponse{

	@SerializedName("payLoad")
	private Report report;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public Report getPayLoad(){
		return report;
	}

	public boolean isSuccess(){
		return success;
	}

	public String getMessage(){
		return message;
	}
}