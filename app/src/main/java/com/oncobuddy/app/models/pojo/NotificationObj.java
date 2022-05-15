package com.oncobuddy.app.models.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationObj {

@SerializedName("callerUserId")
@Expose
private Integer callerUserId;
@SerializedName("callerName")
@Expose
private String callerName;
@SerializedName("callEvent")
@Expose
private String callEvent;

    @SerializedName("appointmentId")
    @Expose
    private String appointmentId;


public Integer getCallerUserId() {
return callerUserId;
}

public void setCallerUserId(Integer callerUserId) {
this.callerUserId = callerUserId;
}

public String getCallerName() {
return callerName;
}

public void setCallerName(String callerName) {
this.callerName = callerName;
}

public String getCallEvent() {
return callEvent;
}

public void setCallEvent(String callEvent) {
this.callEvent = callEvent;
}

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }
}