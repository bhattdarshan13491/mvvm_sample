package com.oncobuddy.app.models.pojo.forums.comments;

import com.google.gson.annotations.SerializedName;

public class Comment{

	@SerializedName("publishedDateTime")
	private String publishedDateTime;

	@SerializedName("authorUserId")
	private int authorUserId;

	@SerializedName("commentType")
	private String commentType;

	@SerializedName("authorUserName")
	private String authorUserName;

	@SerializedName("author")
	private Author author;

	@SerializedName("id")
	private int id;

	@SerializedName("content")
	private String content;

	@SerializedName(("anonymous"))
	private Boolean anonymous;

	public void setPublishedDateTime(String publishedDateTime){
		this.publishedDateTime = publishedDateTime;
	}

	public String getPublishedDateTime(){
		return publishedDateTime;
	}

	public void setAuthorUserId(int authorUserId){
		this.authorUserId = authorUserId;
	}

	public int getAuthorUserId(){
		return authorUserId;
	}

	public void setCommentType(String commentType){
		this.commentType = commentType;
	}

	public String getCommentType(){
		return commentType;
	}

	public void setAuthorUserName(String authorUserName){
		this.authorUserName = authorUserName;
	}

	public String getAuthorUserName(){
		return authorUserName;
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

	public Boolean getAnonymous() {
		return anonymous;
	}

	public void setAnonymous(Boolean anonymous) {
		this.anonymous = anonymous;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}
}