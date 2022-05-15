package com.oncobuddy.app.models.pojo.ecrf_events;

import com.google.gson.annotations.SerializedName;

public class EventMaster{

	@SerializedName("diseaseStatus")
	private String diseaseStatus;

	@SerializedName("comments")
	private String comments;

	@SerializedName("hospitalNameAndLocation")
	private String hospitalNameAndLocation;

	@SerializedName("patient")
	private Patient patient;

	@SerializedName("id")
	private int id;

	@SerializedName("lastModified")
	private String lastModified;

	@SerializedName("eventType")
	private String eventType;

	@SerializedName("isActive")
	private boolean isActive;

	@SerializedName("createdOn")
	private String createdOn;

	public void setDiseaseStatus(String diseaseStatus){
		this.diseaseStatus = diseaseStatus;
	}

	public String getDiseaseStatus(){
		return diseaseStatus;
	}

	public void setComments(String comments){
		this.comments = comments;
	}

	public String getComments(){
		return comments;
	}

	public void setHospitalNameAndLocation(String hospitalNameAndLocation){
		this.hospitalNameAndLocation = hospitalNameAndLocation;
	}

	public String getHospitalNameAndLocation(){
		return hospitalNameAndLocation;
	}

	public void setPatient(Patient patient){
		this.patient = patient;
	}

	public Patient getPatient(){
		return patient;
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

	public void setEventType(String eventType){
		this.eventType = eventType;
	}

	public String getEventType(){
		return eventType;
	}

	public void setIsActive(boolean isActive){
		this.isActive = isActive;
	}

	public boolean isIsActive(){
		return isActive;
	}

	public void setCreatedOn(String createdOn){
		this.createdOn = createdOn;
	}

	public String getCreatedOn(){
		return createdOn;
	}
}