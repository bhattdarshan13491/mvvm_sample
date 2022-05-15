package com.oncobuddy.app.models.pojo.forums.trending_videos;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class TrendingVideosListResponse{

	@SerializedName("payLoad")
	private List<TrendingVideoDetails> payLoad;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public void setPayLoad(List<TrendingVideoDetails> payLoad){
		this.payLoad = payLoad;
	}

	public List<TrendingVideoDetails> getPayLoad(){
		return payLoad;
	}

	public void setSuccess(boolean success){
		this.success = success;
	}

	public boolean isSuccess(){
		return success;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}
}