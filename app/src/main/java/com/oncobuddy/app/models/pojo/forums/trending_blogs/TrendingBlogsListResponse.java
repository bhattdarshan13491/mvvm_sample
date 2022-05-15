package com.oncobuddy.app.models.pojo.forums.trending_blogs;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class TrendingBlogsListResponse{

	@SerializedName("payLoad")
	private List<TrendingBlogDetails> payLoad;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public List<TrendingBlogDetails> getPayLoad(){
		return payLoad;
	}

	public boolean isSuccess(){
		return success;
	}

	public String getMessage(){
		return message;
	}
}