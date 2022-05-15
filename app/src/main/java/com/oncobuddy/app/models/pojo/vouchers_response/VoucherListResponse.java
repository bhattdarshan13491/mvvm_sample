package com.oncobuddy.app.models.pojo.vouchers_response;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class VoucherListResponse{

	@SerializedName("payLoad")
	private List<VoucherDetails> payLoad;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public void setPayLoad(List<VoucherDetails> payLoad){
		this.payLoad = payLoad;
	}

	public List<VoucherDetails> getPayLoad(){
		return payLoad;
	}

	public void setSuccess(boolean success){
		this.success = success;
	}

	public boolean isSuccess(){
		return success;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}
}