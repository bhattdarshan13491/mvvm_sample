package com.oncobuddy.app.models.pojo.doctor_profile.doctor_details;

import com.google.gson.annotations.SerializedName;

public class SubCategory{

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