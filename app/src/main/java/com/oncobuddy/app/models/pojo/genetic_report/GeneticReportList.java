package com.oncobuddy.app.models.pojo.genetic_report;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class GeneticReportList {

	@SerializedName("testStatuses")
	private List<TestStatusesItem> testStatuses;

	public List<TestStatusesItem> getTestStatuses(){
		return testStatuses;
	}
}