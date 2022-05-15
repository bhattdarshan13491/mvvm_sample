package com.oncobuddy.app.models.pojo.doctor_update;

import com.google.gson.annotations.SerializedName;

public class AppUser{

	@SerializedName("id")
	private int id;

	@SerializedName("firstName")
	private String firstName;

	@SerializedName("lastName")
	private String lastName;

	@SerializedName("password")
	private String password;

	@SerializedName("phoneNumber")
	private String phoneNumber;

	@SerializedName("addressDto")
	private AddressDto addressDto;

	@SerializedName("dpLink")
	private String dpLink;

	@SerializedName("headline")
	private String headline;

	@SerializedName("email")
	private String email;

	@SerializedName("dateOfBirth")
	private String dateOfBirth;

	@SerializedName("description")
	private String description;


	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

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

	public void setPassword(String password){
		this.password = password;
	}

	public String getPassword(){
		return password;
	}

	public void setPhoneNumber(String phoneNumber){
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneNumber(){
		return phoneNumber;
	}

	public void setAddressDto(AddressDto addressDto){
		this.addressDto = addressDto;
	}

	public AddressDto getAddressDto(){
		return addressDto;
	}

	public void setDpLink(String dpLink){
		this.dpLink = dpLink;
	}

	public String getDpLink(){
		return dpLink;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}