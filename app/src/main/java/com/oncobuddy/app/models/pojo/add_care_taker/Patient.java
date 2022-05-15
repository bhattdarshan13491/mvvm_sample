package com.oncobuddy.app.models.pojo.add_care_taker;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Patient{

	@SerializedName("appUser")
	private AppUser appUser;

	@SerializedName("cancerSite")
	private Object cancerSite;

	@SerializedName("gender")
	private Object gender;

	@SerializedName("cancerStage")
	private Object cancerStage;

	@SerializedName("dateOfBirth")
	private Object dateOfBirth;

	@SerializedName("id")
	private int id;

	@SerializedName("lastModified")
	private String lastModified;

	@SerializedName("hospital")
	private List<Object> hospital;

	@SerializedName("cancerType")
	private CancerType cancerType;

	@SerializedName("isActive")
	private boolean isActive;

	@SerializedName("createdOn")
	private String createdOn;

	@SerializedName("cancerSubType")
	private CancerSubType cancerSubType;

	public void setAppUser(AppUser appUser){
		this.appUser = appUser;
	}

	public AppUser getAppUser(){
		return appUser;
	}

	public void setCancerSite(Object cancerSite){
		this.cancerSite = cancerSite;
	}

	public Object getCancerSite(){
		return cancerSite;
	}

	public void setGender(Object gender){
		this.gender = gender;
	}

	public Object getGender(){
		return gender;
	}

	public void setCancerStage(Object cancerStage){
		this.cancerStage = cancerStage;
	}

	public Object getCancerStage(){
		return cancerStage;
	}

	public void setDateOfBirth(Object dateOfBirth){
		this.dateOfBirth = dateOfBirth;
	}

	public Object getDateOfBirth(){
		return dateOfBirth;
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

	public void setHospital(List<Object> hospital){
		this.hospital = hospital;
	}

	public List<Object> getHospital(){
		return hospital;
	}

	public void setCancerType(CancerType cancerType){
		this.cancerType = cancerType;
	}

	public CancerType getCancerType(){
		return cancerType;
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

	public void setCancerSubType(CancerSubType cancerSubType){
		this.cancerSubType = cancerSubType;
	}

	public CancerSubType getCancerSubType(){
		return cancerSubType;
	}
}