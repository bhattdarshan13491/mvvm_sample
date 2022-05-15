package com.oncobuddy.app.models.pojo.patient_transactions;

import com.google.gson.annotations.SerializedName;

public class Transaction {

	@SerializedName("amount")
	private int amount;

	@SerializedName("paymentMode")
	private String paymentMode;

	@SerializedName("platformCharges")
	private int platformCharges;

	@SerializedName("currency")
	private String currency;

	@SerializedName("paymentDateTime")
	private String paymentDateTime;

	@SerializedName("receiptNumber")
	private String receiptNumber;

	@SerializedName("transactionId")
	private String transactionId;

	public void setAmount(int amount){
		this.amount = amount;
	}

	public int getAmount(){
		return amount;
	}

	public void setPaymentMode(String paymentMode){
		this.paymentMode = paymentMode;
	}

	public String getPaymentMode(){
		return paymentMode;
	}

	public void setPlatformCharges(int platformCharges){
		this.platformCharges = platformCharges;
	}

	public int getPlatformCharges(){
		return platformCharges;
	}

	public void setCurrency(String currency){
		this.currency = currency;
	}

	public String getCurrency(){
		return currency;
	}

	public void setPaymentDateTime(String paymentDateTime){
		this.paymentDateTime = paymentDateTime;
	}

	public String getPaymentDateTime(){
		return paymentDateTime;
	}

	public void setReceiptNumber(String receiptNumber){
		this.receiptNumber = receiptNumber;
	}

	public String getReceiptNumber(){
		return receiptNumber;
	}

	public void setTransactionId(String transactionId){
		this.transactionId = transactionId;
	}

	public String getTransactionId(){
		return transactionId;
	}
}