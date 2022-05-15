package com.oncobuddy.app.models.pojo.chats.chat_messages;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ChatGroup{

	@SerializedName("expired")
	private boolean expired;

	@SerializedName("members")
	private List<MembersItem> members;

	@SerializedName("id")
	private int id;

	@SerializedName("lastModified")
	private String lastModified;

	@SerializedName("createdOn")
	private String createdOn;

	public void setExpired(boolean expired){
		this.expired = expired;
	}

	public boolean isExpired(){
		return expired;
	}

	public void setMembers(List<MembersItem> members){
		this.members = members;
	}

	public List<MembersItem> getMembers(){
		return members;
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