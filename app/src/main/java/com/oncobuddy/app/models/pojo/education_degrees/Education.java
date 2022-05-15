package com.oncobuddy.app.models.pojo.education_degrees;

import com.google.gson.annotations.SerializedName;

public class Education {

	@SerializedName("degree")
	private String degree;

	@SerializedName("id")
	private int id;

	@SerializedName("branch")
	private String branch;

	public void setDegree(String degree){
		this.degree = degree;
	}

	public String getDegree(){
		return degree;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setBranch(String branch){
		this.branch = branch;
	}

	public String getBranch(){
		return branch;
	}
}