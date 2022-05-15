package com.oncobuddy.app.models.pojo.forums.post_details;

import com.google.gson.annotations.SerializedName;

public class PayLoad{

	@SerializedName("post")
	private Post post;

	@SerializedName("content")
	private String content;

	@SerializedName("description")
	private String description;

	@SerializedName("videoLink")
	private String videoLink;

	public void setPost(Post post){
		this.post = post;
	}

	public Post getPost(){
		return post;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
	}

	public void setVideoLink(String videoLink){
		this.videoLink = videoLink;
	}

	public String getVideoLink(){
		return videoLink;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}