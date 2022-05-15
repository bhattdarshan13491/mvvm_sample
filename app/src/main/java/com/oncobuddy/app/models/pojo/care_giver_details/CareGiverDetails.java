package com.oncobuddy.app.models.pojo.care_giver_details;

import com.google.gson.annotations.SerializedName;

public class CareGiverDetails {

	@SerializedName("phoneNumber")
	private String phoneNumber;

	@SerializedName("name")
	private String name;

	@SerializedName("appUserId")
	private int appUserId;

	@SerializedName("id")
	private int id;

	@SerializedName("relationship")
	private String relationship;

	public String getRelationship() {
		return relationship;
	}

	private boolean isSelected =  false;

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	public void setPhoneNumber(String phoneNumber){
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneNumber(){
		return phoneNumber;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setAppUserId(int appUserId){
		this.appUserId = appUserId;
	}

	public int getAppUserId(){
		return appUserId;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean selected) {
		isSelected = selected;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}