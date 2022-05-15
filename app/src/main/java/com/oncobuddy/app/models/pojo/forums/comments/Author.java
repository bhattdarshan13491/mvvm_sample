package com.oncobuddy.app.models.pojo.forums.comments;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Author{

	@SerializedName("dpLink")
	private String dpLink;

	@SerializedName("name")
	private String name;

	@SerializedName("verified")
	@Expose
	public Boolean verified;

	@SerializedName("userId")
	private int userId;

	@SerializedName("headline")
	private String headline;

	@SerializedName("role")
	private String role;


	public Boolean getVerified() {
		return verified;
	}

	public void setVerified(Boolean verified) {
		this.verified = verified;
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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}