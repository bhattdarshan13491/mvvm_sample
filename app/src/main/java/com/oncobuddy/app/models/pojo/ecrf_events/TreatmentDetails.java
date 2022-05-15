package com.oncobuddy.app.models.pojo.ecrf_events;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class TreatmentDetails{

	@SerializedName("surgeries")
	private List<SurgeriesItem> surgeries;

	@SerializedName("eventDetailsTreatment")
	private EventDetailsTreatment eventDetailsTreatment;

	@SerializedName("id")
	private int id;

	@SerializedName("lastModified")
	private String lastModified;

	@SerializedName("createdOn")
	private String createdOn;

	public void setSurgeries(List<SurgeriesItem> surgeries){
		this.surgeries = surgeries;
	}

	public List<SurgeriesItem> getSurgeries(){
		return surgeries;
	}

	public void setEventDetailsTreatment(EventDetailsTreatment eventDetailsTreatment){
		this.eventDetailsTreatment = eventDetailsTreatment;
	}

	public EventDetailsTreatment getEventDetailsTreatment(){
		return eventDetailsTreatment;
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