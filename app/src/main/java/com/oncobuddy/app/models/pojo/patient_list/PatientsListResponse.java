package com.oncobuddy.app.models.pojo.patient_list;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PatientsListResponse {

@SerializedName("success")
@Expose
private Boolean success;
@SerializedName("message")
@Expose
private String message;
@SerializedName("payLoad")
@Expose
private List<PatientDetails> payLoad = null;

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

public List<PatientDetails> getPayLoad() {
return payLoad;
}

public void setPayLoad(List<PatientDetails> payLoad) {
this.payLoad = payLoad;
}

}