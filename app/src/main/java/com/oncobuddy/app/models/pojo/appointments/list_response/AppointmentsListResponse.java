package com.oncobuddy.app.models.pojo.appointments.list_response;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppointmentsListResponse {

@SerializedName("success")
@Expose
private Boolean success;
@SerializedName("message")
@Expose
private String message;
@SerializedName("payLoad")
@Expose
private List<AppointmentDetails> payLoad = null;

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

public List<AppointmentDetails> getPayLoad() {
return payLoad;
}

public void setPayLoad(List<AppointmentDetails> payLoad) {
this.payLoad = payLoad;
}

}