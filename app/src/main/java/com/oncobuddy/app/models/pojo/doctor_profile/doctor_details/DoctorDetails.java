package com.oncobuddy.app.models.pojo.doctor_profile.doctor_details;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class DoctorDetails implements Parcelable {

	@SerializedName("lastName")
	private String lastName;

	@SerializedName("subCategory")
	private SubCategory subCategory;

	@SerializedName("verified")
	private boolean verified;

	@SerializedName("firstName")
	private String firstName;

	@SerializedName("phoneNumber")
	private String phoneNumber;

	@SerializedName("doctorId")
	private int doctorId;

	@SerializedName("dpLink")
	private String dpLink;

	@SerializedName("specialization")
	private String specialization;

	@SerializedName("designation")
	private String designation;

	@SerializedName("hospital")
	private String hospital;

	@SerializedName("category")
	private Category category;

	@SerializedName("email")
	private String email;

	@SerializedName("consultationFee")
	private int consultationFee;

	@SerializedName("experience")
	private String experience;

	@SerializedName("description")
	private String description;

	public DoctorDetails() {
	}

	protected DoctorDetails(Parcel in) {
		lastName = in.readString();
		verified = in.readByte() != 0;
		firstName = in.readString();
		phoneNumber = in.readString();
		doctorId = in.readInt();
		dpLink = in.readString();
		specialization = in.readString();
		designation = in.readString();
		hospital = in.readString();
		email = in.readString();
		consultationFee = in.readInt();
		experience = in.readString();
		description = in.readString();
	}

	public static final Creator<DoctorDetails> CREATOR = new Creator<DoctorDetails>() {
		@Override
		public DoctorDetails createFromParcel(Parcel in) {
			return new DoctorDetails(in);
		}

		@Override
		public DoctorDetails[] newArray(int size) {
			return new DoctorDetails[size];
		}
	};

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public SubCategory getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(SubCategory subCategory) {
		this.subCategory = subCategory;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public int getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(int doctorId) {
		this.doctorId = doctorId;
	}

	public String getDpLink() {
		return dpLink;
	}

	public void setDpLink(String dpLink) {
		this.dpLink = dpLink;
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

	public String getHospital() {
		return hospital;
	}

	public void setHospital(String hospital) {
		this.hospital = hospital;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getConsultationFee() {
		return consultationFee;
	}

	public void setConsultationFee(int consultationFee) {
		this.consultationFee = consultationFee;
	}

	public String getExperience() {
		return experience;
	}

	public void setExperience(String experience) {
		this.experience = experience;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(lastName);
		parcel.writeByte((byte) (verified ? 1 : 0));
		parcel.writeString(firstName);
		parcel.writeString(phoneNumber);
		parcel.writeInt(doctorId);
		parcel.writeString(dpLink);
		parcel.writeString(specialization);
		parcel.writeString(designation);
		parcel.writeString(hospital);
		parcel.writeString(email);
		parcel.writeInt(consultationFee);
		parcel.writeString(experience);
	    parcel.writeString(description);
	}
}