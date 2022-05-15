package com.oncobuddy.app.models.pojo.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

public class WalletTransaction {

    @SerializedName("reference")
    @Expose
    public String reference;

    @SerializedName("amount")
    @Expose
    public Double amount;

    @SerializedName("deposit")
    @Expose
    public Boolean deposit;

    @SerializedName("createdOn")
    @Expose
    public Timestamp createdOn;

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Boolean getDeposit() {
        return deposit;
    }

    public void setDeposit(Boolean deposit) {
        this.deposit = deposit;
    }
}
