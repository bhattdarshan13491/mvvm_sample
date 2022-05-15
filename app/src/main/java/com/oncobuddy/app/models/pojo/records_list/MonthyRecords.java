package com.oncobuddy.app.models.pojo.records_list;

import java.util.ArrayList;

public class MonthyRecords {

    private String monthName;
    private ArrayList<Record> recordArrayList;
    private Boolean isSHowingDetails = true;

    public MonthyRecords() {
    }



    public MonthyRecords(String monthName) {
        this.monthName = monthName;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public ArrayList<Record> getRecordArrayList() {
        return recordArrayList;
    }

    public void setRecordArrayList(ArrayList<Record> recordArrayList) {
        this.recordArrayList = recordArrayList;
    }

    public Boolean getSHowingDetails() {
        return isSHowingDetails;
    }

    public void setSHowingDetails(Boolean SHowingDetails) {
        isSHowingDetails = SHowingDetails;
    }
}
