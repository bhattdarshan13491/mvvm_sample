package com.oncobuddy.app.models.pojo;

import com.google.gson.annotations.SerializedName;

public class SearchQueryInput {

	@SerializedName("reqPageNo")
	private int reqPageNo;

	@SerializedName("pageSize")
	private int pageSize;

	@SerializedName("searchKey")
	private String searchKey;

	@SerializedName("sortOrder")
	private String sortOrder;

	@SerializedName("sortBy")
	private String sortBy;

	public int getReqPageNo() {
		return reqPageNo;
	}

	public void setReqPageNo(int reqPageNo) {
		this.reqPageNo = reqPageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getSearchKey() {
		return searchKey;
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}
}