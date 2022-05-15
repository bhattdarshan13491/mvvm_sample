package com.oncobuddy.app.models.pojo;

import com.google.gson.annotations.SerializedName;

public class CareGiverInput{

	@SerializedName("dateOfBirth")
	private String dateOfBirth;

	@SerializedName("name")
	private String name;


	@SerializedName("dpLink")
	private String dpLink;

	public void setDateOfBirth(String dateOfBirth){
		this.dateOfBirth = dateOfBirth;
	}

	public String getDateOfBirth(){
		return dateOfBirth;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDpLink() {
		return dpLink;
	}

	public void setDpLink(String dpLink) {
		this.dpLink = dpLink;
	}
}