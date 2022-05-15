package com.oncobuddy.app.models.pojo.registration_process;

import java.io.Serializable;

public class AppUser implements Serializable {


    String email;
    String firstName;
    String lastName;
    String headline;
    String password;
    String phoneNumber;
    int age;
    String Gender;
    String dateOfBirth;
    String dpLink;
    Long cancerSubTypeId;
    Long cancerTypeId;
    AddressDTO addressDTO;



    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public AddressDTO getAddressDTO() {
        return addressDTO;
    }

    public void setAddressDTO(AddressDTO addressDTO) {
        this.addressDTO = addressDTO;
    }

    public Long getCancerSubTypeId() {
        return cancerSubTypeId;
    }

    public void setCancerSubTypeId(Long cancerSubTypeId) {
        this.cancerSubTypeId = cancerSubTypeId;
    }

    public Long getCancerTypeId() {
        return cancerTypeId;
    }

    public void setCancerTypeId(Long cancerTypeId) {
        this.cancerTypeId = cancerTypeId;
    }

    public String getDpLink() {
        return dpLink;
    }

    public void setDpLink(String dpLink) {
        this.dpLink = dpLink;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }
}
