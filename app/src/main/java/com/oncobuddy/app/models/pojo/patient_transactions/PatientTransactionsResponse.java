package com.oncobuddy.app.models.pojo.patient_transactions;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class PatientTransactionsResponse{

	@SerializedName("payLoad")
	private List<Transaction> payLoad;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public void setPayLoad(List<Transaction> payLoad){
		this.payLoad = payLoad;
	}

	public List<Transaction> getPayLoad(){
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