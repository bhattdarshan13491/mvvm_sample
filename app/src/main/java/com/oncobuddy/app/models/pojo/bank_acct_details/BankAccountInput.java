package com.oncobuddy.app.models.pojo.bank_acct_details;

import com.google.gson.annotations.SerializedName;

public class BankAccountInput{

	/*private String phoneNumber;
	private String bankAccountType;

	//If bank account
	private String name;
	private String accountNumber;
	private String ifsc;

	//If UPI
	private String address;*/

	@SerializedName("address")
	private String address;

	@SerializedName("phoneNumber")
	private String phoneNumber;

	@SerializedName("bankAccountType")
	private String bankAccountType;

	@SerializedName("name")
	private String name;

	@SerializedName("accountNumber")
	private String accountNumber;

	@SerializedName("ifsc")
	private String ifsc;

	public void setAddress(String address){
		this.address = address;
	}

	public String getAddress(){
		return address;
	}

	public void setPhoneNumber(String phoneNumber){
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneNumber(){
		return phoneNumber;
	}

	public void setBankAccountType(String bankAccountType){
		this.bankAccountType = bankAccountType;
	}

	public String getBankAccountType(){
		return bankAccountType;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setAccountNumber(String accountNumber){
		this.accountNumber = accountNumber;
	}

	public String getAccountNumber(){
		return accountNumber;
	}

	public void setIfsc(String ifsc){
		this.ifsc = ifsc;
	}

	public String getIfsc(){
		return ifsc;
	}
}