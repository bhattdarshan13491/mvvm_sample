package com.oncobuddy.app.models.pojo.new_tags;

import com.oncobuddy.app.models.pojo.tags_response.Tag;

import java.util.List;

public class SuperTag {
    private int id;
    private String name;
    private List<com.oncobuddy.app.models.pojo.tags_response.Tag> alternattiveNames;
    private List<SubTag> subTagsListing;

    public SuperTag() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Tag> getAlternattiveNames() {
        return alternattiveNames;
    }

    public void setAlternattiveNames(List<Tag> alternattiveNames) {
        this.alternattiveNames = alternattiveNames;
    }

    public List<SubTag> getSubTagsListing() {
        return subTagsListing;
    }

    public void setSubTagsListing(List<SubTag> subTagsListing) {
        this.subTagsListing = subTagsListing;
    }
}
