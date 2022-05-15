package com.oncobuddy.app.models.pojo;

import com.google.gson.annotations.SerializedName;

public class ForumNotification{

	@SerializedName("postType")
	private String postType;

	@SerializedName("postId")
	private int postId;

	@SerializedName("notificationType")
	private String notificationType;

	@SerializedName("title")
	private String title;

	public void setPostType(String postType){
		this.postType = postType;
	}

	public String getPostType(){
		return postType;
	}

	public void setPostId(int postId){
		this.postId = postId;
	}

	public int getPostId(){
		return postId;
	}

	public void setNotificationType(String notificationType){
		this.notificationType = notificationType;
	}

	public String getNotificationType(){
		return notificationType;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}
}