package com.oncobuddy.app.models.pojo.daily_questions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AnswerQuestionInput {

@SerializedName("answer")
@Expose
private String answer;
@SerializedName("answeredDateTime")
@Expose
private String answeredDateTime;
@SerializedName("proQuestionId")
@Expose
private Integer proQuestionId;

public String getAnswer() {
return answer;
}

public void setAnswer(String answer) {
this.answer = answer;
}

public String getAnsweredDateTime() {
return answeredDateTime;
}

public void setAnsweredDateTime(String answeredDateTime) {
this.answeredDateTime = answeredDateTime;
}

public Integer getProQuestionId() {
return proQuestionId;
}

public void setProQuestionId(Integer proQuestionId) {
this.proQuestionId = proQuestionId;
}

}