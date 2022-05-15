package com.oncobuddy.app.models.pojo.appointments.input;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentInput {

@SerializedName("amount")
@Expose
private Integer amount;

@SerializedName("voucherId")
@Expose
private Integer voucherId;

    @SerializedName("discountAmount")
    @Expose
    private Integer discountAmount;

    @SerializedName("platformCharges")
    @Expose
    private Integer platformCharges;

    @SerializedName("voucherUsed")
    @Expose
    private Boolean voucherUsed;

@SerializedName("currency")
@Expose
private String currency;
@SerializedName("paymentDone")
@Expose
private Boolean paymentDone;
@SerializedName("paymentMode")
@Expose
private String paymentMode;
@SerializedName("paymentTime")
@Expose
private String paymentTime;
@SerializedName("transactionId")
@Expose
private String transactionId;

public Integer getAmount() {
return amount;
}

public void setAmount(Integer amount) {
this.amount = amount;
}

public String getCurrency() {
return currency;
}

public void setCurrency(String currency) {
this.currency = currency;
}

public Boolean getPaymentDone() {
return paymentDone;
}

public void setPaymentDone(Boolean paymentDone) {
this.paymentDone = paymentDone;
}

public String getPaymentMode() {
return paymentMode;
}

public void setPaymentMode(String paymentMode) {
this.paymentMode = paymentMode;
}

public String getPaymentTime() {
return paymentTime;
}

public void setPaymentTime(String paymentTime) {
this.paymentTime = paymentTime;
}

public String getTransactionId() {
return transactionId;
}

public void setTransactionId(String transactionId) {
this.transactionId = transactionId;
}

    public Integer getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(Integer voucherId) {
        this.voucherId = voucherId;
    }

    public Integer getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Integer discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Integer getPlatformCharges() {
        return platformCharges;
    }

    public void setPlatformCharges(Integer platformCharges) {
        this.platformCharges = platformCharges;
    }

    public Boolean getVoucherUsed() {
        return voucherUsed;
    }

    public void setVoucherUsed(Boolean voucherUsed) {
        this.voucherUsed = voucherUsed;
    }
}