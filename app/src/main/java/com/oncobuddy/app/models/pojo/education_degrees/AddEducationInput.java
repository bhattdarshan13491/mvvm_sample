package com.oncobuddy.app.models.pojo.education_degrees;

import com.google.gson.annotations.SerializedName;

public class AddEducationInput {

	@SerializedName("degree")
	private String degree;

	@SerializedName("branch")
	private String branch;

	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

	public String getBrench() {
		return branch;
	}

	public void setBrench(String brench) {
		this.branch = brench;
	}
}