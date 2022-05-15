package com.oncobuddy.app.models.pojo.twilio_token;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenDetails {

@SerializedName("token")
@Expose
private String token;

    @SerializedName("roomName")
    @Expose
    private String roomName;

public String getToken() {
return token;
}

public void setToken(String token) {
this.token = token;
}

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}