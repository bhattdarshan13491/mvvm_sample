package com.oncobuddy.app.models.pojo.forums.trending_videos;

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
	private Boolean anonymous = false;

	@SerializedName("publishedDate")
	private String publishedDate;

	@SerializedName("id")
	private int id;

	@SerializedName("title")
	private String title;

	@SerializedName("categories")
	private List<Category> categories;

	@SerializedName("subCategories")
	private List<Category> subCategories;


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

	public Boolean getAnonymous() {
		return anonymous;
	}

	public void setAnonymous(Boolean anonymous) {
		this.anonymous = anonymous;
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

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	public List<Category> getSubCategories() {
		return subCategories;
	}

	public void setSubCategories(List<Category> subCategories) {
		this.subCategories = subCategories;
	}

	public void setTags(List<Object> tags){
		this.tags = tags;
	}

	public List<Object> getTags(){
		return tags;
	}
}