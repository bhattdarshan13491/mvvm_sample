package com.oncobuddy.app.models.pojo.add_care_taker;

import com.google.gson.annotations.SerializedName;

public class PayLoad{

	@SerializedName("appUser")
	private AppUser appUser;

	@SerializedName("patient")
	private Patient patient;

	@SerializedName("mobileNumber")
	private String mobileNumber;

	@SerializedName("id")
	private int id;

	@SerializedName("lastModified")
	private String lastModified;

	@SerializedName("relationship")
	private String relationship;

	@SerializedName("isActive")
	private boolean isActive;

	@SerializedName("createdOn")
	private String createdOn;

	public void setAppUser(AppUser appUser){
		this.appUser = appUser;
	}

	public AppUser getAppUser(){
		return appUser;
	}

	public void setPatient(Patient patient){
		this.patient = patient;
	}

	public Patient getPatient(){
		return patient;
	}

	public void setMobileNumber(String mobileNumber){
		this.mobileNumber = mobileNumber;
	}

	public String getMobileNumber(){
		return mobileNumber;
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

	public void setRelationship(String relationship){
		this.relationship = relationship;
	}

	public String getRelationship(){
		return relationship;
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
}