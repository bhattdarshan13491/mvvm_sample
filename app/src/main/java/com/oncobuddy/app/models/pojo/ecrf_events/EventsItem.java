package com.oncobuddy.app.models.pojo.ecrf_events;

import com.google.gson.annotations.SerializedName;

public class EventsItem{

	@SerializedName("diseaseStatus")
	private String diseaseStatus;

	@SerializedName("treatmentHTDetails")
	private Object treatmentHTDetails;

	@SerializedName("eventId")
	private int eventId;

	@SerializedName("hospitalAndLocation")
	private String hospitalAndLocation;

	@SerializedName("treatmentType")
	private String treatmentType;

	@SerializedName("comments")
	private String comments;

	@SerializedName("treatmentDetails")
	private TreatmentDetails treatmentDetails;

	@SerializedName("details")
	private Details details;

	@SerializedName("eventType")
	private String eventType;

	@SerializedName("treatmentHtType")
	private Object treatmentHtType;

	@SerializedName("eventDate")
	private String eventDate;

	public void setDiseaseStatus(String diseaseStatus){
		this.diseaseStatus = diseaseStatus;
	}

	public String getDiseaseStatus(){
		return diseaseStatus;
	}

	public void setTreatmentHTDetails(Object treatmentHTDetails){
		this.treatmentHTDetails = treatmentHTDetails;
	}

	public Object getTreatmentHTDetails(){
		return treatmentHTDetails;
	}

	public void setEventId(int eventId){
		this.eventId = eventId;
	}

	public int getEventId(){
		return eventId;
	}

	public void setHospitalAndLocation(String hospitalAndLocation){
		this.hospitalAndLocation = hospitalAndLocation;
	}

	public String getHospitalAndLocation(){
		return hospitalAndLocation;
	}

	public void setTreatmentType(String treatmentType){
		this.treatmentType = treatmentType;
	}

	public String getTreatmentType(){
		return treatmentType;
	}

	public void setComments(String comments){
		this.comments = comments;
	}

	public String getComments(){
		return comments;
	}

	public void setTreatmentDetails(TreatmentDetails treatmentDetails){
		this.treatmentDetails = treatmentDetails;
	}

	public TreatmentDetails getTreatmentDetails(){
		return treatmentDetails;
	}

	public void setDetails(Details details){
		this.details = details;
	}

	public Details getDetails(){
		return details;
	}

	public void setEventType(String eventType){
		this.eventType = eventType;
	}

	public String getEventType(){
		return eventType;
	}

	public void setTreatmentHtType(Object treatmentHtType){
		this.treatmentHtType = treatmentHtType;
	}

	public Object getTreatmentHtType(){
		return treatmentHtType;
	}

	public void setEventDate(String eventDate){
		this.eventDate = eventDate;
	}

	public String getEventDate(){
		return eventDate;
	}
}