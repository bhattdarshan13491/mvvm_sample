package com.oncobuddy.app.models.pojo.patient_details_by_cg;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PatientDetails implements Serializable {

	@SerializedName("lastName")
	private String lastName;

	@SerializedName("cancerSite")
	private String cancerSite;

	@SerializedName("deceased")
	private String deceased;

	@SerializedName("gender")
	private String gender;

	@SerializedName("requestPending")
	private boolean requestPending;

	@SerializedName("weight")
	private int weight;

	@SerializedName("doNotContact")
	private boolean doNotContact;

	@SerializedName("patientType")
	private String patientType;

	@SerializedName("isActive")
	private boolean isActive;

	@SerializedName("firstName")
	private String firstName;

	@SerializedName("phoneNumber")
	private String phoneNumber;

	@SerializedName("careCompanion")
	private String careCompanion;

	@SerializedName("aadharNumber")
	private String aadharNumber;

	@SerializedName("dpLink")
	private String dpLink;

	@SerializedName("dob")
	private String dob;

	@SerializedName("requestedUserId")
	private int requestedUserId;

	@SerializedName("cancerStage")
	private String cancerStage;

	@SerializedName("id")
	private int id;

	@SerializedName("cancerType")
	private CancerType cancerType;

	@SerializedName("email")
	private String email;

	@SerializedName("cancerSubType")
	private CancerSubType cancerSubType;

	@SerializedName("height")
	private double height;

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getCancerSite() {
		return cancerSite;
	}

	public void setCancerSite(String cancerSite) {
		this.cancerSite = cancerSite;
	}

	public String getDeceased() {
		return deceased;
	}

	public void setDeceased(String deceased) {
		this.deceased = deceased;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public boolean isRequestPending() {
		return requestPending;
	}

	public void setRequestPending(boolean requestPending) {
		this.requestPending = requestPending;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public boolean isDoNotContact() {
		return doNotContact;
	}

	public void setDoNotContact(boolean doNotContact) {
		this.doNotContact = doNotContact;
	}

	public String getPatientType() {
		return patientType;
	}

	public void setPatientType(String patientType) {
		this.patientType = patientType;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getCareCompanion() {
		return careCompanion;
	}

	public void setCareCompanion(String careCompanion) {
		this.careCompanion = careCompanion;
	}

	public String getAadharNumber() {
		return aadharNumber;
	}

	public void setAadharNumber(String aadharNumber) {
		this.aadharNumber = aadharNumber;
	}

	public String getDpLink() {
		return dpLink;
	}

	public void setDpLink(String dpLink) {
		this.dpLink = dpLink;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public int getRequestedUserId() {
		return requestedUserId;
	}

	public void setRequestedUserId(int requestedUserId) {
		this.requestedUserId = requestedUserId;
	}

	public String getCancerStage() {
		return cancerStage;
	}

	public void setCancerStage(String cancerStage) {
		this.cancerStage = cancerStage;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public CancerType getCancerType() {
		return cancerType;
	}

	public void setCancerType(CancerType cancerType) {
		this.cancerType = cancerType;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public CancerSubType getCancerSubType() {
		return cancerSubType;
	}

	public void setCancerSubType(CancerSubType cancerSubType) {
		this.cancerSubType = cancerSubType;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}
}