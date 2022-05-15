package com.oncobuddy.app.models.pojo.forums;

import com.google.gson.annotations.SerializedName;

public class PostQuestionDto{

	/*
	* private String title;
private String question;
private String attachmentLink;
private String attachmentType;
private String questionType;
	* */

	@SerializedName("question")
	private String question;

	@SerializedName("title")
	private String title;

	@SerializedName("attachmentLink")
	private String attachmentLink;

	@SerializedName("attachmentType")
	private String attachmentType;

	@SerializedName("questionType")
	private String questionType;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setQuestion(String question){
		this.question = question;
	}

	public String getQuestion(){
		return question;
	}

	public String getAttachmentLink() {
		return attachmentLink;
	}

	public void setAttachmentLink(String attachmentLink) {
		this.attachmentLink = attachmentLink;
	}

	public String getAttachmentType() {
		return attachmentType;
	}

	public void setAttachmentType(String attachmentType) {
		this.attachmentType = attachmentType;
	}

	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}
}