package com.oncobuddy.app.models.pojo.login_response;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.oncobuddy.app.models.pojo.doctor_certification.Certification;
import com.oncobuddy.app.models.pojo.doctor_update.AppUser;

import java.io.Serializable;

public class LoginDetails implements Serializable {

    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("userId")
    @Expose
    private int userIdd;

    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;
    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("authToken")
    @Expose
    private String authToken;

    @SerializedName("cancerTypeId")
    @Expose
    private Long cancerTypeId;

    @SerializedName("cancerSubTypeId")
    @Expose
    private Long cancerSubTypeId;

    @SerializedName("dpLink")
    @Expose
    private String dpLink;

    @SerializedName("headline")
    @Expose
    private String headline;

    @SerializedName("gender")
    @Expose
    private String gender;

    @SerializedName("dateOfBirth")
    @Expose
    private String dateOfBirth;

    @SerializedName("aadharNumber")
    @Expose
    private String aadharNumber;



    @SerializedName("cancerStage")
    @Expose
    private String cancerStage;

    @SerializedName("height")
    @Expose
    private String height;

    @SerializedName("linkedCareCode")
    @Expose
    private String linkedCareCode;

    @SerializedName("weight")
    @Expose
    private String weight;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("profile")
    @Expose
    private Profile profile;

    @SerializedName("numberOfPatientsForADoctor")
    @Expose
    private String numberOfPatientsForADoctor;


    private Certification certification;

    @SerializedName("appUser")
    @Expose
    private AppUser appUser;

    @SerializedName("age")
    @Expose
    private int age;

    private Boolean isPatientConnected = false;

    public String getLinkedCareCode() {
        return linkedCareCode;
    }

    public void setLinkedCareCode(String linkedCareCode) {
        this.linkedCareCode = linkedCareCode;
    }

    public Boolean getPatientConnected() {
        return isPatientConnected;
    }

    public void setPatientConnected(Boolean patientConnected) {
        isPatientConnected = patientConnected;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAuthToken() {
        return "Bearer " + authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public int getUserIdd() {
        return userIdd;
    }

    public void setUserIdd(int userIdd) {
        this.userIdd = userIdd;
    }

    public Long getCancerTypeId() {
        return cancerTypeId;
    }

    public void setCancerTypeId(Long cancerTypeId) {
        this.cancerTypeId = cancerTypeId;
    }

    public Long getCancerSubTypeId() {
        return cancerSubTypeId;
    }

    public void setCancerSubTypeId(Long cancerSubTypeId) {
        this.cancerSubTypeId = cancerSubTypeId;
    }

    public String getDpLink() {
        return dpLink;
    }

    public void setDpLink(String dpLink) {
        this.dpLink = dpLink;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
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

    public String getCancerStage() {
        return cancerStage;
    }

    public void setCancerStage(String cancerStage) {
        this.cancerStage = cancerStage;
    }

    public Certification getCertification() {
        return certification;
    }

    public void setCertification(Certification certification) {
        this.certification = certification;
    }

    public String getCareCode() {
        return linkedCareCode;
    }

    public void setCareCode(String careCode) {
        this.linkedCareCode = careCode;
    }

    public String getNumberOfPatientsForADoctor() {
        return numberOfPatientsForADoctor;
    }

    public void setNumberOfPatientsForADoctor(String numberOfPatientsForADoctor) {
        this.numberOfPatientsForADoctor = numberOfPatientsForADoctor;
    }
}