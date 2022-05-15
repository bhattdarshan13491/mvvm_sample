package com.oncobuddy.app.models.pojo.initial_login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VerifyLoginOtpInput {

@SerializedName("deviceType")
@Expose
private String deviceType;
@SerializedName("mobileNumber")
@Expose
private String mobileNumber;
@SerializedName("otp")
@Expose
private String otp;
@SerializedName("token")
@Expose
private String token;

public String getDeviceType() {
return deviceType;
}

public void setDeviceType(String deviceType) {
this.deviceType = deviceType;
}

public String getMobileNumber() {
return mobileNumber;
}

public void setMobileNumber(String mobileNumber) {
this.mobileNumber = mobileNumber;
}

public String getOtp() {
return otp;
}

public void setOtp(String otp) {
this.otp = otp;
}

public String getToken() {
return token;
}

public void setToken(String token) {
this.token = token;
}

}