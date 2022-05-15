package com.oncobuddy.app.models.pojo.doctor_locations;

import com.google.gson.annotations.SerializedName;

public class Address{

	@SerializedName("address2")
	private String address2;

	@SerializedName("city")
	private String city;

	@SerializedName("address1")
	private String address1;

	@SerializedName("postalCode")
	private String postalCode;

	@SerializedName("state")
	private String state;

	public void setAddress2(String address2){
		this.address2 = address2;
	}

	public String getAddress2(){
		return address2;
	}

	public void setCity(String city){
		this.city = city;
	}

	public String getCity(){
		return city;
	}

	public void setAddress1(String address1){
		this.address1 = address1;
	}

	public String getAddress1(){
		return address1;
	}

	public void setPostalCode(String postalCode){
		this.postalCode = postalCode;
	}

	public String getPostalCode(){
		return postalCode;
	}

	public void setState(String state){
		this.state = state;
	}

	public String getState(){
		return state;
	}
}