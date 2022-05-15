package com.oncobuddy.app.models.pojo.new_tags;

import java.util.List;

public class Tag {
    private String name;
    private List<String> alternativeNames;

    public Tag() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAlternativeNames() {
        return alternativeNames;
    }

    public void setAlternativeNames(List<String> alternativeNames) {
        this.alternativeNames = alternativeNames;
    }


}
