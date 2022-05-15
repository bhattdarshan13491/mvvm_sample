package com.oncobuddy.app.models.pojo.ecrf_events;

import com.google.gson.annotations.SerializedName;

public class Details{

	@SerializedName("treatmentType")
	private String treatmentType;

	@SerializedName("eventMaster")
	private EventMaster eventMaster;

	@SerializedName("id")
	private int id;

	@SerializedName("lastModified")
	private String lastModified;

	@SerializedName("createdOn")
	private String createdOn;

	public void setTreatmentType(String treatmentType){
		this.treatmentType = treatmentType;
	}

	public String getTreatmentType(){
		return treatmentType;
	}

	public void setEventMaster(EventMaster eventMaster){
		this.eventMaster = eventMaster;
	}

	public EventMaster getEventMaster(){
		return eventMaster;
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
}