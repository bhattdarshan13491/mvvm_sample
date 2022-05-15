package com.oncobuddy.app.models.pojo.registration_process;

public class RegistrationInput {

    private int userId;
    private AppUser appUser;
    private Long cancerTypeId;
    private Long cancerSubTypeId;
    private int age;
    private String gender;
    private String dateOfBirth;
    private String aadharNumber;
    private String cancerStage;
    private Double height;
    private Double weight;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCancerStage() {
        return cancerStage;
    }

    public void setCancerStage(String cancerStage) {
        this.cancerStage = cancerStage;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Long getCancerTypeId() {
        return cancerTypeId;
    }

    public void setCancerTypeId(Long cancerTypeId) {
        this.cancerTypeId = cancerTypeId;
    }

    public Long getCancerSubTypeId() {
        return cancerSubTypeId;
    }

    public void setCancerSubTypeId(Long cancerSubTypeId) {
        this.cancerSubTypeId = cancerSubTypeId;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
