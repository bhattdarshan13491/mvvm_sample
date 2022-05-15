package com.oncobuddy.app.models.pojo.care_giver_details;

import com.google.gson.annotations.SerializedName;

public class AddUserByCCInput{

	@SerializedName("mobileNumber")
	private String mobileNumber;

	@SerializedName("name")
	private String name;

	public void setMobileNumber(String mobileNumber){
		this.mobileNumber = mobileNumber;
	}

	public String getMobileNumber(){
		return mobileNumber;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}
}