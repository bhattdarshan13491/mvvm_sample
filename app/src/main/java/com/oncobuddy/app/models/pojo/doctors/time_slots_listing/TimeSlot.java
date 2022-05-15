package com.oncobuddy.app.models.pojo.doctors.time_slots_listing;

public class TimeSlot{
    public String time;
    public boolean available;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}