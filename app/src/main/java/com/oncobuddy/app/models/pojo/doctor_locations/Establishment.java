package com.oncobuddy.app.models.pojo.doctor_locations;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Establishment implements Parcelable {

	@SerializedName("address")
	private Address address;

	@SerializedName("establishmentName")
	private String establishmentName;

	@SerializedName("id")
	private int id;

	@SerializedName("doctorConsultationLocationType")
	private String doctorConsultationLocationType;

	protected Establishment(Parcel in) {
		establishmentName = in.readString();
		id = in.readInt();
		doctorConsultationLocationType = in.readString();
	}

	public static final Creator<Establishment> CREATOR = new Creator<Establishment>() {
		@Override
		public Establishment createFromParcel(Parcel in) {
			return new Establishment(in);
		}

		@Override
		public Establishment[] newArray(int size) {
			return new Establishment[size];
		}
	};

	public void setAddress(Address address){
		this.address = address;
	}

	public Address getAddress(){
		return address;
	}

	public void setEstablishmentName(String establishmentName){
		this.establishmentName = establishmentName;
	}

	public String getEstablishmentName(){
		return establishmentName;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setDoctorConsultationLocationType(String doctorConsultationLocationType){
		this.doctorConsultationLocationType = doctorConsultationLocationType;
	}

	public String getDoctorConsultationLocationType(){
		return doctorConsultationLocationType;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(establishmentName);
		parcel.writeInt(id);
		parcel.writeString(doctorConsultationLocationType);
	}
}