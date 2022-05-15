package com.oncobuddy.app.models.pojo.otp_process;

import java.io.Serializable;

public class GenerateOTPInput implements Serializable {

    String phoneNumber;
    String otpPurpose;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOtpPurpose() {
        return otpPurpose;
    }

    public void setOtpPurpose(String otpPurpose) {
        this.otpPurpose = otpPurpose;
    }
}
