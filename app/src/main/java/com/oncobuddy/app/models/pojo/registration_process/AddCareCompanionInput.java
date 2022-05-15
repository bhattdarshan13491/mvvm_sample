package com.oncobuddy.app.models.pojo.registration_process;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddCareCompanionInput {

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("mobileNumber")
    @Expose
    public String mobileNumber;

    @SerializedName("relationship")
    @Expose
    public String relationship;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }
}