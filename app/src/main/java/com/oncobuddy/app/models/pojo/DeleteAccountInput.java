package com.oncobuddy.app.models.pojo;

import com.google.gson.annotations.SerializedName;

public class DeleteAccountInput{

	@SerializedName("reason")
	private String reason;

	@SerializedName("title")
	private String title;

	public void setReason(String reason){
		this.reason = reason;
	}

	public String getReason(){
		return reason;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}
}