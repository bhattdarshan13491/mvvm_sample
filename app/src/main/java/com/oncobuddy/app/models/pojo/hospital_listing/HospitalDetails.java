package com.oncobuddy.app.models.pojo.hospital_listing;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.oncobuddy.app.models.pojo.patient_list.PatientDetails;

import java.util.List;


public class HospitalDetails {

@SerializedName("id")
@Expose
private int id;
@SerializedName("createdOn")
@Expose
private String createdOn;
@SerializedName("lastModified")
@Expose
private String lastModified;
@SerializedName("name")
@Expose
private String name;
@SerializedName("isActive")
@Expose
private Boolean isActive;

    @SerializedName("locations")
    @Expose
    private List<Locations> locationsList = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreatedOn() {
return createdOn;
}

public void setCreatedOn(String createdOn) {
this.createdOn = createdOn;
}

public String getLastModified() {
return lastModified;
}

public void setLastModified(String lastModified) {
this.lastModified = lastModified;
}

public String getName() {
return name;
}

public void setName(String name) {
this.name = name;
}

public Boolean getIsActive() {
return isActive;
}

public void setIsActive(Boolean isActive) {
this.isActive = isActive;
}

    @Override
    public String toString() {
        return this.name;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public List<Locations> getLocationsList() {
        return locationsList;
    }

    public void setLocationsList(List<Locations> locationsList) {
        this.locationsList = locationsList;
    }
}