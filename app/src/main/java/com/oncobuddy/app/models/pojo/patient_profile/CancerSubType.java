package com.oncobuddy.app.models.pojo.patient_profile;

import com.google.gson.annotations.SerializedName;

public class CancerSubType{

	@SerializedName("name")
	private String name;

	@SerializedName("id")
	private int id;

	public String getName(){
		return name;
	}

	public int getId(){
		return id;
	}
}