package com.oncobuddy.app.models.pojo.login_response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.oncobuddy.app.models.pojo.hospital_listing.Locations;

public class Profile {

    @SerializedName("cancerType")
    @Expose
    private CancerType cancerType;

    @SerializedName("cancerSubType")
    @Expose
    private CancerType cancerSubType;

    @SerializedName("cancerStage")
    @Expose
    private String cancerStage;

    @SerializedName("profileCompletionLevel")
    @Expose
    private int profileCompletionLevel;

    @SerializedName("aadharNumber")
    @Expose
    private String aadharNumber;

    @SerializedName("height")
    @Expose
    private String height;

    @SerializedName("weight")
    @Expose
    private String weight;

    @SerializedName("careCode")
    @Expose
    private String careCode;

    @SerializedName("hospital")
    @Expose
    private Hospital hospital;

    @SerializedName("hospitalLocation")
    @Expose
    private Locations selectedLocation;

    @SerializedName("dateOfBirth")
    @Expose
    private String dateOfBirth;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("bankDetailsAdded")
    @Expose
    private String bankDetailsAdded;

    @SerializedName("patientType")
    @Expose
    private String patientType;

    //Doctor Specific fields

  /*  @SerializedName("consultationCategory")
    @Expose
    private String consultationCategory;
*/

    public int getProfileCompletionLevel() {
        return profileCompletionLevel;
    }

    public void setProfileCompletionLevel(int profileCompletionLevel) {
        this.profileCompletionLevel = profileCompletionLevel;
    }

    public String getCancerStage() {
        return cancerStage;
    }

    public void setCancerStage(String cancerStage) {
        this.cancerStage = cancerStage;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    @SerializedName("designation")
    @Expose
    private String designation;

    @SerializedName("currentLocation")
    @Expose
    private String currentLocation;

    @SerializedName("consultationFee")
    @Expose
    private Double consultationFee;

    @SerializedName("specialization")
    @Expose
    private String specialization;

    @SerializedName("experience")
    @Expose
    private String experience;

    @SerializedName("gender")
    @Expose
    private String gender;

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public Double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(Double consultationFee) {
        this.consultationFee = consultationFee;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public CancerType getCancerType() {
        return cancerType;
    }

    public void setCancerType(CancerType cancerType) {
        this.cancerType = cancerType;
    }

    public CancerType getCancerSubType() {
        return cancerSubType;
    }

    public void setCancerSubType(CancerType cancerSubType) {
        this.cancerSubType = cancerSubType;


    }

    public Hospital getHospital() {
        return hospital;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    public Locations getSelectedLocation() {
        return selectedLocation;
    }

    public void setSelectedLocation(Locations selectedLocation) {
        this.selectedLocation = selectedLocation;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getCareCode() {
        return careCode;
    }

    public void setCareCode(String careCode) {
        this.careCode = careCode;
    }

    public String getBankDetailsAdded() {
        return bankDetailsAdded;
    }

    public void setBankDetailsAdded(String bankDetailsAdded) {
        this.bankDetailsAdded = bankDetailsAdded;
    }

    public String getPatientType() {
        return patientType;
    }

    public void setPatientType(String patientType) {
        this.patientType = patientType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}