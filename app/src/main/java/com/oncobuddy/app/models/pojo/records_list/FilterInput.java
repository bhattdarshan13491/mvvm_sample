package com.oncobuddy.app.models.pojo.records_list;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class FilterInput implements Parcelable {

    public String fromDate;
    public String toDate;
    public String reportType;
    public List<String> reportTags;

    public FilterInput() {
    }

    protected FilterInput(Parcel in) {
        fromDate = in.readString();
        toDate = in.readString();
        reportType = in.readString();
        reportTags = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fromDate);
        dest.writeString(toDate);
        dest.writeString(reportType);
        dest.writeStringList(reportTags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FilterInput> CREATOR = new Creator<FilterInput>() {
        @Override
        public FilterInput createFromParcel(Parcel in) {
            return new FilterInput(in);
        }

        @Override
        public FilterInput[] newArray(int size) {
            return new FilterInput[size];
        }
    };

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public List<String> getReportTags() {
        return reportTags;
    }

    public void setReportTags(List<String> reportTags) {
        this.reportTags = reportTags;
    }
}
