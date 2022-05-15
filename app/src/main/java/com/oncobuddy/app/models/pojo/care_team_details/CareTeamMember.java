package com.oncobuddy.app.models.pojo.care_team_details;

import com.google.gson.annotations.SerializedName;

public class CareTeamMember {

	@SerializedName("firstName")
	private String firstName;

	@SerializedName("lastName")
	private String lastName;

	@SerializedName("role")
	private String role;

	@SerializedName("phoneNumber")
	private String phoneNumber;

	@SerializedName("address")
	private Object address;

	@SerializedName("dpLink")
	private String dpLink;

	@SerializedName("id")
	private int id;

	@SerializedName("headline")
	private String headline;

	@SerializedName("email")
	private String email;

	public void setFirstName(String firstName){
		this.firstName = firstName;
	}

	public String getFirstName(){
		return firstName;
	}

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

	public void setPhoneNumber(String phoneNumber){
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneNumber(){
		return phoneNumber;
	}

	public void setAddress(Object address){
		this.address = address;
	}

	public Object getAddress(){
		return address;
	}

	public void setDpLink(String dpLink){
		this.dpLink = dpLink;
	}

	public String getDpLink(){
		return dpLink;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
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