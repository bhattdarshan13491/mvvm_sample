package com.oncobuddy.app.models.pojo.daily_questions;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DailyQuestionsListResponse {

@SerializedName("success")
@Expose
private Boolean success;
@SerializedName("message")
@Expose
private String message;
@SerializedName("payLoad")
@Expose
private List<Question> questionsList = null;

public Boolean getSuccess() {
return success;
}

public void setSuccess(Boolean success) {
this.success = success;
}

public String getMessage() {
return message;
}

public void setMessage(String message) {
this.message = message;
}

public List<Question> getPayLoad() {
return questionsList;
}

public void setPayLoad(List<Question> payLoad) {
this.questionsList = payLoad;
}

}