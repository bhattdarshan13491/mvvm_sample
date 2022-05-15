package com.oncobuddy.app.models.pojo.aws_credentials;

import com.google.gson.annotations.SerializedName;

public class AwsCredentialsResponse{

	@SerializedName("payLoad")
	private AwsCredentials awsCredentials;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public void setPayLoad(AwsCredentials awsCredentials){
		this.awsCredentials = awsCredentials;
	}

	public AwsCredentials getPayLoad(){
		return awsCredentials;
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