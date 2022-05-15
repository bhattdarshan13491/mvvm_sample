package com.oncobuddy.app.models.pojo.forums.shorts;

import com.google.gson.annotations.SerializedName;

public class ShortDetails {

	@SerializedName("imageLink")
	private String imageLink;

	@SerializedName("deleted")
	private boolean deleted;

	@SerializedName("shortType")
	private String shortType;

	@SerializedName("readMoreLink")
	private String readMoreLink;

	@SerializedName("id")
	private int id;

	@SerializedName("lastModified")
	private String lastModified;

	@SerializedName("createdOn")
	private String createdOn;

	@SerializedName("content")
	private String content;

	public void setImageLink(String imageLink){
		this.imageLink = imageLink;
	}

	public String getImageLink(){
		return imageLink;
	}

	public void setDeleted(boolean deleted){
		this.deleted = deleted;
	}

	public boolean isDeleted(){
		return deleted;
	}

	public void setShortType(String shortType){
		this.shortType = shortType;
	}

	public String getShortType(){
		return shortType;
	}

	public void setReadMoreLink(String readMoreLink){
		this.readMoreLink = readMoreLink;
	}

	public String getReadMoreLink(){
		return readMoreLink;
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

	public void setContent(String content){
		this.content = content;
	}

	public String getContent(){
		return content;
	}
}