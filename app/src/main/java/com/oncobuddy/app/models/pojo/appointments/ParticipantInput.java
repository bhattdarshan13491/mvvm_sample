package com.oncobuddy.app.models.pojo.appointments;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ParticipantInput implements Parcelable
{
    @SerializedName("userId")
    @Expose
    private int userId;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("role")
    @Expose
    private String role;

    protected ParticipantInput(Parcel in) {
        userId = in.readInt();
        name = in.readString();
        role = in.readString();
    }

    public ParticipantInput() {
    }

    public static final Creator<ParticipantInput> CREATOR = new Creator<ParticipantInput>() {
        @Override
        public ParticipantInput createFromParcel(Parcel in) {
            return new ParticipantInput(in);
        }

        @Override
        public ParticipantInput[] newArray(int size) {
            return new ParticipantInput[size];
        }
    };

    public void setUserId(int userId){
        this.userId = userId;
    }
    public int getUserId(){
        return this.userId;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setRole(String role){
        this.role = role;
    }
    public String getRole(){
        return this.role;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(userId);
        dest.writeString(name);
        dest.writeString(role);
    }
}
