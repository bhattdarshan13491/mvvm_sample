package com.oncobuddy.app.models.pojo.login_response;

import com.oncobuddy.app.models.pojo.BaseResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginResponse extends BaseResponse {


@SerializedName("payLoad")
@Expose
private LoginDetails payLoad = null;

public LoginDetails getPayLoad() {
return payLoad;
}

public void setPayLoad(LoginDetails payLoad) {
this.payLoad = payLoad;
}

}
