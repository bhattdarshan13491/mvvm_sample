package com.oncobuddy.app.models.pojo.add_care_taker;

import com.google.gson.annotations.SerializedName;

public class AppUser{

	@SerializedName("lastName")
	private Object lastName;

	@SerializedName("role")
	private String role;

	@SerializedName("address")
	private Object address;

	@SerializedName("isActive")
	private Object isActive;

	@SerializedName("createdOn")
	private String createdOn;

	@SerializedName("firstName")
	private String firstName;

	@SerializedName("password")
	private String password;

	@SerializedName("phoneNumber")
	private String phoneNumber;

	@SerializedName("dpLink")
	private Object dpLink;

	@SerializedName("isMobileVerified")
	private boolean isMobileVerified;

	@SerializedName("id")
	private int id;

	@SerializedName("lastModified")
	private String lastModified;

	@SerializedName("headline")
	private Object headline;

	@SerializedName("email")
	private Object email;

	public void setLastName(Object lastName){
		this.lastName = lastName;
	}

	public Object getLastName(){
		return lastName;
	}

	public void setRole(String role){
		this.role = role;
	}

	public String getRole(){
		return role;
	}

	public void setAddress(Object address){
		this.address = address;
	}

	public Object getAddress(){
		return address;
	}

	public void setIsActive(Object isActive){
		this.isActive = isActive;
	}

	public Object getIsActive(){
		return isActive;
	}

	public void setCreatedOn(String createdOn){
		this.createdOn = createdOn;
	}

	public String getCreatedOn(){
		return createdOn;
	}

	public void setFirstName(String firstName){
		this.firstName = firstName;
	}

	public String getFirstName(){
		return firstName;
	}

	public void setPassword(String password){
		this.password = password;
	}

	public String getPassword(){
		return password;
	}

	public void setPhoneNumber(String phoneNumber){
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneNumber(){
		return phoneNumber;
	}

	public void setDpLink(Object dpLink){
		this.dpLink = dpLink;
	}

	public Object getDpLink(){
		return dpLink;
	}

	public void setIsMobileVerified(boolean isMobileVerified){
		this.isMobileVerified = isMobileVerified;
	}

	public boolean isIsMobileVerified(){
		return isMobileVerified;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setLastModified(String lastModified){
		this.lastModified = lastModified;
	}

	public String getLastModified(){
		return lastModified;
	}

	public void setHeadline(Object headline){
		this.headline = headline;
	}

	public Object getHeadline(){
		return headline;
	}

	public void setEmail(Object email){
		this.email = email;
	}

	public Object getEmail(){
		return email;
	}
}