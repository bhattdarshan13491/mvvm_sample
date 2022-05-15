package com.oncobuddy.app.models.pojo.nov_signup;

import com.google.gson.annotations.SerializedName;

public class Profile{

	@SerializedName("gender")
	private Object gender;

	@SerializedName("dateOfBirth")
	private Object dateOfBirth;

	@SerializedName("cancerType")
	private Object cancerType;

	@SerializedName("cancerSubType")
	private Object cancerSubType;

	public void setGender(Object gender){
		this.gender = gender;
	}

	public Object getGender(){
		return gender;
	}

	public void setDateOfBirth(Object dateOfBirth){
		this.dateOfBirth = dateOfBirth;
	}

	public Object getDateOfBirth(){
		return dateOfBirth;
	}

	public void setCancerType(Object cancerType){
		this.cancerType = cancerType;
	}

	public Object getCancerType(){
		return cancerType;
	}

	public void setCancerSubType(Object cancerSubType){
		this.cancerSubType = cancerSubType;
	}

	public Object getCancerSubType(){
		return cancerSubType;
	}
}