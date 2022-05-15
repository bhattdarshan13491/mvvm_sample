package com.oncobuddy.app.models.pojo.appointments.input;

import android.os.Parcel;
import android.os.Parcelable;

import com.oncobuddy.app.models.pojo.appointments.ParticipantInput;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AddAppointmentInput implements Parcelable {

@SerializedName("appointmentPurpose")
@Expose
private String appointmentPurpose;
@SerializedName("doctorId")
@Expose
private Integer doctorId;
@SerializedName("duration")
@Expose
private Integer duration;

@SerializedName("guestId")
@Expose
private Integer guestId;

@SerializedName("notes")
@Expose
private String notes;

@SerializedName("patientId")
@Expose
private Integer patientId;
@SerializedName("scheduledTime")
@Expose
private String scheduledTime;

@SerializedName("participantReqDtoList")
@Expose
private List<ParticipantInput> participantInputList;


    public AddAppointmentInput() {
    }

    protected AddAppointmentInput(Parcel in) {
        appointmentPurpose = in.readString();
        if (in.readByte() == 0) {
            doctorId = null;
        } else {
            doctorId = in.readInt();
        }
        if (in.readByte() == 0) {
            duration = null;
        } else {
            duration = in.readInt();
        }
        if (in.readByte() == 0) {
            guestId = null;
        } else {
            guestId = in.readInt();
        }
        notes = in.readString();
        if (in.readByte() == 0) {
            patientId = null;
        } else {
            patientId = in.readInt();
        }
        scheduledTime = in.readString();
        participantInputList = in.createTypedArrayList(ParticipantInput.CREATOR);
    }

    public static final Creator<AddAppointmentInput> CREATOR = new Creator<AddAppointmentInput>() {
        @Override
        public AddAppointmentInput createFromParcel(Parcel in) {
            return new AddAppointmentInput(in);
        }

        @Override
        public AddAppointmentInput[] newArray(int size) {
            return new AddAppointmentInput[size];
        }
    };

    public String getAppointmentPurpose() {
return appointmentPurpose;
}

public void setAppointmentPurpose(String appointmentPurpose) {
this.appointmentPurpose = appointmentPurpose;
}

public Integer getDoctorId() {
return doctorId;
}

public void setDoctorId(Integer doctorId) {
this.doctorId = doctorId;
}

public Integer getDuration() {
return duration;
}

public void setDuration(Integer duration) {
this.duration = duration;
}

public Integer getGuestId() {
return guestId;
}

public void setGuestId(Integer guestId) {
this.guestId = guestId;
}

public Integer getPatientId() {
return patientId;
}

public void setPatientId(Integer patientId) {
this.patientId = patientId;
}

public String getScheduledTime() {
return scheduledTime;
}

public void setScheduledTime(String scheduledTime) {
this.scheduledTime = scheduledTime;
}

    public List<ParticipantInput> getParticipantInputList() {
        return participantInputList;
    }

    public void setParticipantInputList(List<ParticipantInput> participantInputList) {
        this.participantInputList = participantInputList;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(appointmentPurpose);
        if (doctorId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(doctorId);
        }
        if (duration == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(duration);
        }
        if (guestId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(guestId);
        }
        parcel.writeString(notes);
        if (patientId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(patientId);
        }
        parcel.writeString(scheduledTime);
        parcel.writeTypedList(participantInputList);
    }
}