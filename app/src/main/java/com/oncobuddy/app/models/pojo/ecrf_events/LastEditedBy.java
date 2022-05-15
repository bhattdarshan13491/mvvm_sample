package com.oncobuddy.app.models.pojo.ecrf_events;

import com.google.gson.annotations.SerializedName;

public class LastEditedBy{

	@SerializedName("lastName")
	private String lastName;

	@SerializedName("role")
	private String role;

	@SerializedName("address")
	private String address;

	@SerializedName("mobileNumber")
	private String mobileNumber;

	@SerializedName("active")
	private boolean active;

	@SerializedName("admin")
	private Object admin;

	@SerializedName("employeeId")
	private String employeeId;

	@SerializedName("createdOn")
	private String createdOn;

	@SerializedName("firstName")
	private String firstName;

	@SerializedName("password")
	private String password;

	@SerializedName("id")
	private int id;

	@SerializedName("lastModified")
	private String lastModified;

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

	public void setAddress(String address){
		this.address = address;
	}

	public String getAddress(){
		return address;
	}

	public void setMobileNumber(String mobileNumber){
		this.mobileNumber = mobileNumber;
	}

	public String getMobileNumber(){
		return mobileNumber;
	}

	public void setActive(boolean active){
		this.active = active;
	}

	public boolean isActive(){
		return active;
	}

	public void setAdmin(Object admin){
		this.admin = admin;
	}

	public Object getAdmin(){
		return admin;
	}

	public void setEmployeeId(String employeeId){
		this.employeeId = employeeId;
	}

	public String getEmployeeId(){
		return employeeId;
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

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return email;
	}
}