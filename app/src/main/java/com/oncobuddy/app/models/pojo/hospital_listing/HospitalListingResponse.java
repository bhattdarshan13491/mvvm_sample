package com.oncobuddy.app.models.pojo.hospital_listing;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HospitalListingResponse {

@Expose
private Boolean success;
@SerializedName("message")
@Expose
private String message;
@SerializedName("payLoad")
@Expose
private List<HospitalDetails> payLoad = null;

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

public List<HospitalDetails> getPayLoad() {
return payLoad;
}

public void setPayLoad(List<HospitalDetails> payLoad) {
this.payLoad = payLoad;
}

}