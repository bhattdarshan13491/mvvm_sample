package com.oncobuddy.app.models.pojo;

public class OncoHub {
    private int id;
    private String name;
    private int icon;
    private boolean isSelected;

    public OncoHub() {
    }

    public OncoHub(int id, String name, int icon, boolean isSelected) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.isSelected = isSelected;
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

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
