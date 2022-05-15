package com.oncobuddy.app.models.pojo.auto_tags_response;

import com.google.gson.annotations.SerializedName;

public class PredictTagsReq{

	@SerializedName("extract")
	private String extract;

	public void setExtract(String extract){
		this.extract = extract;
	}

	public String getExtract(){
		return extract;
	}
}