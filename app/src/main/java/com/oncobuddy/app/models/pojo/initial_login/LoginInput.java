package com.oncobuddy.app.models.pojo.initial_login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginInput {

@SerializedName("mobileNumber")
@Expose
private String mobileNumber;

public String getMobileNumber() {
return mobileNumber;
}

public void setMobileNumber(String mobileNumber) {
this.mobileNumber = mobileNumber;
}

}