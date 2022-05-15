package com.oncobuddy.app.models.pojo.auto_tags_response;

import com.google.gson.annotations.SerializedName;

public class PredictedTag {

	@SerializedName("probability")
	private double probability;

	@SerializedName("label")
	private String label;

	public double getProbability(){
		return probability;
	}

	public String getLabel(){
		return label;
	}
}