package com.oncobuddy.app.models.pojo.care_giver_details;

import com.google.gson.annotations.SerializedName;

public class AssignCGInput{

	@SerializedName("relationshipType")
	private String relationshipType;

	@SerializedName("careCode")
	private String careCode;

	public void setRelationshipType(String relationshipType){
		this.relationshipType = relationshipType;
	}

	public String getRelationshipType(){
		return relationshipType;
	}

	public void setCareCode(String careCode){
		this.careCode = careCode;
	}

	public String getCareCode(){
		return careCode;
	}
}