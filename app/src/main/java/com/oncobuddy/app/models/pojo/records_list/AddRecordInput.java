package com.oncobuddy.app.models.pojo.records_list;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AddRecordInput {

    @SerializedName("hospitalName")
    @Expose
    private String hospitalName;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("patientId")
    @Expose
    private Integer patientId;
    @SerializedName("recordDate")
    @Expose
    private String recordDate;
    @SerializedName("recordType")
    @Expose
    private String recordType;
    @SerializedName("size")
    @Expose
    private Integer size;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("extractedTags")
    @Expose
    private List<String> extractedTags = null;

    @SerializedName("tags")
    @Expose
    private List<String> tags = null;

    @SerializedName("categories")
    @Expose
    private List<String> categories = null;

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getExtractedTags() {
        return extractedTags;
    }

    public void setExtractedTags(List<String> extractedTags) {
        this.extractedTags = extractedTags;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
}