package com.oncobuddy.app.models.pojo.extract_doc_response;

import com.google.gson.annotations.SerializedName;

public class DateItem{

	@SerializedName("date")
	private String date;

	@SerializedName("confidenceScore")
	private String confidenceScore;

	@SerializedName("formattedDate")
	private String formattedDate;

	public void setDate(String date){
		this.date = date;
	}

	public String getDate(){
		return date;
	}

	public void setConfidenceScore(String confidenceScore){
		this.confidenceScore = confidenceScore;
	}

	public String getConfidenceScore(){
		return confidenceScore;
	}

	public void setFormattedDate(String formattedDate){
		this.formattedDate = formattedDate;
	}

	public String getFormattedDate(){
		return formattedDate;
	}
}