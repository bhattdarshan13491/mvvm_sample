package com.oncobuddy.app.models.pojo.patient_list;

import com.oncobuddy.app.models.pojo.profile.CancerType;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PatientDetails implements Serializable {


    @SerializedName("firstName")
    @Expose
    private String firstName;

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;

    @SerializedName("dpLink")
    @Expose
    private String dpLink;

    @SerializedName("height")
    @Expose
    private String height;

    @SerializedName("weight")
    @Expose
    private String weight;

    @SerializedName("aadharNumber")
    private String aadharNumber;

    @SerializedName("dob")
    @Expose
    private String dob;

    @SerializedName("gender")
    @Expose
    private String gender;

    @SerializedName("requestPending")
    @Expose
    private boolean requestPending;

    @SerializedName("requestedUserId")
    @Expose
    private int requestedUserId;

    @SerializedName("cancerType")
    @Expose
    private CancerType cancerType;

    @SerializedName("cancerSubType")
    @Expose
    private CancerType cancerSubType;

    @SerializedName("cancerStage")
    @Expose
    private String cancerStage;

    public String getCancerStage() {
        return cancerStage;
    }

    public void setCancerStage(String cancerStage) {
        this.cancerStage = cancerStage;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getDpLink() {
        return dpLink;
    }

    public void setDpLink(String dpLink) {
        this.dpLink = dpLink;
    }

    public boolean isRequestPending() {
        return requestPending;
    }

    public void setRequestPending(boolean requestPending) {
        this.requestPending = requestPending;
    }

    public int getRequestedUserId() {
        return requestedUserId;
    }

    public void setRequestedUserId(int requestedUserId) {
        this.requestedUserId = requestedUserId;
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

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }
}