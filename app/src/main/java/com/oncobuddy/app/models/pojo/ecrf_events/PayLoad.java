package com.oncobuddy.app.models.pojo.ecrf_events;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class PayLoad{

	@SerializedName("events")
	private List<EventsItem> events;

	public void setEvents(List<EventsItem> events){
		this.events = events;
	}

	public List<EventsItem> getEvents(){
		return events;
	}
}