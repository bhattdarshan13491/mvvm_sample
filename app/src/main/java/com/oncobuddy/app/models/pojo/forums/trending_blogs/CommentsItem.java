package com.oncobuddy.app.models.pojo.forums.trending_blogs;

import com.google.gson.annotations.SerializedName;

public class CommentsItem{

	@SerializedName("numberOfLikes")
	private int numberOfLikes;

	@SerializedName("comment")
	private Comment comment;

	public int getNumberOfLikes(){
		return numberOfLikes;
	}

	public Comment getComment(){
		return comment;
	}
}