package com.oncobuddy.app.models.pojo.doctor_certification;

import com.google.gson.annotations.SerializedName;

public class Certification {

	@SerializedName("regNo")
	private String regNo;

	@SerializedName("council")
	private String council;

	@SerializedName("certificateLink")
	private String certificateLink;

	public void setRegNo(String regNo){
		this.regNo = regNo;
	}

	public String getRegNo(){
		return regNo;
	}

	public void setCouncil(String council){
		this.council = council;
	}

	public String getCouncil(){
		return council;
	}

	public void setCertificateLink(String certificateLink){
		this.certificateLink = certificateLink;
	}

	public String getCertificateLink(){
		return certificateLink;
	}
}