package com.oncobuddy.app.models.pojo.care_team_details;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class PayLoad{

	@SerializedName("appUser")
	private CareTeamMember careTeamMember;

	@SerializedName("educationDegrees")
	private List<String> educationDegrees;

	@SerializedName("languagesKnown")
	private String languagesKnown;

	@SerializedName("totalYearsExperience")
	private String totalYearsExperience;

	@SerializedName("about")
	private String about;

	@SerializedName("dateOfBirth")
	private String dateOfBirth;

	@SerializedName("employeeId")
	private String employeeId;

	@SerializedName("supportedPatientsCount")
	private int supportedPatientsCount;

	public CareTeamMember getCareTeamMember() {
		return careTeamMember;
	}

	public void setCareTeamMember(CareTeamMember careTeamMember) {
		this.careTeamMember = careTeamMember;
	}

	public List<String> getEducationDegrees() {
		return educationDegrees;
	}

	public void setEducationDegrees(List<String> educationDegrees) {
		this.educationDegrees = educationDegrees;
	}

	public String getLanguagesKnown() {
		return languagesKnown;
	}

	public void setLanguagesKnown(String languagesKnown) {
		this.languagesKnown = languagesKnown;
	}

	public String getTotalYearsExperience() {
		return totalYearsExperience;
	}

	public void setTotalYearsExperience(String totalYearsExperience) {
		this.totalYearsExperience = totalYearsExperience;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public int getSupportedPatientsCount() {
		return supportedPatientsCount;
	}

	public void setSupportedPatientsCount(int supportedPatientsCount) {
		this.supportedPatientsCount = supportedPatientsCount;
	}
}