package com.oncobuddy.app.models.pojo.forums.comments;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class CommentsListResponse{

	@SerializedName("payLoad")
	private List<CommentItem> commentItemList;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public void setCommentItemList(List<CommentItem> commentItemList){
		this.commentItemList = commentItemList;
	}

	public List<CommentItem> getCommentItemList(){
		return commentItemList;
	}

	public void setSuccess(boolean success){
		this.success = success;
	}

	public boolean isSuccess(){
		return success;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}
}