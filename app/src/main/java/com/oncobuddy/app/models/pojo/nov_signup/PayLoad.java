package com.oncobuddy.app.models.pojo.nov_signup;

import com.google.gson.annotations.SerializedName;

public class PayLoad{

	@SerializedName("headers")
	private Headers headers;

	@SerializedName("statusCodeValue")
	private int statusCodeValue;

	@SerializedName("body")
	private Body body;

	@SerializedName("statusCode")
	private String statusCode;

	@SerializedName("firstName")
	private String firstName;

	@SerializedName("lastName")
	private Object lastName;

	@SerializedName("phoneNumber")
	private String phoneNumber;

	@SerializedName("role")
	private String role;

	@SerializedName("dpLink")
	private Object dpLink;

	@SerializedName("authToken")
	private String authToken;

	@SerializedName("profile")
	private Profile profile;


	@SerializedName("userId")
	private int userIdd;

	@SerializedName("headline")
	private Object headline;

	@SerializedName("email")
	private String email;

	public void setHeaders(Headers headers){
		this.headers = headers;
	}

	public Headers getHeaders(){
		return headers;
	}

	public void setStatusCodeValue(int statusCodeValue){
		this.statusCodeValue = statusCodeValue;
	}

	public int getStatusCodeValue(){
		return statusCodeValue;
	}

	public void setBody(Body body){
		this.body = body;
	}

	public Body getBody(){
		return body;
	}

	public void setStatusCode(String statusCode){
		this.statusCode = statusCode;
	}

	public String getStatusCode(){
		return statusCode;
	}

	public void setFirstName(String firstName){
		this.firstName = firstName;
	}

	public String getFirstName(){
		return firstName;
	}

	public void setLastName(Object lastName){
		this.lastName = lastName;
	}

	public Object getLastName(){
		return lastName;
	}

	public void setPhoneNumber(String phoneNumber){
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneNumber(){
		return phoneNumber;
	}

	public void setRole(String role){
		this.role = role;
	}

	public String getRole(){
		return role;
	}

	public void setDpLink(Object dpLink){
		this.dpLink = dpLink;
	}

	public Object getDpLink(){
		return dpLink;
	}

	public void setAuthToken(String authToken){
		this.authToken = authToken;
	}

	public String getAuthToken(){
		return authToken;
	}

	public void setProfile(Profile profile){
		this.profile = profile;
	}

	public Profile getProfile(){
		return profile;
	}

	public int getUserIdd() {
		return userIdd;
	}

	public void setUserIdd(int userIdd) {
		this.userIdd = userIdd;
	}

	public void setHeadline(Object headline){
		this.headline = headline;
	}

	public Object getHeadline(){
		return headline;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return email;
	}
}