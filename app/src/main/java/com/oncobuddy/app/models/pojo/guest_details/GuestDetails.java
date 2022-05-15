package com.oncobuddy.app.models.pojo.guest_details;

import com.google.gson.annotations.SerializedName;

public class GuestDetails {

	@SerializedName("role")
	private String role;

	@SerializedName("dpLink")
	private String dpLink;

	
	@SerializedName(value="name", alternate={"fullName"})
	private String name;

	@SerializedName("id")
	private int id;

	@SerializedName("lastModified")
	private String lastModified;

	@SerializedName("createdOn")
	private String createdOn;

	@SerializedName("userId")
	private int userId;

	@SerializedName("headline")
	private String headline;

	public void setRole(String role){
		this.role = role;
	}

	public String getRole(){
		return role;
	}

	public void setDpLink(String dpLink){
		this.dpLink = dpLink;
	}

	public String getDpLink(){
		return dpLink;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
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

	public void setCreatedOn(String createdOn){
		this.createdOn = createdOn;
	}

	public String getCreatedOn(){
		return createdOn;
	}

	public void setUserId(int userId){
		this.userId = userId;
	}

	public int getUserId(){
		return userId;
	}

	public void setHeadline(String headline){
		this.headline = headline;
	}

	public Object getHeadline(){
		return headline;
	}
}