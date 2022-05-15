package com.oncobuddy.app.models.pojo.invite;

import com.google.gson.annotations.SerializedName;

public class InviteInput{

	@SerializedName("phoneNumber")
	private String phoneNumber;

	@SerializedName("name")
	private String name;

	@SerializedName("email")
	private String email;

	public String getPhoneNumber(){
		return phoneNumber;
	}

	public String getName(){
		return name;
	}

	public String getEmail(){
		return email;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}