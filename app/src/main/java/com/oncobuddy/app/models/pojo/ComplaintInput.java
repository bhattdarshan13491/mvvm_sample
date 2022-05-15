package com.oncobuddy.app.models.pojo;

import com.google.gson.annotations.SerializedName;

public class ComplaintInput{

	@SerializedName("notes")
	private String notes;

	@SerializedName("title")
	private String title;

	public void setNotes(String notes){
		this.notes = notes;
	}

	public String getNotes(){
		return notes;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}
}