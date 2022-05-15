package com.oncobuddy.app.models.pojo.tags_response;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TagsResponse {

@SerializedName("success")
@Expose
private Boolean success;
@SerializedName("message")
@Expose
private String message;
@SerializedName("data")
@Expose
private List<Tag> data = null;

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

public List<Tag> getData() {
return data;
}

public void setData(List<Tag> data) {
this.data = data;
}

    @Override
    public String toString() {
        return "TagsResponse{" +
                "success:" + success +
                ", message:'" + message + '\'' +
                ", data:" + data +
                '}';
    }
}