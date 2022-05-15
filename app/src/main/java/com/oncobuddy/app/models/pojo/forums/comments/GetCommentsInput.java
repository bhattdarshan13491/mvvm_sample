package com.oncobuddy.app.models.pojo.forums.comments;

import com.google.gson.annotations.SerializedName;

public class GetCommentsInput{

	@SerializedName("reqPageNo")
	private int reqPageNo;

	@SerializedName("pageSize")
	private int pageSize;

	@SerializedName("sortOrder")
	private String sortOrder;

	@SerializedName("sortBy")
	private String sortBy;

	public void setReqPageNo(int reqPageNo){
		this.reqPageNo = reqPageNo;
	}

	public int getReqPageNo(){
		return reqPageNo;
	}

	public void setSortOrder(String sortOrder){
		this.sortOrder = sortOrder;
	}

	public String getSortOrder(){
		return sortOrder;
	}

	public void setSortBy(String sortBy){
		this.sortBy = sortBy;
	}

	public String getSortBy(){
		return sortBy;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
}