package com.oncobuddy.app.models.pojo.forums.trending_blogs;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Post{

	@SerializedName("subCategory")
	private Object subCategory;

	@SerializedName("authorUserName")
	private String authorUserName;

	@SerializedName("postType")
	private String postType;

	@SerializedName("postStatus")
	private String postStatus;

	@SerializedName("anonymous")
	private boolean anonymous;

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

	public Object getSubCategory(){
		return subCategory;
	}

	public String getPostType(){
		return postType;
	}

	public String getPostStatus(){
		return postStatus;
	}

	public boolean isAnonymous(){
		return anonymous;
	}

	public String getPublishedDate(){
		return publishedDate;
	}

	public int getId(){
		return id;
	}

	public String getTitle(){
		return title;
	}

	public Object getCategory(){
		return category;
	}

	public List<Object> getTags(){
		return tags;
	}

	public String getAuthorUserName() {
		return authorUserName;
	}

	public void setAuthorUserName(String authorUserName) {
		this.authorUserName = authorUserName;
	}
}