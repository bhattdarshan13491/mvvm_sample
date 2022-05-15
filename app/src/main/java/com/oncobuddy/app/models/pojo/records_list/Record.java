package com.oncobuddy.app.models.pojo.records_list;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.oncobuddy.app.models.pojo.StringListTypeConverters;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "record")
public class Record implements Parcelable {

    public static final Creator<Record> CREATOR = new Creator<Record>() {
        @Override
        public Record createFromParcel(Parcel in) {
            return new Record(in);
        }

        @Override
        public Record[] newArray(int size) {
            return new Record[size];
        }
    };
    @PrimaryKey
    @SerializedName("id")
    @ColumnInfo(name = "id")
    @Expose
    private Integer id;
    @SerializedName("createdOn")
    @ColumnInfo(name = "createdOn")
    @Expose
    private String createdOn;
    @SerializedName("lastModified")
    @ColumnInfo(name = "lastModified")
    @Expose
    private String lastModified;
    @SerializedName("patientId")
    @ColumnInfo(name = "patientId")
    @Expose
    private Integer patientId;
    @SerializedName("title")
    @ColumnInfo(name = "title")
    @Expose
    private String title;
    @SerializedName("link")
    @ColumnInfo(name = "link")
    @Expose
    private String link;
    @SerializedName("hospitalName")
    @ColumnInfo(name = "hospitalName")
    @Expose
    private String hospitalName;
    @SerializedName("recordDate")
    @ColumnInfo(name = "recordDate")
    @Expose
    private String recordDate;
    @SerializedName("ocrExtract")
    @ColumnInfo(name = "ocrExtract")
    @Expose
    private String ocrExtract;
    @SerializedName("recordType")
    @ColumnInfo(name = "recordType")
    @Expose
    private String recordType;
    @SerializedName("deleted")
    @ColumnInfo(name = "deleted")
    @Expose
    private Boolean deleted;
    @SerializedName("size")
    @ColumnInfo(name = "size")
    @Expose
    private double size;
    @SerializedName("uploadedUserId")
    @ColumnInfo(name = "uploadedUserId")
    @Expose
    private Integer uploadedUserId;

    @SerializedName("uploadedUserName")
    @Expose
    private String uploadedUserName;

    private Boolean isSelected = false;

    public Boolean isShowingDetails = false;

    @SerializedName("tags")
    @ColumnInfo(name = "tags")
    @Expose
    @TypeConverters(StringListTypeConverters.class)
    private List<String> tags = null;

    @SerializedName("categories")
    private List<String> categories = null;

    public Record() {
    }

    protected Record(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        createdOn = in.readString();
        lastModified = in.readString();

        if (in.readByte() == 0) {
            patientId = null;
        } else {
            patientId = in.readInt();
        }
        title = in.readString();
        link = in.readString();
        hospitalName = in.readString();
        recordDate = in.readString();
        recordType = in.readString();
        byte tmpDeleted = in.readByte();
        deleted = tmpDeleted == 0 ? null : tmpDeleted == 1;
        if (in.readByte() == 0) {
            size = Double.parseDouble(null);
        } else {
            size = in.readInt();
        }
        if (in.readByte() == 0) {
            uploadedUserId = null;
        } else {
            uploadedUserId = in.readInt();
        }
    }



    public Boolean getShowingDetails() {
        return isShowingDetails;
    }

    public void setShowingDetails(Boolean showingDetails) {
        isShowingDetails = showingDetails;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public Integer getUploadedUserId() {
        return uploadedUserId;
    }

    public void setUploadedUserId(Integer uploadedUserId) {
        this.uploadedUserId = uploadedUserId;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getOcrExtract() {
        return ocrExtract;
    }

    public void setOcrExtract(String ocrExtract) {
        this.ocrExtract = ocrExtract;
    }

    public String getUploadedUserName() {
        return uploadedUserName;
    }

    public void setUploadedUserName(String uploadedUserName) {
        this.uploadedUserName = uploadedUserName;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(id);
        }
        parcel.writeString(createdOn);
        parcel.writeString(lastModified);
        if (patientId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(patientId);
        }
        parcel.writeString(title);
        parcel.writeList(tags);
        parcel.writeString(link);
        parcel.writeString(hospitalName);
        parcel.writeString(recordDate);
        parcel.writeString(recordType);
        parcel.writeList(categories);
        parcel.writeByte((byte) (deleted == null ? 0 : deleted ? 1 : 2));
        if (size == 0.0) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(size);
        }
        if (uploadedUserId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(uploadedUserId);
        }
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
}