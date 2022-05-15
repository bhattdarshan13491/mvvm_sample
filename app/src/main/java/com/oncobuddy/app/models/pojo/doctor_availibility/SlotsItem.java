package com.oncobuddy.app.models.pojo.doctor_availibility;

import com.google.gson.annotations.SerializedName;

public class SlotsItem{

	@SerializedName("id")
	private int id;

	@SerializedName("startHour")
	private int startHour;

	@SerializedName("startMinutes")
	private int startMinutes;

	@SerializedName("endHour")
	private int endHour;

	@SerializedName("endMinutes")
	private int endMinutes;

	private String slotNumber;

	public SlotsItem(int startHour, int startMinutes, int endHour, int endMinutes) {
		this.startHour = startHour;
		this.startMinutes = startMinutes;
		this.endHour = endHour;
		this.endMinutes = endMinutes;
	}

	public SlotsItem() {
	}

	public void setEndHour(int endHour){
		this.endHour = endHour;
	}

	public int getEndHour(){
		return endHour;
	}

	public void setStartHour(int startHour){
		this.startHour = startHour;
	}

	public int getStartHour(){
		return startHour;
	}

	public void setStartMinutes(int startMinutes){
		this.startMinutes = startMinutes;
	}

	public int getStartMinutes(){
		return startMinutes;
	}

	public void setEndMinutes(int endMinutes){
		this.endMinutes = endMinutes;
	}

	public int getEndMinutes(){
		return endMinutes;
	}

	public String getSlotNumber() {
		return slotNumber;
	}

	public void setSlotNumber(String slotNumber) {
		this.slotNumber = slotNumber;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}