package com.oncobuddy.app.models.pojo.extract_doc_response;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class DateExtract{

	@SerializedName("date")
	private List<DateItem> date;

	@SerializedName("tags")
	private Tags tags;

	public void setDate(List<DateItem> date){
		this.date = date;
	}

	public List<DateItem> getDate(){
		return date;
	}

	public void setTags(Tags tags){
		this.tags = tags;
	}

	public Tags getTags(){
		return tags;
	}
}