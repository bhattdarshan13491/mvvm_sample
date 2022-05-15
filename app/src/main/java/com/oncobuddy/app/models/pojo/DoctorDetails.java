package com.oncobuddy.app.models.pojo;

public class DoctorDetails {
    private String id;
    private String mobileNo;
    private String dpLink;
    private String name;
    private String designation;
    private String specialization;
    private String yearsofExp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getDpLink() {
        return dpLink;
    }

    public void setDpLink(String dpLink) {
        this.dpLink = dpLink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getYearsofExp() {
        return yearsofExp;
    }

    public void setYearsofExp(String yearsofExp) {
        this.yearsofExp = yearsofExp;
    }
}
