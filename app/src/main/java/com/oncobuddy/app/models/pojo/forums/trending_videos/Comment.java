package com.oncobuddy.app.models.pojo.forums.trending_videos;

import com.google.gson.annotations.SerializedName;

public class Comment{

	@SerializedName("publishedDateTime")
	private String publishedDateTime;

	@SerializedName("author")
	private Author author;

	@SerializedName("commentType")
	private String commentType;

	@SerializedName("anonymous")
	private boolean anonymous;

	@SerializedName("id")
	private int id;

	@SerializedName("content")
	private String content;

	public void setPublishedDateTime(String publishedDateTime){
		this.publishedDateTime = publishedDateTime;
	}

	public String getPublishedDateTime(){
		return publishedDateTime;
	}

	public void setAuthor(Author author){
		this.author = author;
	}

	public Author getAuthor(){
		return author;
	}

	public void setCommentType(String commentType){
		this.commentType = commentType;
	}

	public String getCommentType(){
		return commentType;
	}

	public void setAnonymous(boolean anonymous){
		this.anonymous = anonymous;
	}

	public boolean isAnonymous(){
		return anonymous;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setContent(String content){
		this.content = content;
	}

	public String getContent(){
		return content;
	}
}