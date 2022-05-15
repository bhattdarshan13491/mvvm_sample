package com.oncobuddy.app.models.pojo.twilio_token;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenResponse {

@SerializedName("success")
@Expose
private Boolean success;
@SerializedName("message")
@Expose
private String message;
@SerializedName("payLoad")
@Expose
private TokenDetails payLoad;

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

public TokenDetails getPayLoad() {
return payLoad;
}

public void setPayLoad(TokenDetails payLoad) {
this.payLoad = payLoad;
}

}