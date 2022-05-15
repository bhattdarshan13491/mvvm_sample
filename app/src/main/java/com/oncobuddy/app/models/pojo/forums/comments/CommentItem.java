package com.oncobuddy.app.models.pojo.forums.comments;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class CommentItem {

	@SerializedName("numberOfReplies")
	private int numberOfReplies;

	@SerializedName("commentReplyList")
	private List<Object> commentReplyList;

	@SerializedName("numberOfLikes")
	private int numberOfLikes;

	@SerializedName("comment")
	private Comment comment;

	@SerializedName("liked")
	private Boolean liked;



	public void setNumberOfReplies(int numberOfReplies){
		this.numberOfReplies = numberOfReplies;
	}

	public int getNumberOfReplies(){
		return numberOfReplies;
	}

	public void setCommentReplyList(List<Object> commentReplyList){
		this.commentReplyList = commentReplyList;
	}

	public List<Object> getCommentReplyList(){
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

	public Boolean getLiked() {
		return liked;
	}

	public void setLiked(Boolean liked) {
		this.liked = liked;
	}
}