package com.oncobuddy.app.models.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.oncobuddy.app.models.pojo.tags_response.Tag;

import java.util.ArrayList;

public class PDFExtractedData implements Parcelable{

    private String reportId;
    private String reportType;
    private String reportName;
    private ArrayList<Tag> tags = new ArrayList<>();
    private ArrayList<Tag> primaryTags = new ArrayList<>();
    private ArrayList<String> strTags = new ArrayList<>();
    private ArrayList<String> strPtags = new ArrayList<>();
    private String hospitalName;
    private String consulationDate;

    public PDFExtractedData() {
    }

    public PDFExtractedData(Parcel in) {
        reportId = in.readString();
        reportType = in.readString();
        reportName = in.readString();
        tags = in.createTypedArrayList(Tag.CREATOR);
        primaryTags = in.createTypedArrayList(Tag.CREATOR);
        strTags = in.createStringArrayList();
        strPtags = in.createStringArrayList();
        hospitalName = in.readString();
        consulationDate = in.readString();
    }

    public static final Creator<PDFExtractedData> CREATOR = new Creator<PDFExtractedData>() {
        @Override
        public PDFExtractedData createFromParcel(Parcel in) {
            return new PDFExtractedData(in);
        }

        @Override
        public PDFExtractedData[] newArray(int size) {
            return new PDFExtractedData[size];
        }
    };

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    public ArrayList<String> getStrTags() {
        return strTags;
    }

    public void setStrTags(ArrayList<String> strTags) {
        this.strTags = strTags;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getConsulationDate() {
        return consulationDate;
    }

    public void setConsulationDate(String consulationDate) {
        this.consulationDate = consulationDate;
    }

    public ArrayList<Tag> getPrimaryTags() {
        return primaryTags;
    }

    public void setPrimaryTags(ArrayList<Tag> primaryTags) {
        this.primaryTags = primaryTags;
    }

    public ArrayList<String> getStrPtags() {
        return strPtags;
    }

    public void setStrPtags(ArrayList<String> strPtags) {
        this.strPtags = strPtags;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(reportId);
        dest.writeString(reportType);
        dest.writeString(reportName);
        dest.writeTypedList(tags);
        dest.writeStringList(strTags);
        dest.writeTypedList(primaryTags);
        dest.writeStringList(strPtags);
        dest.writeString(hospitalName);
        dest.writeString(consulationDate);
    }
}
