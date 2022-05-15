package com.oncobuddy.app.models.pojo;

public class FilterTag {
    public String tagName;
    public String numbers;
    public boolean isSelected;

    public FilterTag() {
    }

    public FilterTag(String tagName, String numbers, boolean isSelected) {
        this.tagName = tagName;
        this.numbers = numbers;
        this.isSelected = isSelected;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getNumbers() {
        return numbers;
    }

    public void setNumbers(String numbers) {
        this.numbers = numbers;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
