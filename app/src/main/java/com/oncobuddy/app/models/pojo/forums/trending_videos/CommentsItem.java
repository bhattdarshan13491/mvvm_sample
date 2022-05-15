package com.oncobuddy.app.models.pojo.forums.trending_videos;

import com.google.gson.annotations.SerializedName;

public class CommentsItem{

	@SerializedName("numberOfReplies")
	private Object numberOfReplies;

	@SerializedName("commentReplyList")
	private Object commentReplyList;

	@SerializedName("numberOfLikes")
	private int numberOfLikes;

	@SerializedName("comment")
	private Comment comment;

	@SerializedName("liked")
	private boolean liked;

	public void setNumberOfReplies(Object numberOfReplies){
		this.numberOfReplies = numberOfReplies;
	}

	public Object getNumberOfReplies(){
		return numberOfReplies;
	}

	public void setCommentReplyList(Object commentReplyList){
		this.commentReplyList = commentReplyList;
	}

	public Object getCommentReplyList(){
		return commentReplyList;
	}

	public void setNumberOfLikes(int numberOfLikes){
		this.numberOfLikes = numberOfLikes;
	}

	public int getNumberOfLikes(){
		return numberOfLikes;
	}

	public void setComment(Comment comment){
		this.comment = comment;
	}

	public Comment getComment(){
		return comment;
	}

	public void setLiked(boolean liked){
		this.liked = liked;
	}

	public boolean isLiked(){
		return liked;
	}
}