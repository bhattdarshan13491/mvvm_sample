package com.oncobuddy.app.models.pojo.chats.chat_messages;

import com.google.gson.annotations.SerializedName;

public class MembersItem{

	@SerializedName("lastName")
	private String lastName;

	@SerializedName("role")
	private String role;

	@SerializedName("address")
	private Address address;

	@SerializedName("isActive")
	private boolean isActive;

	@SerializedName("createdOn")
	private String createdOn;

	@SerializedName("firstName")
	private String firstName;

	@SerializedName("password")
	private String password;

	@SerializedName("phoneNumber")
	private String phoneNumber;

	@SerializedName("deleted")
	private Object deleted;

	@SerializedName("dpLink")
	private String dpLink;

	@SerializedName("isMobileVerified")
	private boolean isMobileVerified;

	@SerializedName("id")
	private int id;

	@SerializedName("lastModified")
	private String lastModified;

	@SerializedName("showProfileSetupScreen")
	private Object showProfileSetupScreen;

	@SerializedName("headline")
	private String headline;

	@SerializedName("email")
	private String email;

	public void setLastName(String lastName){
		this.lastName = lastName;
	}

	public String getLastName(){
		return lastName;
	}

	public void setRole(String role){
		this.role = role;
	}

	public String getRole(){
		return role;
	}

	public void setAddress(Address address){
		this.address = address;
	}

	public Address getAddress(){
		return address;
	}

	public void setIsActive(boolean isActive){
		this.isActive = isActive;
	}

	public boolean isIsActive(){
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

	public void setDeleted(Object deleted){
		this.deleted = deleted;
	}

	public Object getDeleted(){
		return deleted;
	}

	public void setDpLink(String dpLink){
		this.dpLink = dpLink;
	}

	public String getDpLink(){
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

	public void setShowProfileSetupScreen(Object showProfileSetupScreen){
		this.showProfileSetupScreen = showProfileSetupScreen;
	}

	public Object getShowProfileSetupScreen(){
		return showProfileSetupScreen;
	}

	public void setHeadline(String headline){
		this.headline = headline;
	}

	public String getHeadline(){
		return headline;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return email;
	}
}