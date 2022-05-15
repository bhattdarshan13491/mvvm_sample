package com.oncobuddy.app.models.pojo.patient_profile;

import com.google.gson.annotations.SerializedName;

public class PatientDetails{

	@SerializedName("firstName")
	private String firstName;

	@SerializedName("lastName")
	private Object lastName;

	@SerializedName("phoneNumber")
	private String phoneNumber;

	@SerializedName("dpLink")
	private String dpLink;

	@SerializedName("id")
	private int id;

	@SerializedName("cancerType")
	private CancerType cancerType;

	@SerializedName("email")
	private String email;

	@SerializedName("cancerSubType")
	private CancerSubType cancerSubType;

	public String getFirstName(){
		return firstName;
	}

	public Object getLastName(){
		return lastName;
	}

	public String getPhoneNumber(){
		return phoneNumber;
	}

	public String getDpLink(){
		return dpLink;
	}

	public int getId(){
		return id;
	}

	public CancerType getCancerType(){
		return cancerType;
	}

	public String getEmail(){
		return email;
	}

	public CancerSubType getCancerSubType(){
		return cancerSubType;
	}
}