package com.oncobuddy.app.models.pojo.appointments;

import com.oncobuddy.app.models.pojo.appointments.list_response.AppointmentDetails;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddAppointmentResponse {

@SerializedName("success")
@Expose
private Boolean success;
@SerializedName("message")
@Expose
private String message;
@SerializedName("payLoad")
@Expose
private AppointmentDetails appointmentDetails;

public Boolean getSuccess() {
return success;
}

public void setSuccess(Boolean success) {
this.success = success;
}

public String getMessage() {
return message;
}

public void setMessage(String message) {
this.message = message;
}

    public AppointmentDetails getAppointmentDetails() {
        return appointmentDetails;
    }

    public void setAppointmentDetails(AppointmentDetails appointmentDetails) {
        this.appointmentDetails = appointmentDetails;
    }
}