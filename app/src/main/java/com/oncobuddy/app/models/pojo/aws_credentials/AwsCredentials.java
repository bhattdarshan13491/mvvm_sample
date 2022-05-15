package com.oncobuddy.app.models.pojo.aws_credentials;

import com.google.gson.annotations.SerializedName;

public class AwsCredentials {

	@SerializedName("accessKey")
	private String accessKey;

	@SerializedName("secret")
	private String secret;

	public void setAccessKey(String accessKey){
		this.accessKey = accessKey;
	}

	public String getAccessKey(){
		return accessKey;
	}

	public void setSecret(String secret){
		this.secret = secret;
	}

	public String getSecret(){
		return secret;
	}
}