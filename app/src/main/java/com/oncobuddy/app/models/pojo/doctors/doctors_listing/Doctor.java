package com.oncobuddy.app.models.pojo.doctors.doctors_listing;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Doctor implements Parcelable {
    //public String id;

    @SerializedName(value="firstName", alternate={"name"})
    @Expose
    public String firstName;

    @SerializedName(value="doctorId", alternate={"id"})
    @Expose
    public int doctorId;

    @SerializedName("lastName")
    @Expose
    public String lastName;

    @SerializedName("phoneNumber")
    @Expose
    public String phoneNumber;

    @SerializedName("specialization")
    @Expose
    public String specialization;

    @SerializedName("designation")
    @Expose
    public String designation;

    @SerializedName("email")
    @Expose
    public String email;

    @SerializedName("displayPicUrl")
    @Expose
    public String displayPicUrl;

    @SerializedName("consultationFee")
    @Expose
    public double consultationFee;

    @SerializedName("verified")
    @Expose
    public Boolean verified;

    @SerializedName("requestPending")
    @Expose
    private boolean requestPending;

    @SerializedName("requestedUserId")
    @Expose
    private int requestedUserId;




    @SerializedName(value="hospital", alternate={"hospitalName"})
    @Expose
    public String hospital;


    protected Doctor(Parcel in) {
        firstName = in.readString();
        doctorId = in.readInt();
        lastName = in.readString();
        phoneNumber = in.readString();
        specialization = in.readString();
        designation = in.readString();
        displayPicUrl = in.readString();
        email = in.readString();
        consultationFee = in.readDouble();
        byte tmpVerified = in.readByte();
        verified = tmpVerified == 0 ? null : tmpVerified == 1;
        hospital = in.readString();
    }

    public Doctor() {
    }

    public static final Creator<Doctor> CREATOR = new Creator<Doctor>() {
        @Override
        public Doctor createFromParcel(Parcel in) {
            return new Doctor(in);
        }

        @Override
        public Doctor[] newArray(int size) {
            return new Doctor[size];
        }
    };

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getDisplayPicUrl() {
        return displayPicUrl;
    }

    public void setDisplayPicUrl(String displayPicUrl) {
        this.displayPicUrl = displayPicUrl;
    }

    public boolean isRequestPending() {
        return requestPending;
    }

    public void setRequestPending(boolean requestPending) {
        this.requestPending = requestPending;
    }

    public int getRequestedUserId() {
        return requestedUserId;
    }

    public void setRequestedUserId(int requestedUserId) {
        this.requestedUserId = requestedUserId;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeInt(doctorId);
        dest.writeString(lastName);
        dest.writeString(phoneNumber);
        dest.writeString(specialization);
        dest.writeString(designation);
        dest.writeString(email);
        dest.writeString(displayPicUrl);
        dest.writeDouble(consultationFee);
        dest.writeByte((byte) (verified == null ? 0 : verified ? 1 : 2));
        dest.writeString(hospital);
    }
}