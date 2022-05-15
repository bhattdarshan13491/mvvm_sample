package com.oncobuddy.app.models.pojo.doctor_update;

import com.google.gson.annotations.SerializedName;

public class DoctorRegistrationInput{

	@SerializedName("appUser")
	private AppUser appUser;

	@SerializedName("hospitalLocationId")
	private int hospitalId;

	@SerializedName("specialization")
	private String specialization;

	@SerializedName("designation")
	private String designation;

	@SerializedName("consultationFee")
	private double consultationFee;

	@SerializedName("currentLocation")
	private String currentLocation;

	@SerializedName("experience")
	private String experience;

	@SerializedName("dateOfBirth")
	private String dateOfBirth;

	@SerializedName("description")
	private String description;

	public void setAppUser(AppUser appUser){
		this.appUser = appUser;
	}

	public AppUser getAppUser(){
		return appUser;
	}

	public void setHospitalId(int hospitalId){
		this.hospitalId = hospitalId;
	}

	public int getHospitalId(){
		return hospitalId;
	}

	public void setSpecialization(String specialization){
		this.specialization = specialization;
	}

	public String getSpecialization(){
		return specialization;
	}

	public void setDesignation(String designation){
		this.designation = designation;
	}

	public String getDesignation(){
		return designation;
	}

	public double getConsultationFee() {
		return consultationFee;
	}

	public void setConsultationFee(double consultationFee) {
		this.consultationFee = consultationFee;
	}

	public String getExperience() {
		return experience;
	}

	public void setExperience(String experience) {
		this.experience = experience;
	}

	public String getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(String currentLocation) {
		this.currentLocation = currentLocation;
	}

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
}