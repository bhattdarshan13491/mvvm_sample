package com.oncobuddy.app.models.pojo.appointments.list_response;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.oncobuddy.app.models.pojo.GithubTypeConverters;
import com.oncobuddy.app.models.pojo.appointments.ParticipantDetails;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AppointmentDetails implements Parcelable {

    @PrimaryKey
    @Expose
    private Integer id;

    @SerializedName("createdOn")
    @Expose
    private String createdOn;

    @SerializedName("lastModified")
    @Expose
    private String lastModified;

    @SerializedName("appointmentStatus")
    @Expose
    private String appointmentStatus;

    @SerializedName("scheduledTime")
    private String scheduledTime;

    @SerializedName("duration")
    private Integer duration;

    @SerializedName("paymentStatus")
    private String paymentStatus;

    @SerializedName("notes")
    @ColumnInfo(name = "notes")
    @Expose
    private String notes;

    @SerializedName("paymentDoneToDoctor")
    private Boolean paymentDoneToDoctor;

    @SerializedName("twilioToken")
    private String twilioId;

    @SerializedName("userId")
    private Integer userId;

    @SerializedName("invoiceLink")
    private String invoiceLink;

 /*   @SerializedName("appointmentPurpose")
    @ColumnInfo(name = "appointmentPurpose")
    @Expose
    private String appointmentPurpose;*/

    @SerializedName("participants")
    private List<ParticipantDetails> participants = null;

    @SerializedName("patientRequestedCallback")
    private Boolean patientRequestedCallback;

    @SerializedName("doctorRequestedReschedule")
    private Boolean doctorRequestedReschedule;

    private boolean isShowingDetails = false;


    public AppointmentDetails() {
    }

    protected AppointmentDetails(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        createdOn = in.readString();
        appointmentStatus = in.readString();
        scheduledTime = in.readString();
        if (in.readByte() == 0) {
            duration = null;
        } else {
            duration = in.readInt();
        }
        paymentStatus = in.readString();
        notes = in.readString();
        lastModified = in.readString();
        byte tmpPaymentDoneToDoctor = in.readByte();
        paymentDoneToDoctor = tmpPaymentDoneToDoctor == 0 ? null : tmpPaymentDoneToDoctor == 1;
        twilioId = in.readString();
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readInt();
        }
        //appointmentPurpose = in.readString();
        participants = in.createTypedArrayList(ParticipantDetails.CREATOR);
    }

    public static final Creator<AppointmentDetails> CREATOR = new Creator<AppointmentDetails>() {
        @Override
        public AppointmentDetails createFromParcel(Parcel in) {
            return new AppointmentDetails(in);
        }

        @Override
        public AppointmentDetails[] newArray(int size) {
            return new AppointmentDetails[size];
        }
    };

    public String getInvoiceLink() {
        return invoiceLink;
    }

    public void setInvoiceLink(String invoiceLink) {
        this.invoiceLink = invoiceLink;
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

    public String getAppointmentStatus() {
        return appointmentStatus;
    }

    public void setAppointmentStatus(String appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getPaymentDoneToDoctor() {
        return paymentDoneToDoctor;
    }

    public void setPaymentDoneToDoctor(Boolean paymentDoneToDoctor) {
        this.paymentDoneToDoctor = paymentDoneToDoctor;
    }

    public String getTwilioId() {
        return twilioId;
    }

    public void setTwilioId(String twilioId) {
        this.twilioId = twilioId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Boolean getDoctorRequestedReschedule() {
        return doctorRequestedReschedule;
    }

    public void setDoctorRequestedReschedule(Boolean doctorRequestedReschedule) {
        this.doctorRequestedReschedule = doctorRequestedReschedule;
    }

    /*public String getAppointmentPurpose() {
        return appointmentPurpose;
    }

    public void setAppointmentPurpose(String appointmentPurpose) {
        this.appointmentPurpose = appointmentPurpose;
    }*/

    public List<ParticipantDetails> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ParticipantDetails> participants) {
        this.participants = participants;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(createdOn);
        dest.writeString(appointmentStatus);
        dest.writeString(scheduledTime);
        if (duration == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(duration);
        }
        dest.writeString(paymentStatus);
        dest.writeString(notes);
        dest.writeString(lastModified);
        dest.writeByte((byte) (paymentDoneToDoctor == null ? 0 : paymentDoneToDoctor ? 1 : 2));
        dest.writeString(twilioId);
        if (userId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userId);
        }
       // dest.writeString(appointmentPurpose);
        dest.writeTypedList(participants);
    }

    public Boolean getPatientRequestedCallback() {
        return patientRequestedCallback;
    }

    public void setPatientRequestedCallback(Boolean patientRequestedCallback) {
        this.patientRequestedCallback = patientRequestedCallback;
    }

    public boolean isShowingDetails() {
        return isShowingDetails;
    }

    public void setShowingDetails(boolean showingDetails) {
        isShowingDetails = showingDetails;
    }
}