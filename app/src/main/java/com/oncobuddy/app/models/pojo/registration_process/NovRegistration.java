package com.oncobuddy.app.models.pojo.registration_process;

import com.google.gson.annotations.SerializedName;

public class NovRegistration{

	@SerializedName("password")
	private String password;

	@SerializedName("role")
	private String role;

	@SerializedName("mobileNumber")
	private String mobileNumber;

	@SerializedName("fullName")
	private String fullName;

	@SerializedName("email")
	private String email;

	public String getPassword(){
		return password;
	}

	public String getRole(){
		return role;
	}

	public String getMobileNumber(){
		return mobileNumber;
	}

	public String getFullName(){
		return fullName;
	}

	public String getEmail(){
		return email;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}