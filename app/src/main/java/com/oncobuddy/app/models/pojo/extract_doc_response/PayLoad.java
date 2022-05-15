package com.oncobuddy.app.models.pojo.extract_doc_response;

import com.google.gson.annotations.SerializedName;

public class PayLoad{

	@SerializedName("bucketName")
	private String bucketName;

	@SerializedName("filename")
	private String filename;

	@SerializedName("documentUrl")
	private String documentUrl;

	@SerializedName("dateExtract")
	private DateExtract dateExtract;

	public void setBucketName(String bucketName){
		this.bucketName = bucketName;
	}

	public String getBucketName(){
		return bucketName;
	}

	public void setFilename(String filename){
		this.filename = filename;
	}

	public String getFilename(){
		return filename;
	}

	public void setDocumentUrl(String documentUrl){
		this.documentUrl = documentUrl;
	}

	public String getDocumentUrl(){
		return documentUrl;
	}

	public void setDateExtract(DateExtract dateExtract){
		this.dateExtract = dateExtract;
	}

	public DateExtract getDateExtract(){
		return dateExtract;
	}
}