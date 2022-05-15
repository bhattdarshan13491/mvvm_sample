package com.oncobuddy.app.models.pojo.genetic_report_response;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Report {

	@SerializedName("ocrExtract")
	private Object ocrExtract;

	@SerializedName("patientId")
	private int patientId;

	@SerializedName("recordType")
	private String recordType;

	@SerializedName("link")
	private String link;

	@SerializedName("hospitalName")
	private Object hospitalName;

	@SerializedName("title")
	private Object title;

	@SerializedName("createdOn")
	private String createdOn;

	@SerializedName("uploadedUserName")
	private String uploadedUserName;

	@SerializedName("tags")
	private List<String> tags;

	@SerializedName("deleted")
	private boolean deleted;

	@SerializedName("size")
	private double size;

	@SerializedName("uploadedUserId")
	private int uploadedUserId;

	@SerializedName("recordDate")
	private String recordDate;

	@SerializedName("id")
	private int id;

	@SerializedName("lastModified")
	private String lastModified;

	@SerializedName("categories")
	private List<String> categories;

	@SerializedName("uploadedBy")
	private Object uploadedBy;

	public Object getOcrExtract(){
		return ocrExtract;
	}

	public int getPatientId(){
		return patientId;
	}

	public String getRecordType(){
		return recordType;
	}

	public String getLink(){
		return link;
	}

	public Object getHospitalName(){
		return hospitalName;
	}

	public Object getTitle(){
		return title;
	}

	public String getCreatedOn(){
		return createdOn;
	}

	public String getUploadedUserName(){
		return uploadedUserName;
	}

	public List<String> getTags(){
		return tags;
	}

	public boolean isDeleted(){
		return deleted;
	}

	public double getSize(){
		return size;
	}

	public int getUploadedUserId(){
		return uploadedUserId;
	}

	public String getRecordDate(){
		return recordDate;
	}

	public int getId(){
		return id;
	}

	public String getLastModified(){
		return lastModified;
	}

	public List<String> getCategories(){
		return categories;
	}

	public Object getUploadedBy(){
		return uploadedBy;
	}
}