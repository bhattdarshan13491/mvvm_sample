package com.oncobuddy.app.models.pojo.forums;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class AddQuestionInput{

	@SerializedName("postType")
	private String postType;

	@SerializedName("questionType")
	private String questionType;

	@SerializedName("postStatus")
	private String postStatus;

	@SerializedName("anonymous")
	private boolean anonymous;

	@SerializedName("postQuestionDto")
	private PostQuestionDto postQuestionDto;

	@SerializedName("tags")
	private List<String> tags;

	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	public void setPostType(String postType){
		this.postType = postType;
	}

	public String getPostType(){
		return postType;
	}

	public void setPostStatus(String postStatus){
		this.postStatus = postStatus;
	}

	public String getPostStatus(){
		return postStatus;
	}

	public void setAnonymous(boolean anonymous){
		this.anonymous = anonymous;
	}

	public boolean isAnonymous(){
		return anonymous;
	}

	public void setPostQuestionDto(PostQuestionDto postQuestionDto){
		this.postQuestionDto = postQuestionDto;
	}

	public PostQuestionDto getPostQuestionDto(){
		return postQuestionDto;
	}

	public void setTags(List<String> tags){
		this.tags = tags;
	}

	public List<String> getTags(){
		return tags;
	}
}