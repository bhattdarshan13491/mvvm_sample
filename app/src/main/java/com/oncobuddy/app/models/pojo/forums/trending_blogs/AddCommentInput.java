package com.oncobuddy.app.models.pojo.forums.trending_blogs;

import com.google.gson.annotations.SerializedName;

public class AddCommentInput{

	@SerializedName("commentParentId")
	private int commentParentId;

	@SerializedName("commentType")
	private String commentType;

	@SerializedName("anonymous")
	private boolean anonymous;

	@SerializedName("content")
	private String content;

	public int getCommentParentId() {
		return commentParentId;
	}

	public void setCommentParentId(int commentParentId) {
		this.commentParentId = commentParentId;
	}

	public String getCommentType() {
		return commentType;
	}

	public void setCommentType(String commentType) {
		this.commentType = commentType;
	}

	public boolean isAnonymous() {
		return anonymous;
	}

	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}