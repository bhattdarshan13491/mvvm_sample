package com.oncobuddy.app.models.pojo.appointments.input;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.oncobuddy.app.models.pojo.GuestUserId;

import java.util.ArrayList;

public class AddGuestInput {

private ArrayList<GuestUserId> guestUsers;

    public AddGuestInput(ArrayList<GuestUserId> guestUsers) {
        this.guestUsers = guestUsers;
    }

    public AddGuestInput() {
    }

    public ArrayList<GuestUserId> getGuestUsers() {
        return guestUsers;
    }

    public void setGuestUsers(ArrayList<GuestUserId> guestUsers) {
        this.guestUsers = guestUsers;
    }
}