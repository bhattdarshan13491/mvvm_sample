package com.oncobuddy.app.models.pojo.genetic_report;

import com.google.gson.annotations.SerializedName;

public class TestStatusesItem{

	@SerializedName("name")
	private String name;

	@SerializedName("lastComment")
	private String lastComment;

	@SerializedName("status")
	private String status;

	public String getName(){
		return name;
	}

	public String getLastComment(){
		return lastComment;
	}

	public String getStatus(){
		return status;
	}
}