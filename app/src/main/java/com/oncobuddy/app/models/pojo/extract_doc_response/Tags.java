package com.oncobuddy.app.models.pojo.extract_doc_response;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Tags{

	@SerializedName("secondary_predictions")
	private SecondaryPredictions secondaryPredictions;

	@SerializedName("primary_predictions")
	private List<PrimaryPredictionsItem> primaryPredictions;

	public void setSecondaryPredictions(SecondaryPredictions secondaryPredictions){
		this.secondaryPredictions = secondaryPredictions;
	}

	public SecondaryPredictions getSecondaryPredictions(){
		return secondaryPredictions;
	}

	public void setPrimaryPredictions(List<PrimaryPredictionsItem> primaryPredictions){
		this.primaryPredictions = primaryPredictions;
	}

	public List<PrimaryPredictionsItem> getPrimaryPredictions(){
		return primaryPredictions;
	}
}