package com.oncobuddy.app.models.pojo.doctor_profile.doctor_availibility;

import com.google.gson.annotations.SerializedName;
import com.oncobuddy.app.models.pojo.doctor_availibility.SlotsItem;

import java.util.List;

public class Weekday {

	@SerializedName("callDuration")
	private int callDuration;

	@SerializedName("slots")
	private List<SlotsItem> slots;

	@SerializedName("doctorId")
	private int doctorId;

	@SerializedName("weekday")
	private String weekday;

	@SerializedName("available")
	private boolean available;

	@SerializedName("id")
	private int id;

	@SerializedName("lastModified")
	private String lastModified;

	@SerializedName("createdOn")
	private String createdOn;

	public void setCallDuration(int callDuration){
		this.callDuration = callDuration;
	}

	public int getCallDuration(){
		return callDuration;
	}

	public void setSlots(List<SlotsItem> slots){
		this.slots = slots;
	}

	public List<SlotsItem> getSlots(){
		return slots;
	}

	public void setDoctorId(int doctorId){
		this.doctorId = doctorId;
	}

	public int getDoctorId(){
		return doctorId;
	}

	public void setWeekday(String weekday){
		this.weekday = weekday;
	}

	public String getWeekday(){
		return weekday;
	}

	public void setAvailable(boolean available){
		this.available = available;
	}

	public boolean isAvailable(){
		return available;
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