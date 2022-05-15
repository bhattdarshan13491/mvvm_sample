package com.oncobuddy.app.models.pojo.chats.chat_messages;

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

	@SerializedName("id")
	private int id;

	@SerializedName("lastModified")
	private String lastModified;

	@SerializedName("state")
	private String state;

	@SerializedName("createdOn")
	private String createdOn;

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

	public void setState(String state){
		this.state = state;
	}

	public String getState(){
		return state;
	}

	public void setCreatedOn(String createdOn){
		this.createdOn = createdOn;
	}

	public String getCreatedOn(){
		return createdOn;
	}
}