package com.oncobuddy.app.models.pojo.forums.trending_videos;

import com.google.gson.annotations.SerializedName;

public class Author{

	@SerializedName("role")
	private String role;

	@SerializedName("dpLink")
	private String dpLink;

	@SerializedName("verified")
	private boolean verified;

	@SerializedName("name")
	private String name;

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

	public void setVerified(boolean verified){
		this.verified = verified;
	}

	public boolean isVerified(){
		return verified;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
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

	public String getHeadline(){
		return headline;
	}
}