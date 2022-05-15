package com.oncobuddy.app.models.pojo.ecrf_events;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class CancerSite{

	@SerializedName("cancerTypeList")
	private List<CancerTypeListItem> cancerTypeList;

	@SerializedName("name")
	private String name;

	@SerializedName("id")
	private int id;

	@SerializedName("lastModified")
	private String lastModified;

	@SerializedName("isActive")
	private boolean isActive;

	@SerializedName("createdOn")
	private String createdOn;

	public void setCancerTypeList(List<CancerTypeListItem> cancerTypeList){
		this.cancerTypeList = cancerTypeList;
	}

	public List<CancerTypeListItem> getCancerTypeList(){
		return cancerTypeList;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
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