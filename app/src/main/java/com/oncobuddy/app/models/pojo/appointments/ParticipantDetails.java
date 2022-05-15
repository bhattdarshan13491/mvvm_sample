package com.oncobuddy.app.models.pojo.appointments;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


@Entity(tableName = "participant_details")
public class ParticipantDetails implements Parcelable
{
    @PrimaryKey
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    private int id;

    @SerializedName("createdOn")
    @Expose
    @ColumnInfo(name = "createdOn")
    private String createdOn;

    @SerializedName("lastModified")
    @Expose
    @ColumnInfo(name = "lastModified")
    private String lastModified;

    @SerializedName("userId")
    @Expose
    @ColumnInfo(name = "userId")
    private int userId;

    @SerializedName("name")
    @Expose
    @ColumnInfo(name = "name")
    private String name;

    @SerializedName("role")
    @Expose
    @ColumnInfo(name = "role")
    private String role;

    @SerializedName("dpLink")
    @Expose
    @ColumnInfo(name = "dpLink")
    private String dpLink = "";

    @SerializedName("headline")
    @Expose
    @ColumnInfo(name = "headline")
    private String headline;

    public ParticipantDetails() {
    }

    protected ParticipantDetails(Parcel in) {
        id = in.readInt();
        createdOn = in.readString();
        lastModified = in.readString();
        userId = in.readInt();
        name = in.readString();
        role = in.readString();
        headline = in.readString();
        dpLink = in.readString();
    }

    public static final Creator<ParticipantDetails> CREATOR = new Creator<ParticipantDetails>() {
        @Override
        public ParticipantDetails createFromParcel(Parcel in) {
            return new ParticipantDetails(in);
        }

        @Override
        public ParticipantDetails[] newArray(int size) {
            return new ParticipantDetails[size];
        }
    };

    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }
    public void setCreatedOn(String createdOn){
        this.createdOn = createdOn;
    }
    public String getCreatedOn(){
        return this.createdOn;
    }
    public void setLastModified(String lastModified){
        this.lastModified = lastModified;
    }
    public String getLastModified(){
        return this.lastModified;
    }
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(createdOn);
        dest.writeString(lastModified);
        dest.writeInt(userId);
        dest.writeString(name);
        dest.writeString(role);
        dest.writeString(headline);
        dest.writeString(dpLink);
    }
}
