package com.oncobuddy.app.models.pojo.ecrf_events;

import com.google.gson.annotations.SerializedName;

public class CoMorbiditiesItem{

	@SerializedName("diseaseName")
	private String diseaseName;

	@SerializedName("comments")
	private String comments;

	@SerializedName("id")
	private int id;

	@SerializedName("lastModified")
	private String lastModified;

	@SerializedName("createdOn")
	private String createdOn;

	public void setDiseaseName(String diseaseName){
		this.diseaseName = diseaseName;
	}

	public String getDiseaseName(){
		return diseaseName;
	}

	public void setComments(String comments){
		this.comments = comments;
	}

	public String getComments(){
		return comments;
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