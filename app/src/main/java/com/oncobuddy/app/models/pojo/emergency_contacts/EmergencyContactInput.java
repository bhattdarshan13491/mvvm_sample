package com.oncobuddy.app.models.pojo.emergency_contacts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EmergencyContactInput {
    //public String id;
    @SerializedName("fullName")
    @Expose
    public String fullName;

    @SerializedName("mobileCode")
    @Expose
    public String mobileCode;

    @SerializedName("phoneNumber")
    @Expose
    public String phoneNumber;

    @SerializedName("emailAddress")
    @Expose
    public String emailAddress;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobileCode() {
        return mobileCode;
    }

    public void setMobileCode(String mobileCode) {
        this.mobileCode = mobileCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

}