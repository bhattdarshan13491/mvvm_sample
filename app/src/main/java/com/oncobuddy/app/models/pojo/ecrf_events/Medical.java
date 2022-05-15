package com.oncobuddy.app.models.pojo.ecrf_events;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Medical{

	@SerializedName("hospitalPhoneNumber")
	private String hospitalPhoneNumber;

	@SerializedName("cancerSite")
	private CancerSite cancerSite;

	@SerializedName("hereditaryFactorSignificant")
	private Object hereditaryFactorSignificant;

	@SerializedName("smokingFrequency")
	private Object smokingFrequency;

	@SerializedName("ageAtDiagnosis")
	private int ageAtDiagnosis;

	@SerializedName("cancerGrade")
	private String cancerGrade;

	@SerializedName("isMedicalAllergies")
	private boolean isMedicalAllergies;

	@SerializedName("symptomsBeforeDiagnosis")
	private String symptomsBeforeDiagnosis;

	@SerializedName("createdOn")
	private String createdOn;

	@SerializedName("isSmoking")
	private boolean isSmoking;

	@SerializedName("medicalAllergiesSpecify")
	private Object medicalAllergiesSpecify;

	@SerializedName("isBloodThinningMedications")
	private boolean isBloodThinningMedications;

	@SerializedName("cancerCellType")
	private CancerCellType cancerCellType;

	@SerializedName("doctorName")
	private String doctorName;

	@SerializedName("hereditaryFactor")
	private String hereditaryFactor;

	@SerializedName("id")
	private int id;

	@SerializedName("drinkingFrequency")
	private Object drinkingFrequency;

	@SerializedName("coMorbidities")
	private List<CoMorbiditiesItem> coMorbidities;

	@SerializedName("dateOfConsultation")
	private Object dateOfConsultation;

	@SerializedName("bloodThinningMedicationSpecify")
	private Object bloodThinningMedicationSpecify;

	@SerializedName("hospitalName")
	private String hospitalName;

	@SerializedName("drinkingSpecify")
	private Object drinkingSpecify;

	@SerializedName("isDrinking")
	private boolean isDrinking;

	@SerializedName("cancerStage")
	private String cancerStage;

	@SerializedName("lastModified")
	private String lastModified;

	@SerializedName("cancerType")
	private CancerType cancerType;

	@SerializedName("cancerSubType")
	private CancerSubType cancerSubType;

	@SerializedName("smokingSpecify")
	private Object smokingSpecify;

	public void setHospitalPhoneNumber(String hospitalPhoneNumber){
		this.hospitalPhoneNumber = hospitalPhoneNumber;
	}

	public String getHospitalPhoneNumber(){
		return hospitalPhoneNumber;
	}

	public void setCancerSite(CancerSite cancerSite){
		this.cancerSite = cancerSite;
	}

	public CancerSite getCancerSite(){
		return cancerSite;
	}

	public void setHereditaryFactorSignificant(Object hereditaryFactorSignificant){
		this.hereditaryFactorSignificant = hereditaryFactorSignificant;
	}

	public Object getHereditaryFactorSignificant(){
		return hereditaryFactorSignificant;
	}

	public void setSmokingFrequency(Object smokingFrequency){
		this.smokingFrequency = smokingFrequency;
	}

	public Object getSmokingFrequency(){
		return smokingFrequency;
	}

	public void setAgeAtDiagnosis(int ageAtDiagnosis){
		this.ageAtDiagnosis = ageAtDiagnosis;
	}

	public int getAgeAtDiagnosis(){
		return ageAtDiagnosis;
	}

	public void setCancerGrade(String cancerGrade){
		this.cancerGrade = cancerGrade;
	}

	public String getCancerGrade(){
		return cancerGrade;
	}

	public void setIsMedicalAllergies(boolean isMedicalAllergies){
		this.isMedicalAllergies = isMedicalAllergies;
	}

	public boolean isIsMedicalAllergies(){
		return isMedicalAllergies;
	}

	public void setSymptomsBeforeDiagnosis(String symptomsBeforeDiagnosis){
		this.symptomsBeforeDiagnosis = symptomsBeforeDiagnosis;
	}

	public String getSymptomsBeforeDiagnosis(){
		return symptomsBeforeDiagnosis;
	}

	public void setCreatedOn(String createdOn){
		this.createdOn = createdOn;
	}

	public String getCreatedOn(){
		return createdOn;
	}

	public void setIsSmoking(boolean isSmoking){
		this.isSmoking = isSmoking;
	}

	public boolean isIsSmoking(){
		return isSmoking;
	}

	public void setMedicalAllergiesSpecify(Object medicalAllergiesSpecify){
		this.medicalAllergiesSpecify = medicalAllergiesSpecify;
	}

	public Object getMedicalAllergiesSpecify(){
		return medicalAllergiesSpecify;
	}

	public void setIsBloodThinningMedications(boolean isBloodThinningMedications){
		this.isBloodThinningMedications = isBloodThinningMedications;
	}

	public boolean isIsBloodThinningMedications(){
		return isBloodThinningMedications;
	}

	public void setCancerCellType(CancerCellType cancerCellType){
		this.cancerCellType = cancerCellType;
	}

	public CancerCellType getCancerCellType(){
		return cancerCellType;
	}

	public void setDoctorName(String doctorName){
		this.doctorName = doctorName;
	}

	public String getDoctorName(){
		return doctorName;
	}

	public void setHereditaryFactor(String hereditaryFactor){
		this.hereditaryFactor = hereditaryFactor;
	}

	public String getHereditaryFactor(){
		return hereditaryFactor;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setDrinkingFrequency(Object drinkingFrequency){
		this.drinkingFrequency = drinkingFrequency;
	}

	public Object getDrinkingFrequency(){
		return drinkingFrequency;
	}

	public void setCoMorbidities(List<CoMorbiditiesItem> coMorbidities){
		this.coMorbidities = coMorbidities;
	}

	public List<CoMorbiditiesItem> getCoMorbidities(){
		return coMorbidities;
	}

	public void setDateOfConsultation(Object dateOfConsultation){
		this.dateOfConsultation = dateOfConsultation;
	}

	public Object getDateOfConsultation(){
		return dateOfConsultation;
	}

	public void setBloodThinningMedicationSpecify(Object bloodThinningMedicationSpecify){
		this.bloodThinningMedicationSpecify = bloodThinningMedicationSpecify;
	}

	public Object getBloodThinningMedicationSpecify(){
		return bloodThinningMedicationSpecify;
	}

	public void setHospitalName(String hospitalName){
		this.hospitalName = hospitalName;
	}

	public String getHospitalName(){
		return hospitalName;
	}

	public void setDrinkingSpecify(Object drinkingSpecify){
		this.drinkingSpecify = drinkingSpecify;
	}

	public Object getDrinkingSpecify(){
		return drinkingSpecify;
	}

	public void setIsDrinking(boolean isDrinking){
		this.isDrinking = isDrinking;
	}

	public boolean isIsDrinking(){
		return isDrinking;
	}

	public void setCancerStage(String cancerStage){
		this.cancerStage = cancerStage;
	}

	public String getCancerStage(){
		return cancerStage;
	}

	public void setLastModified(String lastModified){
		this.lastModified = lastModified;
	}

	public String getLastModified(){
		return lastModified;
	}

	public void setCancerType(CancerType cancerType){
		this.cancerType = cancerType;
	}

	public CancerType getCancerType(){
		return cancerType;
	}

	public void setCancerSubType(CancerSubType cancerSubType){
		this.cancerSubType = cancerSubType;
	}

	public CancerSubType getCancerSubType(){
		return cancerSubType;
	}

	public void setSmokingSpecify(Object smokingSpecify){
		this.smokingSpecify = smokingSpecify;
	}

	public Object getSmokingSpecify(){
		return smokingSpecify;
	}
}