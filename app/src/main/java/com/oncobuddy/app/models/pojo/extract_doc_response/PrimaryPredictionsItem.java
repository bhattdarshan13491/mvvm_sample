package com.oncobuddy.app.models.pojo.extract_doc_response;

import com.google.gson.annotations.SerializedName;

public class PrimaryPredictionsItem{

	@SerializedName("probability")
	private double probability;

	@SerializedName("label")
	private String label;

	public void setProbability(double probability){
		this.probability = probability;
	}

	public double getProbability(){
		return probability;
	}

	public void setLabel(String label){
		this.label = label;
	}

	public String getLabel(){
		return label;
	}
}