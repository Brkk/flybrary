package com.uvic.textshare.service.model;

public class User {
	private String name;
	private String email;
	private String uid;
	private int offer_karma;
	private int request_karma;
	private double lat;
	private double lang;
	private String location;
	private double radius; 


	public User(String name, String email, String uid, int offer_karma, int request_karma ,double lat, double lang, String location) {
		super();
		this.name = name;
		this.email = email;
		this.uid = uid;
		this.offer_karma = offer_karma;
		this.request_karma = request_karma;
		this.lat = lat;
		this.lang = lang;
		this.location = location;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public int getOfferKarma() {
		return offer_karma;
	}
	public void setOfferKarma(int offer_karma) {
		this.offer_karma = offer_karma;
	}
	public int getRequestKarma() {
		return request_karma;
	}
	public void setResquestKarma(int request_karma) {
		this.request_karma = request_karma;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public double getLang() {
		return lang;
	}
	public void setLang(Double lang) {
		this.lang = lang;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}

}
