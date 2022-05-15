package com.oncobuddy.app.models.pojo;

public class ProfileStatus {
    private int id;
    private boolean isPending;

    public boolean isPending() {
        return isPending;
    }

    public void setPending(boolean pending) {
        isPending = pending;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ProfileStatus(int id, boolean isPending) {
        this.id = id;
        this.isPending = isPending;
    }
}
