package com.oncobuddy.app.models.pojo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AddPostUserOption implements Serializable {

	@SerializedName("name")
	private String name;

	@SerializedName("id")
	private int id;

	@SerializedName("url")
	private String url;

	public String getName(){
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}