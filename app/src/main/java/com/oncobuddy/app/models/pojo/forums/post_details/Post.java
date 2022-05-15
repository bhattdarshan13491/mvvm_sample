package com.oncobuddy.app.models.pojo.forums.post_details;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Post{

	@SerializedName("subCategory")
	private Object subCategory;

	@SerializedName("postType")
	private String postType;

	@SerializedName("author")
	private Author author;

	@SerializedName("postStatus")
	private String postStatus;

	@SerializedName("anonymous")
	private Object anonymous;

	@SerializedName("publishedDate")
	private String publishedDate;

	@SerializedName("id")
	private int id;

	@SerializedName("title")
	private String title;

	@SerializedName("category")
	private Object category;

	@SerializedName("tags")
	private List<Object> tags;

	public void setSubCategory(Object subCategory){
		this.subCategory = subCategory;
	}

	public Object getSubCategory(){
		return subCategory;
	}

	public void setPostType(String postType){
		this.postType = postType;
	}

	public String getPostType(){
		return postType;
	}

	public void setAuthor(Author author){
		this.author = author;
	}

	public Author getAuthor(){
		return author;
	}

	public void setPostStatus(String postStatus){
		this.postStatus = postStatus;
	}

	public String getPostStatus(){
		return postStatus;
	}

	public void setAnonymous(Object anonymous){
		this.anonymous = anonymous;
	}

	public Object getAnonymous(){
		return anonymous;
	}

	public void setPublishedDate(String publishedDate){
		this.publishedDate = publishedDate;
	}

	public String getPublishedDate(){
		return publishedDate;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	public void setCategory(Object category){
		this.category = category;
	}

	public Object getCategory(){
		return category;
	}

	public void setTags(List<Object> tags){
		this.tags = tags;
	}

	public List<Object> getTags(){
		return tags;
	}
}