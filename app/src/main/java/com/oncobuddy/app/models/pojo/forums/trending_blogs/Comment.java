package com.oncobuddy.app.models.pojo.forums.trending_blogs;

import com.google.gson.annotations.SerializedName;

public class Comment{

	@SerializedName("publishedDateTime")
	private Object publishedDateTime;

	@SerializedName("authorUserId")
	private int authorUserId;

	@SerializedName("commentType")
	private String commentType;

	@SerializedName("authorUserName")
	private String authorUserName;

	@SerializedName("id")
	private int id;

	@SerializedName("content")
	private String content;

	public Object getPublishedDateTime(){
		return publishedDateTime;
	}

	public int getAuthorUserId(){
		return authorUserId;
	}

	public String getCommentType(){
		return commentType;
	}

	public String getAuthorUserName(){
		return authorUserName;
	}

	public int getId(){
		return id;
	}

	public String getContent(){
		return content;
	}
}