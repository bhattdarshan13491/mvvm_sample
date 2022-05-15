package com.oncobuddy.app.models.pojo.ecrf_events;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Patient{

	@SerializedName("motherTongue")
	private String motherTongue;

	@SerializedName("medical")
	private Medical medical;

	@SerializedName("patientMobileNumber")
	private String patientMobileNumber;

	@SerializedName("occupation")
	private String occupation;

	@SerializedName("gender")
	private String gender;

	@SerializedName("ethnicity")
	private String ethnicity;

	@SerializedName("patientId")
	private String patientId;

	@SerializedName("isActive")
	private boolean isActive;

	@SerializedName("createdOn")
	private String createdOn;

	@SerializedName("labTests")
	private List<Object> labTests;

	@SerializedName("patientUUID")
	private String patientUUID;

	@SerializedName("menstruation")
	private Object menstruation;

	@SerializedName("id")
	private int id;

	@SerializedName("diet")
	private String diet;

	@SerializedName("height")
	private int height;

	@SerializedName("patientName")
	private String patientName;

	@SerializedName("appUser")
	private AppUser appUser;

	@SerializedName("takeCareName")
	private Object takeCareName;

	@SerializedName("address")
	private Object address;

	@SerializedName("labTestPackage")
	private Object labTestPackage;

	@SerializedName("weight")
	private int weight;

	@SerializedName("dateOfBirth")
	private String dateOfBirth;

	@SerializedName("lastEditedBy")
	private LastEditedBy lastEditedBy;

	@SerializedName("religion")
	private String religion;

	@SerializedName("childrens")
	private int childrens;

	@SerializedName("addedFromLab")
	private boolean addedFromLab;

	@SerializedName("takeCareContact")
	private Object takeCareContact;

	@SerializedName("patientEmail")
	private String patientEmail;

	@SerializedName("lastModified")
	private String lastModified;

	@SerializedName("patientRelation")
	private Object patientRelation;

	@SerializedName("maritalStatus")
	private String maritalStatus;

	public void setMotherTongue(String motherTongue){
		this.motherTongue = motherTongue;
	}

	public String getMotherTongue(){
		return motherTongue;
	}

	public void setMedical(Medical medical){
		this.medical = medical;
	}

	public Medical getMedical(){
		return medical;
	}

	public void setPatientMobileNumber(String patientMobileNumber){
		this.patientMobileNumber = patientMobileNumber;
	}

	public String getPatientMobileNumber(){
		return patientMobileNumber;
	}

	public void setOccupation(String occupation){
		this.occupation = occupation;
	}

	public String getOccupation(){
		return occupation;
	}

	public void setGender(String gender){
		this.gender = gender;
	}

	public String getGender(){
		return gender;
	}

	public void setEthnicity(String ethnicity){
		this.ethnicity = ethnicity;
	}

	public String getEthnicity(){
		return ethnicity;
	}

	public void setPatientId(String patientId){
		this.patientId = patientId;
	}

	public String getPatientId(){
		return patientId;
	}

	public void setIsActive(boolean isActive){
		this.isActive = isActive;
	}

	public boolean isIsActive(){
		return isActive;
	}

	public void setCreatedOn(String createdOn){
		this.createdOn = createdOn;
	}

	public String getCreatedOn(){
		return createdOn;
	}

	public void setLabTests(List<Object> labTests){
		this.labTests = labTests;
	}

	public List<Object> getLabTests(){
		return labTests;
	}

	public void setPatientUUID(String patientUUID){
		this.patientUUID = patientUUID;
	}

	public String getPatientUUID(){
		return patientUUID;
	}

	public void setMenstruation(Object menstruation){
		this.menstruation = menstruation;
	}

	public Object getMenstruation(){
		return menstruation;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setDiet(String diet){
		this.diet = diet;
	}

	public String getDiet(){
		return diet;
	}

	public void setHeight(int height){
		this.height = height;
	}

	public int getHeight(){
		return height;
	}

	public void setPatientName(String patientName){
		this.patientName = patientName;
	}

	public String getPatientName(){
		return patientName;
	}

	public void setAppUser(AppUser appUser){
		this.appUser = appUser;
	}

	public AppUser getAppUser(){
		return appUser;
	}

	public void setTakeCareName(Object takeCareName){
		this.takeCareName = takeCareName;
	}

	public Object getTakeCareName(){
		return takeCareName;
	}

	public void setAddress(Object address){
		this.address = address;
	}

	public Object getAddress(){
		return address;
	}

	public void setLabTestPackage(Object labTestPackage){
		this.labTestPackage = labTestPackage;
	}

	public Object getLabTestPackage(){
		return labTestPackage;
	}

	public void setWeight(int weight){
		this.weight = weight;
	}

	public int getWeight(){
		return weight;
	}

	public void setDateOfBirth(String dateOfBirth){
		this.dateOfBirth = dateOfBirth;
	}

	public String getDateOfBirth(){
		return dateOfBirth;
	}

	public void setLastEditedBy(LastEditedBy lastEditedBy){
		this.lastEditedBy = lastEditedBy;
	}

	public LastEditedBy getLastEditedBy(){
		return lastEditedBy;
	}

	public void setReligion(String religion){
		this.religion = religion;
	}

	public String getReligion(){
		return religion;
	}

	public void setChildrens(int childrens){
		this.childrens = childrens;
	}

	public int getChildrens(){
		return childrens;
	}

	public void setAddedFromLab(boolean addedFromLab){
		this.addedFromLab = addedFromLab;
	}

	public boolean isAddedFromLab(){
		return addedFromLab;
	}

	public void setTakeCareContact(Object takeCareContact){
		this.takeCareContact = takeCareContact;
	}

	public Object getTakeCareContact(){
		return takeCareContact;
	}

	public void setPatientEmail(String patientEmail){
		this.patientEmail = patientEmail;
	}

	public String getPatientEmail(){
		return patientEmail;
	}

	public void setLastModified(String lastModified){
		this.lastModified = lastModified;
	}

	public String getLastModified(){
		return lastModified;
	}

	public void setPatientRelation(Object patientRelation){
		this.patientRelation = patientRelation;
	}

	public Object getPatientRelation(){
		return patientRelation;
	}

	public void setMaritalStatus(String maritalStatus){
		this.maritalStatus = maritalStatus;
	}

	public String getMaritalStatus(){
		return maritalStatus;
	}
}