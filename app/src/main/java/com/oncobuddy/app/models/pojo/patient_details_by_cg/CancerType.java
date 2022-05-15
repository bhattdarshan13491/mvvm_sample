package com.oncobuddy.app.models.pojo.patient_details_by_cg;

import com.google.gson.annotations.SerializedName;

public class CancerType{

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

	public void setName(String name) {
		this.name = name;
	}

	public void setId(int id) {
		this.id = id;
	}
}