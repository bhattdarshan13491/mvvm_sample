package com.oncobuddy.app.models.pojo.doctor_locations;

import com.google.gson.annotations.SerializedName;

public class EstablishmentInput{

	@SerializedName("address")
	private Address address;

	@SerializedName("establishmentName")
	private String establishmentName;

	@SerializedName("locationType")
	private String locationType;

	public void setAddress(Address address){
		this.address = address;
	}

	public Address getAddress(){
		return address;
	}

	public void setEstablishmentName(String establishmentName){
		this.establishmentName = establishmentName;
	}

	public String getEstablishmentName(){
		return establishmentName;
	}

	public void setLocationType(String locationType){
		this.locationType = locationType;
	}

	public String getLocationType(){
		return locationType;
	}
}