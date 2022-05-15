package com.oncobuddy.app.models.pojo.ecrf_events;

import com.google.gson.annotations.SerializedName;

public class SurgeriesItem{

	@SerializedName("date")
	private String date;

	@SerializedName("id")
	private int id;

	@SerializedName("lastModified")
	private String lastModified;

	@SerializedName("createdOn")
	private String createdOn;

	@SerializedName("surgery")
	private Surgery surgery;

	public void setDate(String date){
		this.date = date;
	}

	public String getDate(){
		return date;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setLastModified(String lastModified){
		this.lastModified = lastModified;
	}

	public String getLastModified(){
		return lastModified;
	}

	public void setCreatedOn(String createdOn){
		this.createdOn = createdOn;
	}

	public String getCreatedOn(){
		return createdOn;
	}

	public void setSurgery(Surgery surgery){
		this.surgery = surgery;
	}

	public Surgery getSurgery(){
		return surgery;
	}
}