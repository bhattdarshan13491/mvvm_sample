package com.oncobuddy.app.models.pojo.profile;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CancerType implements Serializable {

	@SerializedName("name")
	private String name;

	@SerializedName("id")
	private int id;

	public String getName(){
		return name;
	}

	public int getId(){
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setId(int id) {
		this.id = id;
	}
}