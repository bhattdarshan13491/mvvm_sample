package com.oncobuddy.app.models.pojo.daily_questions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Question {

@SerializedName("answerType")
@Expose
private String answerType;
@SerializedName("question")
@Expose
private String question;
@SerializedName("id")
@Expose
private Integer id;

private String answer;

public String getAnswerType() {
return answerType;
}

public void setAnswerType(String answerType) {
this.answerType = answerType;
}

public String getQuestion() {
return question;
}

public void setQuestion(String question) {
this.question = question;
}

public Integer getId() {
return id;
}

public void setId(Integer id) {
this.id = id;
}

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}