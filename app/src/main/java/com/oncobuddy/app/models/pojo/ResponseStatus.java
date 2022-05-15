package com.oncobuddy.app.models.pojo;

import java.io.Serializable;

public class ResponseStatus implements Serializable {

    private Boolean success;
    private String message;

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

    @Override
    public String toString() {
        return "ResponseStatus{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}