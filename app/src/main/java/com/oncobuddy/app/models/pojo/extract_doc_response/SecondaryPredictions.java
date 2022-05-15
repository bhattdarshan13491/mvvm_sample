package com.oncobuddy.app.models.pojo.extract_doc_response;

import com.google.gson.annotations.SerializedName;

public class SecondaryPredictions{

	@SerializedName("prob")
	private double prob;

	@SerializedName("label")
	private String label;

	public void setProb(double prob){
		this.prob = prob;
	}

	public double getProb(){
		return prob;
	}

	public void setLabel(String label){
		this.label = label;
	}

	public String getLabel(){
		return label;
	}
}