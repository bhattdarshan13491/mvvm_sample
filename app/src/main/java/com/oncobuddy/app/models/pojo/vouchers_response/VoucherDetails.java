package com.oncobuddy.app.models.pojo.vouchers_response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class VoucherDetails implements Parcelable {

	@SerializedName("discountPercentage")
	private int discountPercentage;

	@SerializedName("numberOfConsultations")
	private int numberOfConsultations;

	@SerializedName("voucherConsultationPeriod")
	private String voucherConsultationPeriod;

	@SerializedName("doctorCategory")
	private String doctorCategory;

	@SerializedName("description")
	private String description;

	@SerializedName("active")
	private boolean active;

	@SerializedName("id")
	private int id;

	@SerializedName("lastModified")
	private String lastModified;

	@SerializedName("title")
	private String title;

	@SerializedName("createdOn")
	private String createdOn;

	private boolean isSelected;

	protected VoucherDetails(Parcel in) {
		discountPercentage = in.readInt();
		numberOfConsultations = in.readInt();
		voucherConsultationPeriod = in.readString();
		doctorCategory = in.readString();
		description = in.readString();
		active = in.readByte() != 0;
		id = in.readInt();
		lastModified = in.readString();
		title = in.readString();
		createdOn = in.readString();
		isSelected = in.readByte() != 0;
	}

	public static final Creator<VoucherDetails> CREATOR = new Creator<VoucherDetails>() {
		@Override
		public VoucherDetails createFromParcel(Parcel in) {
			return new VoucherDetails(in);
		}

		@Override
		public VoucherDetails[] newArray(int size) {
			return new VoucherDetails[size];
		}
	};

	public int getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(int discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	public int getNumberOfConsultations() {
		return numberOfConsultations;
	}

	public void setNumberOfConsultations(int numberOfConsultations) {
		this.numberOfConsultations = numberOfConsultations;
	}

	public String getVoucherConsultationPeriod() {
		return voucherConsultationPeriod;
	}

	public void setVoucherConsultationPeriod(String voucherConsultationPeriod) {
		this.voucherConsultationPeriod = voucherConsultationPeriod;
	}

	public String getDoctorCategory() {
		return doctorCategory;
	}

	public void setDoctorCategory(String doctorCategory) {
		this.doctorCategory = doctorCategory;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLastModified() {
		return lastModified;
	}

	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean selected) {
		isSelected = selected;
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
		dest.writeInt(discountPercentage);
		dest.writeInt(numberOfConsultations);
		dest.writeString(voucherConsultationPeriod);
		dest.writeString(doctorCategory);
		dest.writeString(description);
		dest.writeByte((byte) (active ? 1 : 0));
		dest.writeInt(id);
		dest.writeString(lastModified);
		dest.writeString(title);
		dest.writeString(createdOn);
		dest.writeByte((byte) (isSelected ? 1 : 0));
	}
}