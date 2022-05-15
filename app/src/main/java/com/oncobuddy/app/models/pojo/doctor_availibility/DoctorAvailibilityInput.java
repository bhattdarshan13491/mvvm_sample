package com.oncobuddy.app.models.pojo.doctor_availibility;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class DoctorAvailibilityInput{

	@SerializedName("callDuration")
	private int callDuration;

	@SerializedName("slots")
	private List<SlotsItem> slots;

	@SerializedName("available")
	private boolean available;

	@SerializedName("weekday")
	private String weekday;

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

	public void setAvailable(boolean available){
		this.available = available;
	}

	public boolean isAvailable(){
		return available;
	}

	public void setWeekday(String weekday){
		this.weekday = weekday;
	}

	public String getWeekday(){
		return weekday;
	}
}